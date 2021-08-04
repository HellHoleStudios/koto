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
import com.badlogic.gdx.scenes.scene2d.Actor
import com.hhs.koto.app.Config
import com.hhs.koto.util.game
import com.hhs.koto.util.outOfWorld
import com.hhs.koto.util.removeNull
import ktx.collections.GdxArray

open class DrawableLayer<T : Drawable>(override val z: Int) : Actor(), IndexedActor {
    val drawables = GdxArray<T>()
    var count: Int = 0
        protected set
    var blankCount: Int = 0
        protected set
    var subFrameTime: Float = 0f

    fun tick() {
        subFrameTime = 0f
        for (i in 0 until drawables.size) {
            if (drawables[i] != null) {
                if (!drawables[i].alive) {
                    drawables[i] = null
                    count--
                    blankCount++
                } else {
                    drawables[i].tick()
                    if (outOfWorld(
                            drawables[i].x,
                            drawables[i].y,
                            drawables[i].boundingWidth,
                            drawables[i].boundingHeight
                        )
                    ) {
                        drawables[i].alive = false
                        drawables[i] = null
                        count--
                        blankCount++
                    }
                }
            }
        }
        if (blankCount >= Config.cleanupBlankCount || drawables.size >= 262144) {
            game.logger.debug("Cleaning up drawable layer: count=$count blankCount=$blankCount")
            drawables.removeNull()
            blankCount = 0
        }
    }

    override fun act(delta: Float) {
        subFrameTime += delta
        super.act(delta)
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

    override fun draw(batch: Batch, parentAlpha: Float) {
        for (i in 0 until drawables.size) {
            if (drawables[i] != null) {
                drawables[i].draw(batch, parentAlpha, subFrameTime)
            }
        }
    }
}