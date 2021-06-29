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

package com.hhs.koto.stg

import com.hhs.koto.app.Config
import com.hhs.koto.util.clamp
import com.hhs.koto.util.dist2
import com.hhs.koto.util.sqr
import kotlin.math.abs

object Collision {

    fun collide(x1: Float, y1: Float, m1: CollisionData?, x2: Float, y2: Float, m2: CollisionData?): Boolean {
        if (m1 is Circle) {
            if (m2 is Circle) {
                return if (Config.orthoCircleCollision) {
                    circleCircleOrtho(x1, y1, m1.radius, x2, y2, m2.radius)
                } else {
                    circleCircle(x1, y1, m1.radius, x2, y2, m2.radius)
                }
            } else if (m2 is Rectangle) {
                return circleRect(x1, y1, m1.radius, x2, y2, m2.width, m2.height)
            }
        } else if (m1 is Rectangle) {
            if (m2 is Circle) {
                return circleRect(x2, y2, m2.radius, x1, y1, m1.width, m1.height)
            } else if (m2 is Rectangle) {
                return rectRect(x1, y1, m1.width, m2.height, x2, y2, m2.width, m2.height)
            }
        }
        return false
    }

//    fun collide(e1: Entity, e2: Entity): Boolean {
//        return collide(e1.getX(), e1.getY(), e1.getCollisionData(e2), e2.getX(), e2.getY(), e2.getCollisionData(e1))
//    }

    fun circleCircle(x1: Float, y1: Float, r1: Float, x2: Float, y2: Float, r2: Float): Boolean {
        if (!squareSquare(x1, y1, r1 * 2, x2, y2, r2 * 2)) {
            return false
        }
        return dist2(x1, y1, x2, y2) <= sqr(r1 + r2)
    }

    fun circleCircleOrtho(x1: Float, y1: Float, r1: Float, x2: Float, y2: Float, r2: Float): Boolean {
        if (!squareSquare(x1, y1, r1 * 2, x2, y2, r2 * 2)) {
            return false
        }
        return dist2(x1, y1, x2, y2) <= sqr(r1) + sqr(r2)
    }

    fun circleRect(x1: Float, y1: Float, r: Float, x2: Float, y2: Float, w: Float, h: Float) =
        dist2(x1, y1, clamp(x1, x2 - w / 2, x2 + w / 2), clamp(y1, y2 - h / 2, y2 + h / 2)) <= sqr(r)

    fun circleSquare(x1: Float, y1: Float, r: Float, x2: Float, y2: Float, s: Float) =
        dist2(x1, y1, clamp(x1, x2 - s / 2, x2 + s / 2), clamp(y1, y2 - s / 2, y2 + s / 2)) <= sqr(r)

    fun rectRect(x1: Float, y1: Float, w1: Float, h1: Float, x2: Float, y2: Float, w2: Float, h2: Float) =
        abs(x1 - x2) <= (w1 + w2) / 2 && abs(y1 - y2) <= (h1 + h2) / 2

    fun squareSquare(x1: Float, y1: Float, s1: Float, x2: Float, y2: Float, s2: Float) =
        rectRect(x1, y1, s1, s1, x2, y2, s2, s2)

    abstract class CollisionData {
        abstract val height: Float
        abstract val width: Float
    }

    class Circle(var radius: Float) : CollisionData() {
        override val width: Float
            get() = radius * 2
        override val height: Float
            get() = radius * 2
    }

    class Rectangle(override val width: Float, override val height: Float) : CollisionData()
}