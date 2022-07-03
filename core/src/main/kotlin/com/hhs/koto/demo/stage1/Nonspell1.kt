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

package com.hhs.koto.demo.stage1

import com.hhs.koto.stg.task.*
import com.hhs.koto.util.create
import com.hhs.koto.util.difficultySelect
import com.hhs.koto.util.sin

object Nonspell1 : BasicNonspell<AyaBoss>(AyaBoss::class.java) {
    override val health: Float = 1000f
    override val maxTime: Int = 1200

    override fun spell(): Task = CoroutineTask {
        val boss = getBoss()
        var f = 0f
        while (true) {
            for (i in 0 until 360 step 72) {
                create("DS_RICE_S_RED", boss.x, boss.y, 2f, i + f)
                create("DS_RICE_S_BLUE", boss.x, boss.y, 2f, i - f)
            }
            f += sin(frame / 2f) * 6f
            wait(difficultySelect(16, 10, 6, 4))
        }
    }
}