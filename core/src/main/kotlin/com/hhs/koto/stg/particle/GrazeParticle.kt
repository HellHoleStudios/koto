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
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils.random
import com.hhs.koto.stg.Bounded
import com.hhs.koto.stg.Drawable
import com.hhs.koto.util.cos
import com.hhs.koto.util.getRegion
import com.hhs.koto.util.sin
import com.hhs.koto.util.tmpColor

class GrazeParticle(
    override var x: Float,
    override var y: Float,
) : Drawable, Bounded {
    override val boundingWidth: Float = 8f
    override val boundingHeight: Float = 8f
    override var alive: Boolean = true
    private var scale: Float = 1f
    private var angle: Float = random(0f, 180f)
    private var speed: Float = 4f
    private var color: Color = Color(1f, 1f, 1f, 0.8f)
    private var t: Int = 0
    val texture: TextureRegion = getRegion("graze_particle.png")

    override fun tick() {
        t++
        x += cos(angle) * speed
        y += sin(angle) * speed
        speed = (speed - 0.2f).coerceAtLeast(0f)
        scale = (scale - 0.05f).coerceAtLeast(0f)
        if (t >= 40) kill()
        println(color)
    }

    override fun kill(): Boolean {
        alive = false
        return true
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        tmpColor.set(batch.color)
        batch.color = color
        batch.color.a *= parentAlpha
        batch.draw(texture, x - 4f, y - 4f, 8f * scale, 8f * scale)
        batch.color = tmpColor
    }
}