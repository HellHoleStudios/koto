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

import com.badlogic.gdx.graphics.Color
import com.hhs.koto.app.Config
import ktx.graphics.copy

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

fun darken(color: Color, factor: Float = 0.5f): Color = if (Config.useHSVShader) {
    color.cpy().mul(1.0f, 1.0f, factor, 1.0f)
} else {
    color.cpy().mul(factor, factor, factor, 1.0f)
}

val tmpHSVArray = FloatArray(3)
fun Color.getHue(): Float {
    toHsv(tmpHSVArray)
    return tmpHSVArray[0] / 360f
}

fun Color.toHSVColor(): Color {
    toHsv(tmpHSVArray)
    return Color(tmpHSVArray[0] / 360f, tmpHSVArray[1], tmpHSVArray[2], a)
}