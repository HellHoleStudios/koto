/*
 * MIT License
 *
 * Copyright (c) 2021-2022 Hell Hole Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.hhs.koto.stg.graphics

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.hhs.koto.util.KotoRuntimeException
import com.hhs.koto.util.StateMachine
import ktx.collections.GdxArray
import ktx.collections.GdxMap
import ktx.collections.lastIndex
import ktx.collections.set

open class StarGraphStateMachineTexture(
    val atlas: TextureAtlas,
    val baseName: String,
    val centerRegionName: String,
    val centerTransitionTime: Int,
) {
    lateinit var stateMachine: StateMachine<TextureRegion, String>
    val branches = GdxMap<String, Branch>()

    val texture: TextureRegion
        get() = stateMachine.state

    data class Branch(
        val vertexRegionName: String? = null,
        val vertexTransitionTime: Int? = null,
        val edgeRegionName: String? = null,
        val edgeTransitionTime: Int? = null,
    )

    fun build() {
        val centerRegions = atlas.findRegions(baseName + centerRegionName)
        val vertexRegions = GdxMap<String, GdxArray<TextureAtlas.AtlasRegion>>()
        val edgeRegions = GdxMap<String, GdxArray<TextureAtlas.AtlasRegion>>()

        branches.forEach {
            val branch = it.value
            if (branch.vertexRegionName != null && branch.vertexTransitionTime != null) {
                vertexRegions[it.key] = atlas.findRegions(baseName + branch.vertexRegionName)
            }
            if (branch.edgeRegionName != null && branch.edgeTransitionTime != null) {
                edgeRegions[it.key] = atlas.findRegions(baseName + branch.edgeRegionName)
            }
        }

        stateMachine = StateMachine(centerRegions[0])
        for (i in 0 until centerRegions.size) {
            stateMachine.states[centerRegions[i]] = {
                if (it == "") Pair(centerRegions[(i + 1) % centerRegions.size], centerTransitionTime)
                else {
                    val branch = branches[it] ?: throw KotoRuntimeException("Branch $it does not exist!!")
                    if (branch.edgeRegionName != null && branch.edgeTransitionTime != null) {
                        Pair(
                            edgeRegions[it][0],
                            branch.edgeTransitionTime,
                        )
                    } else {
                        Pair(
                            vertexRegions[it][0],
                            branch.vertexTransitionTime!!,
                        )
                    }
                }
            }
        }
        branches.forEach {
            val branchName = it.key
            val branch = it.value
            if (branch.edgeRegionName == null || branch.edgeTransitionTime == null) {
                for (i in 0 until vertexRegions[branchName].size) {
                    stateMachine.states[vertexRegions[branchName][i]] = {
                        when (it) {
                            branchName -> Pair(
                                vertexRegions[branchName][(i + 1) % vertexRegions[branchName].size],
                                branch.vertexTransitionTime!!,
                            )
                            else -> Pair(centerRegions[0], branch.vertexTransitionTime!!)
                        }
                    }
                }
            } else if (branch.vertexRegionName == null || branch.vertexTransitionTime == null) {
                for (i in 0 until edgeRegions[branchName].size) {
                    stateMachine.states[edgeRegions[branchName][i]] = {
                        when (it) {
                            branchName -> if (i == edgeRegions[branchName].lastIndex) {
                                Pair(edgeRegions[branchName][i], branch.edgeTransitionTime)
                            } else {
                                Pair(edgeRegions[branchName][i + 1], branch.edgeTransitionTime)
                            }
                            else -> if (i == 0) {
                                Pair(centerRegions[0], branch.edgeTransitionTime)
                            } else {
                                Pair(edgeRegions[branchName][i - 1], branch.edgeTransitionTime)
                            }
                        }
                    }
                }
            } else {
                for (i in 0 until vertexRegions[branchName].size) {
                    stateMachine.states[vertexRegions[branchName][i]] = {
                        when (it) {
                            branchName -> Pair(
                                vertexRegions[branchName][(i + 1) % vertexRegions[branchName].size],
                                branch.vertexTransitionTime
                            )
                            else -> Pair(edgeRegions[branchName].last(), branch.edgeTransitionTime)
                        }
                    }
                }
                for (i in 0 until edgeRegions[branchName].size) {
                    stateMachine.states[edgeRegions[branchName][i]] = {
                        when (it) {
                            branchName -> if (i == edgeRegions[branchName].lastIndex) {
                                Pair(vertexRegions[branchName][0], branch.edgeTransitionTime)
                            } else {
                                Pair(edgeRegions[branchName][i + 1], branch.edgeTransitionTime)
                            }
                            else -> if (i == 0) {
                                Pair(centerRegions[0], branch.edgeTransitionTime)
                            } else {
                                Pair(edgeRegions[branchName][i - 1], branch.edgeTransitionTime)
                            }
                        }
                    }
                }
            }
        }
    }

    fun update(condition: String) {
        stateMachine.update(condition)
    }
}