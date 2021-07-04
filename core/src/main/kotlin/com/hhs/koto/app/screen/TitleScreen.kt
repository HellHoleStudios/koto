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
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.Config
import com.hhs.koto.app.ui.Grid
import com.hhs.koto.app.ui.GridButton
import com.hhs.koto.stg.GameMode
import com.hhs.koto.util.SystemFlag
import com.hhs.koto.util.getFont
import com.hhs.koto.util.getRegion
import com.hhs.koto.util.koto

class TitleScreen : BasicScreen("mus/E.0120.ogg", getRegion("bg/title.png")) {
    private val grid: Grid = Grid()
    private val title: Label = Label(
        "Koto Demonstration",
        LabelStyle(getFont("font/SourceHanSerifSC-Bold.otf", 120, Color.BLACK, 5f, Color.WHITE), Color.WHITE),
    )
    private val subtitle = Label(
        "by Hell Hole Studios 2021",
        LabelStyle(getFont("font/SourceSerifPro-Italic.ttf", 36), Color.BLACK),
    )
    private val titles: Group = Group()

    init {
        title.setPosition(80f, 860f)
        subtitle.setPosition(100f, 820f)
        titles.addActor(title)
        titles.addActor(subtitle)
        st.addActor(titles)
        st.addActor(grid)

        grid.add(GridButton("Game Start", 36, 0, 0) {
            SystemFlag.gamemode = GameMode.STORY
            koto.setScreen("difficultySelect", 0.5f)
        })
        grid.add(GridButton("Extra Start", 36, 0, 1) {
            SystemFlag.gamemode = GameMode.EXTRA
            koto.setScreen("difficultySelect", 0.5f)
        })
        grid.add(GridButton("Stage Practice", 36, 0, 2) {
            SystemFlag.gamemode = GameMode.STAGE_PRACTICE
            koto.setScreen("difficultySelect", 0.5f)
        })
        grid.add(GridButton("Spell Practice", 36, 0, 3) {
            SystemFlag.gamemode = GameMode.SPELL_PRACTICE
            koto.setScreen("difficultySelect", 0.5f)
        })
        grid.add(GridButton("Replay", 36, 0, 4)).disable()
        grid.add(GridButton("Player Data", 36, 0, 5)).disable()
        grid.add(GridButton("Music Room", 36, 0, 6) {
            koto.setScreen("musicRoom", 0.5f)
        })
        grid.add(GridButton("Options", 36, 0, 7) {
            koto.setScreen("options", 0.5f)
        })
        grid.add(GridButton("Quit", 36, 0, 8, triggerSound = null) {
            onQuit()
        })
        grid.arrange(1050f, 450f, 0f, -45f)
        grid.selectFirst()
        grid.updateComponent()
        input.addProcessor(grid)
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        grid.clearActions()
        grid.setPosition(400f, 0f)
        grid.addAction(Actions.moveTo(0f, 0f, duration, Interpolation.pow5Out))
        titles.clearActions()
        titles.setPosition(0f, 400f)
        titles.addAction(Actions.moveTo(0f, 0f, duration, Interpolation.pow5Out))
    }

    override fun fadeOut(newScreen: KotoScreen?, duration: Float) {
        super.fadeOut(newScreen, duration)
        grid.clearActions()
        grid.addAction(Actions.moveTo(400f, 0f, duration, Interpolation.sineOut))
        titles.clearActions()
        titles.addAction(Actions.moveTo(0f, 400f, duration, Interpolation.sineOut))
    }

    override fun onQuit() {
        if (grid.selectedY == 8) {
            super.onQuit()
            koto.setScreen("", 1f)
        } else {
            grid.select(0, 8)
        }
    }
}