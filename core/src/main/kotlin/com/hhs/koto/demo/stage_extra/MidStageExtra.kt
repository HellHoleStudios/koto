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

package com.hhs.koto.demo.stage_extra

import com.hhs.koto.stg.bullet.BulletGroup
import com.hhs.koto.stg.task.*
import com.hhs.koto.util.create
import com.hhs.koto.util.defaultShotSheet
import com.hhs.koto.util.min
import kotlinx.coroutines.yield

object MidStageExtra : TaskBuilder {
    override fun build(): Task = CoroutineTask {
        var offset = 0f
        val group = BulletGroup()
        task {
            wait(8 * 60)
            repeat(180) {
                group.forEach {
                    it.angle += 0.5f
                }
                yield()
            }
        }
        repeat(15 * 60) {
            if (frame % 3 == 0) {
                for (i in 0..6) {
                    group.addBullet(
                        create(
                            defaultShotSheet[i + 9],
                            0f,
                            100f,
                            min(frame % 60, 60 - frame % 60) / 60f + 1,
                            i * 360f / 7f + offset,
                        )
                    )
                }
            }
            offset += (frame / 30) / 8f + 1
            yield()
        }
        wait(180)
        group.forEach { it.destroy() }
    }
}