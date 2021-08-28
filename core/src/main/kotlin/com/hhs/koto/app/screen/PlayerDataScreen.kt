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

package com.hhs.koto.app.screen

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.Config
import com.hhs.koto.app.ui.*
import com.hhs.koto.stg.GameBuilder
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.util.*
import ktx.actors.alpha
import ktx.actors.plusAssign
import ktx.graphics.copy
import java.text.SimpleDateFormat

class PlayerDataScreen : BasicScreen(Config.uiBgm, getRegion(Config.uiBackground)) {
    private val noEntry = Label(
        bundle["ui.playerData.noEntry"],
        Label.LabelStyle(getFont(120), Color(0f, 0f, 1f, 0.5f)),
    ).apply {
        st += this
        setAlignment(Align.center)
        setBounds(200f, 300f, 1040f, 500f)
    }
    private val title = Label(
        bundle["ui.playerData.title"],
        Label.LabelStyle(getFont(72, bundle["font.title"]), WHITE_HSV),
    ).apply {
        setPosition(80f, 900f)
        st += this
    }
    private val shottypeGrid = Grid().register(st)
    private val difficultyGrid = Grid().register(st, input)
    private val arrows = Group().apply {
        st += this
        addActor(
            Image(getRegion("ui/arrow.png")).apply {
                setBounds(0f, 0f, 48f, 48f)
            }
        )
        addActor(
            Image(getRegion("ui/arrow.png")).apply {
                setBounds(360f, 0f, 48f, 48f)
                setScale(-1f, 1f)
            }
        )
        setPosition(550f, 920f)
    }
    private val selectionBackground = Image(getRegion("ui/blank.png")).apply {
        color = Color(0f, 0f, 1f, 0.5f)
        setSize(1440f, 35f)
        st += this
    }
    private val grid = ConstrainedGrid(
        120f,
        400f,
        10000f,
        430f,
        animationDuration = 0.5f,
        interpolation = Interpolation.pow5Out,
    ).setCullingToConstraint().register(st, input)

    lateinit var selectedDifficulty: GameDifficulty
    lateinit var selectedShottype: String

    companion object {
        fun getAlternativeActiveAction(button: GridButton, vararg actions: () -> Action): () -> Action = {
            val ret = ParallelAction()
            ret.addAction(
                Actions.sequence(
                    Actions.show(),
                    Actions.color(Color.WHITE),
                    Actions.moveTo(button.staticX + 2, button.staticY, 0.03f, Interpolation.sine),
                    Actions.moveTo(button.staticX - 4, button.staticY, 0.06f, Interpolation.sine),
                    Actions.moveTo(button.staticX, button.staticY, 0.03f, Interpolation.sine),
                )
            )
            for (action in actions) {
                ret.addAction(action())
            }
            ret
        }

        fun getAlternativeInactiveAction(button: GridButton, vararg actions: () -> Action): () -> Action = {
            val ret = ParallelAction()
            ret.addAction(Actions.hide())
            for (action in actions) {
                ret.addAction(action())
            }
            ret
        }

        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")

        fun shottypeColor(name: String): Color {
            val result = if (name.last().isUpperCase()) {
                PlayerSelectScreen.playerColor(name.dropLast(1))
            } else {
                PlayerSelectScreen.playerColor(name)
            }
            return result.toHSVColor()
        }
    }

