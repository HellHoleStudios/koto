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

import com.badlogic.gdx.utils.GdxRuntimeException
import com.hhs.koto.stg.task.SpellCardBuilder
import com.hhs.koto.stg.task.StageBuilder
import com.hhs.koto.stg.task.Task
import com.hhs.koto.stg.task.TaskBuilder
import com.hhs.koto.util.SystemFlag
import ktx.collections.GdxArray
import ktx.collections.filter

object GameBuilder {
    lateinit var regularGame: TaskBuilder
    lateinit var extraGame: TaskBuilder
    val stages = GdxArray<StageBuilder>()
    val spellCards = GdxArray<SpellCardBuilder>()

    fun build(): KotoGame =
        when (SystemFlag.gamemode) {
            GameMode.STORY -> buildRegularGame()
            GameMode.EXTRA -> buildExtraGame()
            GameMode.STAGE_PRACTICE -> buildStagePractice(
                SystemFlag.name ?: throw GdxRuntimeException("name flag is null!"),
                SystemFlag.difficulty ?: throw GdxRuntimeException("difficulty flag is null!"),
            )
            GameMode.SPELL_PRACTICE -> buildStagePractice(
                SystemFlag.name ?: throw GdxRuntimeException("name flag is null!"),
                SystemFlag.difficulty ?: throw GdxRuntimeException("difficulty flag is null!"),
            )
            null -> throw GdxRuntimeException("gameMode flag is null!")
        }

    fun buildRegularGame(): KotoGame = buildGameWithTask(regularGame.build())

    fun buildExtraGame(): KotoGame = buildGameWithTask(extraGame.build())

    fun getAvailableStages(difficulty: GameDifficulty): GdxArray<StageBuilder> =
        stages.filter { difficulty in it.availableDifficulties }

    fun buildStagePractice(name: String, difficulty: GameDifficulty): KotoGame {
        val stageBuilder = stages.find { it.name == name } ?: throw GdxRuntimeException("Stage \"$name\" not found!")
        if (difficulty !in stageBuilder.availableDifficulties) {
            throw GdxRuntimeException("Stage \"$name\" does not support difficulty \"$difficulty\"")
        }
        return buildGameWithTask(stageBuilder.build())
    }

    fun buildSpellPractice(name: String, difficulty: GameDifficulty): KotoGame {
        val spellCardBuilder =
            spellCards.find { it.name == name } ?: throw GdxRuntimeException("SpellCard \"$name\" not found!")
        if (difficulty !in spellCardBuilder.availableDifficulties) {
            throw GdxRuntimeException("SpellCard \"$name\" does not support difficulty \"$difficulty\"")
        }
        return buildGameWithTask(spellCardBuilder.build())
    }

    fun buildGameWithTask(task: Task): KotoGame {
        val game = KotoGame()
        game.tasks.addTask(task)
        return game
    }

}