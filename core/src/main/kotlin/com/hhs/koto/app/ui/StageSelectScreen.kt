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

package com.hhs.koto.app.ui

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.hhs.koto.app.screen.BasicScreen
import com.hhs.koto.app.screen.KotoScreen
import com.hhs.koto.stg.GameBuilder
import com.hhs.koto.util.*
import ktx.actors.plusAssign

class StageSelectScreen : BasicScreen("mus/E0120.ogg", getRegion("bg/generic.png")) {
    private val grid = ConstrainedGrid(
        120f,
        200f,
        1200f,
        630f,
        animationDuration = 0.5f,
        interpolation = Interpolation.pow5Out,
    ).setCullingToConstraint().register(st, input)
    private val title = Label(bundle["ui.stageSelect.title"], getUILabelStyle(72)).apply {
        setPosition(80f, 900f)
        st += this
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        val stages = GameBuilder.getAvailableStages()
        grid.clear()
        for (i in 0 until stages.size) {
            grid.add(GridButton(bundle["game.stage.${stages[i].name}.name"], 36, 0, i) {
                SystemFlag.name = stages[i].name
                SystemFlag.redirect = "game"
                SystemFlag.redirectDuration = 0.5f
                app.setScreen("blank", 0.5f)
            })
        }
        grid.arrange(0f, 1000f, 0f, -45f)
        grid.selectFirst()
        grid.finishAnimation()
    }

    override fun onQuit() {
        super.onQuit()
        app.setScreen("playerSelect", 0.5f)
    }
}