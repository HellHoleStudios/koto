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
import com.hhs.koto.app.Config.worldH
import com.hhs.koto.app.Config.worldOriginX
import com.hhs.koto.app.Config.worldOriginY
import com.hhs.koto.app.Config.worldW
import com.hhs.koto.stg.CircleCollision
import com.hhs.koto.stg.Collision.collide
import com.hhs.koto.stg.GameState
import com.hhs.koto.stg.Player
import com.hhs.koto.stg.PlayerState
import com.hhs.koto.stg.particle.DeathParticle
import com.hhs.koto.stg.particle.Explosion
import com.hhs.koto.stg.task.*
import com.hhs.koto.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.yield
import ktx.collections.GdxArray
import kotlin.math.absoluteValue

open class BasicPlayer(
    val texture: BasicPlayerTexture,
    hitboxTexture: TextureRegion,
    hitRadius: Float,
    var speedHigh: Float,
    var speedLow: Float,
    var deathbombTime: Int,
    grazeRadius: Float = hitRadius * 10f,
    itemRadius: Float = hitRadius * 15f,
    val itemCollectLineHeight: Float = worldH / 4f * 3f - worldOriginY,
    val width: Float = texture.texture.regionWidth.toFloat(),
    val height: Float = texture.texture.regionHeight.toFloat(),
    val textureOriginX: Float = texture.texture.regionWidth / 2f,
    val textureOriginY: Float = texture.texture.regionHeight / 2f,
    val leftMargin: Float = 8f,
    val rightMargin: Float = 8f,
    val bottomMargin: Float = 16f,
    val topMargin: Float = 16f,
    val spawnX: Float = 0f,
    val spawnY: Float = -worldOriginY + 48f,
    override val zIndex: Int = -200,
) : Player {
    override var alive: Boolean = true
    override val hitCollision = CircleCollision(hitRadius)
    override val grazeCollision = CircleCollision(grazeRadius)
    override val itemCollision = CircleCollision(itemRadius)
    override var playerState = PlayerState.NORMAL
    var dx: Int = 0
    var dy: Int = 0
    var speed: Float = 0f
    var invincible: Boolean = false
    var rotation: Float = 0f
    var scaleX = 1f
    var scaleY = 1f
    var color: Color = Color.WHITE
    val attachedTasks = GdxArray<Task>()
    protected val effect = DeathEffect().apply {
        game.vfx.addEffectRegistered(this)
    }
    protected var respawnAnimationPercentage: Float = 0f
    protected var counter: Int = 0
    protected var hitbox = BasicPlayerHitbox(hitboxTexture, this).apply {
        game.stage.addDrawable(this)
    }
    override var x: Float = spawnX
    override var y: Float = spawnY
    var frame: Int = 0

    init {
        game.stage.addDrawable(this)
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        batch.end()

        game.normalBatch.projectionMatrix = batch.projectionMatrix
        game.normalBatch.begin()

        val tmpColor = game.normalBatch.color.cpy()
        game.normalBatch.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        val tmpX: Float = if (playerState == PlayerState.RESPAWNING) {
            smoothstep(spawnX, spawnX, respawnAnimationPercentage)
        } else {
            clampX(x + speed * dx * subFrameTime)
        }
        val tmpY: Float = if (playerState == PlayerState.RESPAWNING) {
            smoothstep(spawnY - 96f, spawnY, respawnAnimationPercentage)
        } else {
            clampY(y + speed * dy * subFrameTime)
        }

        game.normalBatch.draw(
            texture.texture,
            tmpX - textureOriginX,
            tmpY - textureOriginY,
            textureOriginX,
            textureOriginY,
            width,
            height,
            scaleX,
            scaleY,
            rotation,
        )
        game.normalBatch.color = tmpColor

        hitbox.x = clampX(x + speed * dx * subFrameTime)
        hitbox.y = clampY(y + speed * dy * subFrameTime)

        game.normalBatch.end()
        batch.begin()
    }

    override fun tick() {
        frame++
        if (playerState != PlayerState.RESPAWNING) {
            if (playerState != PlayerState.DEATHBOMBING) {
                move()
            }
            if (y >= itemCollectLineHeight) {
                game.items.forEach {
                    it.onCollect(x, y, true)
                }
            } else {
                game.items.forEach {
                    if (collide(it.collision, it.x, it.y, itemCollision, x, y)) {
                        it.onCollect(x, y, false)
                    }
                }
            }
            game.bullets.forEach {
                if (collide(it.collision, it.x, it.y, grazeCollision, x, y)) {
                    it.onGraze()
                }
            }
        }
        if (playerState == PlayerState.NORMAL) {
            if (!invincible && !game.inDialog) {
                var hit = false
                game.bullets.forEach {
                    if (collide(it.collision, it.x, it.y, hitCollision, x, y)) {
                        hit = true
                        if(it.destroyable) {
                            it.kill()
                        }
                    }
                }
                if (hit) {
                    onHit()
                }
            }
        }
        if (!game.inDialog) {
            if (playerState == PlayerState.DEATHBOMBING) {
                if (counter <= 0) {
                    playerState = PlayerState.RESPAWNING
                    onDeath()
                } else if (game.bomb.completedCount > 0 && game.pressed(VK.BOMB)) {
                    onBomb(true)
                    playerState = PlayerState.BOMBING
                }
            } else if (playerState == PlayerState.NORMAL) {
                if (game.bomb.completedCount > 0 && game.pressed(VK.BOMB)) {
                    onBomb(false)
                    playerState = PlayerState.BOMBING
                }
            }
        }
        if (counter > 0) counter--
        for (i in 0 until attachedTasks.size) {
            if (attachedTasks[i].alive) {
                attachedTasks[i].tick()
            } else {
                attachedTasks[i] = null
            }
        }
        attachedTasks.removeNull()
    }

    open fun move() {
        speed = if (game.pressed(VK.SLOW)) {
            speedLow
        } else {
            speedHigh
        }
        dx = 0
        dy = 0
        if (game.pressed(VK.LEFT)) {
            dx--
        }
        if (game.pressed(VK.RIGHT)) {
            dx++
        }
        if (game.pressed(VK.DOWN)) {
            dy--
        }
        if (game.pressed(VK.UP)) {
            dy++
        }
        if (dx.absoluteValue > 0 && dy.absoluteValue > 0) {
            speed /= SQRT2
        }
        x += speed * dx
        y += speed * dy
        x = clampX(x)
        y = clampY(y)
        texture.update(dx)
    }

    open fun onBomb(isDeathBomb: Boolean) {
        game.event.trigger("player.bomb", isDeathBomb)
        if (SystemFlag.replay == null) {
            gameData.bombCount++
            saveGameData()
        }
        invincible = true
        SE.play("bomb")
        game.bomb.removeCompleted(1)
        task {
            repeat(290) {
                color = if (frame % 6 <= 1) {
                    Color.BLUE
                } else {
                    Color.WHITE
                }
                yield()
            }
            color = Color.WHITE
        }

        game.stage.addDrawable(BasicPlayerBomb(playerX, playerY))
        task {
            wait(290)
            playerState = PlayerState.NORMAL
            invincible = false
        }
    }

    override fun onHit() {
        if (invincible || game.inDialog) return
        SE.play("pldead")
        playerState = PlayerState.DEATHBOMBING
        counter = deathbombTime
        task {
            repeat(5) {
                repeat(2) {
                    game.addParticle(
                        DeathParticle(
                            x,
                            y,
                            20f,
                            random(0f, 360f),
                            24f,
                            duration = 10,
                            color = Color(0f, 0f, 0f, 0.8f),
                        )
                    )
                    game.addParticle(
                        DeathParticle(
                            x,
                            y,
                            20f,
                            random(0f, 360f),
                            24f,
                            duration = 10,
                            color = Color(0.85f, 0.35f, 1f, 0.8f),
                            additive = true
                        )
                    )
                }
                yield()
            }
        }
    }

    open fun onDeath() {
        invincible = true
        game.event.trigger("player.death")
        if (SystemFlag.replay == null) {
            gameData.deathCount++
            saveGameData()
        }
        task {
            game.addParticle(Explosion(x, y, 64f, 64f, 384f, 384f, duration = 10))
            task {
                repeat(10) {
                    repeat(3) {
                        game.addParticle(
                            DeathParticle(
                                x,
                                y,
                                20f,
                                random(0f, 360f),
                                96f,
                                duration = 20,
                                color = Color(0.85f, 0.35f, 1f, 0.8f),
                                additive = true
                            )
                        )
                    }
                    wait(2)
                }
            }
            respawnAnimationPercentage = 0f
            effect.start(x, y)
            wait(30)
            if (game.life.completedCount > 0) {
                game.life.removeCompleted(1)
            } else {
                if (game.creditCount < game.maxCredit) {
                    game.maxCredit--
                    game.creditCount++
                    game.score = 0L
                    game.state = GameState.GAME_OVER
                    task {
                        wait(10)
                        game.life.set(game.initialLife)
                        game.bomb.set(game.initialBomb)
                    }
                } else {
                    game.state = GameState.GAME_OVER_NO_CREDIT
                }
            }
            game.bomb.completedCount = game.initialBomb.completedCount
            game.bomb.set(game.initialBomb)
            repeat(70) {
                respawnAnimationPercentage += 1 / 70f
                yield()
            }
            effect.end()
            x = spawnX
            y = spawnY
            respawnAnimationPercentage = 0f
            playerState = PlayerState.RESPAWNED
            repeat(190) {
                color = if (frame % 6 <= 1) {
                    Color.BLUE
                } else {
                    Color.WHITE
                }
                yield()
            }
            color = Color.WHITE
            invincible = false
            playerState = PlayerState.NORMAL
        }
    }

    fun attachTask(task: Task): BasicPlayer {
        attachedTasks.add(task)
        return this
    }

    fun task(block: suspend CoroutineScope.() -> Unit): BasicPlayer {
        attachTask(CoroutineTask(obj = this, block = block))
        return this
    }

    override fun kill(): Boolean {
        alive = false
        attachedTasks.forEach { it.kill() }
        effect.dispose()
        game.vfx.removeEffectRegistered(effect)
        game.stage.removeDrawable(hitbox)
        return true
    }

    fun clampX(x: Float): Float {
        if (playerState == PlayerState.RESPAWNING) return x
        return clamp(x, leftMargin - worldOriginX, worldW - worldOriginX - rightMargin)
    }


    fun clampY(y: Float): Float {
        if (playerState == PlayerState.RESPAWNING) return y
        return clamp(y, bottomMargin - worldOriginY, worldH - worldOriginY - topMargin)
    }

}