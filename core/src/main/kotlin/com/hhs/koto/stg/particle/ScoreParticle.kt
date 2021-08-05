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
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.hhs.koto.app.Config
import com.hhs.koto.stg.Drawable
import com.hhs.koto.util.*
import ktx.collections.GdxArray

class ScoreParticle(
    override var x: Float,
    override var y: Float,
    score: Long,
    var color: Color = Color.WHITE,
) : Drawable {
    override val boundingWidth: Float
    override val boundingHeight: Float = 9f
    val glyphWidth: Int
    override var alive: Boolean = true
    val variant: String = bundle["game.scoreParticle.variant"]
    val atlas: TextureAtlas
    val digits = GdxArray<Int>()
    var t: Int = 0

    init {
        if (Config.useHSVShader) {
            atlas = A["score_particle_hsv.atlas"]
            color = color.toHSVColor()
        } else {
            atlas = A["score_particle.atlas"]
        }
        glyphWidth = atlas.findRegion("0_$variant", 0).regionWidth
        val str = score.toString()
        for (i in str) {
            digits.add(i.code - '0'.code)
        }
        boundingWidth = digits.size * glyphWidth / 2f
    }

    override fun tick() {
        t++
        y += 1f
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        val baseX = x - boundingWidth / 2
        val baseY = y - boundingHeight / 2
        tmpColor.set(batch.color)
        batch.color = color
        batch.color.a *= parentAlpha
        for (i in 0 until digits.size) {
            val stage = getStage(i)
            if (stage != 3) {
                batch.draw(
                    atlas.findRegion("${digits[i]}_$variant", stage),
                    baseX + i * glyphWidth / 2f, baseY, glyphWidth / 2f, 9f,
                )
            }
        }
        batch.color = tmpColor
    }

    private fun getStage(i: Int): Int =
        clamp(t / 2 - 10 - i, 0, 3)
}