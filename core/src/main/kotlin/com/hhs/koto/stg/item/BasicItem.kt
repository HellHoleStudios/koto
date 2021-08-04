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

package com.hhs.koto.stg.item

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.hhs.koto.stg.CircleCollision
import com.hhs.koto.stg.CollisionShape
import com.hhs.koto.stg.bullet.Bullet
import com.hhs.koto.util.*
import kotlin.math.absoluteValue

open class BasicItem(
    override var x: Float,
    override var y: Float,
    val texture: TextureRegion,
    speed: Float = 2f,
    angle: Float = 90f,
    radius: Float = 10f,
    var gravity: Float = 0.05f,
    var xDamping: Float = 0.05f,
    var xDampingLimit: Float = 0.05f,
    var maxSpeed: Float = 2f,
    var originX: Float = texture.regionWidth.toFloat() / 2f,
    var originY: Float = texture.regionHeight.toFloat() / 2f,
    var scaleX: Float = 1f,
    var scaleY: Float = 1f,
    var rotation: Float = 0f,
    var color: Color = Color.WHITE,
) : Item {
    companion object {
        val tempColor: Color = Color()
    }

    var deltaX: Float = cos(angle) * speed
    var deltaY: Float = sin(angle) * speed
    override var alive: Boolean = true
    override val boundingWidth
        get() = texture.regionWidth * scaleX + texture.regionHeight * scaleY
    override val boundingHeight
        get() = texture.regionWidth * scaleX + texture.regionHeight * scaleY
    override val collision: CollisionShape = CircleCollision(radius)
    var collected: Boolean = false
    protected var counter: Int = 0

    override fun tick() {
        if (collected) {
            val angle = atan2(x, y, playerX, playerY)
            deltaX = cos(angle) * 8f
            deltaY = sin(angle) * 8f
            if (dist(x, y, playerX, playerY) <= 5f) {
                destroy()
            }
        } else {
            deltaY = (deltaY - gravity).coerceAtLeast(-maxSpeed)
            deltaX = if (deltaX > 0) {
                (deltaX - xDamping).coerceAtLeast(0f)
            } else {
                (deltaX + xDamping).coerceAtMost(0f)
            }
            if (deltaX.absoluteValue <= xDampingLimit) {
                deltaX = 0f
            }
        }
        x += deltaX
        y += deltaY
    }

    fun destroy() {
        alive = false
    }

    override fun collect() {
        collected = true
    }

    open fun collected() = Unit

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (!outOfFrame(x, y, boundingWidth, boundingHeight)) {
            Bullet.tempColor.set(batch.color)
            batch.color = color
            batch.color.a *= parentAlpha
            var tmpX = x
            var tmpY = y
            if (subFrameTime != 0f) {
                tmpX += deltaX * subFrameTime
                tmpY += deltaY * subFrameTime
            }
            if (rotation != 0f || scaleX != 1f || scaleY != 1f) {
                batch.draw(
                    texture,
                    tmpX - originX,
                    tmpY - originY,
                    originX,
                    originY,
                    texture.regionWidth.toFloat(),
                    texture.regionHeight.toFloat(),
                    scaleX,
                    scaleY,
                    rotation,
                )
            } else {
                batch.draw(
                    texture,
                    tmpX - originX,
                    tmpY - originY,
                    texture.regionWidth.toFloat(),
                    texture.regionHeight.toFloat(),
                )
            }
            batch.color = Bullet.tempColor
        }
    }
}