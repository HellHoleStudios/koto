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

package com.hhs.koto.stg.particle

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.hhs.koto.stg.SpriteDrawable
import com.hhs.koto.util.alpha
import com.hhs.koto.util.getRegion
import com.hhs.koto.util.toHSVColor

class DeathParticle(
    x: Float,
    y: Float,
    angle: Float,
    speed: Float,
    val size: Float = 16f,
    rotation: Float = 0f,
    val duration: Int = 20,
    color: Color = Color(0f, 1f, 1f, 1f).toHSVColor(),
    private val additive: Boolean = false,
) : SpriteDrawable(
    getRegion("square_particle.png"),
    x,
    y,
    angle,
    speed,
    size,
    size,
    rotation,
    color,
) {
    init {
        sprite.setSize(1f, 1f)
        sprite.setOriginCenter()
    }

    override fun tick() {
        super.tick()
        speed = (speed - 0.2f).coerceAtLeast(0f)
        sprite.setAlpha((sprite.alpha - 1f / duration).coerceAtLeast(0f))
        sprite.setScale((size * (1f - 1f / duration * t)).coerceAtLeast(0f))
        if (t >= duration) kill()
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (additive) {
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        }
        super.draw(batch, parentAlpha, subFrameTime)
        if (additive) {
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        }
    }
}