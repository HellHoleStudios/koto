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

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import java.lang.Float.min

open class ConstrainedGrid(
    private var constraintX: Float = 0f,
    private var constraintY: Float = 0f,
    private val constraintWidth: Float,
    private val constraintHeight: Float,
    gridX: Int = 0,
    gridY: Int = 0,
    cycle: Boolean = true,
    protected val animationDuration: Float = 0f,
    protected val interpolation: Interpolation = Interpolation.linear,
    staticX: Float = 0f,
    staticY: Float = 0f,
    width: Float = 0f,
    height: Float = 0f,
    activeAction: (() -> Action)? = null,
    inactiveAction: (() -> Action)? = null,
) : Grid(gridX, gridY, cycle, staticX, staticY, width, height, activeAction, inactiveAction) {
    var startX: Float = 0f
    var startY: Float = 0f
    var targetX: Float = 0f
    var targetY: Float = 0f
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

    override fun act(delta: Float) {
        updateTarget(delta)
        super.act(delta)
    }

    fun finishAnimation() {
        updateTarget(0f)
        t = animationDuration
    }

    fun setCullingToConstraint(): ConstrainedGrid {
        cullingArea = Rectangle(constraintX, constraintY, constraintWidth, constraintHeight)
        return this
    }

    override fun updateComponent(): Grid {
        for (i in 0 until grid.size) {
            val component = grid[i]
            if (component.active && component is Actor) {
                targetX = calculateTarget(component.staticX, component.width, constraintX, constraintWidth, targetX)
                targetY = calculateTarget(component.staticY, component.height, constraintY, constraintHeight, targetY)
                startX = targetX
                startY = targetY
                break
            }
        }
        return super.updateComponent()
    }

    open fun updateTarget(delta: Float) {
        var x: Float? = null
        var y: Float? = null
        var width = 0f
        var height = 0f
        for (i in 0 until grid.size) {
            val component = grid[i]
            if (component.active && component is Actor) {
                x = component.staticX
                y = component.staticY
                width = width.coerceAtLeast(component.width)
                height = height.coerceAtLeast(component.height)
            }
        }
        if (x != null && y != null) {
            if (targetX + constraintX <= x &&
                targetX + constraintX + constraintWidth >= x + width &&
                targetY + constraintY <= y &&
                targetY + constraintY + constraintHeight >= y + height
            ) {
                if (t < animationDuration) {
                    t = min(animationDuration, t + delta)
                }
            } else {
                startX = getCurrentX()
                startY = getCurrentY()
                t = 0f
                targetX = calculateTarget(x, width, constraintX, constraintWidth, targetX)
                targetY = calculateTarget(y, height, constraintY, constraintHeight, targetY)
            }
        }
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

    protected open fun calculateTarget(x: Float, width: Float, x2: Float, width2: Float, target: Float): Float {
        if (x < target + x2) {
            return x - x2
        }
        if (x + width > target + x2 + width2) {
            return x + width - x2 - width2
        }
        return target
    }
}