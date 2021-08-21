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
import com.hhs.koto.util.*
import kotlin.math.absoluteValue

open class BasicItem(
    override var x: Float,
    override var y: Float,
    val texture: TextureRegion,
    val width: Float,
    val height: Float,
    speed: Float = 2f,
    angle: Float = 90f,
    radius: Float = 10f,
    val gravity: Float = 0.05f,
    val xDamping: Float = 0.05f,
    val xDampingLimit: Float = 0.05f,
    val maxSpeed: Float = 2f,
    val originX: Float = texture.regionWidth / 2f,
    val originY: Float = texture.regionHeight / 2f,
    var scaleX: Float = 1f,
    var scaleY: Float = 1f,
    var rotation: Float = 0f,
    var color: Color = Color.WHITE,
) : Item {
    var deltaX: Float = cos(angle) * speed
    var deltaY: Float = sin(angle) * speed
    override var alive: Boolean = true
    override val boundingRadiusX
        get() = texture.regionWidth * scaleX + texture.regionHeight * scaleY
    override val boundingRadiusY
        get() = texture.regionWidth * scaleX + texture.regionHeight * scaleY
    override val collision: CollisionShape = CircleCollision(radius)
    var collected: Boolean = false
    var autoCollected: Boolean? = null
    var collectPositionX: Float? = null
    var collectPositionY: Float? = null
    protected var t: Int = 0

    override fun tick() {
        t++
        if (collected) {
            val angle = atan2(x, y, playerX, playerY)
            deltaX = cos(angle) * 8f
            deltaY = sin(angle) * 8f
            if (dist(x, y, playerX, playerY) <= 5f) {
                onCollected(collectPositionX!!, collectPositionY!!, autoCollected!!)
                kill()
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
        if (t <= 32 && t % 4 == 0) {
            rotation += 45f
        }
        x += deltaX
        y += deltaY
    }

    override fun onCollect(collectPositionX: Float, collectPositionY: Float, autoCollect: Boolean) {
        if (!collected) {
            this.autoCollected = autoCollect
            this.collectPositionX = collectPositionX
            this.collectPositionY = collectPositionY
            collected = true
        }
    }

    open fun onCollected(collectPositionX: Float, collectPositionY: Float, autoCollected: Boolean) {
        SE.play("item")
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (!outOfFrame(x, y, boundingRadiusX, boundingRadiusY)) {
            val tmpColor = batch.color.cpy()
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)
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
                    width,
                    height,
                    scaleX,
                    scaleY,
                    rotation,
                )
            } else {
                batch.draw(
                    texture,
                    tmpX - originX,
                    tmpY - originY,
                    width,
                    height,
                )
            }
            batch.color = tmpColor
        }
    }
}