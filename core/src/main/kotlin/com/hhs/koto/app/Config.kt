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

package com.hhs.koto.app

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.Scaling
import ktx.collections.GdxArray
import java.util.*

data class Options(
    var fps: Int = 60,
    var fpsMultiplier: Int = 1,
    var vsyncEnabled: Boolean = false,
    var startupWindowWidth: Int = 960,
    var startupWindowHeight: Int = 720,
    var startupFullScreen: Boolean = false,
    var speedUpMultiplier: Int = 4,
    var musicVolume: Float = 1f,
    var SEVolume: Float = 0.5f,
    var keyDown: GdxArray<Int> = GdxArray.with(Keys.DOWN),
    var keyUp: GdxArray<Int> = GdxArray.with(Keys.UP),
    var keyLeft: GdxArray<Int> = GdxArray.with(Keys.LEFT),
    var keyRight: GdxArray<Int> = GdxArray.with(Keys.RIGHT),
    var keySelect: GdxArray<Int> = GdxArray.with(Keys.Z, Keys.ENTER),
    var keyCancel: GdxArray<Int> = GdxArray.with(Keys.X, Keys.ESCAPE),
    var keyShot: GdxArray<Int> = GdxArray.with(Keys.Z),
    var keySlow: GdxArray<Int> = GdxArray.with(Keys.SHIFT_LEFT),
    var keyBomb: GdxArray<Int> = GdxArray.with(Keys.X),
    var keyPause: GdxArray<Int> = GdxArray.with(Keys.ESCAPE),
    var keyCustom: GdxArray<Int> = GdxArray.with(Keys.C),
    var keyFullScreen: GdxArray<Int> = GdxArray.with(Keys.F4),
    var keySpeedUp: GdxArray<Int> = GdxArray.with(Keys.CONTROL_LEFT),
    var locale: Locale = Locale.getDefault(),
    var locales: GdxArray<Locale> = GdxArray.with(
        Locale.ROOT,
        Locale.ENGLISH,
        Locale.JAPANESE,
        Locale.SIMPLIFIED_CHINESE,
        Locale.TRADITIONAL_CHINESE,
    ),
)

object Config {
    var logLevel: Int = Logger.DEBUG
    const val useHSVShader: Boolean = true
    val textureMinFilter: TextureFilter = TextureFilter.MipMapLinearLinear
    val textureMagFilter: TextureFilter = TextureFilter.Linear
    const val genMipMaps: Boolean = true
    const val windowTitle: String = "Koto"
    const val screenWidth: Float = 1440f
    const val screenHeight: Float = 1080f
    const val allowFullScreen: Boolean = true
    const val allowResize: Boolean = true
    val windowScaling: Scaling = Scaling.fit
    const val defaultBlending: String = "ALPHA"
    const val fw: Int = 864
    const val fh: Int = 1008
    const val w: Float = 384f
    const val h: Float = 448f
    const val originX: Float = w / 2
    const val originY: Float = h / 2
    const val bulletDeleteDistance: Float = 1024f
    const val safeDistance: Float = 16f
    const val orthoCircleCollision: Boolean = true
    const val cleanupBlankCount: Int = 512
    const val defaultShotSheet: String = "danmakufu_shot.shot"
    const val allowSpeedUpOutOfReplay: Boolean = true
    const val musicCount: Int = 1
    const val noDuplicateSE: Boolean = true
    lateinit var UIFont: String
    lateinit var UIFontSmall: String
    val UIFontColor: Color = Color.WHITE
    val UIFontBorderWidthFunction: (Int) -> Float = { fontSize: Int ->
        if (fontSize <= 48) {
            fontSize * 0.1f
        } else {
            (fontSize - 48) * 0.02f + 3.6f
        }
    }
    val UIFontBorderColor: Color = Color.BLACK
    const val debugActorLayout: Boolean = false
}