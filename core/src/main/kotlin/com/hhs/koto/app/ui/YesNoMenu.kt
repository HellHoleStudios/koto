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

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions

class YesNoMenu(
    var yesRunnable: () -> Unit,
    var noRunnable: () -> Unit,
    text: String = "Really?",
    yesText: String = "Yes!",
    noText: String = "No!",
    defaultNo: Boolean = false,
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

    val label = GridLabel(text, 48, gridX = 0, gridY = 0, staticY = 130f)
    val yesButton = GridButton(yesText, 36, gridX = 0, gridY = 1, staticY = 50f, runnable = yesRunnable)
    val noButton = GridButton(noText, 36, gridX = 0, gridY = 2, staticY = 0f, runnable = noRunnable)

    init {
        this.activeAction = activeAction ?: {
            clearActions()
            Actions.parallel(
                Actions.moveTo(staticX, staticY, 1f, Interpolation.pow5Out),
                Actions.fadeIn(1f, Interpolation.pow5Out),
            )
        }
        this.inactiveAction = inactiveAction ?: {
            clearActions()
            Actions.parallel(
                Actions.moveTo(staticX + 100f, staticY, 1f, Interpolation.pow5Out),
                Actions.fadeOut(1f, Interpolation.pow5Out),
            )
        }
        add(label)
        add(yesButton)
        add(noButton)
        if (defaultNo) {
            selectLast()
        } else {
            selectFirst()
        }
    }
}