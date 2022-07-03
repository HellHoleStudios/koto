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

package com.hhs.koto.stg.particle

import com.badlogic.gdx.graphics.Color
import com.hhs.koto.stg.graphics.SpriteDrawable
import com.hhs.koto.util.BLACK_HSV
import com.hhs.koto.util.BlendingMode
import com.hhs.koto.util.alpha
import com.hhs.koto.util.getRegion

class DeathParticle(
    x: Float,
    y: Float,
    speed: Float,
    angle: Float,
    val size: Float = 16f,
    rotation: Float = 0f,
    val duration: Int = 20,
    color: Color = BLACK_HSV,
    additive: Boolean = false,
) : SpriteDrawable(
    getRegion("particle/death_particle.png"),
    x,
    y,
    speed,
    angle,
    size,
    size,
    1f,
    1f,
    rotation,
    color = color,
) {
    override val blending: BlendingMode = if (additive) {
        BlendingMode.ADD
    } else {
        BlendingMode.ALPHA
    }

    override fun tick() {
        super.tick()
        speed = (speed - 0.2f).coerceAtLeast(0f)
        sprite.setAlpha((sprite.alpha - 1f / duration).coerceAtLeast(0f))
        sprite.setScale((size * (1f - 1f / duration * t)).coerceAtLeast(0f))
        if (t >= duration) kill()
    }
}