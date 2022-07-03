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

package com.hhs.koto.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputProcessor

class InputBlocker : InputProcessor {

    var isBlocking = false
        set(value) {
            if (value) {
                if (!isBlocking) Gdx.app.log("InputBlocker", "Blocking enabled.")
            } else {
                if (isBlocking) Gdx.app.log("InputBlocker", "Blocking disabled.")
            }
            field = value
        }

    override fun keyDown(keycode: Int) = isBlocking

    override fun keyUp(keycode: Int) = isBlocking

    override fun keyTyped(character: Char) = isBlocking

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = isBlocking

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = isBlocking

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = isBlocking

    override fun mouseMoved(screenX: Int, screenY: Int) = isBlocking

    override fun scrolled(amountX: Float, amountY: Float) = isBlocking
}

class KeyListener(private val vk: VK, private var action: () -> Unit) : InputAdapter() {
    override fun keyDown(keycode: Int): Boolean {
        if (keycode == vk.uiKeycode) {
            action()
        }
        return false
    }
}