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
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.hhs.koto.util.SE

class GridImage(
    texture: TextureRegion,
    override val gridX: Int = 0,
    override val gridY: Int = 0,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    override var activeAction: (() -> Action)? = null,
    override var inactiveAction: (() -> Action)? = null,
    override var triggerSound: String? = "ok",
    override var runnable: (() -> Unit)? = null,
) : Image(texture), GridComponent, GridButtonBase {
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
        texture: TextureRegion,
        gridX: Int,
        gridY: Int,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        triggerSound: String? = "ok",
        runnable: (() -> Unit)? = null,
    ) : this(
        texture,
        gridX,
        gridY,
        x,
        y,
        width,
        height,
        null,
        null,
        triggerSound,
        runnable,
    ) {
        activeAction = getActivateAction()
        inactiveAction = getDeactivateAction()
    }


    override fun update() {
        color = if (enabled) {
            Color.WHITE
        } else {
            Color.GRAY
        }
        if (active && enabled) {
            actions.clear()
            if (activeAction != null) {
                addAction(activeAction!!())
            }
        } else {
            actions.clear()
            if (inactiveAction != null) {
                addAction(inactiveAction!!())
            }
        }
    }

    override fun trigger() {
        if (triggerSound != null) {
            SE.play(triggerSound!!)
        }
        if (enabled && runnable != null) {
            runnable!!()
        }
    }

    fun getActivateAction(vararg actions: () -> Action): () -> Action {
        return {
            val ret = ParallelAction()
            ret.addAction(
                Actions.moveTo(staticX - 10, staticY, 1f, Interpolation.pow5Out)
            )
            ret.addAction(
                Actions.color(Color.WHITE, 0.5f)
            )
            for (action in actions) {
                ret.addAction(action())
            }
            ret
        }
    }

    fun getDeactivateAction(vararg actions: () -> Action): () -> Action {
        return {
            val ret = ParallelAction()
            ret.addAction(
                Actions.color(Color.GRAY)
            )
            ret.addAction(
                Actions.moveTo(staticX, staticY, 1f, Interpolation.pow5Out),
            )
            for (action in actions) {
                ret.addAction(action())
            }
            ret
        }
    }
}