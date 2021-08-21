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

package com.hhs.koto.app.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.screen.DifficultySelectScreen
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.GameMode
import com.hhs.koto.stg.KotoGame
import com.hhs.koto.util.*
import ktx.graphics.copy
import kotlin.math.roundToInt

class GameStatus(val game: KotoGame) : Group() {
    companion object {
        fun difficultyColor(difficulty: GameDifficulty): Color =
            DifficultySelectScreen.difficultyColor(difficulty).copy(blue = 1f)
    }

    private val highScore: Label
    private val score: Label
    private val lifePieces: Label
    private val bombPieces: Label
    private val powerInteger: Label
    private val powerFraction: Label
    private val maxPoint: Label
    private val graze: Label
    private val iconAtlas: TextureAtlas = A["ui/icon.atlas"]

    init {
        addActor(Label(
            bundle["ui.game.status.difficulty.${SystemFlag.difficulty!!.name.lowercase()}"],
            Label.LabelStyle(
                getFont(
                    bundle["font.gameStatus.difficulty"],
                    32, Color.WHITE,
                    borderWidth = 3f, borderColor = Color(0.8f, 0f, 0f, 1f),
                ),
                difficultyColor(SystemFlag.difficulty!!),
            ),
        ).apply {
            setBounds(980f, 1020f, 410f, 40f)
            setAlignment(Align.bottom)
        })

        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(930f, 960f, 500f, 3f)
            color = Color(0f, 0f, 1f, 0.7f)
        })
        addActor(Label(
            bundle["ui.game.status.highScore"],
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.label"], 32, Color.RED),
                Color(0f, 0f, 0.7f, 1f),
            ),
        ).apply {
            setBounds(980f, 960f, 410f, 40f)
        })
        var tmpHighScore = 0L
        when (SystemFlag.gamemode!!) {
            GameMode.STORY, GameMode.EXTRA -> {
                gameData.currentElement.score.forEach {
                    tmpHighScore = tmpHighScore.coerceAtLeast(it.score)
                }
            }
            GameMode.STAGE_PRACTICE -> {
                tmpHighScore = gameData.currentElement.practiceHighScore[SystemFlag.name!!]
            }
            GameMode.SPELL_PRACTICE -> {
                tmpHighScore = gameData.currentElement.spell[SystemFlag.name!!].highScore
            }
        }
        highScore = Label(
            String.format("%,d", tmpHighScore),
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.value"], 36, Color.RED),
                Color(0f, 0f, 0.7f, 1f),
            ),
        ).apply {
            setBounds(980f, 960f, 410f, 40f)
            setAlignment(Align.right)
        }
        addActor(highScore)

        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(930f, 920f, 500f, 3f)
            color = Color(0f, 0f, 1f, 0.7f)
        })
        addActor(Label(
            bundle["ui.game.status.score"],
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.label"], 32, Color.RED),
                WHITE_HSV,
            ),
        ).apply {
            setBounds(980f, 920f, 410f, 40f)
        })
        score = Label(
            String.format("%,d", game.score),
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.value"], 36, Color.RED),
                WHITE_HSV,
            ),
        ).apply {
            setBounds(980f, 920f, 410f, 40f)
            setAlignment(Align.right)
        }
        addActor(score)

        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(930f, 810f, 500f, 3f)
            color = Color(0f, 0f, 1f, 0.7f)
        })
        addActor(Label(
            bundle["ui.game.status.life"],
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.label"], 32, Color.RED),
                Color(0.85f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(980f, 840f, 410f, 40f)
        })
        addActor(
            Label(
                bundle["ui.game.status.lifePieces"],
                Label.LabelStyle(
                    getFont(bundle["font.gameStatus.label"], 24, Color.RED),
                    WHITE_HSV,
                ),
            ).apply {
                setBounds(1220f, 810f, 50f, 30f)
            }
        )
        lifePieces = Label(
            String.format("%d / %d", game.life.fragmentCount, game.life.fragmentFactor),
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.value"], 24, Color.RED),
                WHITE_HSV,
            ),
        ).apply {
            setBounds(980f, 810f, 410f, 30f)
            setAlignment(Align.right)
        }
        addActor(lifePieces)

        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(930f, 730f, 500f, 3f)
            color = Color(0f, 0f, 1f, 0.7f)
        })
        addActor(Label(
            bundle["ui.game.status.bomb"],
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.label"], 32, Color.RED),
                Color(0.4f, 0.3f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(980f, 760f, 410f, 40f)
        })
        addActor(
            Label(
                bundle["ui.game.status.lifePieces"],
                Label.LabelStyle(
                    getFont(bundle["font.gameStatus.label"], 24, Color.RED),
                    WHITE_HSV,
                ),
            ).apply {
                setBounds(1220f, 730f, 50f, 30f)
            }
        )
        bombPieces = Label(
            String.format("%d / %d", game.bomb.fragmentCount, game.bomb.fragmentFactor),
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.value"], 24, Color.RED),
                WHITE_HSV,
            ),
        ).apply {
            setBounds(980f, 730f, 410f, 30f)
            setAlignment(Align.right)
        }
        addActor(bombPieces)

        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(1000f, 640f, 400f, 3f)
            color = Color(0f, 0f, 1f, 0.7f)
        })
        addActor(Label(
            bundle["ui.game.status.power"],
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.label"], 32, Color.RED),
                Color(0f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(1030f, 640f, 350f, 40f)
        })
        addActor(Image(getRegion("item/power.png")).apply {
            setBounds(980f, 640f, 40f, 40f)
        })
        val (tmpInteger, tmpFraction) = splitDecimal(game.maxPower)
        addActor(Label(
            "/ $tmpInteger",
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.value"], 40, Color.RED),
                Color(0f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(1290f, 640f, 100f, 40f)
        })
        addActor(Label(
            tmpFraction,
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.value"], 28, Color.RED),
                Color(0f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(1355f, 645f, 50f, 20f)
        })
        val (tmpInteger2, tmpFraction2) = splitDecimal(game.power)
        powerInteger = Label(
            tmpInteger2,
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.value"], 40, Color.RED),
                Color(0f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(1210f, 640f, 100f, 40f)
        }
        addActor(powerInteger)
        powerFraction = Label(
            tmpFraction2,
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.value"], 28, Color.RED),
                Color(0f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(1245f, 645f, 50f, 20f)
        }
        addActor(powerFraction)

        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(1000f, 600f, 400f, 3f)
            color = Color(0f, 0f, 1f, 0.7f)
        })
        addActor(Label(
            bundle["ui.game.status.maxPoint"],
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.label"], 32, Color.RED),
                Color(0.6f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(1030f, 600f, 360f, 40f)
        })
        addActor(Image(getRegion("item/point.png")).apply {
            setBounds(980f, 600f, 40f, 40f)
        })
        maxPoint = Label(
            String.format("%,d", game.maxPoint),
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.value"], 40, Color.RED),
                Color(0.6f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(1030f, 600f, 360f, 40f)
            setAlignment(Align.right)
        }
        addActor(maxPoint)

        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(1000f, 560f, 400f, 3f)
            color = Color(0f, 0f, 1f, 0.7f)
        })
        addActor(Label(
            bundle["ui.game.status.graze"],
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.label"], 32, Color.RED),
                Color(0f, 0f, 0.7f, 1f),
            ),
        ).apply {
            setBounds(1030f, 560f, 360f, 40f)
        })
        graze = Label(
            String.format("%,d", game.graze),
            Label.LabelStyle(
                getFont(bundle["font.gameStatus.value"], 40, Color.RED),
                WHITE_HSV,
            ),
        ).apply {
            setBounds(1030f, 560f, 360f, 40f)
            setAlignment(Align.right)
        }
        addActor(graze)
    }

    override fun act(delta: Float) {
        score.setText(String.format("%,d", game.score))
        lifePieces.setText(String.format("%d / %d", game.life.fragmentCount, game.life.fragmentFactor))
        bombPieces.setText(String.format("%d / %d", game.bomb.fragmentCount, game.bomb.fragmentFactor))
        val (tmpInteger2, tmpFraction2) = splitDecimal(game.power)
        powerInteger.setText(tmpInteger2)
        powerFraction.setText(tmpFraction2)
        maxPoint.setText(String.format("%,d", game.maxPoint))
        graze.setText(String.format("%,d", game.graze))
        super.act(delta)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        val tmpColor = batch.color.cpy()
        batch.setColor(1f, 1f, 1f, parentAlpha)
        for (i in 0 until 8) {
            if (i < game.life.completedCount) {
                batch.draw(
                    iconAtlas.findRegion("life_icon", 5),
                    x + 1130f + 32f * i, y + 840f, 32f, 36f,
                )
            } else if (i == game.life.completedCount) {
                batch.draw(
                    iconAtlas.findRegion(
                        "life_icon",
                        (game.life.fragmentCount.toFloat() / game.life.fragmentFactor * 5).roundToInt(),
                    ),
                    x + 1130f + 32f * i, y + 840f, 32f, 36f,
                )
            } else {
                batch.draw(
                    iconAtlas.findRegion("life_icon", 0),
                    x + 1130f + 32f * i, y + 840f, 32f, 36f,
                )
            }
        }
        for (i in 0 until 8) {
            if (i < game.bomb.completedCount) {
                batch.draw(
                    iconAtlas.findRegion("bomb_icon", 5),
                    x + 1130f + 32f * i, y + 760f, 32f, 36f,
                )
            } else if (i == game.bomb.completedCount) {
                batch.draw(
                    iconAtlas.findRegion(
                        "bomb_icon",
                        (game.bomb.fragmentCount.toFloat() / game.bomb.fragmentFactor * 5).roundToInt(),
                    ),
                    x + 1130f + 32f * i, y + 760f, 32f, 36f,
                )
            } else {
                batch.draw(
                    iconAtlas.findRegion("bomb_icon", 0),
                    x + 1130f + 32f * i, y + 760f, 32f, 36f,
                )
            }
        }
        batch.color = tmpColor
    }
}