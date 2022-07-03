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
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.Graphics.DisplayMode
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils.ceil
import com.badlogic.gdx.utils.Align
import com.crashinvaders.vfx.VfxManager
import com.crashinvaders.vfx.effects.ChainVfxEffect
import com.hhs.koto.app.Config
import com.hhs.koto.app.Options
import ktx.collections.GdxSet
import ktx.graphics.copy
import kotlin.math.roundToInt

data class ResolutionMode(
    val windowWidth: Int,
    val windowHeight: Int,
    val frameBufferWidth: Int,
    val frameBufferHeight: Int,
    val name: String = "${windowWidth}x${windowHeight}",
) {
    constructor(windowHeight: Int) : this(
        (windowHeight / Config.screenHeight * Config.screenWidth).roundToInt(),
        windowHeight,
        (windowHeight / Config.screenHeight * Config.frameWidth).roundToInt(),
        (windowHeight / Config.screenHeight * Config.frameHeight).roundToInt(),
    )

    companion object {
        fun findOptimalIndex(displayMode: DisplayMode): Int {
            var modeIndex = Config.resolutionModes.indexOfLast {
                it.windowWidth <= displayMode.width && it.windowHeight <= displayMode.height - 100
            }
            if (modeIndex == -1) modeIndex = 0
            return modeIndex
        }

        fun findOptimal(displayMode: DisplayMode): ResolutionMode =
            Config.resolutionModes[findOptimalIndex(displayMode)]
    }

    fun saveTo(options: Options) {
        options.windowWidth = windowWidth
        options.windowHeight = windowHeight
        options.frameBufferWidth = frameBufferWidth
        options.frameBufferHeight = frameBufferHeight
    }
}

