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
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.hhs.koto.app.Config
import com.hhs.koto.app.ui.ConstrainedGrid
import com.hhs.koto.app.ui.GridButton
import com.hhs.koto.app.ui.hsvColor
import com.hhs.koto.app.ui.register
import com.hhs.koto.stg.GameBuilder
import com.hhs.koto.util.*
import ktx.actors.alpha
import ktx.actors.plusAssign

class StageSelectScreen : BasicScreen(Config.uiBgm, getRegion(Config.uiBackground)) {
    private val selectionBackground = Image(getRegion("ui/blank.png")).apply {
        color = Color(0f, 0.5f, 1f, 0.5f)
        setSize(1440f, 60f)
        st += this
    }
    private val title = Label(
        bundle["ui.stageSelect.title"],
        Label.LabelStyle(getFont(72, bundle["font.title"]), WHITE_HSV),
    ).apply {
        setPosition(80f, 1100f)
        st += this
    }
    private val grid = ConstrainedGrid(
        120f,
        200f,
        10000f,
        630f,
        animationDuration = 0.5f,
        interpolation = Interpolation.pow5Out,
    ).setCullingToConstraint().register(st, input)

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        title.addAction(moveTo(80f, 900f, 0.5f, Interpolation.pow5Out))

        val stages = GameBuilder.getAvailableStages()

        grid.clear()
        selectionBackground.clearActions()
        if (stages.size > 0) {
            for (i in 0 until stages.size) {
                val unlocked = gameData.currentElement.practiceUnlocked[stages[i].name]
                grid.add(
                    GridButton(
                        if (unlocked) {
                            bundle["game.stage.${stages[i].name}.name"]
                        } else {
                            bundle["ui.stageSelect.lockedName"]
                        },
                        48, 0, i,
                        triggerSound = if (unlocked) "ok" else "invalid",
                    ) {
                        if (unlocked) {
                            SystemFlag.sessionName = stages[i].name
                            SystemFlag.redirect = "game"
                            SystemFlag.redirectDuration = 0.5f
                            SystemFlag.replay = null
                            SystemFlag.checkpoint = null
                            SystemFlag.ending = null
                            app.setScreen("blank", 0.5f)
                        }
                    }.apply {
                        activeAction = getActiveAction({
                            forever(Actions.run {
                                selectionBackground.clearActions()
                                selectionBackground.addAction(
                                    parallel(
                                        hsvColor(
                                            Color(i.toFloat() / stages.size, 0.5f, 1f, 0.5f),
                                            0.5f,
                                        ),
                                        moveTo(
                                            0f, y - grid.targetY + 1f,
                                            1f,
                                            Interpolation.pow5Out,
                                        ),
                                    )
                                )
                            })
                        })
                    })
            }
            grid.arrange(0f, 1000f, 0f, -60f)
            grid.selectFirst()
            grid.finishAnimation()
            selectionBackground.setPosition(0f, (grid[0] as Actor).y - grid.targetY + 1f)
        } else {
            selectionBackground.alpha = 0f
        }
    }

    override fun fadeOut(newScreen: KotoScreen?, duration: Float) {
        super.fadeOut(newScreen, duration)
        title.addAction(moveTo(80f, 1100f, 0.5f, Interpolation.pow5Out))
    }

    override fun onQuit() {
        super.onQuit()
        app.setScreen("playerSelect", 0.5f)
    }
}