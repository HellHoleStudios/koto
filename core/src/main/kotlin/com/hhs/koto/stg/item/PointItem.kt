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
import com.badlogic.gdx.math.MathUtils.random
import com.hhs.koto.app.Config
import com.hhs.koto.stg.addParticle
import com.hhs.koto.stg.particle.ScoreParticle
import com.hhs.koto.util.game
import com.hhs.koto.util.getRegion

class PointItem(
    x: Float,
    y: Float,
    speed: Float = 2f,
    angle: Float = 90f,
    radius: Float = 10f,
    scaleX: Float = 1f,
    scaleY: Float = 1f,
    color: Color = Color.WHITE,
) : BasicItem(
    x,
    y,
    getRegion("item/point.png"),
    speed,
    angle,
    radius,
    scaleX = scaleX,
    scaleY = scaleY,
    color = color,
) {
    override fun collected(collectPositionX: Float, collectPositionY: Float, autoCollected: Boolean) {
        super.collected(collectPositionX, collectPositionY, autoCollected)
        val amount: Long = if (autoCollected || collectPositionY >= game.maxScoreHeight) {
            game.maxScore
        } else {
            (game.maxScore * 0.9f / (game.maxScoreHeight + Config.originY) *
                    (y + Config.originY) + game.maxScore * 0.1f).toLong()
        }
        game.score += amount
        addParticle(
            ScoreParticle(
                x + random(-20f, 20f),
                y + random(-10f, 10f),
                amount,
                if (amount == game.maxScore) {
                    Color.YELLOW
                } else {
                    Color.WHITE
                },
            )
        )
    }
}