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

package com.hhs.koto.demo.player

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.hhs.koto.stg.SpriteDrawable
import com.hhs.koto.util.alpha
import com.hhs.koto.util.ensureBlendFunction
import com.hhs.koto.util.getRegion

class AmuletParticle(
    x: Float,
    y: Float,
    val duration: Int = 10,
) : SpriteDrawable(
    getRegion("reimu_amulet_particle.png"),
    x,
    y,
    0f,
    0f,
    1f,
    1f,
    16f,
    16f,
    90f,
) {
    override fun tick() {
        super.tick()
        sprite.setAlpha((sprite.alpha - 1f / duration).coerceAtLeast(0f))
        if (t >= duration) kill()
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        batch.ensureBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        super.draw(batch, parentAlpha, subFrameTime)
        batch.ensureBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }
}