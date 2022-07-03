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

package com.hhs.koto.app

import com.badlogic.gdx.Input
import com.hhs.koto.util.matchLocale
import ktx.collections.GdxArray
import java.util.*

data class Options(
    var fps: Int = 60,
    var fpsMultiplier: Int = 1,
    var vsyncEnabled: Boolean = false,
    var windowWidth: Int = 960,
    var windowHeight: Int = 720,
    var frameBufferWidth: Int = 576,
    var frameBufferHeight: Int = 672,
    var displayMode: String = "windowed",
    var speedUpMultiplier: Int = 4,
    var musicVolume: Float = 1f,
    var SEVolume: Float = 0.5f,
    var deadzone: Float = 0.1f,
    var keyRepeatDelay: Float = 0.4f,
    var keyRepeatInterval: Float = 0.12f,
    var keyDown: GdxArray<Int> = GdxArray.with(Input.Keys.DOWN),
    var keyUp: GdxArray<Int> = GdxArray.with(Input.Keys.UP),
    var keyLeft: GdxArray<Int> = GdxArray.with(Input.Keys.LEFT),
    var keyRight: GdxArray<Int> = GdxArray.with(Input.Keys.RIGHT),
    var keySelect: GdxArray<Int> = GdxArray.with(Input.Keys.Z, Input.Keys.ENTER),
    var keyCancel: GdxArray<Int> = GdxArray.with(Input.Keys.X, Input.Keys.ESCAPE),
    var keyShot: GdxArray<Int> = GdxArray.with(Input.Keys.Z),
    var keySlow: GdxArray<Int> = GdxArray.with(Input.Keys.SHIFT_LEFT),
    var keyBomb: GdxArray<Int> = GdxArray.with(Input.Keys.X),
    var keyPause: GdxArray<Int> = GdxArray.with(Input.Keys.ESCAPE),
    var keyCustom: GdxArray<Int> = GdxArray.with(Input.Keys.C),
    var keyCustom2: GdxArray<Int> = GdxArray.with(Input.Keys.D),
    var keyRestart: GdxArray<Int> = GdxArray.with(Input.Keys.R),
    var keySpeedUp: GdxArray<Int> = GdxArray.with(Input.Keys.CONTROL_LEFT),
    var locales: GdxArray<Locale> = GdxArray.with(
        Locale.ROOT,
        Locale.ENGLISH,
        Locale.JAPANESE,
        Locale.SIMPLIFIED_CHINESE,
        Locale.TRADITIONAL_CHINESE,
    ),
    var locale: Locale = matchLocale(locales, Locale.getDefault())
)