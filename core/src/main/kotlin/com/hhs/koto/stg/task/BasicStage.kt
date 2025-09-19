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

package com.hhs.koto.stg.task

import com.badlogic.gdx.graphics.Color
import com.hhs.koto.util.*
import ktx.collections.set

abstract class BasicStage : StageBuilder {
    abstract fun stage(): Task
    open val isFinalStage: Boolean = false
    open val isExtraStage: Boolean = false

    fun defaultBonus(stage: Int) {
        var amount = stage * 5000000L
        if (isFinalStage) {
            amount += game.life.completedCount * 10000000L
            amount += game.life.completedCount * 3000000L
        }
        if (isExtraStage) {
            amount += game.life.completedCount * 40000000L
            amount += game.life.completedCount * 4000000L
        }
        game.bonus(bundle["game.stageClear"], amount, Color(0.1f, 1f, 1f, 1f))
    }

    override fun build(): Task = BuilderSequence(
        taskBuilder {
            RunnableTask {
                if (SystemFlag.replay == null) {
                    game.replay.stage = name
                }
                game.resetPlayer()
                if (SystemFlag.replay == null) game.replay.createCheckpoint(game, name)
            }
        },
        taskBuilder { stage() },
        taskBuilder {
            game.stage.recycle()
            RunnableTask {
                if (SystemFlag.replay == null) {
                    if (isFinalStage && game.creditCount == 0) {
                        gameData.data[SystemFlag.shottype!!].extraUnlocked = true
                        gameData.spellPracticeUnlocked = true
                    }
                    gameData.currentElement.practiceUnlocked[name] = true
                    saveGameData()
                }
            }
        },
    ).build()
}