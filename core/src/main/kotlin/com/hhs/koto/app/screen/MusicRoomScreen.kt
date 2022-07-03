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

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.Config
import com.hhs.koto.app.ui.ConstrainedGrid
import com.hhs.koto.app.ui.GridButton
import com.hhs.koto.app.ui.register
import com.hhs.koto.util.*
import ktx.actors.plusAssign

class MusicRoomScreen : BasicScreen(-1, getRegion("bg/music_room.png")) {
    private val title = Label(
        bundle["ui.music.title"],
        Label.LabelStyle(getFont(72, bundle["font.title"]), WHITE_HSV),
    ).apply {
        setPosition(80f, 1100f)
        st += this
    }
    private val titles = ConstrainedGrid(
        120f,
        400f,
        1200f,
        450f,
        animationDuration = 0.5f,
        interpolation = Interpolation.pow5Out,
    ).setCullingToConstraint().register(st, input)
    private val comment = Label("", getUILabelStyle(36)).apply {
        setAlignment(Align.topLeft)
        wrap = true
        setText(bundle["music.0.comment"])
        setBounds(120f, 100f, 1200f, 300f)
        st += this
    }
    private var warning: Int = -1

    init {
        for (i in 0 until Config.musicCount) {
            titles.add(GridButton("#${i + 1} " + bundle["music.$i.title"], 36, 0, i) {
                if (gameData.musicUnlocked[i] || warning == i) {
                    BGM.stop()
                    BGM.play(i)
                    comment.style.fontColor = WHITE_HSV
                    comment.setText(bundle["music.$i.comment"])
                } else {
                    warning = i
                    comment.style.fontColor = RED_HSV
                    comment.setText(bundle["ui.music.warning"])
                }
            })
        }
        titles.arrange(0f, 1000f, 0f, -45f)
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        warning = -1
        titles.selectFirst()
        titles.finishAnimation()
        comment.style.fontColor = WHITE_HSV
        comment.setText(bundle["music.0.comment"])
        title.addAction(Actions.moveTo(80f, 900f, 0.5f, Interpolation.pow5Out))
    }

    override fun fadeOut(newScreen: KotoScreen?, duration: Float) {
        super.fadeOut(newScreen, duration)
        title.addAction(Actions.moveTo(80f, 1100f, 0.5f, Interpolation.pow5Out))
    }

    override fun onQuit() {
        super.onQuit()
        app.setScreen("title", 1f)
    }
}