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

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import kotlin.math.absoluteValue

class HSVColorAction : TemporalAction() {
    private var startH = 0f
    private var startS = 0f
    private var startV = 0f
    private var startA = 0f

    var color: Color? = null
    private val end = Color()

    override fun begin() {
        if (color == null) color = target.color
        startH = color!!.r
        startS = color!!.g
        startV = color!!.b
        startA = color!!.a
    }

    override fun update(percent: Float) {
        when (percent) {
            0f -> color!!.set(startH, startS, startV, startA)
            1f -> color!!.set(end)
            else -> {
                val h = interpolateH(startH, end.r, percent)
                val s = startS + (end.g - startS) * percent
                val v = startV + (end.b - startV) * percent
                val a = startA + (end.a - startA) * percent
                color!!.set(h, s, v, a)
            }
        }
    }

    override fun reset() {
        super.reset()
        color = null
    }

    var endColor: Color?
        get() = end
        set(color) {
            end.set(color)
        }

    fun interpolateH(startH: Float, endH: Float, percent: Float): Float {
        if ((endH - startH).absoluteValue <= 0.5) {
            return startH + (endH - startH) * percent
        } else {
            if (endH < startH) {
                return (startH + (endH + 1f - startH) * percent) % 1f
            } else {
                return (startH + (endH - 1f - startH) * percent + 1f) % 1f
            }
        }
    }
}

fun hsvColor(color: Color): HSVColorAction {
    return hsvColor(color, 0f, null)
}

fun hsvColor(color: Color, duration: Float): HSVColorAction {
    return hsvColor(color, duration, null)
}

fun hsvColor(color: Color, duration: Float, interpolation: Interpolation?): HSVColorAction {
    val action = Actions.action(HSVColorAction::class.java)
    action.endColor = color
    action.duration = duration
    action.interpolation = interpolation
    return action
}