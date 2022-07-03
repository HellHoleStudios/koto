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
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.hhs.koto.stg.Bounded
import com.hhs.koto.stg.Drawable
import com.hhs.koto.util.A
import com.hhs.koto.util.WHITE_HSV
import com.hhs.koto.util.bundle
import com.hhs.koto.util.clamp
import ktx.collections.GdxArray

class ScoreParticle(
    override var x: Float,
    override var y: Float,
    score: Long,
    var color: Color = WHITE_HSV,
    val glyphScale: Float = 0.5f,
) : Drawable, Bounded {
    override val boundingRadiusX: Float
    override val boundingRadiusY: Float
    protected val glyphWidth: Float
    protected val glyphHeight: Float
    override var alive: Boolean = true
    var deltaY: Float = 3f
    val variant: String = bundle["game.scoreParticle.variant"]
    val atlas: TextureAtlas
    val digits = GdxArray<Int>()
    protected var t: Int = 0

    init {
        if (score < 0) {
            throw IllegalArgumentException("Score cannot be lower than 0!")
        }
        atlas = A["particle/score_particle_hsv.atlas"]
        glyphWidth = atlas.findRegion("0_$variant", 0).regionWidth.toFloat() * glyphScale
        glyphHeight = atlas.findRegion("0_$variant", 0).regionHeight.toFloat() * glyphScale
        val str = score.toString()
        for (i in str) {
            digits.add(i.digitToInt())
        }
        boundingRadiusX = digits.size * glyphWidth / 2f
        boundingRadiusY = glyphHeight / 2f
    }

    override fun tick() {
        t++
        y += deltaY
        deltaY = (deltaY - 0.15f).coerceAtLeast(0f)
        if (t >= 40) kill()
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        val baseX = x - boundingRadiusX / 2
        val baseY = y - boundingRadiusY / 2
        val tmpColor = batch.color.cpy()
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)
        for (i in 0 until digits.size) {
            val stage = getStage(i)
            if (stage != 3) {
                batch.draw(
                    atlas.findRegion("${digits[i]}_$variant", stage),
                    baseX + i * glyphWidth, baseY, glyphWidth, glyphHeight,
                )
            }
        }
        batch.color = tmpColor
    }

    private fun getStage(i: Int): Int =
        clamp(t / 2 - 10 - i, 0, 3)
}