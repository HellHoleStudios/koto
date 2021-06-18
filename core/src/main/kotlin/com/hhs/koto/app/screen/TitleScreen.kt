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
import com.hhs.koto.app.ui.Grid
import com.hhs.koto.app.ui.GridButton
import com.hhs.koto.util.*
import ktx.collections.set

class TitleScreen : BasicScreen("mus/E.0120.ogg", getRegion("bg/title.png"), "title") {
    private var grid: Grid = Grid()
    private var title: Label = Label(
        "Jade Demo Game",
        LabelStyle(getFont("font/LBRITE.ttf", 120, Color.BLACK, 5f, Color.WHITE), Color.WHITE)
    )
    private var subtitle = Label(
        "by Hell Hole Studios 2020",
        LabelStyle(getFont("font/LBRITEI.ttf", 36), Color.BLACK)
    )
    private var titles: Group = Group()

    init {
        title = Label(
            "Jade Demo Game",
            LabelStyle(getFont("font/LBRITE.ttf", 120, Color.BLACK, 5f, Color.WHITE), Color.WHITE)
        )
        title.setPosition(180f, 780f)
        subtitle.setPosition(800f, 720f)
        titles.addActor(title)
        titles.addActor(subtitle)
        st.addActor(titles)
        st.addActor(grid)
        grid.add(GridButton("Game Start", 48, 860f, 580f, 400f, 60f, 0, 0) {
            global["_gameMode"] = "regular"
            koto.setScreen("difficultySelect", 0.5f)
        })
        grid.add(GridButton("Extra Start", 48, 840f, 520f, 400f, 60f, 0, 1) {
            global["_gameMode"] = "extra"
            koto.setScreen("difficultySelect", 0.5f)
        })
        grid.add(GridButton("Stage Practice", 48, 820f, 460f, 400f, 60f, 0, 2) {
            global["_gameMode"] = "stagePractice"
            koto.setScreen("difficultySelect", 0.5f)
        })
        grid.add(GridButton("Spell Practice", 48, 800f, 400f, 400f, 60f, 0, 3) {
            global["_gameMode"] = "spellPractice"
            koto.setScreen("difficultySelect", 0.5f)
        })
        grid.add(GridButton("Replay", 48, 780f, 340f, 400f, 60f, 0, 4)).enabled = false
        grid.add(GridButton("Player Data", 48, 760f, 280f, 400f, 60f, 0, 5)).enabled = false
        grid.add(GridButton("Music Room", 48, 740f, 220f, 400f, 60f, 0, 6) {
            koto.setScreen("musicRoom", 0.5f)
        })
        grid.add(GridButton("Option", 48, 720f, 160f, 400f, 60f, 0, 7) {
            koto.setScreen("option", 0.5f)
        })
        grid.add(GridButton("Quit", 48, 700f, 100f, 400f, 60f, 0, 8) { exitApp() })
        grid.selectFirst()
        grid.updateComponent()
        input.addProcessor(grid)
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        grid.clearActions()
        grid.setPosition(400f, 0f)
        grid.addAction(Actions.moveTo(0f, 0f, duration, Interpolation.sineOut))
        titles.clearActions()
        titles.setPosition(0f, 400f)
        titles.addAction(Actions.moveTo(0f, 0f, duration, Interpolation.sineOut))
    }

    fun onFadeOut(duration: Float) {
        grid.clearActions()
        grid.addAction(Actions.moveTo(400f, 0f, duration, Interpolation.sineOut))
        titles.clearActions()
        titles.addAction(Actions.moveTo(0f, 400f, duration, Interpolation.sineOut))
    }

    override fun onQuit() {
        if (grid.selectedY == 8) {
            super.onQuit()
            exitApp()
        } else {
            grid.select(0, 8)
        }
    }
}