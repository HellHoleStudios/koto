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

package com.hhs.koto.stg.graphics

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.hhs.koto.stg.CircleCollision
import com.hhs.koto.stg.Collision
import com.hhs.koto.stg.PlayerState
import com.hhs.koto.stg.item.PointItem
import com.hhs.koto.stg.item.PowerItem
import com.hhs.koto.stg.particle.DeathParticle
import com.hhs.koto.stg.particle.Explosion
import com.hhs.koto.stg.task.CoroutineTask
import com.hhs.koto.stg.task.Task
import com.hhs.koto.util.*
import kotlinx.coroutines.CoroutineScope
import ktx.collections.GdxArray
import kotlin.math.absoluteValue
import kotlin.math.min

open class BasicEnemy(
    override var x: Float,
    override var y: Float,
    val texture: BasicEnemyTexture,
    val health: Float,
    var pointCount: Int,
    var powerCount: Int,
    bulletCollisionRadius: Float = min(texture.texture.regionWidth, texture.texture.regionHeight).toFloat() / 1.5f,
    playerCollisionRadius: Float = bulletCollisionRadius / 3f,
    val width: Float = texture.texture.regionWidth.toFloat(),
    val height: Float = texture.texture.regionHeight.toFloat(),
    var textureOriginX: Float = texture.texture.regionWidth / 2f,
    var textureOriginY: Float = texture.texture.regionHeight / 2f,
    override val zIndex: Int = -300,
) : Enemy {
    var hp: Float = health
    var invulnerable: Boolean = false
    var rotation: Float = 0f
    var scaleX = 1f
    var scaleY = 1f
    var color: Color = Color.WHITE
    val bulletCollision = CircleCollision(bulletCollisionRadius)
    val playerCollision = CircleCollision(playerCollisionRadius)
    val attachedTasks = GdxArray<Task>()
    protected var oldX: Float = x
    override var alive: Boolean = true

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (color.a < 0.001f) return

        val tmpColor = batch.color.cpy()
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        val tmpScaleX = if (x - oldX < 0) {
            -scaleX
        } else {
            scaleX
        }

        batch.draw(
            texture.texture,
            x - textureOriginX,
            y - textureOriginY,
            textureOriginX,
            textureOriginY,
            width,
            height,
            tmpScaleX,
            scaleY,
            rotation,
        )
        batch.color = tmpColor
    }

    override fun tick() {
        game.playerBullets.forEach {
            if (Collision.collide(it.collision, it.x, it.y, bulletCollision, x, y)) {
                it.hit(this)
            }
        }
        if (
            game.player.playerState == PlayerState.NORMAL &&
            Collision.collide(game.player.hitCollision, playerX, playerY, playerCollision, x, y)
        ) {
            game.player.onHit()
        }
        if ((x - oldX).absoluteValue < 0.1f) {
            texture.update(0)
        } else {
            texture.update(1)
        }
        oldX = x
        for (i in 0 until attachedTasks.size) {
            if (attachedTasks[i].alive) {
                attachedTasks[i].tick()
            } else {
                attachedTasks[i] = null
            }
        }
        attachedTasks.removeNull()
    }

    override fun onHit(damage: Float, isBomb: Boolean) {
        if (!invulnerable) {
            hp -= damage
            if (!isBomb) {
                if (health < 1000f) {
                    SE.play("damage0")
                } else {
                    if (hp < 500f) {
                        SE.play("damage1")
                    } else {
                        SE.play("damage0")
                    }
                }
            }
            if (hp <= 0) {
                destroy()
            }
        }
    }

    override fun destroy() {
        SE.play("enemydead")
        ringCloud(x, y, powerCount, bulletCollision.radius) { x, y ->
            game.addItem(PowerItem(x, y))
        }
        ringCloud(x, y, pointCount, bulletCollision.radius) { x, y ->
            game.addItem(PointItem(x, y))
        }
        game.addParticle(
            Explosion(
                x, y, 32f, 64f, 16f, 256f, random(0f, 360f), 15
            )
        )
        repeat(5) {
            game.addParticle(DeathParticle(x, y, 10f, random(0f, 360f), 24f, 0f, 20))
        }
        kill()
    }

    override fun kill(): Boolean {
        alive = false
        attachedTasks.forEach { it.kill() }
        return true
    }

    fun attachTask(task: Task): BasicEnemy {
        attachedTasks.add(task)
        return this
    }

    fun task(index: Int = 0, block: suspend CoroutineScope.() -> Unit): BasicEnemy {
        attachTask(CoroutineTask(index, this, block))
        return this
    }
}