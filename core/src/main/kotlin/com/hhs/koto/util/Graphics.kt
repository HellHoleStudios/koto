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

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.crashinvaders.vfx.VfxManager
import com.crashinvaders.vfx.effects.ChainVfxEffect
import com.hhs.koto.app.ui.HSVColorAction
import ktx.collections.GdxSet
import ktx.graphics.copy

data class BlendingMode(
    val srcRGB: Int,
    val dstRGB: Int,
    val srcAlpha: Int,
    val dstAlpha: Int,
    val equationRGB: Int,
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
            else -> ALPHA
        }
    }

    constructor(src: Int, dst: Int, equation: Int = GL20.GL_FUNC_ADD) : this(src, dst, src, dst, equation, equation)
}

fun Batch.setBlending(blending: BlendingMode) {
    setBlendFunctionSeparate(blending.srcRGB, blending.dstRGB, blending.srcAlpha, blending.dstAlpha)
    Gdx.gl.glBlendEquationSeparate(blending.equationRGB, blending.equationAlpha)
}

var tmpColor: Color = Color.WHITE

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

fun Color.toHSVColor(): Color {
    toHsv(tmpHSVArray)
    return Color(tmpHSVArray[0] / 360f, tmpHSVArray[1], tmpHSVArray[2], a)
}

fun Color.tintHSV(tint: Color): Color {
    return Color((r + tint.r) % 1f, g * tint.g, b * tint.b, a * tint.a)
}

fun hsvColor(color: Color): HSVColorAction {
    return hsvColor(color, 0f, null)
}

fun hsvColor(color: Color, duration: Float): HSVColorAction {
    return hsvColor(color, duration, null)
}

fun hsvColor(color: Color, duration: Float, interpolation: Interpolation?): HSVColorAction {
    val action = Actions.action(HSVColorAction::class.java)
    action.endColor = color
    action.duration = duration
    action.interpolation = interpolation
    return action
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
}