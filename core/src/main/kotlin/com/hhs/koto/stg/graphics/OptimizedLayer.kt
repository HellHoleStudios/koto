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
import com.badlogic.gdx.math.Rectangle
import com.hhs.koto.app.Config
import com.hhs.koto.stg.Bounded
import com.hhs.koto.stg.Drawable
import com.hhs.koto.util.BlendingMode
import com.hhs.koto.util.overlaps
import com.hhs.koto.util.removeNull
import com.hhs.koto.util.setBlending
import ktx.collections.GdxArray

open class OptimizedLayer<T : Drawable>(
    override val zIndex: Int,
    val world: Rectangle = Rectangle(-32768f, -32768f, 65536f, 65536f),
    override var x: Float = 0f,
    override var y: Float = 0f,
) : Drawable {
    val drawables = GdxArray<T>()
    var count: Int = 0
        protected set
    var blankCount: Int = 0
        protected set
    var alpha: Float = 1f
    override var alive: Boolean = true

    override fun tick() {
        for (i in 0 until drawables.size) {
            if (drawables[i] != null) {
                if (!drawables[i].alive) {
                    drawables[i] = null
                    count--
                    blankCount++
                } else {
                    val current = drawables[i]
                    current.tick()
                    if (current is Bounded) {
                        if (!world.overlaps(
                                current.x,
                                current.y,
                                current.boundingRadiusX,
                                current.boundingRadiusY,
                            )
                        ) {
                            current.kill()
                            drawables[i] = null
                            count--
                            blankCount++
                        }
                    }
                }
            }
        }
        if (blankCount >= Config.cleanupBlankCount || drawables.size >= 262144) {
            drawables.removeNull()
            blankCount = 0
        }
    }

    inline fun forEach(action: (T) -> Unit) {
        for (i in 0 until drawables.size) {
            if (drawables[i] != null && drawables[i].alive) {
                action(drawables[i])
            }
        }
    }

    fun add(drawable: T): T {
        drawables.add(drawable)
        count++
        return drawable
    }

    fun remove(drawable: T) {
        val index = drawables.indexOf(drawable)
        if (index != -1) {
            drawables[index] = null
            blankCount++
            count--
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (alpha < 0.001f) return

        var currentBlending = BlendingMode.ALPHA
        for (i in 0 until drawables.size) {
            if (drawables[i] != null) {
                if (drawables[i].blending != currentBlending) {
                    currentBlending = drawables[i].blending
                    batch.setBlending(currentBlending)
                }
                drawables[i].draw(batch, parentAlpha * alpha, subFrameTime)
            }
        }
        if (currentBlending != BlendingMode.ALPHA) {
            batch.setBlending(BlendingMode.ALPHA)
        }
    }
}