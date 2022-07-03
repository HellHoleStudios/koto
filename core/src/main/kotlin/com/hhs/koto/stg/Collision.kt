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

package com.hhs.koto.stg

import com.hhs.koto.util.KotoRuntimeException
import com.hhs.koto.util.clamp
import com.hhs.koto.util.dist2
import kotlin.math.abs

object Collision {

    fun collide(s1: CollisionShape, x1: Float, y1: Float, s2: CollisionShape, x2: Float, y2: Float): Boolean {
        var tmp = s1.collide(s2, x1, y1, x2, y2)
        if (tmp != null) return tmp
        tmp = s2.collide(s1, x2, y2, x1, y1)
        if (tmp != null) return tmp
        throw KotoRuntimeException("Cannot compute collision between $s1 and $s2")
    }

    fun collide(e1: Entity, e2: Entity): Boolean {
        return collide(e1.collision, e1.x, e1.y, e2.collision, e2.x, e2.y)
    }

    fun circleCircle(x1: Float, y1: Float, r1: Float, x2: Float, y2: Float, r2: Float): Boolean {
        if (!squareSquare(x1, y1, r1 * 2, x2, y2, r2 * 2)) {
            return false
        }
        val r = r1 + r2
        return dist2(x1, y1, x2, y2) <= r * r
    }

    fun circleCircleOrtho(x1: Float, y1: Float, r1: Float, x2: Float, y2: Float, r2: Float): Boolean {
        if (!squareSquare(x1, y1, r1 * 2, x2, y2, r2 * 2)) {
            return false
        }
        return dist2(x1, y1, x2, y2) <= r1 * r1 + r2 * r2
    }

    fun circleRect(x1: Float, y1: Float, r: Float, x2: Float, y2: Float, w: Float, h: Float) =
        dist2(x1, y1, clamp(x1, x2 - w / 2, x2 + w / 2), clamp(y1, y2 - h / 2, y2 + h / 2)) <= r * r

    fun circleSquare(x1: Float, y1: Float, r: Float, x2: Float, y2: Float, s: Float) =
        dist2(x1, y1, clamp(x1, x2 - s / 2, x2 + s / 2), clamp(y1, y2 - s / 2, y2 + s / 2)) <= r * r

    fun rectRect(x1: Float, y1: Float, w1: Float, h1: Float, x2: Float, y2: Float, w2: Float, h2: Float) =
        abs(x1 - x2) <= (w1 + w2) / 2 && abs(y1 - y2) <= (h1 + h2) / 2

    fun squareSquare(x1: Float, y1: Float, s1: Float, x2: Float, y2: Float, s2: Float) =
        rectRect(x1, y1, s1, s1, x2, y2, s2, s2)
}