data class BlendingMode(
    val srcColor: Int,
    val dstColor: Int,
    val srcAlpha: Int,
    val dstAlpha: Int,
    val equationColor: Int,
    val equationAlpha: Int,
) {
    companion object {
        val ALPHA = BlendingMode(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        val ADD = BlendingMode(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        val MULTIPLY = BlendingMode(GL20.GL_ZERO, GL20.GL_SRC_COLOR)
        val SUBTRACT = BlendingMode(GL20.GL_SRC_ALPHA, GL20.GL_ONE, GL20.GL_FUNC_REVERSE_SUBTRACT)

        fun forName(blendingString: String): BlendingMode = when (blendingString) {
            "ALPHA", "DANMAKUFU_ALPHA" -> ALPHA
            "ADD", "DANMAKUFU_ADD_ARGB" -> ADD
            "DANMAKUFU_ADD", "DANMAKUFU_ADD_RGB" -> BlendingMode(GL20.GL_ONE, GL20.GL_ONE)
            "MULTIPLY", "DANMAKUFU_MULTIPLY" -> MULTIPLY
            "SUBTRACT", "DANMAKUFU_SUBTRACT" -> SUBTRACT
            "DANMAKUFU_SHADOW" -> BlendingMode(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_COLOR)
            "DANMAKUFU_INV_DESTRGB" -> BlendingMode(GL20.GL_ONE_MINUS_DST_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR)
            else -> throw KotoRuntimeException("Unsupported blend mode: $blendingString")
        }
    }

    constructor(src: Int, dst: Int, equation: Int = GL20.GL_FUNC_ADD) : this(
        src,
        dst,
        GL20.GL_ONE,
        GL20.GL_ONE,
        equation,
        GL20.GL_FUNC_ADD,
    )
}

fun Batch.setBlending(blending: BlendingMode, immediately: Boolean = false) {
    setBlendFunctionSeparate(blending.srcColor, blending.dstColor, blending.srcAlpha, blending.dstAlpha)
    Gdx.gl.glBlendEquationSeparate(blending.equationColor, blending.equationAlpha)
    if (immediately) {
        Gdx.gl.glBlendFuncSeparate(blending.srcColor, blending.dstColor, blending.srcAlpha, blending.dstAlpha)
    }
}

operator fun Color.plus(other: Color): Color = this.copy().add(other)

operator fun Color.plusAssign(other: Color) {
    add(other)
}

operator fun Color.minus(other: Color): Color = this.copy().sub(other)

operator fun Color.minusAssign(other: Color) {
    sub(other)
}

operator fun Color.times(other: Color): Color = this.copy().mul(other)

operator fun Color.timesAssign(other: Color) {
    mul(other)
}

inline var Sprite.alpha: Float
    get() = this.color.a
    set(value) {
        this.setAlpha(value)
    }

fun BitmapFont.draw(
    batch: Batch,
    alphaModulation: Float,
    text: String,
    fontScale: Float,
    x: Float,
    y: Float,
    color: Color = WHITE_HSV,
    targetWidth: Float = 0f,
    halign: Int = Align.left,
    wrap: Boolean = false,
) {
    val oldScaleX = data.scaleX
    val oldScaleY = data.scaleY
    data.setScale(oldScaleX * fontScale, oldScaleY * fontScale)
    setColor(color.r, color.g, color.b, color.a * alphaModulation)
    draw(batch, text, x, y, targetWidth, halign, wrap)
    data.setScale(oldScaleX, oldScaleY)
}

fun darken(color: Color, factor: Float = 0.5f): Color =
    color.cpy().mul(1.0f, 1.0f, factor, 1.0f)

private val tmpHSVArray = FloatArray(3)
fun Color.getHue(): Float {
    toHsv(tmpHSVArray)
    return tmpHSVArray[0] / 360f
}

fun fromHsv(h: Float, s: Float, v: Float, a: Float): Color {
    val color = Color()
    color.fromHsv(h * 360f, s, v)
    color.a = a
    return color
}

val NO_TINT_HSV: Color = Color(0f, 1f, 1f, 1f)
    get() = field.cpy()
val TRANSPARENT_HSV: Color = Color(0f, 1f, 1f, 0f)
    get() = field.cpy()
val WHITE_HSV: Color = Color.WHITE.toHSVColor()
    get() = field.cpy()
val BLACK_HSV: Color = Color.BLACK.toHSVColor()
    get() = field.cpy()
val GRAY_HSV: Color = Color.GRAY.toHSVColor()
    get() = field.cpy()
val LIGHT_GRAY_HSV: Color = Color.LIGHT_GRAY.toHSVColor()
    get() = field.cpy()
val DARK_GRAY_HSV: Color = Color.DARK_GRAY.toHSVColor()
    get() = field.cpy()
val RED_HSV: Color = Color.RED.toHSVColor()
    get() = field.cpy()
val GREEN_HSV: Color = Color.GREEN.toHSVColor()
    get() = field.cpy()
val BLUE_HSV: Color = Color.BLUE.toHSVColor()
    get() = field.cpy()
val CYAN_HSV: Color = Color.CYAN.toHSVColor()
    get() = field.cpy()
val MAGENTA_HSV: Color = Color.MAGENTA.toHSVColor()
    get() = field.cpy()
val YELLOW_HSV: Color = Color.YELLOW.toHSVColor()
    get() = field.cpy()

fun Color.toHSVColor(): Color {
    toHsv(tmpHSVArray)
    return Color(tmpHSVArray[0] / 360f, tmpHSVArray[1], tmpHSVArray[2], a)
}

fun Color.tintHSV(tint: Color): Color {
    return Color((r + tint.r) % 1f, g * tint.g, b * tint.b, a * tint.a)
}

val registeredEffects = GdxSet<ChainVfxEffect>()

fun VfxManager.addEffectRegistered(effect: ChainVfxEffect, priority: Int = 0) {
    addEffect(effect, priority)
    registeredEffects.add(effect)
}

fun VfxManager.removeEffectRegistered(effect: ChainVfxEffect) {
    removeEffect(effect)
    registeredEffects.remove(effect)
}

fun disposeRegisteredEffects() {
    registeredEffects.forEach {
        it.dispose()
    }
    registeredEffects.clear()
}

fun getTrueFPSMultiplier(fpsMultiplier: Int): Float {
    if (fpsMultiplier == 0) return 1f
    if (fpsMultiplier < 0) return 1f / -fpsMultiplier
    return fpsMultiplier.toFloat()
}

fun safeDeltaTime() = clamp(Gdx.graphics.deltaTime, 0f, 0.1f)