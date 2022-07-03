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
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.Config.worldH
import com.hhs.koto.app.Config.worldOriginY
import com.hhs.koto.stg.Drawable
import com.hhs.koto.util.*

class SpellTimer(
    override var x: Float = 0f,
    override var y: Float = worldH - worldOriginY - 30f,
    override val zIndex: Int = 1000,
) : Drawable {
    override var alive: Boolean = true
    var scale: Float = 1f

    val integer = TextDrawable(
        getFont(40, bundle["font.spellTimer"], borderWidth = 3f, borderColor = Color.BLACK),
        20f / 40,
        "",
        x + 3f,
        y,
        0f,
        Align.right,
        Color(0f, 0f, 1f, 0f),
    )
    val fraction = TextDrawable(
        getFont(20, bundle["font.spellTimer"], borderWidth = 3f, borderColor = Color.BLACK),
        10f / 20,
        "",
        x + 3f,
        y - 7f,
        0f,
        Align.left,
        Color(0f, 0f, 1f, 0f),
    )
    var time: Int = 0
    var visible: Boolean = false

    fun show(time: Int) {
        scale = 1f
        this.time = time
        visible = true
    }

    fun hide() {
        this.time = 0
        visible = false
    }

    fun tickTime() {
        time = (time - 1).coerceAtLeast(0)
        if (time <= options.fps * 10) {
            integer.color = Color(0f, 1f, 0.85f, 1f)
            fraction.color = Color(0f, 1f, 0.85f, 1f)
            if (time % options.fps == 0 && time > 0) {
                if (time <= options.fps * 5) {
                    SE.play("timeout1")
                } else {
                    SE.play("timeout0")
                }
            }
        } else {
            integer.color = WHITE_HSV
            fraction.color = WHITE_HSV
        }
        val splitResult = splitDecimal(time.toFloat() / options.fps)
        integer.text = splitResult.integer
        fraction.text = splitResult.fraction
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        val distanceAlpha = lerp(
            0.1f, 1f,
            Rectangle(x - 20f, y - 20f, 40f, 40f).distanceTo(playerX, playerY) / 30f,
        )

        var oldScale = integer.fontScale
        integer.fontScale *= scale
        integer.draw(batch, parentAlpha * distanceAlpha, subFrameTime)
        integer.fontScale = oldScale

        oldScale = fraction.fontScale
        fraction.fontScale *= scale
        fraction.draw(batch, parentAlpha * distanceAlpha, subFrameTime)
        fraction.fontScale = oldScale
    }

    override fun tick() {
        if (visible) {
            integer.color.a = (integer.color.a + 0.03f).coerceAtMost(1f)
            fraction.color.a = (fraction.color.a + 0.03f).coerceAtMost(1f)
        } else {
            integer.color.a = (integer.color.a - 0.03f).coerceAtLeast(0f)
            fraction.color.a = (fraction.color.a - 0.03f).coerceAtLeast(0f)
        }
        if (time <= 600) {
            scale = Interpolation.pow5Out.apply(1.3f, 1f, (60 - time % 60) / 60f)
        }
        integer.x = x + 3f
        integer.y = y
        fraction.x = x + 3f
        fraction.y = y - 7f
        integer.tick()
        fraction.tick()
    }
}