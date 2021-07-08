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

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.hhs.koto.util.safeIterator
import java.lang.Float.min

open class ScrollingGrid(
    gridX: Int = 0,
    gridY: Int = 0,
    cycle: Boolean = true,
    protected val animationDuration: Float = 0f,
    protected val interpolation: Interpolation = Interpolation.linear,
    staticX: Float = 0f,
    staticY: Float = 0f,
    activeAction: (() -> Action)? = null,
    inactiveAction: (() -> Action)? = null,
) : Grid(gridX, gridY, cycle, staticX, staticY, activeAction, inactiveAction) {
    protected var startX: Float = 0f
    protected var startY: Float = 0f
    protected var targetX: Float = 0f
    protected var targetY: Float = 0f
    protected var t: Float = 0f

    fun getCurrentX() = if (animationDuration == 0f) {
        targetX
    } else {
        interpolation.apply(startX, targetX, t / animationDuration)
    }

    fun getCurrentY() = if (animationDuration == 0f) {
        targetY
    } else {
        interpolation.apply(startY, targetY, t / animationDuration)
    }

    override fun updateComponent() {
        for (i in grid.safeIterator()) {
            if (i.active && i is Actor) {
                startX = i.staticX
                startY = i.staticY
                targetX = i.staticX
                targetY = i.staticY
                break
            }
        }
        super.updateComponent()
    }

    override fun act(delta: Float) {
        for (i in grid.safeIterator()) {
            if (i.active && i is Actor) {
                if (targetX == i.staticX && targetY == i.staticY) {
                    if (t < animationDuration) {
                        t = min(animationDuration, t + delta)
                    }
                } else {
                    startX = getCurrentX()
                    startY = getCurrentY()
                    t = 0f
                    targetX = i.staticX
                    targetY = i.staticY
                }
                break
            }
        }
        super.act(delta)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val tmpX1 = x
        val tmpY1 = y
        val currentX = getCurrentX()
        val currentY = getCurrentY()
        if (cullingArea != null) {
            val tmpX2 = cullingArea.getX()
            val tmpY2 = cullingArea.getY()
            setPosition(tmpX1 - currentX, tmpY1 - currentY)
            cullingArea.setPosition(tmpX2 + currentX, tmpY2 + currentY)
            super.draw(batch, parentAlpha)
            setPosition(tmpX1, tmpY1)
            cullingArea.setPosition(tmpX2, tmpY2)
        } else {
            setPosition(tmpX1 - currentX, tmpY1 - currentY)
            super.draw(batch, parentAlpha)
            setPosition(tmpX1, tmpY1)
        }
    }
}
