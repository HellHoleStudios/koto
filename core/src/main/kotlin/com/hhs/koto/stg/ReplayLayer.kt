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

import com.badlogic.gdx.math.RandomXS128
import com.hhs.koto.util.KotoRuntimeException
import com.hhs.koto.util.SystemFlag
import com.hhs.koto.util.VK
import com.hhs.koto.util.replayKeyMask

class ReplayLayer {
    var replay = Replay()
    var frame = -1

    var rng = RandomXS128()

    /**
     * If not in replay mode, create a new start point with name
     *
     * Or load state in a start point
     */
    fun setOrLoadStartPoint(name: String) {
        if (SystemFlag.replay == true) { //using replay mode
            val start = replay.startPoint.first { it.name == name }
            rng = RandomXS128(start.rng)

            //TODO Load System State
        } else {
            //recording a replay
            val newRng = System.currentTimeMillis()
            rng = RandomXS128(newRng)
            replay.startPoint.add(StartPoint(frame, newRng, name))

            //TODO Save System State
        }
    }

    /**
     * Should be called each frame. Will update frame index and more.
     */
    fun tick() {
        frame++

        if (SystemFlag.replay == true) {
            //TODO using replay. Don't do anything?
        } else {
            var mask = 0
            for ((index, key) in replayKeyMask.withIndex()) {
                if (key.pressed()) {
                    mask = mask or (1 shl index)
                }
            }

            replay.mask.add(mask)

            if (replay.mask.size - 1 != frame) {
                throw KotoRuntimeException("Replay Dislocation! Expected $frame but found ${replay.mask.size - 1}")
            }
        }
    }

    /**
     * Finish a replay recording
     */
    fun conclude() {
        if (SystemFlag.replay == true) {
            //TODO IDK What to put here
        } else {
            replay.date = System.currentTimeMillis()
            replay.user = "Leatherman"
        }
    }

    fun random(a: Float, b: Float): Float {
        return rng.nextFloat() * (b - a) + a
    }

    fun pressed(code: VK): Boolean {
        return pressed(replayKeyMask.indexOf(code))
    }

    fun pressed(code: Int): Boolean {
        return if (SystemFlag.replay == true) {
            (replay.mask[frame] and (1 shl code)) > 0
        } else {
            replayKeyMask[code].pressed()
        }
    }

    fun justPressed(code: Int): Boolean {
        return if (SystemFlag.replay == true) {
            (replay.mask[frame] and (1 shl code)) > 0 && (frame == 0 || (replay.mask[frame - 1] and (1 shl code)) == 0)
        } else {
            replayKeyMask[code].justPressed()
        }
    }
}