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

import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controller
import ktx.collections.GdxArray

enum class VK {
    DOWN {
        override val uiKeycode: Int = Input.Keys.DOWN
        override val canRepeat: Boolean = true
        override val keycodes
            get() = options.keyDown

        override fun checkController(controller: Controller): Boolean {
            if (controller.getButton(controller.mapping.buttonDpadDown)) return true
            if (checkStick(controller, 202.5f, 337.5f)) return true
            return false
        }
    },
    UP {
        override val uiKeycode: Int = Input.Keys.UP
        override val canRepeat: Boolean = true
        override val keycodes
            get() = options.keyUp

        override fun checkController(controller: Controller): Boolean {
            if (controller.getButton(controller.mapping.buttonDpadUp)) return true
            if (checkStick(controller, 22.5f, 157.5f)) return true
            return false
        }
    },
    LEFT {
        override val uiKeycode: Int = Input.Keys.LEFT
        override val canRepeat: Boolean = true
        override val keycodes
            get() = options.keyLeft

        override fun checkController(controller: Controller): Boolean {
            if (controller.getButton(controller.mapping.buttonDpadLeft)) return true
            if (checkStick(controller, 112.5f, 247.5f)) return true
            return false
        }
    },
    RIGHT {
        override val uiKeycode: Int = Input.Keys.RIGHT
        override val canRepeat: Boolean = true
        override val keycodes
            get() = options.keyRight

        override fun checkController(controller: Controller): Boolean {
            if (controller.getButton(controller.mapping.buttonDpadRight)) return true
            if (checkStick(controller, 292.5f, 67.5f)) return true
            return false
        }
    },
    SELECT {
        override val uiKeycode: Int = Input.Keys.Z
        override val keycodes
            get() = options.keySelect

        override fun checkController(controller: Controller): Boolean {
            if (controller.getButton(controller.mapping.buttonA)) return true
            return false
        }
    },
    CANCEL {
        override val uiKeycode: Int = Input.Keys.X
        override val keycodes
            get() = options.keyCancel

        override fun checkController(controller: Controller): Boolean {
            if (controller.getButton(controller.mapping.buttonB)) return true
            return false
        }
    },
    SHOT {
        override val uiKeycode: Int = Input.Keys.Z
        override val keycodes
            get() = options.keyShot

        override fun checkController(controller: Controller): Boolean {
            if (controller.getButton(controller.mapping.buttonA)) return true
            return false
        }
    },
    SLOW {
        override val uiKeycode: Int = Input.Keys.SHIFT_LEFT
        override val keycodes
            get() = options.keySlow

        override fun checkController(controller: Controller): Boolean {
            if (controller.getButton(controller.mapping.buttonR1)) return true
            if (controller.getButton(controller.mapping.buttonR2)) return true
            return false
        }
    },
    BOMB {
        override val uiKeycode: Int = Input.Keys.X
        override val keycodes
            get() = options.keyBomb

        override fun checkController(controller: Controller): Boolean {
            if (controller.getButton(controller.mapping.buttonB)) return true
            return false
        }
    },
    PAUSE {
        override val uiKeycode: Int = Input.Keys.ESCAPE
        override val keycodes
            get() = options.keyPause

        override fun checkController(controller: Controller): Boolean {
            if (controller.getButton(controller.mapping.buttonStart)) return true
            return false
        }
    },
    CUSTOM {
        override val uiKeycode: Int = Input.Keys.C
        override val keycodes
            get() = options.keyCustom

        override fun checkController(controller: Controller): Boolean {
            if (controller.getButton(controller.mapping.buttonX)) return true
            return false
        }
    },
    CUSTOM2 {
        override val uiKeycode: Int = Input.Keys.D
        override val keycodes
            get() = options.keyCustom2

        override fun checkController(controller: Controller): Boolean {
            if (controller.getButton(controller.mapping.buttonY)) return true
            return false
        }
    },
    RESTART {
        override val uiKeycode: Int = Input.Keys.R
        override val keycodes
            get() = options.keyRestart

        override fun checkController(controller: Controller): Boolean {
            if (controller.getButton(controller.mapping.buttonL1)) return true
            if (controller.getButton(controller.mapping.buttonL2)) return true
            return false
        }
    },
    SPEED_UP {
        override val uiKeycode: Int = Input.Keys.CONTROL_RIGHT
        override val keycodes
            get() = options.keySpeedUp

        override fun checkController(controller: Controller): Boolean {
            return false
        }
    };

    abstract val keycodes: GdxArray<Int>
    abstract fun checkController(controller: Controller): Boolean
    open val uiKeycode: Int = 0
    open val canRepeat: Boolean = false

    fun pressed(): Boolean = app.input.pressed(this)
    fun justPressed(): Boolean = app.input.justPressed(this)

    companion object {
        private fun checkStick(controller: Controller, minAngle: Float, maxAngle: Float): Boolean {
            val x = controller.getAxis(controller.mapping.axisLeftX)
            val y = -controller.getAxis(controller.mapping.axisLeftY)
            if (angleInRange(atan2(y, x), minAngle, maxAngle) && len(x, y) > options.deadzone) return true
            return false
        }
    }
}