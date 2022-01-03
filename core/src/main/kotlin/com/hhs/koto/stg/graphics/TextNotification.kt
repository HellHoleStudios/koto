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

package com.hhs.koto.stg.graphics

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.utils.Align
import com.hhs.koto.util.WHITE_HSV
import com.hhs.koto.util.bundle
import com.hhs.koto.util.getFont
import com.hhs.koto.util.lerp
import ktx.graphics.copy

class TextNotification(
    text: String,
    val targetY: Float = 150f,
    color: Color = WHITE_HSV,
    fontSize: Int = 48,
    font: String = bundle["font.notification"],
    val duration: Int = 60,
    override val zIndex: Int = 5000,
) : TextDrawable(
    getFont(fontSize, font),
    0.5f,
    text,
    0f,
    targetY + 30f,
    0f,
    Align.center,
    color.copy(alpha = 0f),
) {
    var t: Int = 0

    override fun tick() {
        if (t <= 30) {
            y = targetY + Interpolation.pow5Out.apply(30f, 0f, t / 30f)
            color.a = lerp(0f, 1f, t / 15f)
        } else if (t >= duration) {
            color.a = lerp(1f, 0f, (t - duration) / 30f)
        }
        if (t >= duration + 30) {
            kill()
        }
        t++
    }
}