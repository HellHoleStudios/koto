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
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.hhs.koto.util.NO_TINT_HSV
import com.hhs.koto.util.alpha
import com.hhs.koto.util.lerp

class Cutin(
    texture: TextureRegion,
    width: Float = 300f,
    height: Float = width / texture.regionWidth * texture.regionHeight,
    val targetX: Float = 100f,
    val targetY: Float = -50f,
    val startOffsetX: Float = 0f,
    val startOffsetY: Float = -100f,
    val endOffsetX: Float = 0f,
    val endOffsetY: Float = 200f,
    val sustainSpeedX: Float = 0f,
    val sustainSpeedY: Float = 1f,
    val fadeInTime: Int = 20,
    val sustainTime: Int = 60,
    val fadeOutTime: Int = 20,
    val color: Color = NO_TINT_HSV,
    override val zIndex: Int = -300,
) : SpriteDrawable(
    texture,
    targetX + startOffsetX,
    targetY + startOffsetY,
    0f,
    0f,
    1f,
    1f,
    width,
    height,
    0f,
    color = color,
) {
    init {
        sprite.alpha = 0f
    }

    override fun tick() {
        super.tick()
        if (t <= fadeInTime + sustainTime + fadeOutTime) {
            x = targetX + (t - fadeInTime) * sustainSpeedX
            y = targetY + (t - fadeInTime) * sustainSpeedY
            if (t <= fadeInTime) {
                x += Interpolation.sineOut.apply(
                    startOffsetX + sustainSpeedX * fadeInTime, 0f, t.toFloat() / fadeInTime
                )
                y += Interpolation.sineOut.apply(
                    startOffsetY + sustainSpeedY * fadeInTime, 0f, t.toFloat() / fadeInTime
                )
                sprite.setColor(
                    color.r,
                    color.g,
                    color.b,
                    color.a * lerp(0f, 1f, t.toFloat() / fadeInTime),
                )
            } else if (t > fadeInTime + sustainTime) {
                x += Interpolation.sineIn.apply(
                    0f, endOffsetX - sustainSpeedX * fadeOutTime,
                    (t - fadeInTime - sustainTime).toFloat() / fadeOutTime,
                )
                y += Interpolation.sineIn.apply(
                    0f, endOffsetY - sustainSpeedY * fadeOutTime,
                    (t - fadeInTime - sustainTime).toFloat() / fadeOutTime,
                )
                sprite.setColor(
                    color.r,
                    color.g,
                    color.b,
                    color.a * lerp(
                        1f, 0f,
                        (t - fadeInTime - sustainTime).toFloat() / fadeOutTime,
                    ),
                )
            }
        } else {
            kill()
        }
    }
}