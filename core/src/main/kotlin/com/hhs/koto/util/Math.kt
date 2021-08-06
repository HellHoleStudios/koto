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

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import kotlin.math.sqrt

const val SQRT2 = 1.41421356237309505f

fun clamp(value: Int, min: Int, max: Int) = MathUtils.clamp(value, min, max)

fun clamp(value: Long, min: Long, max: Long) = MathUtils.clamp(value, min, max)

fun clamp(value: Float, min: Float, max: Float) = MathUtils.clamp(value, min, max)

fun clamp(value: Double, min: Double, max: Double) = MathUtils.clamp(value, min, max)

fun safeMod(x: Int, mod: Int): Int {
    return if (x >= 0) {
        x % mod
    } else {
        (x % mod + mod) % mod
    }
}

fun safeMod(x: Float, mod: Float): Float {
    return if (x >= 0) {
        x % mod
    } else {
        (x % mod + mod) % mod
    }
}

fun normalizeAngle(angle: Float): Float {
    var angle1 = angle
    angle1 = if (angle1 > 0) {
        angle1 - MathUtils.round(angle1 / 360) * 360
    } else {
        angle1 + MathUtils.round(-angle1 / 360) * 360
    }
    if (angle1 == -180f) {
        angle1 = 180f
    }
    return angle1
}

fun sqr(x: Float): Float =
    x * x

fun sqrt(x: Float): Float =
    sqrt(x.toDouble()).toFloat()

fun len2(x: Float, y: Float): Float =
    x * x + y * y

fun len(x: Float, y: Float): Float =
    sqrt(x * x + y * y)

fun dist(x1: Float, y1: Float, x2: Float, y2: Float): Float =
    sqrt(sqr(x1 - x2) + sqr(y1 - y2))

fun dist2(x1: Float, y1: Float, x2: Float, y2: Float): Float =
    sqr(x1 - x2) + sqr(y1 - y2)

fun atan2(x: Float, y: Float): Float =
    MathUtils.atan2(y, x) * MathUtils.radiansToDegrees

fun atan2(x1: Float, y1: Float, x2: Float, y2: Float): Float =
    MathUtils.atan2(y2 - y1, x2 - x1) * MathUtils.radiansToDegrees

fun sin(degrees: Float) = MathUtils.sinDeg(degrees)

fun cos(degrees: Float) = MathUtils.cosDeg(degrees)

fun tan(degrees: Float) =
    MathUtils.radiansToDegrees * kotlin.math.tan((degrees * MathUtils.degreesToRadians).toDouble())

private val tmpRectangle = Rectangle()
fun Rectangle.contains(x: Float, y: Float, rx: Float, ry: Float): Boolean {
    tmpRectangle.set(x - rx, y - ry, rx * 2, ry * 2)
    return contains(tmpRectangle)
}

fun Rectangle.overlaps(x: Float, y: Float, rx: Float, ry: Float): Boolean {
    tmpRectangle.set(x - rx, y - ry, rx * 2, ry * 2)
    return overlaps(tmpRectangle)
}