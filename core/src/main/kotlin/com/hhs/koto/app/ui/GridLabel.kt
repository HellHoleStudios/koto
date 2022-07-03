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

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.hhs.koto.util.getUILabelStyle


class GridLabel(
    text: CharSequence,
    override val gridX: Int = 0,
    override val gridY: Int = 0,
    override var staticX: Float = 0f,
    override var staticY: Float = 0f,
    width: Float = 512f,
    height: Float = 512f,
    var action: (() -> Action)? = null,
    style: LabelStyle,
) : Label(text, style), GridComponent {
    override var active = true

    @Suppress("SetterBackingFieldAssignment", "UNUSED_PARAMETER")
    override var enabled = false
        set(value) = Unit

    override var parent: Grid? = null

    init {
        setBounds(staticX, staticY, width, height)
        if (action != null) {
            addAction(action!!())
        }
    }

    constructor(
        text: CharSequence,
        fontSize: Int,
        gridX: Int = 0,
        gridY: Int = 0,
        staticX: Float = 0f,
        staticY: Float = 0f,
        width: Float = 512f,
        height: Float = fontSize.toFloat(),
    ) : this(
        text,
        gridX,
        gridY,
        staticX,
        staticY,
        width,
        height,
        null,
        getUILabelStyle(fontSize),
    )

    override fun update() = Unit

    override fun trigger() = Unit
}
