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

package com.hhs.koto.app.screen

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.Config
import com.hhs.koto.app.ui.*
import com.hhs.koto.stg.Checkpoint
import com.hhs.koto.stg.GameMode
import com.hhs.koto.stg.Replay
import com.hhs.koto.util.*
import ktx.actors.alpha
import ktx.actors.plusAssign
import java.text.SimpleDateFormat

class ReplayScreen : BasicScreen(Config.uiBgm, getRegion(Config.uiBackground)) {
    companion object {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")

        fun getDisplayName(replay: Replay, name: String): String =
            if (replay.gameMode!! == GameMode.SPELL_PRACTICE) {
                bundle["game.spell.${name}.name"]
            } else {
                bundle["game.stage.${name}.name"]
            }

        fun launchGame(replay: Replay, checkpoint: Checkpoint) {
            SystemFlag.redirect = "game"
            SystemFlag.redirectDuration = 0.5f
            SystemFlag.replay = replay
            SystemFlag.checkpoint = checkpoint
            SystemFlag.ending = null
            replay.decodeKeys()
            replay.applySystemFlags()
            app.setScreen("blank", 0.5f)
        }
    }

    private val noEntry = Label(
        bundle["ui.replay.noEntry"],
        getUILabelStyle(120, Color(0f, 0f, 1f, 0.5f)),
    ).apply {
        st += this
        setAlignment(Align.center)
        setBounds(200f, 300f, 1040f, 500f)
    }
    private val title = Label(
        bundle["ui.replay.title"],
        Label.LabelStyle(getFont(72, bundle["font.title"]), WHITE_HSV),
    ).apply {
        setPosition(80f, 1100f)
        st += this
    }
    private val selectionBackground = Image(getRegion("ui/blank.png")).apply {
        color = Color(0f, 0f, 1f, 0.5f)
        setSize(1440f, 35f)
        st += this
    }
    private val checkpointSelectMenu = object : Grid() {
        override fun exit() {
            super.exit()
            SE.play("cancel")
            hideCheckpointSelectionMenu()
        }
    }.register(st, input).apply {
        setPosition(500f, 700f)
        activeAction = { Actions.fadeIn(0.5f, Interpolation.pow5Out) }
        inactiveAction = { Actions.fadeOut(0.5f, Interpolation.pow5Out) }
        alpha = 0f
        deactivate()
    }
    private val grid = object : ConstrainedGrid(
        120f,
        200f,
        10000f,
        630f,
        animationDuration = 0.5f,
        interpolation = Interpolation.pow5Out,
    ) {
        override fun exit() {
            super.exit()
            SE.play("cancel")
            app.setScreen("title", 0.5f)
        }
    }.setCullingToConstraint().register(st, input)
    private val checkpointSelectBackground = Image(getRegion("ui/blank.png")).apply {
        st += this
        setBounds(470f, 350f, 500f, 400f)
        color = Color(0f, 0f, 0.2f, 0f)
        checkpointSelectMenu.toFront()
    }

    private fun showCheckpointSelectionMenu(replay: Replay) {
        checkpointSelectMenu.clear()
        for (i in 0 until replay.checkpoints.size) {
            val checkpoint = replay.checkpoints[i]
            checkpointSelectMenu.add(
                GridButton(
                    getDisplayName(replay, checkpoint.name),
                    36,
                    0,
                    i,
                    staticX = 0f,
                    staticY = -45f * i,
                ) {
                    launchGame(replay, checkpoint)
                }
            )
            checkpointSelectMenu.add(
                GridButton(
                    String.format("%012d", checkpoint.score),
                    36,
                    0,
                    i,
                    staticX = 200f,
                    staticY = -45f * i,
                    triggerSound = null,
                )
            )
        }
        checkpointSelectMenu.selectFirst()
        checkpointSelectMenu.activate()
        checkpointSelectBackground.addAction(Actions.alpha(0.8f, 0.5f, Interpolation.pow5Out))
        input.removeProcessor(grid)
    }

    private fun hideCheckpointSelectionMenu() {
        checkpointSelectMenu.deactivate()
        checkpointSelectBackground.addAction(Actions.fadeOut(0.5f, Interpolation.pow5Out))
        input.addProcessor(grid)
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        title.addAction(Actions.moveTo(80f, 900f, 0.5f, Interpolation.pow5Out))

        if (oldScreen is TitleScreen) {
            val replays = loadReplays()
            grid.clear()
            selectionBackground.clearActions()
            if (replays.size > 0) {
                for (i in 0 until replays.size) {
                    val replay = replays[i]
                    grid.add(GridButton(
                        replay.name,
                        28,
                        0,
                        i,
                        staticY = 1000f - i * 35f,
                        staticX = 0f,
                    ) {
                        if (replay.checkpoints.size > 1) {
                            showCheckpointSelectionMenu(replay)
                        } else {
                            launchGame(replay, replay.checkpoints.last())
                        }
                    }.apply {
                        activeAction = getActiveAction({
                            Actions.forever(Actions.run {
                                selectionBackground.clearActions()
                                selectionBackground.addAction(
                                    Actions.parallel(
                                        hsvColor(
                                            Color(i.toFloat() / replays.size, 0.5f, 1f, 0.5f),
                                            0.5f,
                                        ),
                                        Actions.moveTo(
                                            0f, y - grid.targetY + 2f,
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
                            dateFormat.format(replay.date),
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
                            bundle["ui.replay.difficulty.${replay.difficulty!!.name.lowercase()}"],
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
                            bundle["ui.replay.shottype.${replay.shottype!!}"],
                            28,
                            0,
                            i,
                            staticY = 1000f - i * 35f,
                            staticX = 650f,
                            triggerSound = null,
                        )
                    )
                    grid.add(
                        GridButton(
                            if (replay.stage == "clear") {
                                bundle["ui.replay.clear"]
                            } else {
                                getDisplayName(replay, replay.stage)
                            },
                            28,
                            0,
                            i,
                            staticY = 1000f - i * 35f,
                            staticX = 800f,
                            triggerSound = null,
                        )
                    )
                }
                grid.selectFirst()
                grid.finishAnimation()
                selectionBackground.setPosition(0f, (grid[0] as Actor).y - grid.targetY + 3f)
                noEntry.alpha = 0f
            } else {
                selectionBackground.alpha = 0f
                noEntry.alpha = 1f
            }
        }
    }

    override fun fadeOut(newScreen: KotoScreen?, duration: Float) {
        super.fadeOut(newScreen, duration)
        title.addAction(Actions.moveTo(80f, 1100f, 0.5f, Interpolation.pow5Out))
    }

    override fun onQuit() = Unit
}