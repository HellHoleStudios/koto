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

package com.hhs.koto.stg.drawable

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.Config.worldH
import com.hhs.koto.app.Config.worldOriginY
import com.hhs.koto.stg.Drawable
import com.hhs.koto.util.SE
import com.hhs.koto.util.WHITE_HSV
import com.hhs.koto.util.bundle
import com.hhs.koto.util.getFont

class SpellTimer(
    override var x: Float = 0f,
    override var y: Float = worldH - worldOriginY - 25f,
    override val zIndex: Int = 0,
) : Drawable {
    override var alive: Boolean = true

    val integer = TextDrawable(
        getFont(bundle["font.spellTimer"], 36, Color.RED, 3f, Color.BLACK),
        18f / 36,
        "",
        x,
        y,
        0f,
        Align.right,
        Color(0f, 0f, 1f, 0f),
    )
    val fraction = TextDrawable(
        getFont(bundle["font.spellTimer"], 18, Color.RED, 3f, Color.BLACK),
        9f / 18,
        "",
        x,
        y - 7f,
        0f,
        Align.left,
        Color(0f, 0f, 1f, 0f),
    )
    var time: Int = 0
    var warningTime: Int = 600
    var visible: Boolean = false

    fun show(time: Int, warningTime: Int = 600) {
        this.time = time
        this.warningTime = warningTime
        visible = true
    }

    fun hide() {
        this.time = 0
        this.warningTime = 600
        visible = false
    }

    fun tickTime() {
        time = (time - 1).coerceAtLeast(0)
        if (time <= warningTime) {
            integer.color = Color(0f, 1f, 0.85f, 1f)
            fraction.color = Color(0f, 1f, 0.85f, 1f)
            if (time % 60 == 0) {
                SE.play("timeout")
            }
        } else {
            integer.color = WHITE_HSV
            fraction.color = WHITE_HSV
        }
        val text = String.format("%.2f", time / 60f)
        val index = text.indexOf(".")
        integer.text = text.substring(0, index + 1)
        fraction.text = text.substring(index + 1)
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        integer.draw(batch, parentAlpha, subFrameTime)
        fraction.draw(batch, parentAlpha, subFrameTime)
    }

    override fun tick() {
        if (visible) {
            integer.color.a = (integer.color.a + 0.03f).coerceAtMost(1f)
            fraction.color.a = (fraction.color.a + 0.03f).coerceAtMost(1f)
        } else {
            integer.color.a = (integer.color.a - 0.03f).coerceAtLeast(0f)
            fraction.color.a = (fraction.color.a - 0.03f).coerceAtLeast(0f)
        }
        integer.x = x
        integer.y = y
        fraction.x = x
        fraction.y = y - 7f
        integer.tick()
        fraction.tick()
    }
}