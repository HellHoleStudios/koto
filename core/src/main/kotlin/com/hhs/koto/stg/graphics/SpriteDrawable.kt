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
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.hhs.koto.stg.Bounded
import com.hhs.koto.stg.Drawable
import com.hhs.koto.util.alpha
import com.hhs.koto.util.cos
import com.hhs.koto.util.sin

open class SpriteDrawable(
    texture: TextureRegion,
    override var x: Float,
    override var y: Float,
    var speed: Float,
    var angle: Float,
    scaleX: Float,
    scaleY: Float,
    width: Float,
    height: Float,
    rotation: Float,
    originX: Float = width / 2f,
    originY: Float = height / 2f,
    color: Color = Color.WHITE,
) : Drawable, Bounded {
    protected val sprite = Sprite(texture).apply {
        setOrigin(originX, originY)
        setOriginBasedPosition(x, y)
        setSize(width, height)
        setScale(scaleX, scaleY)
        setColor(color)
        setRotation(rotation)
    }
    protected var t: Int = 0
    override val boundingRadiusX: Float
        get() = sprite.width * sprite.scaleX + sprite.height * sprite.scaleY
    override val boundingRadiusY: Float
        get() = sprite.width * sprite.scaleX + sprite.height * sprite.scaleY
    override var alive: Boolean = true

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (sprite.alpha < 0.001f) return

        sprite.setOriginBasedPosition(
            x + subFrameTime * cos(angle) * speed,
            y + subFrameTime * cos(angle) * speed,
        )
        sprite.draw(batch, parentAlpha)
    }

    override fun tick() {
        t++
        x += cos(angle) * speed
        y += sin(angle) * speed
    }
}