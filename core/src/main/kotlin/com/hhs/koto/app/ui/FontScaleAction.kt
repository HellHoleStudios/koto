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

package com.hhs.koto.app.ui

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import com.badlogic.gdx.scenes.scene2d.ui.Label

class FontScaleAction : TemporalAction() {
    var startX = 0f
    var startY = 0f
    var endX = 0f
    var endY = 0f

    override fun begin() {
        val t = target as Label
        startX = t.fontScaleX
        startY = t.fontScaleY
    }

    override fun update(percent: Float) {
        val t = target as Label
        when (percent) {
            0f -> t.setFontScale(startX, startY)
            1f -> t.setFontScale(endX, endY)
            else -> t.setFontScale(
                startX + (endX - startX) * percent,
                startY + (endY - startY) * percent,
            )
        }
    }
}

fun fontScaleTo(x: Float, y: Float, duration: Float): FontScaleAction {
    val act = Actions.action(FontScaleAction::class.java)
    act.duration = duration
    act.endX = x
    act.endY = y
    return act
}
