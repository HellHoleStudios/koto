/*
 * MIT License
 *
 * Copyright (c) 2021 Hell Hole Studios
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

package com.hhs.koto.stg.drawable

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.hhs.koto.util.StateMachine
import ktx.collections.set

class BasicEnemyTexture(
    atlas: TextureAtlas,
    baseName: String,
    centerRegionName: String = "_center",
    movingRightRegionName: String = "_movingRight",
    centerTransitionTime: Int = 5,
    movingRightTransitionTime: Int = 10,
) {

    protected val stateMachine: StateMachine<TextureRegion, String>

    init {
        val centerRegions = atlas.findRegions(baseName + centerRegionName)
        val movingRightRegions = atlas.findRegions(baseName + movingRightRegionName)
        stateMachine = StateMachine(centerRegions[0])
        for (i in 0 until centerRegions.size) {
            stateMachine.states[centerRegions[i]] = {
                when (it) {
                    "right" -> Pair(movingRightRegions[0], movingRightTransitionTime)
                    else -> Pair(centerRegions[(i + 1) % centerRegions.size], centerTransitionTime)
                }
            }
        }
        for (i in 0 until movingRightRegions.size) {
            stateMachine.states[movingRightRegions[i]] = {
                when (it) {
                    "right" -> if (i == movingRightRegions.size - 1) {
                        Pair(movingRightRegions[i - 1], movingRightTransitionTime)
                    } else {
                        Pair(movingRightRegions[i], movingRightTransitionTime)
                    }
                    else -> if (i == 0) {
                        Pair(centerRegions[0], movingRightTransitionTime)
                    } else {
                        Pair(movingRightRegions[i + 1], movingRightTransitionTime)
                    }
                }
            }
        }
    }

    fun update(condition: Int) {
        when (condition) {
            0 -> stateMachine.update("")
            1 -> stateMachine.update("right")
        }
    }

    val texture: TextureRegion
        get() = stateMachine.state
}