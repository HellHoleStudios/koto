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
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.hhs.koto.util.SE
import com.hhs.koto.util.darken
import com.hhs.koto.util.getUILabelStyle

class GridButton(
    text: CharSequence,
    override val gridX: Int = 0,
    override val gridY: Int = 0,
    override var staticX: Float = 0f,
    override var staticY: Float = 0f,
    width: Float? = null,
    height: Float? = null,
    override var activeAction: (() -> Action)? = null,
    override var inactiveAction: (() -> Action)? = null,
    activeStyle: LabelStyle,
    inactiveStyle: LabelStyle? = null,
    override var enabled: Boolean = true,
    override var triggerSound: String? = "ok",
    override var ignoreParent: Boolean = false,
    override var runnable: (() -> Unit)? = null,
) : Label(text, inactiveStyle ?: activeStyle), GridButtonBase {
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
    override var active = false
        set(value) {
            field = value
            update()
        }
    override var parent: Grid? = null

    init {
        setPosition(staticX, staticY)
        if (width != null) setWidth(width)
        if (height != null) setHeight(height)
    }

    constructor(
        text: CharSequence,
        fontSize: Int,
        gridX: Int = 0,
        gridY: Int = 0,
        staticX: Float = 0f,
        staticY: Float = 0f,
        width: Float? = null,
        height: Float? = null,
        triggerSound: String? = "ok",
        ignoreParent: Boolean = false,
        enabled: Boolean = true,
        runnable: (() -> Unit)? = null,
    ) : this(
        text,
        gridX,
        gridY,
        staticX,
        staticY,
        width,
        height,
        null,
        null,
        getUILabelStyle(fontSize),
        getUILabelStyle(fontSize),
        enabled,
        triggerSound,
        ignoreParent,
        runnable,
    ) {
        activeAction = getActiveAction()
        inactiveAction = getInactiveAction()
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
        if ((parent?.active != false || ignoreParent) && active && enabled) {
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
            style = LabelStyle(inactiveStyle.font, darken(inactiveStyle.fontColor))
        }
    }

    fun getActiveAction(vararg actions: () -> Action): () -> Action = {
        val ret = ParallelAction()
        ret.addAction(
            sequence(
                color(Color.WHITE),
                moveTo(staticX - 10, staticY, 1f, Interpolation.pow5Out),
            )
        )
        ret.addAction(
            forever(
                sequence(
                    color(darken(Color.WHITE, 0.9f), 0.5f),
                    color(Color.WHITE, 0.5f),
                )
            )
        )
        for (action in actions) {
            ret.addAction(action())
        }
        ret
    }

    fun getInactiveAction(vararg actions: () -> Action): () -> Action = {
        val ret = ParallelAction()
        ret.addAction(
            moveTo(staticX, staticY, 1f, Interpolation.pow5Out)
        )
        ret.addAction(
            color(darken(Color.WHITE, 0.7f))
        )
        for (action in actions) {
            ret.addAction(action())
        }
        ret
    }
}