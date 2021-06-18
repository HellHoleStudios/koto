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

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.hhs.koto.util.getUILabelStyle
import java.util.concurrent.Callable

class GridButton(
    initialText: CharSequence,
    fontSize: Int,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    override val gridX: Int = 0,
    override val gridY: Int = 0,
    var hasSound: Boolean = true,
    var activeAction: (() -> Action)? = null,
    var inactiveAction: (() -> Action)? = null,
    activeStyle: LabelStyle,
    inactiveStyle: LabelStyle? = null,
    var runnable: (() -> Unit)? = null
) : Label(initialText, activeStyle), GridComponent {
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
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        gridX: Int,
        gridY: Int,
        runnable: (() -> Unit)? = null
    ) : this(
        text,
        fontSize,
        x,
        y,
        width,
        height,
        gridX,
        gridY,
        true,
        null,
        null,
        getUILabelStyle(fontSize),
        getUILabelStyle(fontSize),
        runnable
    ) {
        activeAction = {
            Actions.parallel(
                Actions.sequence(
                    Actions.color(Color.WHITE),
                    Actions.moveTo(staticX + 2, staticY, 0.03f, Interpolation.sine),
                    Actions.moveTo(staticX - 4, staticY, 0.06f, Interpolation.sine),
                    Actions.moveTo(staticX, staticY, 0.03f, Interpolation.sine)
                ),
                Actions.forever(
                    Actions.sequence(
                        Actions.color(Color(0.9f, 0.9f, 0.9f, 1f), 0.5f),
                        Actions.color(Color.WHITE, 0.5f)
                    )
                )
            )
        }
        inactiveAction = {
            Actions.parallel(
                Actions.alpha(1f),
                Actions.moveTo(staticX, staticY, 0.1f, Interpolation.sine),
                Actions.color(Color(0.7f, 0.7f, 0.7f, 1f))
            )
        }
    }

    override fun trigger() {
        if (enabled && runnable != null) {
            runnable!!()
        }
    }

    override fun update() {
        if (active) {
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

    fun activate(): GridButton {
        active = true
        return this
    }

    fun deactivate(): GridButton {
        active = false
        return this
    }

    fun enable(): GridButton {
        enabled = true
        return this
    }

    fun disable(): GridButton {
        enabled = false
        return this
    }
}