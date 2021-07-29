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

package com.hhs.koto.stg

import com.hhs.koto.util.options

class FrameScheduler(private val game: KotoGame) {
    private var subFrame: Int = 0
    private var actUpdateFactor1: Int = 0
    private var actUpdateFactor2: Int = 0
    private var actDelta1 = Pair(1f, 0f)
    private var actDelta2 = Pair(1f, 0f)

    private fun calculateDelta(n: Int): Pair<Float, Float> {
        val deltaSmall: Float = 1f / n
        var sumDelta = 0f
        repeat(n - 1) {
            sumDelta += deltaSmall
        }
        return Pair(deltaSmall, 1f - sumDelta)
    }

    fun update() {
        val fpsMul = options.fpsMultiplier
        val speedMul = game.speedUpMultiplier
        if (fpsMul > 1) {
            if (speedMul < fpsMul) {
                if (subFrame == 0) {
                    actUpdateFactor2 = fpsMul / speedMul
                    actUpdateFactor1 = fpsMul % speedMul + actUpdateFactor2

                    actDelta1 = calculateDelta(actUpdateFactor1)
                    actDelta2 = calculateDelta(actUpdateFactor2)
                }
                if (subFrame < actUpdateFactor1) {
                    if (subFrame == 0) {
                        game.tasks.update()
                        game.stage.act(actDelta1.first)
                        game.frame++
                    } else {
                        game.stage.act(actDelta1.second)
                    }
                } else {
                    if ((subFrame - actUpdateFactor1) % actUpdateFactor2 == 0) {
                        game.tasks.update()
                        game.stage.act(actDelta2.first)
                        game.frame++
                    } else {
                        game.stage.act(actDelta2.second)
                    }
                }
            } else {
                if (subFrame == 0) {
                    actUpdateFactor1 = speedMul % fpsMul
                    actUpdateFactor2 = speedMul / fpsMul
                }
                repeat(
                    if (subFrame < actUpdateFactor1) {
                        actUpdateFactor2 + 1
                    } else {
                        actUpdateFactor2
                    }
                ) {
                    game.tasks.update()
                    game.stage.act(1f)
                    game.frame++
                }
            }
            subFrame = (subFrame + 1) % fpsMul
        } else {
            val repeatCount = if (fpsMul < 0) {
                speedMul * -fpsMul
            } else {
                speedMul
            }
            repeat(repeatCount) {
                game.tasks.update()
                game.stage.act(1f)
                game.frame++
            }
        }
    }
}