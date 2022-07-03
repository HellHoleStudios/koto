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

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.Config.worldOriginX
import com.hhs.koto.app.Config.worldOriginY
import com.hhs.koto.app.Config.worldW
import com.hhs.koto.util.bundle
import com.hhs.koto.util.getFont
import com.hhs.koto.util.lerp

class BGMNameDisplay(bgmId: Int, override val zIndex: Int = 100) : TextDrawable(
    getFont(24, bundle["font.BGMNameDisplay"]),
    12f / 24,
    String.format(bundle["game.bgmDisplay"], bundle["music.$bgmId.title"]),
    worldW - worldOriginX - 10f,
    -worldOriginY - 15f,
    halign = Align.right,
    targetWidth = 0f,
) {
    var t: Int = 0

    override fun tick() {
        if (t <= 30) {
            y = -worldOriginY + Interpolation.pow5Out.apply(0f, 15f, t / 30f)
        } else if (t >= 90) {
            color.a = lerp(1f, 0f, (t - 90) / 30f)
        } else if (t >= 120) {
            kill()
        }
        t++
    }
}