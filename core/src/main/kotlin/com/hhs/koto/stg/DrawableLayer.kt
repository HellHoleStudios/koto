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

package com.hhs.koto.stg

import com.badlogic.gdx.graphics.g2d.Batch
import com.hhs.koto.util.removeNull
import ktx.collections.GdxArray

class DrawableLayer(override val zIndex: Int = 0) : Drawable {
    override val x: Float = 0f
    override val y: Float = 0f
    override var alive: Boolean = true

    val drawables = GdxArray<Drawable>()

    fun addDrawable(drawable: Drawable) {
        for (i in 0 until drawables.size) {
            if (drawables[i].zIndex > drawable.zIndex) {
                drawables.insert(i, drawable)
                return
            }
        }
        drawables.add(drawable)
    }

    fun removeDrawable(drawable: Drawable) {
        drawables.removeValue(drawable, true)
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        for (i in 0 until drawables.size) {
            drawables[i].draw(batch, parentAlpha, subFrameTime)
        }
    }

    override fun tick() {
        for (i in 0 until drawables.size) {
            if (drawables[i] != null) {
                if (!drawables[i].alive) {
                    drawables[i] = null
                } else {
                    drawables[i].tick()
                }
            }
        }
        drawables.removeNull()
    }
}