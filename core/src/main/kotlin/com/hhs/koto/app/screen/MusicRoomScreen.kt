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
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.Config
import com.hhs.koto.app.ui.ConstrainedGrid
import com.hhs.koto.app.ui.GridButton
import com.hhs.koto.app.ui.register
import com.hhs.koto.util.*
import ktx.actors.plusAssign
import ktx.actors.txt

class MusicRoomScreen : BasicScreen("", getRegion("bg/music_room.png")) {
    private val titles = ConstrainedGrid(
        120f,
        400f,
        1200f,
        600f,
        animationDuration = 0.5f,
        interpolation = Interpolation.pow5Out,
    ).register(st, input).apply {
        cullingArea = Rectangle(120f, 450f, 1200f, 600f)
    }
    private val comment = Label("", getUILabelStyle(36)).apply {
        setAlignment(Align.topLeft)
        wrap = true
        txt = bundle["music.1.comment"]
        setBounds(120f, 100f, 1200f, 300f)
        st += this
    }

    init {
        for (i in 1..Config.musicCount) {
            titles.add(GridButton(bundle["music.$i.title"], 36, 0, i) {
                BGM.stop()
                BGM.play(bundle["music.$i.file"])
                comment.txt = bundle["music.$i.comment"]
            })
        }
        titles.arrange(0f, 1000f, 0f, -45f)
        titles.selectFirst()
        titles.finishAnimation()
    }

    override fun onQuit() {
        app.setScreen("title", 1f)
    }
}