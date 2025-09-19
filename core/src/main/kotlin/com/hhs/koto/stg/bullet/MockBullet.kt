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

package com.hhs.koto.stg.bullet

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.hhs.koto.stg.CollisionShape
import com.hhs.koto.stg.NoCollision
import com.hhs.koto.stg.task.CoroutineTask
import com.hhs.koto.stg.task.Task
import com.hhs.koto.util.cos
import com.hhs.koto.util.removeNull
import com.hhs.koto.util.sin
import kotlinx.coroutines.CoroutineScope
import ktx.collections.GdxArray

class MockBullet(
    override var x: Float,
    override var y: Float,
    override var speed: Float = 0f,
    override var angle: Float = 0f,
    override var destroyable: Boolean = true
) : Bullet {
    var attachedTasks: GdxArray<Task>? = null
    override var rotation: Float = 0f
    override var tint: Color = Color.WHITE
    var t: Int = 0

    override fun onGraze() = Unit

    override val collision: CollisionShape = NoCollision()

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) = Unit

    override fun tick() {
        x += cos(angle) * speed
        y += sin(angle) * speed
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

    override fun attachTask(task: Task): MockBullet {
        if (attachedTasks == null) {
            attachedTasks = GdxArray()
        }
        attachedTasks!!.add(task)
        return this
    }

    override fun task(index: Int, block: suspend CoroutineScope.() -> Unit): MockBullet {
        attachTask(CoroutineTask(index, this, block))
        return this
    }

    override fun destroy() {
        kill()
    }

    override var alive: Boolean = true

    override fun kill(): Boolean {
        alive = false
        attachedTasks?.forEach { it.kill() }
        return true
    }
}