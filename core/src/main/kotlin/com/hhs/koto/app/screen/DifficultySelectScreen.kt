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

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.hhs.koto.app.ui.Grid
import com.hhs.koto.app.ui.GridImage
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.GameMode
import com.hhs.koto.util.getRegion
import com.hhs.koto.util.koto
import com.hhs.koto.util.systemFlag

class DifficultySelectScreen : BasicScreen("mus/E.0120.ogg", getRegion("bg/generic.png")) {
    val grid = Grid()

    init {
        st.addActor(grid)
        val switchTarget: String = when (systemFlag.gamemode) {
            GameMode.SPELL_PRACTICE -> "spellSelect"
            GameMode.STAGE_PRACTICE -> "stageSelect"
            else -> "playerSelect"
        }
        grid.add(GridImage(getRegion("diff/easy.png"), 0, 0, 120f, 740f) {
            systemFlag.difficulty = GameDifficulty.EASY
            koto.setScreen(switchTarget, 0.5f)
        })
        grid.add(GridImage(getRegion("diff/normal.png"), 0, 1, 120f, 560f) {
            systemFlag.difficulty = GameDifficulty.NORMAL
            koto.setScreen(switchTarget, 0.5f)
        })
        grid.add(GridImage(getRegion("diff/hard.png"), 0, 2, 120f, 380f) {
            systemFlag.difficulty = GameDifficulty.HARD
            koto.setScreen(switchTarget, 0.5f)
        })
        grid.add(GridImage(getRegion("diff/lunatic.png"), 0, 3, 120f, 200f) {
            systemFlag.difficulty = GameDifficulty.LUNATIC
            koto.setScreen(switchTarget, 0.5f)
        })
        grid.add(GridImage(getRegion("diff/extra.png"), 0, 4, 120f, 20f) {
            systemFlag.difficulty = GameDifficulty.EXTRA
            koto.setScreen(switchTarget, 0.5f)
        })
        grid.updateComponent()
        grid.update()
        input.addProcessor(grid)
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        grid.clearActions()
        grid.setPosition(-400f, 0f)
        grid.addAction(Actions.moveTo(0f, 0f, duration, Interpolation.sineOut))

        for (i in 0..3) {
            grid[i].enabled = systemFlag.gamemode!!.hasRegularDifficulty()
            (grid[i] as GridImage).isVisible = systemFlag.gamemode!!.hasRegularDifficulty()
        }
        grid[4].enabled = systemFlag.gamemode!!.hasExtraDifficulty()
        (grid[4] as GridImage).isVisible = systemFlag.gamemode!!.hasExtraDifficulty()

        if (systemFlag.difficulty == null) {
            grid.selectFirst()
        } else {
            grid.select(0, systemFlag.difficulty!!.toInt() - 1)
        }
    }

    override fun fadeOut(newScreen: KotoScreen?, duration: Float) {
        super.fadeOut(newScreen, duration)
        grid.clearActions()
        grid.setPosition(0f, 0f)
        grid.addAction(Actions.moveTo(-400f, 0f, duration, Interpolation.sineOut))
    }

    override fun onQuit() {
        super.onQuit()
        koto.setScreen("title", 0.5f)
    }
}