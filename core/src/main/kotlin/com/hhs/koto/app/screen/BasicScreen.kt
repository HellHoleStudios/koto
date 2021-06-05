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
import com.badlogic.gdx.scenes.scene2d.Stage
import com.hhs.koto.app.KotoApp
import com.hhs.koto.util.KeyListener
import com.hhs.koto.util.config
import com.hhs.koto.util.safeDeltaTime
import ktx.app.KtxScreen

open class BasicScreen(private val game: KotoApp) : KtxScreen {
    private val st = Stage(game.viewport)
    private val input = InputMultiplexer()

    init {
        st.isDebugAll = config.debugActorLayout

        input.addProcessor(st)
        input.addProcessor(KeyListener(config.keyCancel) { onQuit() })

        game.input.addProcessor(input)
    }

    override fun render(delta: Float) {
        game.batch.begin()
        st.act(safeDeltaTime())
        st.draw()
        game.batch.end()
    }

    override fun show() {

    }

    override fun hide() {
        game.input.removeProcessor(input)
    }

    open fun onQuit() {

    }
}