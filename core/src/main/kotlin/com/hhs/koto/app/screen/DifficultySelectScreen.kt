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
import com.hhs.koto.app.ui.Grid
import com.hhs.koto.app.ui.GridImage
import com.hhs.koto.app.ui.ScrollingGrid
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.GameMode
import com.hhs.koto.util.SystemFlag
import com.hhs.koto.util.app
import com.hhs.koto.util.getRegion

class DifficultySelectScreen : BasicScreen("mus/E.0120.ogg", getRegion("bg/generic.png")) {
    val grid =
        ScrollingGrid(staticX = 120f, staticY = 200f, animationDuration = 1f, interpolation = Interpolation.pow5Out)

    init {
        st.addActor(grid)
        val switchTarget: String = when (SystemFlag.gamemode) {
            GameMode.SPELL_PRACTICE -> "spellSelect"
            GameMode.STAGE_PRACTICE -> "stageSelect"
            else -> "playerSelect"
        }
        grid.add(
            Grid(staticX = 120f, staticY = 740f).add(
                GridImage(
                    getRegion("ui/diff_bg.png"),
                    width = 720f,
                    height = 360f,
                    tint = Color.RED
                ) {
                    SystemFlag.difficulty = GameDifficulty.EASY
                    app.setScreen(switchTarget, 0.5f)
                }).selectFirst().apply {
                width = 720f
                height = 360f
            }
        )
        grid.add(GridImage(getRegion("ui/normal.png"), 0, 1, 120f, 560f, 720f, 360f) {
            SystemFlag.difficulty = GameDifficulty.NORMAL
            app.setScreen(switchTarget, 0.5f)
        })
        grid.add(GridImage(getRegion("ui/hard.png"), 0, 2, 120f, 380f, 720f, 360f) {
            SystemFlag.difficulty = GameDifficulty.HARD
            app.setScreen(switchTarget, 0.5f)
        })
        grid.add(GridImage(getRegion("ui/lunatic.png"), 0, 3, 120f, 200f, 720f, 360f) {
            SystemFlag.difficulty = GameDifficulty.LUNATIC
            app.setScreen(switchTarget, 0.5f)
        })
        grid.add(GridImage(getRegion("ui/extra.png"), 0, 4, 120f, 20f, 720f, 360f) {
            SystemFlag.difficulty = GameDifficulty.EXTRA
            app.setScreen(switchTarget, 0.5f)
        })
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
            grid[i].enabled = SystemFlag.gamemode!!.hasRegularDifficulty()
            (grid[i] as Actor).isVisible = SystemFlag.gamemode!!.hasRegularDifficulty()
        }
        grid[4].enabled = SystemFlag.gamemode!!.hasExtraDifficulty()
        (grid[4] as Actor).isVisible = SystemFlag.gamemode!!.hasExtraDifficulty()

        if (SystemFlag.difficulty == null) {
            grid.selectFirst()
        } else {
            grid.select(0, SystemFlag.difficulty!!.toInt() - 1)
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