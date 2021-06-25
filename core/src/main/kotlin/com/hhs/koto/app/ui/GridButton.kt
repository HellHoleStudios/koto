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
import com.hhs.koto.util.SE
import com.hhs.koto.util.getButtonActivateAction
import com.hhs.koto.util.getButtonDeactivateAction
import com.hhs.koto.util.getUILabelStyle

class GridButton(
    text: CharSequence,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    override val gridX: Int = 0,
    override val gridY: Int = 0,
    override var activeAction: (() -> Action)? = null,
    override var inactiveAction: (() -> Action)? = null,
    activeStyle: LabelStyle,
    inactiveStyle: LabelStyle? = null,
    override var triggerSound: String? = "ok",
    override var runnable: (() -> Unit)? = null
) : Label(text, activeStyle), GridComponent, GridButtonBase {
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
    override var enabled = true
    override var parent: Grid? = null
    override var staticX = 0f
    override var staticY = 0f

    init {
        staticX = x
        staticY = y
        setBounds(x, y, width, height)
    }

    constructor(
        text: CharSequence,
        fontSize: Int,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        gridX: Int,
        gridY: Int,
        triggerSound: String? = "ok",
        runnable: (() -> Unit)? = null
    ) : this(
        text,
        x,
        y,
        width,
        height,
        gridX,
        gridY,
        null,
        null,
        getUILabelStyle(fontSize),
        getUILabelStyle(fontSize),
        triggerSound,
        runnable
    ) {
        activeAction = getButtonActivateAction(this)
        inactiveAction = getButtonDeactivateAction(this)
    }

    override fun trigger() {
        if (triggerSound != null) {
            SE.play(triggerSound!!)
        }
        if (enabled && runnable != null) {
            runnable!!()
        }
    }

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
}