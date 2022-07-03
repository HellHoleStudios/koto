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

package com.hhs.koto.stg

import com.hhs.koto.util.options
import ktx.collections.GdxArray

class FrameScheduler(private val game: KotoGame) {
    private var subFrame: Int = 0
    lateinit var arrangement: GdxArray<Int>
    private var actUpdateFactor: Int = 0
    private var actUpdateCount: Int = 0
    private var actDelta1 = Pair(1f, 0f)
    private var actDelta2 = Pair(1f, 0f)
    private var cleanSpeedMul: Int = 0
    var canPause: Boolean = true
        private set

    fun update() {
        canPause = true
        val fpsMul = options.fpsMultiplier
        if (fpsMul > 1) {
            if (subFrame == 0 && game.speedUpMultiplier != cleanSpeedMul) {
                cleanSpeedMul = game.speedUpMultiplier
                if (cleanSpeedMul < fpsMul) {
                    calculateA()
                } else {
                    calculateB()
                }
            }
            if (cleanSpeedMul < fpsMul) {
                updateA()
            } else {
                updateB()
            }
            subFrame = (subFrame + 1) % fpsMul
        } else {
            repeat(
                if (fpsMul < 0) {
                    game.speedUpMultiplier * -fpsMul
                } else {
                    game.speedUpMultiplier
                } - 1
            ) {
                game.tick()
                game.act(1f)
            }
            game.tick()
            game.draw()
            game.act(1f)
        }
    }

    // fpsMul higher than speedMul
    private fun calculateA() {
        val fpsMul = options.fpsMultiplier
        val speedMul = cleanSpeedMul

        actUpdateCount = fpsMul / speedMul
        actUpdateFactor = fpsMul % speedMul

        arrangement = GdxArray()
        arrange(actUpdateFactor, speedMul).forEach {
            if (it) {
                arrangement.add(0)
                repeat(actUpdateCount) {
                    arrangement.add(1)
                }
            } else {
                arrangement.add(2)
                repeat(actUpdateCount - 1) {
                    arrangement.add(3)
                }
            }
        }

        actDelta1 = calculateDelta(actUpdateCount + 1)
        actDelta2 = calculateDelta(actUpdateCount)

        cleanSpeedMul = speedMul
    }

    private fun updateA() {
        when (arrangement[subFrame]) {
            0 -> {
                game.tick()
                game.draw()
                game.act(actDelta1.first)
            }
            1 -> {
                game.draw()
                game.act(actDelta1.second)
                canPause = false
            }
            2 -> {
                game.tick()
                game.draw()
                game.act(actDelta2.first)
            }
            3 -> {
                game.draw()
                game.act(actDelta2.second)
                canPause = false
            }
        }
    }

    // speedMul higher than fpsMul
    private fun calculateB() {
        val fpsMul = options.fpsMultiplier
        val speedMul = cleanSpeedMul

        actUpdateCount = speedMul / fpsMul
        actUpdateFactor = speedMul % fpsMul

        arrangement = GdxArray()
        arrange(actUpdateFactor, fpsMul).forEach {
            arrangement.add(
                if (it) {
                    0
                } else {
                    1
                }
            )
        }
    }

    private fun updateB() {
        repeat(
            if (arrangement[subFrame] == 0) {
                actUpdateCount
            } else {
                actUpdateCount - 1
            }
        ) {
            game.tick()
            game.act(1f)
        }
        game.tick()
        game.draw()
        game.act(1f)
    }

    // arrange [n] elements with [factor] majors
    private fun arrange(factor: Int, n: Int): GdxArray<Boolean> {
        if (factor <= n / 2) {
            val result = GdxArray<Boolean>()
            if (factor == 0) {
                repeat(n) {
                    result.add(false)
                }
                return result
            }
            val k = n / factor
            repeat(factor) {
                result.add(true)
                repeat(k - 1) {
                    result.add(false)
                }
            }
            repeat(n - result.size) {
                result.add(false)
            }
            return result
        } else {
            val result = arrange(n - factor, n)
            for (i in 0 until result.size) {
                result[i] = !result[i]
            }
            return result
        }
    }

    private fun calculateDelta(n: Int): Pair<Float, Float> {
        val deltaSmall: Float = 1f / n
        var sumDelta = 0f
        repeat(n - 1) {
            sumDelta += deltaSmall
        }
        return Pair(1f - sumDelta + 1e-6f, deltaSmall)
    }
}