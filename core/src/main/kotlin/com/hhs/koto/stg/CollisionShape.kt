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

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.hhs.koto.app.Config
import com.hhs.koto.stg.bullet.BasicBullet
import com.hhs.koto.util.*
import ktx.math.minus
import ktx.math.plus
import ktx.math.vec2

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

    val radiusSq: Float
        get() = radius * radius

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

class SLCollision(val angle: Float, val length: Float, val width: Float, val headHit: Float, val widthHit: Float) :
    CollisionShape {
    override val boundingHeight = max(length, width)
    override val boundingWidth = max(length, width)

    override fun collide(other: CollisionShape, x1: Float, y1: Float, x2: Float, y2: Float): Boolean {
        if (other is CircleCollision) {
            val vlt = vec2(length * cos(angle), length * sin(angle))
            val vlr = vlt.cpy().setLength(vlt.len() * (1 - headHit) / 2)
            val vlro = vlt.cpy().rotate90(1).setLength(width * widthHit / 2)
            val xx = x1 + cos(angle) * length
            val yy = y1 + sin(angle) * length
            val v1 = vec2(x1, y1) + vlr + vlro
            val v2 = vec2(x1, y1) + vlr - vlro
            val v3 = vec2(xx, yy) - vlr - vlro
            val v4 = vec2(xx, yy) - vlr + vlro
            val cc = vec2(x2, y2)

            return Intersector.intersectSegmentCircle(v1, v2, cc, other.radiusSq) ||
                    Intersector.intersectSegmentCircle(v2, v3, cc, other.radiusSq) ||
                    Intersector.intersectSegmentCircle(v3, v4, cc, other.radiusSq) ||
                    Intersector.intersectSegmentCircle(v4, v1, cc, other.radiusSq)
        } else {
            throw UnsupportedOperationException("Only point is supported now")
        }
    }
}

class LaserCollision(val bullet: BasicBullet) : CollisionShape {
    override val boundingHeight = 1e9f
    override val boundingWidth = 1e9f

    override fun collide(other: CollisionShape, x1: Float, y1: Float, x2: Float, y2: Float): Boolean? {
        if (other is CircleCollision) {
            //for simplicity, only a point is considered :3
            val last = bullet.prev
            val now = bullet
            if (last != null && bullet.laserActivated) {
                val vec = vec2(now.x - last.x, now.y - last.y)
                vec.rotate90(1).setLength(bullet.width * bullet.hitRatio / 2)
                val _x1 = vec.x + last.x
                val _y1 = vec.y + last.y
                val _x2 = vec.x + now.x
                val _y2 = vec.y + now.y

                vec.rotateDeg(180f)
                val x3 = vec.x + last.x
                val y3 = vec.y + last.y
                val x4 = vec.x + now.x
                val y4 = vec.y + now.y

                //2|==|4
                //1|==|3

                return Intersector.intersectSegmentCircle(
                    vec2(_x1, _y1),
                    vec2(_x2, _y2),
                    vec2(x2, y2),
                    other.radius * other.radius
                ) ||
                        Intersector.intersectSegmentCircle(
                            vec2(_x2, _y2),
                            vec2(x4, y4),
                            vec2(x2, y2),
                            other.radius * other.radius
                        ) ||
                        Intersector.intersectSegmentCircle(
                            vec2(x4, y4),
                            vec2(x3, y3),
                            vec2(x2, y2),
                            other.radius * other.radius
                        ) ||
                        Intersector.intersectSegmentCircle(
                            vec2(_x1, _y1),
                            vec2(x3, y3),
                            vec2(x2, y2),
                            other.radius * other.radius
                        )
            }

            return false
        } else {
            throw UnsupportedOperationException("Only point is supported now")
        }
    }
}