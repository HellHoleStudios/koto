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

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import kotlin.math.abs
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

fun normalizeAngle(angle: Float): Float = safeMod(angle, 360f)

@Suppress("ConvertTwoComparisonsToRangeCheck")
fun angleInRange(angle: Float, min: Float, max: Float): Boolean {
    val angle0 = normalizeAngle(angle)
    val min0 = normalizeAngle(min)
    val max0 = normalizeAngle(max)
    return if (min0 < max0) {
        min0 <= angle0 && angle0 <= max0
    } else {
        min0 <= angle0 || angle0 <= max0
    }
}

fun min(vararg x: Float): Float = x.minOrNull()!!

fun min(vararg x: Double): Double = x.minOrNull()!!

fun min(vararg x: Int): Int = x.minOrNull()!!

fun min(vararg x: Long): Long = x.minOrNull()!!

fun max(vararg x: Float): Float = x.maxOrNull()!!

fun max(vararg x: Double): Double = x.maxOrNull()!!

fun max(vararg x: Int): Int = x.maxOrNull()!!

fun max(vararg x: Long): Long = x.maxOrNull()!!

fun sqrt(x: Float): Float =
    sqrt(x.toDouble()).toFloat()

fun len2(x: Float, y: Float): Float =
    x * x + y * y

fun len(x: Float, y: Float): Float =
    sqrt(x * x + y * y)

fun dist(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    val deltaX = x1 - x2
    val deltaY = y1 - y2
    return sqrt(deltaX * deltaX + deltaY * deltaY)
}

/**
 * Returns the distance from point(x1,y1) to line(x2,y2)--(x3,y3)
 */
fun dist(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float): Float{
    if(x2==x3){
        return abs(x1-x2)
    }
    val k=(y3-y2)/(x3-x2)
    val b=y2-x2*k
    return abs(x1*k+b-y1)/sqrt(k*k+1)
}

fun dist2(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    val deltaX = x1 - x2
    val deltaY = y1 - y2
    return deltaX * deltaX + deltaY * deltaY
}

fun atan2(y: Float, x: Float): Float =
    normalizeAngle(MathUtils.atan2(y, x) * MathUtils.radiansToDegrees)

fun atan2(x1: Float, y1: Float, x2: Float, y2: Float): Float =
    normalizeAngle(MathUtils.atan2(y2 - y1, x2 - x1) * MathUtils.radiansToDegrees)

fun sin(degrees: Float): Float = MathUtils.sinDeg(degrees)

fun cos(degrees: Float): Float = MathUtils.cosDeg(degrees)

fun tan(degrees: Float): Float = sin(degrees) / cos(degrees)

fun lerp(start: Float, end: Float, a: Float): Float {
    if (a < 0f) return start
    if (a > 1f) return end
    return (end - start) * a + start
}

/**
 * Performs smooth Hermite interpolation between 0 and 1.
 */
fun smoothstep(start: Float, end: Float, a: Float): Float {
    if (a < 0f) return start
    if (a > 1f) return end
    return (end - start) * a * a * (3 - 2 * a) + start
}

private val tmpRectangle = Rectangle()
fun Rectangle.contains(x: Float, y: Float, rx: Float, ry: Float): Boolean {
    tmpRectangle.set(x - rx, y - ry, rx * 2, ry * 2)
    return contains(tmpRectangle)
}

fun Rectangle.overlaps(x: Float, y: Float, rx: Float, ry: Float): Boolean {
    tmpRectangle.set(x - rx, y - ry, rx * 2, ry * 2)
    return overlaps(tmpRectangle)
}

fun Rectangle.distanceTo(x: Float, y: Float): Float {
    val deltaX = max(this.x - x, 0f, x - this.x - width)
    val deltaY = max(this.y - y, 0f, y - this.y - height)
    return sqrt(deltaX * deltaX + deltaY * deltaY)
}

fun completeInterpolation(interpolation: Interpolation, start: Float, end: Float, t: Int, duration: Int): Float {
    return if (t + 1 >= duration) {
        end
    } else {
        interpolation.apply(start, end, (t + 1).toFloat() / duration)
    }
}

// TODO docs

