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

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.hhs.koto.stg.Drawable
import com.hhs.koto.util.*

class SpellAttackOverlay(
    val duration: Int = 60,
    override var x: Float = 0f,
    override var y: Float = 0f,
    var rotation: Float = 30f,
    override val zIndex: Int = 100,
) : Drawable {
    var t: Int = 0
    val sprite = Sprite(getRegion("ui/spell_attack_text.png")).apply {
        setSize(128f, 16f)
    }
    var offset: Float = 0f
    override var alive: Boolean = true

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (sprite.alpha < 0.001f) return

        val xOffset = 128f * cos(rotation)
        val yOffset = 128f * sin(rotation)
        sprite.rotation = rotation
        for (row in -4..5) {
            for (i in -3..3) {
                val offsetFactor = i + if (row % 2 == 0) offset else -offset
                sprite.setOriginBasedPosition(
                    x + xOffset * offsetFactor,
                    y + yOffset * offsetFactor + row * 30f,
                )
                sprite.draw(batch, parentAlpha)
            }
        }
    }

    override fun tick() {
        if (t >= duration) {
            sprite.alpha = lerp(1f, 0f, (t - duration) / 40f)
        }
        if (t >= duration + 40) kill()
        offset = (offset + 0.015f) % 1f
        t++
    }
}