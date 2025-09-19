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

package com.hhs.koto.stg.graphics

import com.badlogic.gdx.graphics.g2d.Batch
import com.hhs.koto.stg.Drawable
import com.hhs.koto.util.BlendingMode
import com.hhs.koto.util.removeNull
import com.hhs.koto.util.setBlending
import ktx.collections.GdxArray

class DrawableLayer<T : Drawable>(override val zIndex: Int = 0) : Drawable {
    override var x: Float = 0f
    override var y: Float = 0f
    override var alive: Boolean = true
    var alpha: Float = 1f

    val drawables = GdxArray<T>()

    inline fun forEach(action: (T) -> Unit) {
        for (i in 0 until drawables.size) {
            if (drawables[i] != null && drawables[i].alive) {
                action(drawables[i])
            }
        }
    }

    fun addDrawable(drawable: T): T {
        for (i in 0 until drawables.size) {
            if (drawables[i].zIndex > drawable.zIndex) {
                drawables.insert(i, drawable)
                return drawable
            }
        }
        drawables.add(drawable)
        return drawable
    }

    fun removeDrawable(drawable: T) {
        drawables.removeValue(drawable, true)
    }

    fun clear() {
        drawables.clear()
    }

    /**
     * Recycle objects marked with [recyclable]
     *
     * Called at the end of each stage automatically
     */
    fun recycle(){
        drawables.removeAll { it.recyclable }
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (alpha < 0.001f) return

        var currentBlending = BlendingMode.ALPHA
        for (i in 0 until drawables.size) {
            if (drawables[i].blending != currentBlending) {
                currentBlending = drawables[i].blending
                batch.setBlending(currentBlending)
            }
            drawables[i].draw(batch, parentAlpha * alpha, subFrameTime)
        }
        if (currentBlending != BlendingMode.ALPHA) {
            batch.setBlending(BlendingMode.ALPHA)
        }
    }

    override fun tick() {
        for (i in 0 until drawables.size) {
            if (!drawables[i].alive) {
                drawables[i] = null
            } else {
                drawables[i].tick()
            }
        }
        drawables.removeNull()
    }
}