/** Returns a random number between 0 (inclusive) and the specified value (inclusive). */
fun random(range: Int): Int {
    return game.random.nextInt(range + 1)
}

/** Returns a random number between start (inclusive) and end (inclusive).  */
fun random(start: Int, end: Int): Int {
    return start + game.random.nextInt(end - start + 1)
}

/** Returns a random number between 0 (inclusive) and the specified value (inclusive).  */
fun random(range: Long): Long {
    // Uses the lower-bounded overload defined below, which is simpler and doesn't lose much optimization.
    return random(0L, range)
}

/** Returns a random number between start (inclusive) and end (inclusive).  */
fun random(start: Long, end: Long): Long {
    var start0 = start
    var end0 = end
    val rand = game.random.nextLong()
    // In order to get the range to go from start to end, instead of overflowing after end and going
    // back around to start, start must be less than end.
    if (end0 < start0) {
        val t = end0
        end0 = start0
        start0 = t
    }
    val bound = end0 - start0 + 1L // inclusive on end
    // Credit to https://oroboro.com/large-random-in-range/ for the following technique
    // It's a 128-bit-product where only the upper 64 of 128 bits are used.
    val randLow = rand and 0xFFFFFFFFL
    val boundLow = bound and 0xFFFFFFFFL
    val randHigh = rand ushr 32
    val boundHigh = bound ushr 32
    return start0 + (randHigh * boundLow ushr 32) + (randLow * boundHigh ushr 32) + randHigh * boundHigh
}

/** Returns a random boolean value.  */
fun randomBoolean(): Boolean {
    return game.random.nextBoolean()
}

/** Returns true if a random value between 0 and 1 is less than the specified value.  */
fun randomBoolean(chance: Float): Boolean {
    return random() < chance
}

/** Returns random number between 0.0 (inclusive) and 1.0 (exclusive).  */
fun random(): Float {
    return game.random.nextFloat()
}

/** Returns a random number between 0 (inclusive) and the specified value (exclusive).  */
fun random(range: Float): Float {
    return game.random.nextFloat() * range
}

/** Returns a random number between start (inclusive) and end (exclusive).  */
fun random(start: Float, end: Float): Float {
    return start + game.random.nextFloat() * (end - start)
}

/** Returns -1 or 1, randomly.  */
fun randomSign(): Int {
    return 1 or (game.random.nextInt() shr 31)
}

/** Returns a triangularly distributed random number between -1.0 (exclusive) and 1.0 (exclusive), where values around zero are
 * more likely.
 *
 *
 * This is an optimized version of [randomTriangular(-1, 1, 0)][.randomTriangular]  */
fun randomTriangular(): Float {
    return game.random.nextFloat() - game.random.nextFloat()
}

/** Returns a triangularly distributed random number between `-max` (exclusive) and `max` (exclusive), where values
 * around zero are more likely.
 *
 *
 * This is an optimized version of [randomTriangular(-max, max, 0)][.randomTriangular]
 * @param max the upper limit
 */
fun randomTriangular(max: Float): Float {
    return (game.random.nextFloat() - game.random.nextFloat()) * max
}

/** Returns a triangularly distributed random number between `min` (inclusive) and `max` (exclusive), where the
 * `mode` argument defaults to the midpoint between the bounds, giving a symmetric distribution.
 *
 *
 * This method is equivalent of [randomTriangular(min, max, (min + max) * 0.5f)][.randomTriangular]
 * @param min the lower limit
 * @param max the upper limit
 */
fun randomTriangular(min: Float, max: Float): Float {
    return randomTriangular(min, max, (min + max) * 0.5f)
}

/** Returns a triangularly distributed random number between `min` (inclusive) and `max` (exclusive), where values
 * around `mode` are more likely.
 * @param min the lower limit
 * @param max the upper limit
 * @param mode the point around which the values are more likely
 */
fun randomTriangular(min: Float, max: Float, mode: Float): Float {
    val u = game.random.nextFloat()
    val d = max - min
    return if (u <= (mode - min) / d) min + sqrt((u * d * (mode - min)).toDouble())
        .toFloat() else max - sqrt(((1 - u) * d * (max - mode)).toDouble()).toFloat()
}