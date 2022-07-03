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
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.Config
import com.hhs.koto.app.ui.*
import com.hhs.koto.stg.GameBuilder
import com.hhs.koto.util.*
import ktx.actors.alpha
import ktx.actors.plusAssign

class SpellSelectScreen : BasicScreen(Config.uiBgm, getRegion(Config.uiBackground)) {
    private val title = Label(
        bundle["ui.spellSelect.title"],
        Label.LabelStyle(getFont(72, bundle["font.title"]), WHITE_HSV),
    ).apply {
        setPosition(80f, 1100f)
        st += this
    }
    private val selectionBackground = Image(getRegion("ui/blank.png")).apply {
        color = Color(0f, 0f, 1f, 0.5f)
        setSize(1440f, 45f)
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

        val spells = GameBuilder.getAvailableSpells()
        grid.clear()
        selectionBackground.clearActions()
        if (spells.size > 0) {
            for (i in 0 until spells.size) {
                val spellEntry = gameData.currentElement.spell[spells[i].name]

                // I don't understand ZUN logic...
                var unlocked: Boolean = spellEntry.practiceUnlocked
                gameData.data.safeValues().forEach {
                    unlocked = unlocked || it.data[SystemFlag.difficulty!!.name].spell[spells[i].name].practiceUnlocked
                }
                val labelStyle = getUILabelStyle(
                    36,
                    if (unlocked && spellEntry.successfulAttempt > 0) {
                        CYAN_HSV
                    } else {
                        WHITE_HSV
                    },
                )
                grid.add(GridButton(
                    if (unlocked) {
                        bundle["game.spell.${spells[i].name}.name"]
                    } else {
                        bundle["ui.spellSelect.lockedName"]
                    },
                    0, i, activeStyle = labelStyle,
                    staticY = 1000f - i * 45f, width = 1000f, height = 36f,
                    triggerSound = if (unlocked) "ok" else "invalid",
                ) {
                    if (unlocked) {
                        SystemFlag.sessionName = spells[i].name
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
                                        Color(i.toFloat() / spells.size, 0.5f, 1f, 0.5f),
                                        0.5f,
                                    ),
                                    moveTo(
                                        0f, y - grid.targetY - 3f,
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
                        if (unlocked) {
                            String.format("%d / %d", spellEntry.successfulAttempt, spellEntry.totalAttempt)
                        } else {
                            bundle["ui.spellSelect.lockedHistory"]
                        },
                        0, i, staticX = 1000f, staticY = 1000f - i * 45f, width = 100f, height = 36f,
                        activeStyle = labelStyle, triggerSound = null,
                    ).apply {
                        setAlignment(Align.right)
                    }
                )
            }
            grid.selectFirst()
            grid.finishAnimation()
            selectionBackground.setPosition(0f, (grid[0] as Actor).y - grid.targetY - 3f)
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