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

import com.hhs.koto.stg.task.*
import com.hhs.koto.util.*
import ktx.collections.GdxArray
import ktx.collections.set

object GameBuilder {
    val usedDifficulties: GdxArray<GameDifficulty> = GdxArray.with(
        GameDifficulty.EASY,
        GameDifficulty.NORMAL,
        GameDifficulty.HARD,
        GameDifficulty.LUNATIC,
        GameDifficulty.EXTRA,
    )
    val regularStages = GdxArray<StageBuilder>()
    var regularEnding: TaskBuilder? = null
    val extraStages = GdxArray<StageBuilder>()
    var extraEnding: TaskBuilder? = null
    val spells = GdxArray<SpellBuilder>()
    val shottypes = GdxArray<Pair<String, () -> Player>>()

    fun build(): KotoGame {
        game = when (SystemFlag.gamemode) {
            GameMode.REGULAR -> buildRegularGame()
            GameMode.EXTRA -> buildExtraGame()
            GameMode.STAGE_PRACTICE -> buildStagePractice(
                SystemFlag.sessionName ?: throw KotoRuntimeException("sessionName flag is null!"),
                SystemFlag.difficulty ?: throw KotoRuntimeException("difficulty flag is null!"),
            )
            GameMode.SPELL_PRACTICE -> buildSpellPractice(
                SystemFlag.sessionName ?: throw KotoRuntimeException("sessionName flag is null!"),
                SystemFlag.difficulty ?: throw KotoRuntimeException("difficulty flag is null!"),
            )
            null -> throw KotoRuntimeException("gameMode flag is null!")
        }
        return game
    }

    fun buildRegularGame(): KotoGame {
        if (SystemFlag.replay == null) {
            gameData.playCount++
            saveGameData()
        }
        val builder = BuilderSequence()
        var startStageIndex = 0
        if (SystemFlag.checkpoint != null) {
            while (regularStages[startStageIndex].name != SystemFlag.checkpoint!!.name) startStageIndex++
        }
        for (i in startStageIndex until regularStages.size) {
            builder.add(regularStages[i])
        }
        if (SystemFlag.replay == null && regularEnding != null) builder.add(regularEnding!!)
        builder.add(taskBuilder {
            RunnableTask {
                game.end()
                if (SystemFlag.replay == null) {
                    game.replay.stage = "clear"
                    gameData.clearCount++
                    saveGameData()
                }
            }
        })
        return buildGameWithTask(builder.build())
    }

    fun buildExtraGame(): KotoGame {
        if (SystemFlag.replay == null) {
            gameData.playCount++
            saveGameData()
        }
        val builder = BuilderSequence()
        var startStageIndex = 0
        if (SystemFlag.checkpoint != null) {
            while (extraStages[startStageIndex].name != SystemFlag.checkpoint!!.name) startStageIndex++
        }
        for (i in startStageIndex until extraStages.size) {
            builder.add(extraStages[i])
        }
        if (SystemFlag.replay == null && extraEnding != null) builder.add(extraEnding!!)
        builder.add(taskBuilder {
            RunnableTask {
                game.end()
                if (SystemFlag.replay == null) {
                    gameData.clearCount++
                    saveGameData()
                }
            }
        })
        return buildGameWithTask(builder.build())
    }

    fun getAvailableStages(): GdxArray<StageBuilder> {
        if (SystemFlag.difficulty == null) throw KotoRuntimeException("difficulty flag is null!")
        val result = GdxArray<StageBuilder>()
        regularStages.forEach {
            if (SystemFlag.difficulty!! in it.availableDifficulties) {
                result.add(it)
            }
        }
        extraStages.forEach {
            if (SystemFlag.difficulty!! in it.availableDifficulties) {
                result.add(it)
            }
        }
        return result
    }

    fun getAvailableSpells(): GdxArray<SpellBuilder> {
        if (SystemFlag.difficulty == null) throw KotoRuntimeException("difficulty flag is null!")
        val result = GdxArray<SpellBuilder>()
        spells.forEach {
            if (SystemFlag.difficulty!! in it.availableDifficulties) {
                result.add(it)
            }
        }
        return result
    }

    fun buildStagePractice(name: String, difficulty: GameDifficulty): KotoGame {
        if (SystemFlag.replay == null) {
            gameData.practiceCount++
            saveGameData()
        }
        val stageBuilder =
            regularStages.find { it.name == name } ?: extraStages.find { it.name == name }
            ?: throw KotoRuntimeException("Stage \"$name\" not found!")
        if (difficulty !in stageBuilder.availableDifficulties) {
            throw KotoRuntimeException("Stage \"$name\" does not support difficulty \"$difficulty\"")
        }
        return buildGameWithTask(
            BuilderSequence(
                stageBuilder,
                taskBuilder {
                    RunnableTask {
                        game.end()
                        if (SystemFlag.replay == null) {
                            gameData.currentElement.practiceHighScore[name] =
                                gameData.currentElement.practiceHighScore[name].coerceAtLeast(game.score)
                            saveGameData()
                        }
                    }
                },
            ).build()
        )
    }

    fun buildSpellPractice(name: String, difficulty: GameDifficulty): KotoGame {
        if (SystemFlag.replay == null) {
            gameData.practiceCount++
            saveGameData()
        }
        val spellBuilder =
            spells.find { it.name == name } ?: throw KotoRuntimeException("Spell \"$name\" not found!")
        if (difficulty !in spellBuilder.availableDifficulties) {
            throw KotoRuntimeException("Spell \"$name\" does not support difficulty \"$difficulty\"")
        }
        return buildGameWithTask(spellBuilder.buildSpellPractice())
    }

    fun buildGameWithTask(task: Task): KotoGame {
        val game = KotoGame()
        if (SystemFlag.checkpoint != null) {
            SystemFlag.checkpoint!!.apply(game)
        }
        game.tasks.addTask(task)
        return game
    }

}