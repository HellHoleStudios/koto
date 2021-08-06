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
import com.hhs.koto.stg.bullet.PlayerBullet
import com.hhs.koto.util.game
import com.hhs.koto.util.tmpColor

open class Enemy(
    override var x: Float,
    override var y: Float,
    var hp: Float,
    val texture: BasicPlayerTexture,
    hitRadius: Float,
    var textureOriginX: Float = texture.texture.regionWidth.toFloat() / 2,
    var textureOriginY: Float = texture.texture.regionHeight.toFloat() / 2,
    override val zIndex: Int = -300,
) : Drawable {
    var speed: Float = 0f
    var invulnerable: Boolean = false
    var rotation: Float = 0f
    var scaleX = 1f
    var scaleY = 1f
    var color: Color = Color.WHITE
    val hitCollision = CircleCollision(hitRadius)
    override var alive: Boolean = true

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        tmpColor.set(batch.color)
        batch.color = color
        batch.color.a *= parentAlpha

        if (rotation != 0f || scaleX != 1f || scaleY != 1f) {
            batch.draw(
                texture.texture,
                x - textureOriginX,
                y - textureOriginY,
                textureOriginX,
                textureOriginY,
                texture.texture.regionWidth.toFloat(),
                texture.texture.regionHeight.toFloat(),
                scaleX,
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
        game.playerBullets.forEach {
            if (Collision.collide(it.collision, it.x, it.y, hitCollision, x, y)) {
                it.hit(this)
            }
        }
    }

    open fun death() {

    }
}