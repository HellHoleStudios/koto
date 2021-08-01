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

package com.hhs.koto.stg.bullet

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.hhs.koto.stg.addTask
import com.hhs.koto.stg.task.CoroutineTask
import com.hhs.koto.util.*
import kotlinx.coroutines.CoroutineScope

open class Bullet(
    var x: Float,
    var y: Float,
    speed: Float = 0f,
    angle: Float = 0f,
    val data: BulletData,
    var scaleX: Float = 1f,
    var scaleY: Float = 1f,
    var rotation: Float = 0f,
    var color: Color = Color.WHITE,
) {
    companion object {
        val tempColor: Color = Color()
    }

    var speed: Float = speed
        set(value) {
            field = value
            calculateDelta()
        }
    var angle: Float = angle
        set(value) {
            field = value
            calculateDelta()
        }

    var deltaX: Float = 0f
    var deltaY: Float = 0f
    var alive: Boolean = true
    var t: Int = 0
    val boundingWidth
        get() = data.texture.maxWidth * scaleX
    val boundingHeight
        get() = data.texture.maxHeight * scaleY

    init {
        calculateDelta()
    }

    fun setDeltas(deltaX: Float, deltaY: Float) {
        this.deltaX = deltaX
        this.deltaY = deltaY
        calculateAngleSpeed()
    }

    fun calculateDelta() {
        deltaX = cos(angle) * speed
        deltaY = sin(angle) * speed
    }

    fun calculateAngleSpeed() {
        angle = atan2(deltaX, deltaY)
        speed = dist(deltaX, deltaY)
    }

    fun task(index: Int = 0, block: suspend CoroutineScope.() -> Unit): Bullet {
        val task = CoroutineTask(index = index, bullet = this, block = block)
        addTask(task)
        return this
    }

    fun tick() {
        x += deltaX
        y += deltaY
        t++
    }

    fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (!outOfFrame(x, y, boundingWidth, boundingHeight)) {
            val texture = data.texture.getFrame(t)
            tempColor.set(batch.color)
            batch.color = color
            batch.color.a *= parentAlpha
            var tmpX = x
            var tmpY = y
            if (subFrameTime != 0f) {
                tmpX += deltaX * subFrameTime
                tmpY += deltaY * subFrameTime
            }
            batch.draw(
                texture,
                tmpX,
                tmpY,
                data.originX,
                data.originY,
                texture.regionWidth.toFloat(),
                texture.regionHeight.toFloat(),
                scaleX,
                scaleY,
                rotation + data.rotation
            )
            batch.color = tempColor
        }
    }
}