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

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.Scaling
import com.hhs.koto.util.ResolutionMode
import ktx.collections.GdxArray

object Config {
    var logLevel: Int = Logger.DEBUG
    val resolutionModes: GdxArray<ResolutionMode> = GdxArray.with(
        ResolutionMode(480),
        ResolutionMode(600),
        ResolutionMode(720),
        ResolutionMode(900),
        ResolutionMode(960),
        ResolutionMode(1080),
    )
    val textureMinFilter: TextureFilter = TextureFilter.MipMapLinearLinear
    val textureMagFilter: TextureFilter = TextureFilter.Linear
    const val genMipMaps: Boolean = true
    const val windowTitle: String = "Koto"
    const val replayPrefix: String = "koto"
    const val musicCount: Int = 4
    const val uiBgm: Int = 0
    const val uiBackground: String = "bg/generic.png"
    const val screenWidth: Float = 1440f
    const val screenHeight: Float = 1080f
    const val frameWidth: Float = 864f
    const val frameHeight: Float = 1008f
    const val allowResize: Boolean = true
    val windowScaling: Scaling = Scaling.fit
    const val defaultBlending: String = "ALPHA"
    const val worldW: Float = 384f
    const val worldH: Float = 448f
    const val worldOriginX: Float = worldW / 2
    const val worldOriginY: Float = worldH / 2
    const val bulletDeleteDistance: Float = 1024f
    const val orthoCircleCollision: Boolean = true
    const val cleanupBlankCount: Int = 512
    const val defaultShotSheet: String = "danmakufu_shot.shot"
    const val allowSpeedUpOutOfReplay: Boolean = true
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
    val UIFontBorderColor: Color? = Color.BLACK
    val UIFontShadowOffsetXFunction: (Int) -> Int = { fontSize: Int ->
        fontSize / 10
    }
    val UIFontShadowOffsetYFunction: (Int) -> Int = { fontSize: Int ->
        fontSize / 10
    }
    val UIFontShadowColor: Color? = null
    const val debugActorLayout: Boolean = false
}