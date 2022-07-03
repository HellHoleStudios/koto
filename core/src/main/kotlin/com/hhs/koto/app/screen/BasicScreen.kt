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

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.hhs.koto.app.Config
import com.hhs.koto.util.*
import ktx.actors.plusAssign


open class BasicScreen(
    private val bgmId: Int? = -1,
    backgroundTexture: TextureRegion? = null,
) : KotoScreen {
    val blocker = InputBlocker()
    lateinit var background: Image
    val st = Stage(app.viewport, app.batch).apply {
        isDebugAll = Config.debugActorLayout
    }
    val input = InputMultiplexer().apply {
        addProcessor(blocker)
        addProcessor(st)
        addProcessor(KeyListener(VK.CANCEL) { onQuit() })
    }
    override var state: ScreenState = ScreenState.HIDDEN

    init {
        if (backgroundTexture != null) {
            background = Image(backgroundTexture)
            background.zIndex = 0
            background.setBounds(0f, 0f, Config.screenWidth, Config.screenHeight)
            st += background
        }
    }

    override fun render(delta: Float) {
        st.act(delta)
        st.draw()
    }

    override fun show() {
        if (bgmId == null || bgmId != -1) {
            BGM.play(bgmId)
        }
    }

    override fun hide() {
        state = ScreenState.HIDDEN
    }

    override fun fadeOut(newScreen: KotoScreen?, duration: Float) {
        app.input.removeProcessor(input)
        blocker.isBlocking = true
        state = ScreenState.FADING_OUT
        st.root.clearActions()
        st.root.addAction(sequence(fadeOut(duration, Interpolation.pow3Out), Actions.run { hide() }))
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        app.input.addProcessor(input)
        blocker.isBlocking = false
        state = ScreenState.FADING_IN
        show()
        st.root.clearActions()
        st.root.color.a = 1f
        st.root.addAction(sequence(delay(duration), Actions.run {
            state = ScreenState.SHOWN
        }))
    }

    override fun dispose() {
        st.dispose()
    }

    open fun onQuit() {
        SE.play("cancel")
    }

}