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

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils.random
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
    override var hp: Float,
    var pointCount: Int,
    var powerCount: Int,
    bulletCollisionRadius: Float = min(texture.texture.regionWidth, texture.texture.regionHeight).toFloat() / 1.5f,
    playerCollisionRadius: Float = bulletCollisionRadius / 3f,
    var textureOriginX: Float = texture.texture.regionWidth.toFloat() / 2,
    var textureOriginY: Float = texture.texture.regionHeight.toFloat() / 2,
    override val zIndex: Int = -300,
) : Enemy {
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
        tmpColor.set(batch.color)
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        val tmpScaleX = if (x - oldX < 0) {
            -scaleX
        } else {
            scaleX
        }

        if (rotation != 0f || tmpScaleX != 1f || scaleY != 1f) {
            batch.draw(
                texture.texture,
                x - textureOriginX,
                y - textureOriginY,
                textureOriginX,
                textureOriginY,
                texture.texture.regionWidth.toFloat(),
                texture.texture.regionHeight.toFloat(),
                tmpScaleX,
                scaleY,
                rotation,
            )
        } else {
            batch.draw(
                texture.texture,
                x - textureOriginX,
                y - textureOriginY,
                texture.texture.regionWidth.toFloat(),
                texture.texture.regionHeight.toFloat(),
            )
        }
        batch.color = tmpColor
    }

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
        if ((x - oldX).absoluteValue < 0.1f) {
            texture.update(0)
        } else {
            texture.update(1)
        }
        oldX = x
        if (hp <= 0) {
            onDeath()
        }
    }

    override fun onDeath() {
        SE.play("enemydead")
        ringCloud(x, y, powerCount, bulletCollision.radius) { x, y ->
            addItem(PowerItem(x, y))
        }
        ringCloud(x, y, pointCount, bulletCollision.radius) { x, y ->
            addItem(PointItem(x, y))
        }
        addParticle(
            Explosion(
                x, y, 32f, 64f, 16f, 256f, random(0f, 360f), 15
            )
        )
        repeat(5) {
            addParticle(DeathParticle(x, y, 10f, random(0f, 360f), 24f, 0f, 20))
        }
        kill()
    }

    override fun kill(): Boolean {
        alive = false
        attachedTasks.forEach { it.kill() }
        return true
    }

    fun task(index: Int = 0, block: suspend CoroutineScope.() -> Unit): BasicEnemy {
        val task = CoroutineTask(index, this, block)
        addTask(task)
        attachedTasks.add(task)
        return this
    }
}