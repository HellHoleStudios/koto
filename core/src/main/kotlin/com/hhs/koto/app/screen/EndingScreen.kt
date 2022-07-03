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

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.hhs.koto.util.*
import ktx.actors.plusAssign
import ktx.collections.GdxArray

class EndingScreen : BasicScreen(2, getRegion("bg/ending.png")) {

    val image = Image().apply {
        st += this
        setBounds(320f, 350f, 800f, 600f)
    }
    val text = Label("", getUILabelStyle(36)).apply {
        st += this
        wrap = true
        setAlignment(Align.topLeft)
        setBounds(300f, 50f, 840f, 250f)
    }

    val ending = GdxArray<EndingSegment>()
    var currentSegment: Int = 0

    data class EndingSegment(
        val text: String,
        val image: TextureRegion? = null,
    )

    override fun render(delta: Float) {
        if (state == ScreenState.SHOWN) {
            if (app.input.justPressed(VK.SELECT)) {
                SE.play("select")
                currentSegment++
                if (currentSegment >= ending.size) {
                    SystemFlag.redirect = "credit"
                    SystemFlag.redirectDuration = 1f
                    app.setScreen("blank", 2f)
                } else {
                    if (ending[currentSegment].image != null) {
                        image.drawable = TextureRegionDrawable(ending[currentSegment].image)
                    }
                    text.setText(ending[currentSegment].text)
                }
            }
        }
        super.render(delta)
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        ending.clear()
        currentSegment = 0
        when (SystemFlag.ending!!) {
            "ending1" -> {
                ending.add(EndingSegment(bundle["ui.ending.ending1.text1"], getRegion("ending/rick.png")))
                ending.add(EndingSegment(bundle["ui.ending.ending1.text2"]))
            }
            "ending2" -> {
                ending.add(EndingSegment(bundle["ui.ending.ending2.text1"], getRegion("ending/rick.png")))
                ending.add(EndingSegment(bundle["ui.ending.ending2.text2"]))
            }
        }
        image.drawable = TextureRegionDrawable(ending[currentSegment].image!!)
        text.setText(ending[currentSegment].text)
    }

    override fun onQuit() {
        app.setScreen("game", 1f)
    }

}