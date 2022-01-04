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

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.hhs.koto.stg.Drawable
import com.hhs.koto.stg.Player
import com.hhs.koto.stg.PlayerState
import com.hhs.koto.util.VK
import com.hhs.koto.util.alpha
import com.hhs.koto.util.game

class BasicPlayerHitbox(
    hitboxTexture: TextureRegion,
    protected var player: Player,
    width: Float = 64f,
    height: Float = width,
    override val zIndex: Int = 1000,
) : Drawable {
    override var alive: Boolean = true
    override var x: Float = player.x
    override var y: Float = player.y
    protected var sprite = Sprite(hitboxTexture).apply {
        setOriginCenter()
        setSize(width, height)
        alpha = 0f
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        sprite.setOriginBasedPosition(x, y)
        sprite.draw(batch, parentAlpha)
        sprite.rotation = -sprite.rotation
        sprite.draw(batch, parentAlpha)
        sprite.rotation = -sprite.rotation
    }

    override fun tick() {
        sprite.rotate(4f)
        if (sprite.rotation >= 360f) sprite.rotation -= 360f
        if (player.playerState != PlayerState.RESPAWNING) {
            if (game.pressed(VK.SLOW)) {
                sprite.alpha = 1f
                sprite.setScale((sprite.scaleX - 0.02f).coerceAtLeast(1f))
            } else {
                sprite.alpha = 0f
                sprite.setScale(1.3f)
            }
        }
    }
}