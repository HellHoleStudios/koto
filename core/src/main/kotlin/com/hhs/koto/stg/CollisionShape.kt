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

import com.hhs.koto.app.Config

interface CollisionShape {
    val boundingHeight: Float
    val boundingWidth: Float
    fun collide(other: CollisionShape, x1: Float, y1: Float, x2: Float, y2: Float): Boolean?
}

class CircleCollision(var radius: Float) : CollisionShape {
    override val boundingWidth: Float
        get() = radius * 2

    override val boundingHeight: Float
        get() = radius * 2

    override fun collide(other: CollisionShape, x1: Float, y1: Float, x2: Float, y2: Float): Boolean? {
        return when (other) {
            is CircleCollision -> {
                if (Config.orthoCircleCollision) {
                    Collision.circleCircleOrtho(x1, y1, radius, x2, y2, other.radius)
                } else {
                    Collision.circleCircle(x1, y1, radius, x2, y2, other.radius)
                }
            }
            is AABBCollision -> Collision.circleRect(x1, y1, radius, x2, y2, other.boundingWidth, other.boundingHeight)
            else -> null
        }
    }
}

class AABBCollision(override val boundingWidth: Float, override val boundingHeight: Float) : CollisionShape {
    override fun collide(other: CollisionShape, x1: Float, y1: Float, x2: Float, y2: Float): Boolean? {
        return when (other) {
            is AABBCollision -> Collision.rectRect(
                x1,
                y1,
                boundingWidth,
                boundingHeight,
                x2,
                y2,
                other.boundingWidth,
                other.boundingHeight
            )
            is CircleCollision -> other.collide(this, x2, y2, x1, y1)
            else -> null
        }
    }
}

class NoCollision : CollisionShape {
    override val boundingHeight = 0f
    override val boundingWidth = 0f
    override fun collide(other: CollisionShape, x1: Float, y1: Float, x2: Float, y2: Float) = false
}