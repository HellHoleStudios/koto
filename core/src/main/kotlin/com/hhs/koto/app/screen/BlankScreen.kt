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
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.delay
import com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence
import com.hhs.koto.util.SystemFlag
import com.hhs.koto.util.app
import com.hhs.koto.util.getRegion

class BlankScreen : BasicScreen(null, getRegion("ui/blank.png")) {

    init {
        background.color = Color.BLACK
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
            if (SystemFlag.redirect != null) {
                if (SystemFlag.redirectDuration != null) {
                    app.setScreen(SystemFlag.redirect!!, SystemFlag.redirectDuration!!)
                    SystemFlag.redirectDuration = null
                } else {
                    app.setScreen(SystemFlag.redirect!!)
                }
                SystemFlag.redirect = null
            }
        }))
    }

    override fun onQuit() = Unit
}
