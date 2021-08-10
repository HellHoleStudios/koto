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
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.hhs.koto.stg.Bounded
import com.hhs.koto.stg.CollisionShape
import com.hhs.koto.stg.addParticle
import com.hhs.koto.stg.particle.GrazeParticle
import com.hhs.koto.stg.task.CoroutineTask
import com.hhs.koto.stg.task.Task
import com.hhs.koto.util.*
import kotlinx.coroutines.CoroutineScope
import ktx.collections.GdxArray

open class BasicBullet(
    override var x: Float,
    override var y: Float,
    speed: Float = 0f,
    angle: Float = 0f,
    val data: BulletData,
    var scaleX: Float = 1f,
    var scaleY: Float = 1f,
    override var rotation: Float = 0f,
    override var tint: Color = Color(0f, 1f, 1f, 1f),
    val delay: Int = 8,
) : Bullet, Bounded {
    override val blending
        get() = if (t >= delay) {
            data.blending
        } else {
            data.delayBlending
        }
    var attachedTasks: GdxArray<Task>? = null
    override val collision: CollisionShape
        get() = data.collision

    override var speed: Float = speed
        set(value) {
            field = value
            calculateDelta()
        }
    override var angle: Float = angle
        set(value) {
            field = value
            calculateDelta()
        }

    val sprite = Sprite()
    var deltaX: Float = 0f
    var deltaY: Float = 0f
    override var alive: Boolean = true

    var grazeCounter: Int = 0
    var t: Int = 0
    override val boundingRadiusX
        get() = data.texture.maxWidth * scaleX + data.texture.maxHeight * scaleY
    override val boundingRadiusY
        get() = data.texture.maxWidth * scaleX + data.texture.maxHeight * scaleY

    init {
        calculateDelta()
    }

    fun calculateDelta() {
        deltaX = cos(angle) * speed
        deltaY = sin(angle) * speed
    }

    override fun attachTask(task: Task): BasicBullet {
        if (attachedTasks == null) {
            attachedTasks = GdxArray()
        }
        attachedTasks!!.add(task)
        return this
    }

    override fun task(index: Int, block: suspend CoroutineScope.() -> Unit): BasicBullet {
        attachTask(CoroutineTask(index, this, block))
        return this
    }

    override fun tick() {
        if (t >= delay) {
            x += deltaX
            y += deltaY
        }
        t++
        if (attachedTasks != null) {
            for (i in 0 until attachedTasks!!.size) {
                if (attachedTasks!![i].alive) {
                    attachedTasks!![i].tick()
                } else {
                    attachedTasks!![i] = null
                }
            }
            attachedTasks!!.removeNull()
        }
    }

    override fun kill(): Boolean {
        alive = false
        attachedTasks?.forEach { it.kill() }
        // TODO particle&animation
        return true
    }

    override fun onGraze() {
        if (grazeCounter <= 0) {
            grazeCounter++
            game.graze++
            SE.play("graze")
            addParticle(GrazeParticle(x, y))
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (!outOfFrame(x, y, boundingRadiusX, boundingRadiusY)) {
            var tmpX = x
            var tmpY = y
            if (subFrameTime != 0f) {
                tmpX += deltaX * subFrameTime
                tmpY += deltaY * subFrameTime
            }
            val texture = data.texture.getFrame(t)
            tmpColor.set(batch.color)
            if (t >= delay) {
                val bulletColor = data.color.tintHSV(tint)
                bulletColor.a *= parentAlpha
                batch.color = bulletColor
                if ((rotation + data.rotation) != 0f || scaleX != 1f || scaleY != 1f) {
                    batch.draw(
                        texture,
                        tmpX - data.originX,
                        tmpY - data.originY,
                        data.originX,
                        data.originY,
                        data.width,
                        data.height,
                        scaleX,
                        scaleY,
                        rotation + data.rotation,
                    )
                } else {
                    batch.draw(
                        texture,
                        tmpX - data.originX,
                        tmpY - data.originY,
                        data.width,
                        data.height,
                    )
                }
            } else {
                val scaleFactor = Interpolation.linear.apply(2f, 0.8f, t.toFloat() / delay)
                val delayColor = data.delayColor.tintHSV(tint)
                delayColor.a *= parentAlpha
                delayColor.a *= Interpolation.linear.apply(0.2f, 1f, t.toFloat() / delay)
                batch.color = delayColor
                batch.draw(
                    data.delayTexture,
                    tmpX - data.originX,
                    tmpY - data.originY,
                    data.originX,
                    data.originY,
                    data.width,
                    data.height,
                    scaleX * scaleFactor,
                    scaleY * scaleFactor,
                    rotation,
                )
            }
            batch.color = tmpColor
        }
    }
}