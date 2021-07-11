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
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.ui.Grid
import com.hhs.koto.app.ui.GridButton
import com.hhs.koto.app.ui.GridImage
import com.hhs.koto.app.ui.ScrollingGrid
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.GameMode
import com.hhs.koto.util.SystemFlag
import com.hhs.koto.util.app
import com.hhs.koto.util.getFont
import com.hhs.koto.util.getRegion

class DifficultySelectScreen : BasicScreen("mus/E.0120.ogg", getRegion("bg/generic.png")) {
    val grid =
        ScrollingGrid(staticX = 120f, staticY = 300f, animationDuration = 1f, interpolation = Interpolation.pow5Out)

    init {
        st.addActor(grid)
        val switchTarget: String = when (SystemFlag.gamemode) {
            GameMode.SPELL_PRACTICE -> "spellSelect"
            GameMode.STAGE_PRACTICE -> "stageSelect"
            else -> "playerSelect"
        }

        grid.add(
            Grid(gridX = 0, gridY = -4, width = 720f, height = 360f).add(
                GridImage(
                    getRegion("ui/diff_bg.png"),
                    width = 720f,
                    height = 360f,
                    tint = Color(0.29f, 1f, 0.72f, 1f)
                ) {
                    SystemFlag.difficulty = GameDifficulty.EASY
                    app.setScreen(switchTarget, 0.5f)
                }
            ).add(
                GridButton("EASY", 140, staticY = 150f, width = 720f, height = 200f).apply {
                    setAlignment(Align.bottom)
                }
            ).add(
                GridButton(
                    "Only kids play in EASY MODO!!",
                    staticY = 50f,
                    width = 720f,
                    height = 130f,
                    activeStyle = Label.LabelStyle(
                        getFont("font/SourceSerifPro-BoldItalic.ttf", 48, borderColor = null), Color.WHITE
                    )
                ).apply {
                    activeAction = getActiveAction()
                    inactiveAction = getInactiveAction()
                    setAlignment(Align.center)
                }
            ).apply {
                setOrigin(Align.left)
                activeAction = { Actions.scaleTo(1f, 1f, 0.5f, Interpolation.pow5Out) }
                inactiveAction = { Actions.scaleTo(0.6f, 0.6f, 0.5f, Interpolation.pow5Out) }
            }.selectFirst()
        )
        grid.add(
            Grid(gridX = 0, gridY = -3, width = 720f, height = 360f).add(
                GridImage(
                    getRegion("ui/diff_bg.png"),
                    width = 720f,
                    height = 360f,
                    tint = Color(0.46f, 1f, 0.72f, 1f)
                ) {
                    SystemFlag.difficulty = GameDifficulty.NORMAL
                    app.setScreen(switchTarget, 0.5f)
                }
            ).add(
                GridButton("NORMAL", 140, staticY = 150f, width = 720f, height = 200f).apply {
                    setAlignment(Align.bottom)
                }
            ).add(
                GridButton(
                    "Average Touhou Enjoyer",
                    staticY = 50f,
                    width = 720f,
                    height = 130f,
                    activeStyle = Label.LabelStyle(
                        getFont("font/SourceSerifPro-BoldItalic.ttf", 48, borderColor = null), Color.WHITE
                    )
                ).apply {
                    activeAction = getActiveAction()
                    inactiveAction = getInactiveAction()
                    setAlignment(Align.center)
                }
            ).apply {
                setOrigin(Align.left)
                activeAction = { Actions.scaleTo(1f, 1f, 0.5f, Interpolation.pow5Out) }
                inactiveAction = { Actions.scaleTo(0.6f, 0.6f, 0.5f, Interpolation.pow5Out) }
            }.selectFirst()
        )
        grid.add(
            Grid(gridX = 0, gridY = -2, width = 720f, height = 360f).add(
                GridImage(
                    getRegion("ui/diff_bg.png"),
                    width = 720f,
                    height = 360f,
                    tint = Color(0.6f, 1f, 0.72f, 1f)
                ) {
                    SystemFlag.difficulty = GameDifficulty.HARD
                    app.setScreen(switchTarget, 0.5f)
                }
            ).add(
                GridButton("HARD", 140, staticY = 150f, width = 720f, height = 200f).apply {
                    setAlignment(Align.bottom)
                }
            ).add(
                GridButton(
                    "It's big brain time",
                    staticY = 50f,
                    width = 720f,
                    height = 130f,
                    activeStyle = Label.LabelStyle(
                        getFont("font/SourceSerifPro-BoldItalic.ttf", 48, borderColor = null), Color.WHITE
                    )
                ).apply {
                    activeAction = getActiveAction()
                    inactiveAction = getInactiveAction()
                    setAlignment(Align.center)
                }
            ).apply {
                setOrigin(Align.left)
                activeAction = { Actions.scaleTo(1f, 1f, 0.5f, Interpolation.pow5Out) }
                inactiveAction = { Actions.scaleTo(0.6f, 0.6f, 0.5f, Interpolation.pow5Out) }
            }.selectFirst()
        )
        grid.add(
            Grid(gridX = 0, gridY = -1, width = 720f, height = 360f).add(
                GridImage(
                    getRegion("ui/diff_bg.png"),
                    width = 720f,
                    height = 360f,
                    tint = Color(0.74f, 1f, 0.72f, 1f)
                ) {
                    SystemFlag.difficulty = GameDifficulty.LUNATIC
                    app.setScreen(switchTarget, 0.5f)
                }
            ).add(
                GridButton("LUNATIC", 140, staticY = 150f, width = 720f, height = 200f).apply {
                    setAlignment(Align.bottom)
                }
            ).add(
                GridButton(
                    "My goals are beyond\nyour understanding",
                    staticY = 50f,
                    width = 720f,
                    height = 130f,
                    activeStyle = Label.LabelStyle(
                        getFont("font/SourceSerifPro-BoldItalic.ttf", 48, borderColor = null), Color.WHITE
                    )
                ).apply {
                    activeAction = getActiveAction()
                    inactiveAction = getInactiveAction()
                    setAlignment(Align.center)
                }
            ).apply {
                setOrigin(Align.left)
                activeAction = { Actions.scaleTo(1f, 1f, 0.5f, Interpolation.pow5Out) }
                inactiveAction = { Actions.scaleTo(0.6f, 0.6f, 0.5f, Interpolation.pow5Out) }
            }.selectFirst()
        )
        grid.add(
            Grid(gridX = 0, gridY = 0, width = 720f, height = 360f).add(
                GridImage(
                    getRegion("ui/diff_bg.png"),
                    width = 720f,
                    height = 360f,
                    tint = Color(0f, 1f, 0.72f, 1f)
                ) {
                    SystemFlag.difficulty = GameDifficulty.NORMAL
                    app.setScreen(switchTarget, 0.5f)
                }
            ).add(
                GridButton("EXTRA", 140, staticY = 150f, width = 720f, height = 200f).apply {
                    setAlignment(Align.bottom)
                }
            ).add(
                GridButton(
                    "But wait\nThere's more",
                    staticY = 50f,
                    width = 720f,
                    height = 130f,
                    activeStyle = Label.LabelStyle(
                        getFont("font/SourceSerifPro-BoldItalic.ttf", 48, borderColor = null), Color.WHITE
                    )
                ).apply {
                    activeAction = getActiveAction()
                    inactiveAction = getInactiveAction()
                    setAlignment(Align.center)
                }
            ).apply {
                setOrigin(Align.left)
                activeAction = { Actions.scaleTo(1f, 1f, 0.5f, Interpolation.pow5Out) }
                inactiveAction = { Actions.scaleTo(0.6f, 0.6f, 0.5f, Interpolation.pow5Out) }
            }.selectFirst()
        )
        grid.arrange(0f, 0f, 0f, -250f)
        grid.selectFirst()
        grid.updateComponent()
        input.addProcessor(grid)
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        grid.clearActions()
        grid.updateComponent()
        grid.setPosition(grid.staticX - 400f, grid.staticY)
        grid.addAction(Actions.moveTo(grid.staticX, grid.staticY, duration, Interpolation.sineOut))

        for (i in 0..3) {
            grid[i].active = SystemFlag.gamemode!!.hasRegularDifficulty()
            grid[i].enabled = SystemFlag.gamemode!!.hasRegularDifficulty()
            (grid[i] as Actor).isVisible = SystemFlag.gamemode!!.hasRegularDifficulty()
        }
        grid[4].active = SystemFlag.gamemode!!.hasExtraDifficulty()
        grid[4].enabled = SystemFlag.gamemode!!.hasExtraDifficulty()
        (grid[4] as Actor).isVisible = SystemFlag.gamemode!!.hasExtraDifficulty()

        if (SystemFlag.difficulty != null && grid[SystemFlag.difficulty!!.toInt() - 1].enabled) {
            grid.select(0, SystemFlag.difficulty!!.toInt() - 1)
        } else {
            grid.selectFirst()
        }
    }

    override fun fadeOut(newScreen: KotoScreen?, duration: Float) {
        super.fadeOut(newScreen, duration)
        grid.clearActions()
        grid.addAction(Actions.moveTo(grid.staticX - 400f, grid.staticY, duration, Interpolation.sineOut))
    }

    override fun onQuit() {
        super.onQuit()
        app.setScreen("title", 0.5f)
    }
}