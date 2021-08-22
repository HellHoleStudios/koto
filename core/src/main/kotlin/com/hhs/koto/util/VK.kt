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

package com.hhs.koto.util

import com.badlogic.gdx.Input
import ktx.collections.GdxArray

enum class VK {
    DOWN {
        override val uiKeycode: Int = Input.Keys.DOWN
        override val keycodes
            get() = options.keyDown
    },
    UP {
        override val uiKeycode: Int = Input.Keys.UP
        override val keycodes
            get() = options.keyUp
    },
    LEFT {
        override val uiKeycode: Int = Input.Keys.LEFT
        override val keycodes
            get() = options.keyLeft
    },
    RIGHT {
        override val uiKeycode: Int = Input.Keys.RIGHT
        override val keycodes
            get() = options.keyRight
    },
    SELECT {
        override val uiKeycode: Int = Input.Keys.Z
        override val keycodes
            get() = options.keySelect
    },
    CANCEL {
        override val uiKeycode: Int = Input.Keys.X
        override val keycodes
            get() = options.keyCancel
    },
    SHOT {
        override val uiKeycode: Int = Input.Keys.Z
        override val keycodes
            get() = options.keyShot
    },
    SLOW {
        override val uiKeycode: Int = Input.Keys.SHIFT_LEFT
        override val keycodes
            get() = options.keySlow
    },
    BOMB {
        override val uiKeycode: Int = Input.Keys.X
        override val keycodes
            get() = options.keyBomb
    },
    PAUSE {
        override val uiKeycode: Int = Input.Keys.ESCAPE
        override val keycodes
            get() = options.keyPause
    },
    CUSTOM {
        override val uiKeycode: Int = Input.Keys.C
        override val keycodes
            get() = options.keyCustom
    },
    RESTART {
        override val uiKeycode: Int = Input.Keys.R
        override val keycodes
            get() = options.keyRestart
    },
    FULL_SCREEN {
        override val uiKeycode: Int = Input.Keys.F4
        override val keycodes
            get() = options.keyFullScreen
    },
    SPEED_UP {
        override val uiKeycode: Int = Input.Keys.CONTROL_RIGHT
        override val keycodes
            get() = options.keySpeedUp
    };

    companion object {
        val list: GdxArray<VK> = GdxArray.with(
            DOWN, UP, LEFT, RIGHT, SELECT, CANCEL, SHOT, SLOW, BOMB, PAUSE, CUSTOM, RESTART, FULL_SCREEN, SPEED_UP
        )
    }

    abstract val keycodes: GdxArray<Int>
    open val uiKeycode: Int = 0

    fun pressed(): Boolean = keyPressed(keycodes)
    fun justPressed(): Boolean = keyJustPressed(keycodes)
}