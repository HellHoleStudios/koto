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

package com.hhs.koto.app.ui

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.hhs.koto.util.getUILabelStyle


class GridLabel(
    text: CharSequence,
    fontSize: Int,
    override val gridX: Int,
    override val gridY: Int,
    x: Float,
    y: Float,
    width: Float = 1024f,
    height: Float = 1024f,
    var activeAction: (() -> Action)? = null,
    var inactiveAction: (() -> Action)? = null,
    activeStyle: LabelStyle = getUILabelStyle(fontSize),
    inactiveStyle: LabelStyle? = getUILabelStyle(fontSize),
) : Label(text, activeStyle), GridComponent {
    var activeStyle: LabelStyle = activeStyle
        set(value) {
            field = value
            update()
        }
    var inactiveStyle: LabelStyle = inactiveStyle ?: activeStyle
        set(value) {
            field = value
            update()
        }
    override var active = true
        set(value) {
            field = value
            update()
        }

    @Suppress("SetterBackingFieldAssignment", "UNUSED_PARAMETER")
    override var enabled = false
        set(value) = Unit
    override var parent: Grid? = null
    var staticX = 0f
    var staticY = 0f

    init {
        staticX = x
        staticY = y
        setBounds(x, y, width, height)
    }

    constructor(
        text: CharSequence,
        fontSize: Int,
        gridX: Int,
        gridY: Int,
        x: Float,
        y: Float,
        width: Float = 1024f,
        height: Float = 1024f,
    ) : this(
        text,
        fontSize,
        gridX,
        gridY,
        x,
        y,
        width,
        height,
        null,
        null,
        getUILabelStyle(fontSize),
        getUILabelStyle(fontSize),
    )

    override fun update() {
        if (active && enabled) {
            style = activeStyle
            actions.clear()
            if (activeAction != null) {
                addAction(activeAction!!())
            }
        } else {
            style = inactiveStyle
            actions.clear()
            if (inactiveAction != null) {
                addAction(inactiveAction!!())
            }
        }
        if (!enabled) {
            style = LabelStyle(inactiveStyle.font, inactiveStyle.fontColor.cpy().mul(0.5f, 0.5f, 0.5f, 1f))
        }
    }

    override fun trigger() = Unit
}
