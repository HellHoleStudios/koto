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
import com.hhs.koto.util.ResolutionMode
import ktx.collections.GdxArray
import java.util.*

data class Options(
    var fps: Int = 60,
    var fpsMultiplier: Int = 1,
    var vsyncEnabled: Boolean = false,
    var windowWidth: Int = 960,
    var windowHeight: Int = 720,
    var frameWidth: Int = 576,
    var frameHeight: Int = 672,
    var fullScreen: Boolean = false,
    var speedUpMultiplier: Int = 4,
    var musicVolume: Float = 1f,
    var SEVolume: Float = 0.5f,
    val deadzone: Float = 0.1f,
    val keyRepeatDelay: Float = 0.4f,
    val keyRepeatInterval: Float = 0.12f,
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
    var keyCustom2: GdxArray<Int> = GdxArray.with(Keys.D),
    var keyRestart: GdxArray<Int> = GdxArray.with(Keys.R),
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
    val resolutionModes: GdxArray<ResolutionMode> = GdxArray.with(
        ResolutionMode(640),
        ResolutionMode(800),
        ResolutionMode(960),
        ResolutionMode(1200),
        ResolutionMode(1280),
        ResolutionMode(1440),
    )
    val textureMinFilter: TextureFilter = TextureFilter.MipMapLinearLinear
    val textureMagFilter: TextureFilter = TextureFilter.Linear
    const val genMipMaps: Boolean = true
    const val windowTitle: String = "Koto"
    const val replayPrefix: String = "koto"
    const val screenWidth: Float = 1440f
    const val screenHeight: Float = 1080f
    const val allowFullScreen: Boolean = true
    const val allowResize: Boolean = true
    val windowScaling: Scaling = Scaling.fit
    const val defaultBlending: String = "ALPHA"
    const val worldW: Float = 384f
    const val worldH: Float = 448f
    const val worldOriginX: Float = worldW / 2f
    const val worldOriginY: Float = worldH / 2f
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
    val UIFontShadowOffsetXFunction: (Int) -> Int = { fontSize: Int ->
        fontSize / 10
    }
    val UIFontShadowOffsetYFunction: (Int) -> Int = { fontSize: Int ->
        fontSize / 10
    }
    val UIFontShadowColor: Color? = null
    const val debugActorLayout: Boolean = false
}