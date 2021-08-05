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

package com.hhs.koto.stg.player

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.hhs.koto.util.StateMachine
import ktx.collections.set

class BasicPlayerTexture(
    atlas: TextureAtlas,
    baseName: String,
    centerName: String = "_center",
    leftName: String = "_left",
    rightName: String = "_right",
    movingLeftName: String = "_movingLeft",
    movingRightName: String = "_movingRight",
    centerTransitionTime: Int = 5,
    leftTransitionTime: Int = 5,
    rightTransitionTime: Int = 5,
    movingLeftTransitionTime: Int = 2,
    movingRightTransitionTime: Int = 2,
) {
    private val stateMachine: StateMachine<TextureRegion, Int>

    init {
        val center = atlas.findRegions(baseName + centerName)
        val left = atlas.findRegions(baseName + leftName)
        val right = atlas.findRegions(baseName + rightName)
        val movingLeft = atlas.findRegions(baseName + movingLeftName)
        val movingRight = atlas.findRegions(baseName + movingRightName)
        stateMachine = StateMachine(center[0])
        for (i in 0 until center.size) {
            stateMachine.states[center[i]] = {
                when (it) {
                    -1 -> Pair(left[0], movingLeftTransitionTime)
                    1 -> Pair(right[0], movingRightTransitionTime)
                    else -> Pair(center[(i + 1) % center.size], centerTransitionTime)
                }
            }
        }
        for (i in 0 until left.size) {
            stateMachine.states[left[i]] = {
                when (it) {
                    -1 -> Pair(left[(i + 1) % left.size], leftTransitionTime)
                    else -> Pair(movingLeft.last(), movingLeftTransitionTime)
                }
            }
        }
        for (i in 0 until right.size) {
            stateMachine.states[right[i]] = {
                when (it) {
                    1 -> Pair(right[(i + 1) % right.size], rightTransitionTime)
                    else -> Pair(movingRight.last(), movingRightTransitionTime)
                }
            }
        }
        for (i in 0 until movingLeft.size) {
            stateMachine.states[movingLeft[i]] = {
                when (it) {
                    -1 -> if (i == movingLeft.size - 1) {
                        Pair(left[0], movingLeftTransitionTime)
                    } else {
                        Pair(movingLeft[i + 1], movingLeftTransitionTime)
                    }
                    else -> if (i == 0) {
                        Pair(center[0], movingLeftTransitionTime)
                    } else {
                        Pair(movingLeft[i - 1], movingLeftTransitionTime)
                    }
                }
            }
        }
        for (i in 0 until movingRight.size) {
            stateMachine.states[movingRight[i]] = {
                when (it) {
                    1 -> if (i == movingRight.size - 1) {
                        Pair(right[0], movingRightTransitionTime)
                    } else {
                        Pair(movingRight[i + 1], movingRightTransitionTime)
                    }
                    else -> if (i == 0) {
                        Pair(center[0], movingRightTransitionTime)
                    } else {
                        Pair(movingRight[i - 1], movingRightTransitionTime)
                    }
                }
            }
        }
    }

    fun update(condition: Int) {
        stateMachine.update(condition)
    }

    val texture: TextureRegion
        get() = stateMachine.state
}