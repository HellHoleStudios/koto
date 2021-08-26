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

package com.hhs.koto.stg.task

import com.hhs.koto.util.bundle
import com.hhs.koto.util.game

abstract class BasicStage : StageBuilder {
    abstract val bonus: Long
    abstract fun stage(): Task

    companion object {
        fun defaultBonus(stage: Int, isFinalStage: Boolean = false, isExtraStage: Boolean = false): Long {
            var result = stage * 5000000L
            if (isFinalStage) {
                result += game.life.completedCount * 10000000L
                result += game.life.completedCount * 3000000L
            }
            if (isExtraStage) {
                result += game.life.completedCount * 40000000L
                result += game.life.completedCount * 4000000L
            }
            return result
        }
    }

    override fun build(): Task = BuilderSequence(
        taskBuilder {
            RunnableTask {
                game.currentStage = name
                game.replay.createCheckpoint(game, name)
            }
        },
        taskBuilder { stage() },
        taskBuilder {
            RunnableTask {
                game.bonus(bundle["game.stageClear"], bonus)
            }
        },
        taskBuilder { DelayTask(120) },
    ).build()
}