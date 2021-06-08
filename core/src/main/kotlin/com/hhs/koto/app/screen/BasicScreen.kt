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

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.hhs.koto.util.BGM
import com.hhs.koto.util.KeyListener
import com.hhs.koto.util.config
import com.hhs.koto.util.koto
import ktx.app.KtxScreen

open class BasicScreen(private val backgroundMusic: String, private val backgroundTexture: TextureRegion) : KtxScreen {
    private val st = Stage(koto.viewport)
    private val input = InputMultiplexer()
    private val background = Image(backgroundTexture)

    init {
        st.isDebugAll = config.debugActorLayout

        background.zIndex = 0
        background.setBounds(0f, 0f, config.screenWidth, config.screenHeight)
        st.addActor(background)

        input.addProcessor(st)
        input.addProcessor(KeyListener(config.keyCancel) { onQuit() })
    }

    override fun render(delta: Float) {
        koto.batch.begin()
        st.act(delta)
        st.draw()
        koto.batch.end()
    }

    override fun show() {
        BGM.play(backgroundMusic)

        koto.input.addProcessor(input)
    }

    override fun hide() {
        koto.input.removeProcessor(input)
    }

    open fun onQuit() {

    }
}