    init {
        for (i in 0 until GameBuilder.usedDifficulties.size) {
            val difficulty = GameBuilder.usedDifficulties[i]
            if (!difficulty.isRegular()) continue
            difficultyGrid.add(
                GridButton(
                    bundle["ui.playerData.difficulty.${difficulty!!.name.lowercase()}"],
                    i,
                    0,
                    width = 250f,
                    height = 48f,
                    ignoreParent = true,
                    activeStyle = Label.LabelStyle(
                        getFont(48),
                        DifficultySelectScreen.difficultyColor(difficulty).copy(blue = 1f),
                    )
                ) {
                    input.removeProcessor(difficultyGrid)
                    input.addProcessor(shottypeGrid)
                    arrows.setPosition(970f, 920f)
                }.apply {
                    setAlignment(Align.center)
                    activeAction = getAlternativeActiveAction(this, {
                        Actions.run {
                            selectedDifficulty = difficulty
                            resetGrid()
                        }
                    })
                    inactiveAction = getAlternativeInactiveAction(this)
                }
            )
        }
        difficultyGrid.arrange(600f, 920f, 0f, 0f)
        difficultyGrid.selectFirst()

        for (i in 0 until GameBuilder.shottypes.size) {
            val shottype = GameBuilder.shottypes[i].first
            shottypeGrid.add(
                GridButton(
                    bundle["ui.playerData.shottype.$shottype"],
                    i,
                    0,
                    width = 300f,
                    height = 48f,
                    ignoreParent = true,
                    activeStyle = Label.LabelStyle(
                        getFont(48),
                        shottypeColor(shottype),
                    )
                ) {
                    input.removeProcessor(shottypeGrid)
                    input.addProcessor(difficultyGrid)
                    arrows.setPosition(550f, 920f)
                }.apply {
                    setAlignment(Align.center)
                    activeAction = getAlternativeActiveAction(this, {
                        Actions.run {
                            selectedShottype = shottype
                            resetGrid()
                        }
                    })
                    inactiveAction = getAlternativeInactiveAction(this)
                }
            )
        }
        shottypeGrid.arrange(1000f, 920f, 0f, 0f)
        shottypeGrid.selectFirst()
    }

    fun resetGrid() {
        if (!this::selectedDifficulty.isInitialized || !this::selectedShottype.isInitialized) return
        grid.clear()
        selectionBackground.clearActions()
        val scores = gameData.data[selectedShottype].data[selectedDifficulty.name].score
        if (scores.size > 0) {
            for (i in 0 until scores.size) {
                val score = scores[i]
                grid.add(GridButton(
                    score.name,
                    28,
                    0,
                    i,
                    staticY = 1000f - i * 35f,
                    staticX = 0f,
                ).apply {
                    activeAction = getActiveAction({
                        Actions.forever(Actions.run {
                            selectionBackground.clearActions()
                            selectionBackground.addAction(
                                Actions.parallel(
                                    hsvColor(
                                        Color(i.toFloat() / scores.size, 0.5f, 1f, 0.5f),
                                        0.5f,
                                    ),
                                    Actions.moveTo(
                                        0f, y - grid.targetY - 2.5f,
                                        1f,
                                        Interpolation.pow5Out,
                                    ),
                                )
                            )
                        })
                    })
                })
                grid.add(
                    GridButton(
                        String.format("%,d", score.score),
                        28,
                        0,
                        i,
                        staticY = 1000f - i * 35f,
                        staticX = 300f,
                        triggerSound = null,
                    )
                )
                grid.add(
                    GridButton(
                        dateFormat.format(score.date),
                        28,
                        0,
                        i,
                        staticY = 1000f - i * 35f,
                        staticX = 600f,
                        triggerSound = null,
                    )
                )
                grid.add(
                    GridButton(
                        String.format(bundle["ui.playerData.creditCount"], score.creditCount),
                        28,
                        0,
                        i,
                        staticY = 1000f - i * 35f,
                        staticX = 900f,
                        triggerSound = null,
                    )
                )
            }
            grid.selectFirst()
            grid.finishAnimation()
            selectionBackground.setPosition(0f, (grid[0] as Actor).y - grid.targetY - 2.5f)
            noEntry.alpha = 0f
        } else {
            selectionBackground.alpha = 0f
            noEntry.alpha = 1f
        }
    }

    override fun onQuit() {
        super.onQuit()
        app.setScreen("title", 0.5f)
    }
}