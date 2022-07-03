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
import com.hhs.koto.util.NO_TINT_HSV
import com.hhs.koto.util.alpha
import com.hhs.koto.util.getRegion
import com.hhs.koto.util.lerp

class Explosion(
    x: Float,
    y: Float,
    val startSizeX: Float,
    val startSizeY: Float = startSizeX,
    val endSizeX: Float,
    val endSizeY: Float = endSizeX,
    rotation: Float = 0f,
    val duration: Int,
    color: Color = NO_TINT_HSV,
) : SpriteDrawable(
    getRegion("particle/explosion.png"),
    x,
    y,
    0f,
    0f,
    startSizeX,
    startSizeY,
    1f,
    1f,
    rotation,
    color = color,
) {
    override fun tick() {
        super.tick()
        sprite.setAlpha((sprite.alpha - 1f / duration).coerceAtLeast(0f))
        sprite.setScale(
            lerp(startSizeX, endSizeX, t.toFloat() / duration),
            lerp(startSizeY, endSizeY, t.toFloat() / duration),
        )
        if (t >= duration) kill()
    }
}