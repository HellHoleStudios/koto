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
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.hhs.koto.util.safeIterator

open class ConstrainedGrid(
    val region: Rectangle,
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    gridX: Int = 0,
    gridY: Int = 0,
    cycle: Boolean = true,
    activeAction: (() -> Action)? = null,
    inactiveAction: (() -> Action)? = null
) : Grid(gridX, gridY, cycle, activeAction, inactiveAction) {

    override fun act(delta: Float) {
        for (component in grid.safeIterator()) {
            if (component.active && component is Actor) {
                val actor = component as Actor
                val dx = calculateDelta(actor.x + offsetX, actor.width, region.getX(), region.getWidth())
                val dy = calculateDelta(actor.y + offsetY, actor.height, region.getY(), region.getHeight())
                if (dx == 0f && dy == 0f) continue
                offsetX -= dx
                offsetY -= dy
                break
            }
        }
        super.act(delta)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val tmpX1 = x
        val tmpY1 = y
        val cullingArea = cullingArea
        if (cullingArea != null) {
            val tmpX2 = cullingArea.getX()
            val tmpY2 = cullingArea.getY()
            setPosition(tmpX1 + offsetX, tmpY1 + offsetY)
            cullingArea.setPosition(tmpX2 - offsetX, tmpY2 - offsetY)
            super.draw(batch, parentAlpha)
            setPosition(tmpX1, tmpY1)
            cullingArea.setPosition(tmpX2, tmpY2)
        } else {
            setPosition(tmpX1 + offsetX, tmpY1 + offsetY)
            super.draw(batch, parentAlpha)
            setPosition(tmpX1, tmpY1)
        }
    }

    private fun calculateDelta(x: Float, width: Float, x2: Float, width2: Float): Float {
        if (x < x2) {
            return x - x2
        }
        return if (x + width > x2 + width2) {
            x + width - x2 - width2
        } else 0f
    }
}