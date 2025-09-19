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
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.hhs.koto.app.Config.worldOriginX
import com.hhs.koto.app.Config.worldW
import com.hhs.koto.stg.CircleCollision
import com.hhs.koto.stg.Collision
import com.hhs.koto.stg.PlayerState
import com.hhs.koto.stg.particle.DeathParticle
import com.hhs.koto.stg.particle.Explosion
import com.hhs.koto.stg.pattern.move
import com.hhs.koto.stg.task.CoroutineTask
import com.hhs.koto.stg.task.EmptyTask
import com.hhs.koto.stg.task.Task
import com.hhs.koto.util.*
import kotlinx.coroutines.CoroutineScope
import ktx.actors.alpha
import ktx.actors.plusAssign
import ktx.collections.GdxArray
import kotlin.math.absoluteValue

abstract class BasicBoss(
    override var x: Float,
    override var y: Float,
    bulletCollisionRadius: Float,
    playerCollisionRadius: Float = bulletCollisionRadius / 2f,
) : Boss {
    var invulnerable: Boolean = false
    val attachedTasks = GdxArray<Task>()
    val bulletCollision = CircleCollision(bulletCollisionRadius)
    val playerCollision = CircleCollision(playerCollisionRadius)
    var rotation: Float = 0f
    var scaleX = 1f
    var scaleY = 1f
    var color: Color = Color.WHITE
    val magicCircle = MagicCircle()
    var useDistortionEffect: Boolean = true

    val bossDistortionEffect = BossDistortionEffect().apply {
        game.backgroundVfx.addEffectRegistered(this)
    }
    override val healthBar = HealthBar(this).apply {
        game.hud.addDrawable(this)
    }
    override val spellAttackCircle = SpellAttackCircle(this).apply {
        game.hud.addDrawable(this)
    }
    val marker = Image(getRegion("ui/bg.png")).apply {
        setSize(72f, 36f)
        game.overlay += this
        color = Color(0f, 1f, 0.5f, 0f)
    }

    abstract val name: String
    open val nameColor = CYAN_HSV
    abstract val texture: TextureRegion
    abstract val width: Float
    abstract val height: Float
    open val textureOriginX: Float
        get() = texture.regionWidth / 2f
    open val textureOriginY: Float
        get() = texture.regionHeight / 2f
    override var alive: Boolean = true

    open fun creationTask(): Task = CoroutineTask {
        x = 300f
        y = 300f
        invulnerable = true
        move(this@BasicBoss, 0f, 0f, 120)
        invulnerable = false
    }

    open fun createSpellBackground(): Task = EmptyTask()

    open fun removeSpellBackground(): Task = EmptyTask()

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

        magicCircle.tick()
        if (x >= -worldOriginX && x <= worldW - worldOriginX) {
            marker.setPosition((x + worldOriginX) / worldW * 864f + 36f, 0f)
            var tmpAlpha = 1f
            tmpAlpha = tmpAlpha.coerceAtMost(lerp(0f, 1f, (x + worldOriginX) / 50f))
            tmpAlpha = tmpAlpha.coerceAtMost(lerp(0f, 1f, (worldW - worldOriginX - x / 50f)))
            tmpAlpha = tmpAlpha.coerceAtMost(lerp(0.5f, 1f, (playerX - x).absoluteValue / 100f))
            marker.alpha = tmpAlpha
        }

        if (useDistortionEffect) {
            if (!bossDistortionEffect.enabled) bossDistortionEffect.start()
            bossDistortionEffect.tick(x, y)
        } else {
            if (bossDistortionEffect.enabled) bossDistortionEffect.end()
        }
        for (i in 0 until attachedTasks.size) {
            if (attachedTasks[i].alive) {
                attachedTasks[i].tick()
            } else {
                attachedTasks[i] = null
            }
        }
        attachedTasks.removeNull()
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        magicCircle.draw(batch, parentAlpha, x, y)

        val tmpColor = batch.color.cpy()
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)
        batch.draw(
            texture,
            x - textureOriginX,
            y - textureOriginY,
            textureOriginX,
            textureOriginY,
            width,
            height,
            scaleX,
            scaleY,
            rotation,
        )
        batch.color = tmpColor
    }

    override fun onHit(damage: Float, isBomb: Boolean) {
        if (!invulnerable) {
            healthBar.damage(damage)
            if (!isBomb) {
                if (healthBar.currentHealth < 500f) {
                    SE.play("damage1")
                } else {
                    SE.play("damage0")
                }
            }
        }
    }

    override fun destroy() {
        // TODO boss death animation
        // TODO play boss death SE "bossdead"
    }

    override fun kill(): Boolean {
        alive = false
        attachedTasks.forEach { it.kill() }
        bossDistortionEffect.end()
        bossDistortionEffect.dispose()
        marker.remove()
        game.backgroundVfx.removeEffectRegistered(bossDistortionEffect)

        return true
    }

    fun attachTask(task: Task): BasicBoss {
        attachedTasks.add(task)
        return this
    }

    fun task(block: suspend CoroutineScope.() -> Unit): BasicBoss {
        attachTask(CoroutineTask(obj = this, block = block))
        return this
    }
}