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
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.controllers.Controllers

class EmulatedInput : InputMultiplexer() {
    private val pressedTime = FloatArray(VK.values().size)
    private val justPressed = BooleanArray(VK.values().size)
    private val repeatCounter = FloatArray(VK.values().size)
    private val firedDown = BooleanArray(Input.Keys.MAX_KEYCODE + 1)
    private val firedUp = BooleanArray(Input.Keys.MAX_KEYCODE + 1)

    fun fireDown(vk: VK) {
        if (!firedDown[vk.uiKeycode]) {
            super.keyDown(vk.uiKeycode)
            firedDown[vk.uiKeycode] = true
        }
    }

    fun fireUp(vk: VK) {
        if (!firedUp[vk.uiKeycode]) {
            super.keyUp(vk.uiKeycode)
            firedUp[vk.uiKeycode] = true
        }
    }

    fun update(delta: Float) {
        firedDown.fill(false)
        firedUp.fill(false)
        justPressed.fill(false)
        VK.values().forEach { vk ->
            var flag = false
            vk.keycodes.forEach { keycode ->
                if (Gdx.input.isKeyPressed(keycode)) {
                    flag = true
                }
            }
            Controllers.getControllers().forEach { controller ->
                if (vk.checkController(controller)) {
                    flag = true
                }
            }
            if (flag) {
                if (pressedTime[vk.ordinal] == 0f) {
                    justPressed[vk.ordinal] = true
                    fireDown(vk)
                }
                pressedTime[vk.ordinal] += delta
                if (vk.canRepeat && pressedTime[vk.ordinal] >= options.keyRepeatDelay) {
                    repeatCounter[vk.ordinal] += delta
                    if (repeatCounter[vk.ordinal] >= options.keyRepeatInterval) {
                        fireDown(vk)
                        repeatCounter[vk.ordinal] = 0f
                    }
                }
            } else {
                if (pressedTime[vk.ordinal] != 0f) {
                    fireUp(vk)
                }
                pressedTime[vk.ordinal] = 0f
                repeatCounter[vk.ordinal] = 0f
            }
        }
    }

    fun pressed(vk: VK): Boolean {
        return pressedTime[vk.ordinal] != 0f
    }

    fun justPressed(vk: VK): Boolean {
        return justPressed[vk.ordinal]
    }

    override fun keyDown(keycode: Int): Boolean = false

    override fun keyUp(keycode: Int): Boolean = false

}