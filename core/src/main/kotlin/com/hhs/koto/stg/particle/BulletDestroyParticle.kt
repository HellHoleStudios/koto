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
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.hhs.koto.stg.graphics.SpriteDrawable
import com.hhs.koto.util.BlendingMode
import com.hhs.koto.util.alpha

class BulletDestroyParticle(
    region: TextureRegion,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    scaleX: Float,
    scaleY: Float,
    color: Color,
    rotation: Float,
    val duration: Int = 10,
) : SpriteDrawable(
    region,
    x,
    y,
    0f,
    0f,
    scaleX,
    scaleY,
    width,
    height,
    rotation,
    width / 2f,
    height / 2f,
    color,
) {
    override val blending: BlendingMode = BlendingMode.ADD

    init {
        sprite.alpha = 0.5f
    }

    override fun tick() {
        super.tick()
        sprite.setScale(sprite.scaleX - 1f / duration)
        sprite.alpha -= 0.5f / duration
        if (t >= duration) kill()
    }
}