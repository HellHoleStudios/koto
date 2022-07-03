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

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction
import com.hhs.koto.util.SE
import com.hhs.koto.util.bundle
import ktx.actors.then

class ConfirmationMenu(
    yesRunnable: (() -> Unit)? = null,
    noRunnable: (() -> Unit)? = null,
    var exitRunnable: (() -> Unit)? = null,
    text: String = bundle["ui.confirmation"],
    yesText: String = bundle["ui.yes"],
    noText: String = bundle["ui.no"],
    gridX: Int = 0,
    gridY: Int = 0,
    cycle: Boolean = true,
    staticX: Float,
    staticY: Float,
    width: Float = 0f,
    height: Float = 0f,
    activeAction: (() -> Action)? = null,
    inactiveAction: (() -> Action)? = null,
    selectSound: String? = "select",
) : Grid(gridX, gridY, cycle, staticX, staticY, width, height, selectSound = selectSound) {
    val label = GridLabel(text, 48, gridX = 0, gridY = 0, staticX = -20f, staticY = 120f)
    val yesButton = GridButton(yesText, 36, gridX = 0, gridY = 1, staticY = 55f, runnable = yesRunnable)
    val noButton = GridButton(
        noText, 36, gridX = 0, gridY = 2, staticY = 0f, runnable = noRunnable,
        triggerSound = "cancel"
    )

    var yesRunnable
        get() = yesButton.runnable
        set(value) {
            yesButton.runnable = value
        }
    var noRunnable
        get() = noButton.runnable
        set(value) {
            noButton.runnable = value
        }

    init {
        this.activeAction = activeAction ?: getActiveAction()
        this.inactiveAction = inactiveAction ?: getInactiveAction()
        add(label)
        add(yesButton)
        add(noButton)
        selectLast()
    }

    override fun exit() {
        super.exit()
        SE.play("cancel")
        if (exitRunnable != null) exitRunnable!!()
    }

    fun getActiveAction(vararg actions: () -> Action): () -> Action = {
        val ret = ParallelAction()
        ret.addAction(moveTo(staticX, staticY, 1f, Interpolation.pow5Out))
        ret.addAction(show() then fadeIn(1f, Interpolation.pow5Out))
        for (action in actions) {
            ret.addAction(action())
        }
        ret
    }

    fun getInactiveAction(vararg actions: () -> Action): () -> Action = {
        val ret = ParallelAction()
        ret.addAction(moveTo(staticX - 200f, staticY, 1f, Interpolation.pow5Out))
        ret.addAction(fadeOut(1f, Interpolation.pow5Out) then hide())
        for (action in actions) {
            ret.addAction(action())
        }
        ret
    }
}