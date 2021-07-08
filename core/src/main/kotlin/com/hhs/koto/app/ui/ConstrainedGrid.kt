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
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.hhs.koto.util.safeIterator
import java.lang.Float.min

open class ConstrainedGrid(
    val region: Rectangle,
    gridX: Int = 0,
    gridY: Int = 0,
    cycle: Boolean = true,
    animationDuration: Float = 0f,
    interpolation: Interpolation = Interpolation.linear,
    staticX: Float = 0f,
    staticY: Float = 0f,
    activeAction: (() -> Action)? = null,
    inactiveAction: (() -> Action)? = null,
) : ScrollingGrid(
    gridX,
    gridY,
    cycle,
    animationDuration,
    interpolation,
    staticX,
    staticY,
    activeAction,
    inactiveAction
) {
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
                    targetX = calculateTarget(i.staticX, i.width, region.x, region.width)
                    targetY = calculateTarget(i.staticY, i.height, region.y, region.height)
                }
                break
            }
        }
        super.act(delta)
    }

    private fun calculateTarget(x: Float, width: Float, x2: Float, width2: Float): Float {
        if (x < x2) {
            return x2
        }
        if (x + width > x2 + width2) {
            return x2 + width2 - width
        }
        return x
    }
}