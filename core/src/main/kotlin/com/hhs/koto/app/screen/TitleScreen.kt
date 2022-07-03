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
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.hhs.koto.app.Config
import com.hhs.koto.app.ui.Grid
import com.hhs.koto.app.ui.GridButton
import com.hhs.koto.app.ui.register
import com.hhs.koto.stg.GameMode
import com.hhs.koto.util.*
import ktx.actors.plusAssign

class TitleScreen : BasicScreen(Config.uiBgm, getRegion("bg/title.png")) {
    private val grid = Grid().register(st, input)
    private val titles = Group().apply { st += this }
    private val title = Label(
        "Koto Demonstration",
        LabelStyle(getFont(120, bundle["font.title"], Color.BLACK, 5f, Color.WHITE), WHITE_HSV),
    ).apply {
        setPosition(80f, 860f)
        titles += this
    }
    private val subtitle = Label(
        "by Hell Hole Studios 2021-2022",
        LabelStyle(getFont(40, bundle["font.subtitle"], borderColor = null), BLACK_HSV),
    ).apply {
        setPosition(100f, 820f)
        titles += this
    }
    private val versionLabel = Label("ver.alpha 26", getUILabelStyle(28)).apply {
        setPosition(10f, 10f)
        st += this
    }

    init {
        grid.add(GridButton(bundle["ui.title.startStory"], 36, 0, -8) {
            SystemFlag.gamemode = GameMode.REGULAR
            app.setScreen("difficultySelect", 0.5f)
        })
        grid.add(GridButton(bundle["ui.title.startExtra"], 36, 0, -7) {
            SystemFlag.gamemode = GameMode.EXTRA
            app.setScreen("difficultySelect", 0.5f)
        })
        grid.add(GridButton(bundle["ui.title.startStagePractice"], 36, 0, -6) {
            SystemFlag.gamemode = GameMode.STAGE_PRACTICE
            app.setScreen("difficultySelect", 0.5f)
        })
        grid.add(GridButton(bundle["ui.title.startSpellPractice"], 36, 0, -5) {
            SystemFlag.gamemode = GameMode.SPELL_PRACTICE
            app.setScreen("difficultySelect", 0.5f)
        })
        grid.add(GridButton(bundle["ui.title.replay"], 36, 0, -4) {
            app.setScreen("replay", 0.5f)
        })
        grid.add(GridButton(bundle["ui.title.playerData"], 36, 0, -3) {
            app.setScreen("playerData", 0.5f)
        })
        grid.add(GridButton(bundle["ui.title.musicRoom"], 36, 0, -2) {
            app.setScreen("musicRoom", 0.5f)
        })
        grid.add(GridButton(bundle["ui.title.options"], 36, 0, -1) {
            app.setScreen("options", 0.5f)
        })
        grid.add(GridButton(bundle["ui.title.quit"], 38, 0, 0, triggerSound = null) {
            onQuit()
        })
        grid.arrange(1050f, 100f, 0f, -45f)
        grid.selectFirst()
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        grid.clearActions()
        grid.setPosition(grid.staticX + 400f, grid.staticY)
        grid.addAction(moveTo(grid.staticX, grid.staticY, duration, Interpolation.pow5Out))
        titles.clearActions()
        titles.setPosition(0f, 400f)
        titles.addAction(moveTo(0f, 0f, duration, Interpolation.pow5Out))
        versionLabel.setPosition(10f, -60f)
        versionLabel.clearActions()
        versionLabel.addAction(moveTo(10f, 10f, duration, Interpolation.pow5Out))

        var extraEnabled = false
        gameData.data.safeValues().forEach {
            extraEnabled = extraEnabled || it.extraUnlocked
        }
        grid[1].enabled = extraEnabled
        grid[1].update()
        grid[3].enabled = gameData.spellPracticeUnlocked
        grid[3].update()
    }

    override fun fadeOut(newScreen: KotoScreen?, duration: Float) {
        super.fadeOut(newScreen, duration)
        grid.clearActions()
        grid.addAction(moveTo(grid.staticX + 400f, grid.staticY, duration, Interpolation.pow5Out))
        titles.clearActions()
        titles.addAction(moveTo(0f, 400f, duration, Interpolation.pow5Out))
        versionLabel.clearActions()
        versionLabel.addAction(moveTo(10f, -60f, duration, Interpolation.pow5Out))
    }

    override fun onQuit() {
        if (grid.selectedY == 0) {
            super.onQuit()
            app.setScreen("", 1f)
        } else {
            grid.select(0, 0)
        }
    }
}