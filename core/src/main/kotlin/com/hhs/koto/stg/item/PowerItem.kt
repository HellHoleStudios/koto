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

package com.hhs.koto.stg.item

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.hhs.koto.stg.particle.ScoreParticle
import com.hhs.koto.util.game
import com.hhs.koto.util.getRegion

class PowerItem(
    x: Float,
    y: Float,
    speed: Float = 2f,
    angle: Float = 90f,
    radius: Float = 10f,
    scaleX: Float = 1f,
    scaleY: Float = 1f,
    val amount: Float = 0.01f,
    color: Color = Color.WHITE,
) : BasicItem(
    x,
    y,
    getRegion("item/power.png"),
    16f,
    16f,
    speed,
    angle,
    radius,
    scaleX = scaleX,
    scaleY = scaleY,
    color = color,
) {
    override fun onCollected(collectPositionX: Float, collectPositionY: Float, autoCollected: Boolean) {
        super.onCollected(collectPositionX, collectPositionY, autoCollected)
        if (game.power >= game.maxPower) {
            game.score += 10000
            game.addParticle(
                ScoreParticle(
                    x + MathUtils.random(-20f, 20f),
                    y + MathUtils.random(-10f, 10f),
                    10000
                )
            )
        } else {
            game.score += 10
            game.addParticle(
                ScoreParticle(
                    x + MathUtils.random(-20f, 20f),
                    y + MathUtils.random(-10f, 10f),
                    10
                )
            )
            game.power = (game.power + amount).coerceAtMost(game.maxPower)
        }
    }
}