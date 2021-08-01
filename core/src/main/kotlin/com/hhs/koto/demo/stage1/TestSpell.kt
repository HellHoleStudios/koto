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

package com.hhs.koto.demo.stage1

import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.task.*
import com.hhs.koto.util.B
import com.hhs.koto.util.create
import kotlinx.coroutines.yield
import ktx.collections.GdxArray

class TestSpell : SpellBuilder {
    override val name = "stage1.spell1"
    override val availableDifficulties: GdxArray<GameDifficulty> = GameDifficulty.REGULAR_AVAILABLE

    override fun build(): Task = CoroutineTask {
        while (true) {
            for (i in 0 until 360 step 15) {
                create(B["DS_BALL_S_RED"], 0f, 0f, i.toFloat(), 2f).task {
                    while (true) {
                        bullet.angle += 0.5f
                        yield()
                    }
                }
            }
            wait(10)
        }
    }
}
