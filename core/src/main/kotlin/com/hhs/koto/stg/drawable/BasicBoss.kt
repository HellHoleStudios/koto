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

package com.hhs.koto.stg.drawable

import com.badlogic.gdx.graphics.g2d.Batch
import com.hhs.koto.stg.CircleCollision
import com.hhs.koto.stg.Collision
import com.hhs.koto.stg.PlayerState
import com.hhs.koto.stg.addTask
import com.hhs.koto.stg.task.CoroutineTask
import com.hhs.koto.stg.task.Task
import com.hhs.koto.util.game
import com.hhs.koto.util.playerX
import com.hhs.koto.util.playerY
import kotlinx.coroutines.CoroutineScope
import ktx.collections.GdxArray

class BasicBoss(
    override val x: Float,
    override val y: Float,
    bulletCollisionRadius: Float,
    playerCollisionRadius: Float = bulletCollisionRadius / 3f,
) : Enemy {
    var invulnerable: Boolean = false
    val attachedTasks = GdxArray<Task>()
    val bulletCollision = CircleCollision(bulletCollisionRadius)
    val playerCollision = CircleCollision(playerCollisionRadius)
    override var alive: Boolean = true

    override fun tick() {
        if (!invulnerable) {
            game.playerBullets.forEach {
                if (Collision.collide(it.collision, it.x, it.y, bulletCollision, x, y)) {
                    it.hit(this)
                }
            }
        }
        if (
            game.player.playerState == PlayerState.NORMAL &&
            Collision.collide(game.player.hitCollision, playerX, playerY, playerCollision, x, y)
        ) {
            game.player.onHit()
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {

    }

    override fun onHit(damage: Float) {

    }

    override fun onDeath() {

    }

    override fun kill(): Boolean {
        alive = false
        attachedTasks.forEach { it.kill() }
        return true
    }

    fun task(index: Int = 0, block: suspend CoroutineScope.() -> Unit): BasicBoss {
        val task = CoroutineTask(index, this, block)
        addTask(task)
        attachedTasks.add(task)
        return this
    }
}