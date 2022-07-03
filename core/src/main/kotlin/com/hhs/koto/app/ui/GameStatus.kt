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

package com.hhs.koto.app.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.screen.DifficultySelectScreen
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.KotoGame
import com.hhs.koto.util.*
import ktx.actors.alpha
import ktx.actors.plus
import ktx.graphics.copy
import java.lang.Long.max
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
    private val pointValue: Label
    private val graze: Label
    private val iconAtlas: TextureAtlas = A["ui/icon.atlas"]

    private var delay = 0.1f
    private val lifeDelay: Float
    private val bombDelay: Float

    var counter = 0f
    var lifeAlpha = 0f
    var bombAlpha = 0f

    private fun addDelayAction(me: Actor) {
        me.alpha = 0f
        me.addAction(delay(delay) + fadeIn(0.5f))
        delay += 0.02f
    }

    private fun addScaleAction(me: Actor) {
        me.scaleX = 0f
        me.setOrigin(Align.center)
        me.addAction(delay(delay) + scaleTo(1f, 1f, 0.5f))
        delay += 0.1f
    }

    init {
        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(930f, 960f, 500f, 3f)
            color = Color(0f, 0f, 1f, 0.7f)
            addScaleAction(this)
        })
        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(930f, 920f, 500f, 3f)
            color = Color(0f, 0f, 1f, 0.7f)
            addScaleAction(this)
        })
        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(930f, 810f, 500f, 3f)
            color = Color(0f, 0f, 1f, 0.7f)
            addScaleAction(this)
        })
        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(930f, 730f, 500f, 3f)
            color = Color(0f, 0f, 1f, 0.7f)
            addScaleAction(this)
        })
        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(1000f, 640f, 400f, 3f)
            color = Color(0f, 0f, 1f, 0.7f)
            addScaleAction(this)
        })
        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(1000f, 600f, 400f, 3f)
            color = Color(0f, 0f, 1f, 0.7f)
            addScaleAction(this)
        })
        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(1000f, 560f, 400f, 3f)
            color = Color(0f, 0f, 1f, 0.7f)
            addScaleAction(this)
        })

        // difficulty
        addActor(Label(
            bundle["ui.game.status.difficulty.${SystemFlag.difficulty!!.name.lowercase()}"],
            Label.LabelStyle(
                getFont(
                    32,
                    bundle["font.gameStatus.difficulty"], Color.WHITE,
                    borderWidth = 3f, borderColor = Color(0.8f, 0f, 0f, 1f),
                ),
                difficultyColor(SystemFlag.difficulty!!),
            ),
        ).apply {
            setBounds(980f, 1020f, 410f, 40f)
            setAlignment(Align.bottom)
            addAction(
                repeat(
                    4, sequence(
                        alpha(0.1f, 0.2f),
                        alpha(1f, 0.2f),
                    )
                )
            )
        })

        // high score
        addActor(Label(
            bundle["ui.game.status.highScore"],
            Label.LabelStyle(
                getFont(32, bundle["font.gameStatus.label"]),
                Color(0f, 0f, 0.7f, 1f),
            ),
        ).apply {
            setBounds(980f, 960f, 410f, 40f)
            addDelayAction(this)
        })

        highScore = Label(
            String.format("%,d", max(game.highScore, game.score)),
            Label.LabelStyle(
                getFont(36, bundle["font.gameStatus.value"]),
                Color(0f, 0f, 0.7f, 1f),
            ),
        ).apply {
            setBounds(980f, 960f, 410f, 40f)
            setAlignment(Align.right)
            addDelayAction(this)
        }
        addActor(highScore)

        // score
        addActor(Label(
            bundle["ui.game.status.score"],
            Label.LabelStyle(
                getFont(32, bundle["font.gameStatus.label"]),
                WHITE_HSV,
            ),
        ).apply {
            setBounds(980f, 920f, 410f, 40f)
            addDelayAction(this)
        })
        score = Label(
            String.format("%,d", game.score),
            Label.LabelStyle(
                getFont(36, bundle["font.gameStatus.value"]),
                WHITE_HSV,
            ),
        ).apply {
            setBounds(980f, 920f, 410f, 40f)
            setAlignment(Align.right)
            addDelayAction(this)
        }
        addActor(score)

        // life
        addActor(Label(
            bundle["ui.game.status.life"],
            Label.LabelStyle(
                getFont(32, bundle["font.gameStatus.label"]),
                Color(0.85f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(980f, 840f, 410f, 40f)
            addDelayAction(this)
        })
        addActor(
            Label(
                bundle["ui.game.status.lifePieces"],
                Label.LabelStyle(
                    getFont(24, bundle["font.gameStatus.label"]),
                    WHITE_HSV,
                ),
            ).apply {
                setBounds(1220f, 810f, 50f, 30f)
                addDelayAction(this)
            }
        )
        lifePieces = Label(
            String.format("%d / %d", game.life.fragmentCount, game.life.fragmentFactor),
            Label.LabelStyle(
                getFont(24, bundle["font.gameStatus.value"]),
                WHITE_HSV,
            ),
        ).apply {
            setBounds(980f, 810f, 410f, 30f)
            setAlignment(Align.right)
            addDelayAction(this)
        }
        addActor(lifePieces)

        lifeDelay = delay

        // bomb
        addActor(Label(
            bundle["ui.game.status.bomb"],
            Label.LabelStyle(
                getFont(32, bundle["font.gameStatus.label"]),
                Color(0.4f, 0.3f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(980f, 760f, 410f, 40f)
            addDelayAction(this)
        })
        addActor(
            Label(
                bundle["ui.game.status.lifePieces"],
                Label.LabelStyle(
                    getFont(24, bundle["font.gameStatus.label"]),
                    WHITE_HSV,
                ),
            ).apply {
                setBounds(1220f, 730f, 50f, 30f)
                addDelayAction(this)
            }
        )
        bombPieces = Label(
            String.format("%d / %d", game.bomb.fragmentCount, game.bomb.fragmentFactor),
            Label.LabelStyle(
                getFont(24, bundle["font.gameStatus.value"]),
                WHITE_HSV,
            ),
        ).apply {
            setBounds(980f, 730f, 410f, 30f)
            setAlignment(Align.right)
            addDelayAction(this)
        }
        addActor(bombPieces)
        bombDelay = delay

        // power
        addActor(Label(
            bundle["ui.game.status.power"],
            Label.LabelStyle(
                getFont(32, bundle["font.gameStatus.label"]),
                Color(0f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(1030f, 640f, 350f, 40f)
            addDelayAction(this)
        })
        addActor(Image(A.get<TextureAtlas>("item/item.atlas").findRegion("power")).apply {
            setBounds(980f, 640f, 40f, 40f)
            addDelayAction(this)
        })

        val (tmpInteger, tmpFraction) = splitDecimal(game.maxPower)
        addActor(Label(
            "/ $tmpInteger",
            Label.LabelStyle(
                getFont(40, bundle["font.gameStatus.value"]),
                Color(0f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(1290f, 640f, 100f, 40f)
            addDelayAction(this)
        })
        addActor(Label(
            tmpFraction,
            Label.LabelStyle(
                getFont(28, bundle["font.gameStatus.value"]),
                Color(0f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(1355f, 645f, 50f, 20f)
            addDelayAction(this)
        })
        val (tmpInteger2, tmpFraction2) = splitDecimal(game.power)
        powerInteger = Label(
            tmpInteger2,
            Label.LabelStyle(
                getFont(40, bundle["font.gameStatus.value"]),
                Color(0f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(1210f, 640f, 100f, 40f)
            addDelayAction(this)
        }
        addActor(powerInteger)
        powerFraction = Label(
            tmpFraction2,
            Label.LabelStyle(
                getFont(28, bundle["font.gameStatus.value"]),
                Color(0f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(1245f, 645f, 50f, 20f)
            addDelayAction(this)
        }
        addActor(powerFraction)

        // value
        addActor(Label(
            bundle["ui.game.status.maxPoint"],
            Label.LabelStyle(
                getFont(32, bundle["font.gameStatus.label"]),
                Color(0.6f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(1030f, 600f, 360f, 40f)
            addDelayAction(this)
        })
        addActor(Image(A.get<TextureAtlas>("item/item.atlas").findRegion("point")).apply {
            setBounds(980f, 600f, 40f, 40f)
            addDelayAction(this)
        })
        pointValue = Label(
            String.format("%,d", game.pointValue),
            Label.LabelStyle(
                getFont(40, bundle["font.gameStatus.value"]),
                Color(0.6f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(1030f, 600f, 360f, 40f)
            setAlignment(Align.right)
            addDelayAction(this)
        }
        addActor(pointValue)

        // graze
        addActor(Label(
            bundle["ui.game.status.graze"],
            Label.LabelStyle(
                getFont(32, bundle["font.gameStatus.label"]),
                Color(0f, 0f, 0.7f, 1f),
            ),
        ).apply {
            setBounds(1030f, 560f, 360f, 40f)
            addDelayAction(this)
        })
        graze = Label(
            String.format("%,d", game.graze),
            Label.LabelStyle(
                getFont(40, bundle["font.gameStatus.value"]),
                WHITE_HSV,
            ),
        ).apply {
            setBounds(1030f, 560f, 360f, 40f)
            setAlignment(Align.right)
            addDelayAction(this)
        }
        addActor(graze)
    }

    override fun act(delta: Float) {
        counter += delta
        lifeAlpha = lerp(0f, 1f, clamp(counter - lifeDelay, 0f, 0.5f) / 0.5f)
        bombAlpha = lerp(0f, 1f, clamp(counter - bombDelay, 0f, 0.5f) / 0.5f)

        score.setText(String.format("%,d", game.score))
        highScore.setText(String.format("%,d", max(game.score, game.highScore)))
        lifePieces.setText(String.format("%d / %d", game.life.fragmentCount, game.life.fragmentFactor))
        bombPieces.setText(String.format("%d / %d", game.bomb.fragmentCount, game.bomb.fragmentFactor))
        val (tmpInteger2, tmpFraction2) = splitDecimal(game.power)
        powerInteger.setText(tmpInteger2)
        powerFraction.setText(tmpFraction2)
        pointValue.setText(String.format("%,d", game.pointValue))
        graze.setText(String.format("%,d", game.graze))
        super.act(delta)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        val tmpColor = batch.color.cpy()

        batch.setColor(1f, 1f, 1f, parentAlpha * lifeAlpha)
        for (i in 0 until game.life.limit) {
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

        batch.setColor(1f, 1f, 1f, parentAlpha * bombAlpha)
        for (i in 0 until game.bomb.limit) {
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