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

package com.hhs.koto.stg.graphics

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.hhs.koto.stg.Drawable
import com.hhs.koto.util.game
import com.hhs.koto.util.getRegion
import com.hhs.koto.util.lerp
import ktx.math.vec2
import space.earlygrey.shapedrawer.ShapeDrawer

/**
 * aka. Timer Circle
 *
 * Change size according to boss timer
 */
class SpellAttackCircle(
    val boss: Boss,
    val maxSize: Float = 128f,
    val setupTime: Int = 60,
    val numVertex: Int = 2
) : Drawable {
    val texture = getRegion("ui/blank.png")

    override var x = 0f
    override var y = 0f
    override var alive = boss.alive

    var visible = false
    var size = 0f

    /**
     * Animation State. 0 = expanding 1 = normal 2 = shrinking
     */
    var state = 0

    var tmp = -1f

    /**
     * Frame counter
     */
    var t = 0

    var maxTime = 1
    var nowTime = 1

    // TODO
    var shapeDrawer = ShapeDrawer(game.batch, texture).apply {
        pixelSize = 0.1f
    }

    fun reset(maxTime: Int) {
        state = 0
        x = boss.x
        y = boss.y
        tmp = -1f
        t = 0
        visible = true
        this.maxTime = maxTime
    }

    private fun getTimePercentage() = nowTime / (0f + maxTime)

    fun end() {
        state = 2 //mark end
        t = 0
        tmp = size
    }

    override fun tick() {
        val a = vec2(boss.x - x, boss.y - y).limit(3f)
        x += a.x
        y += a.y
        t++

        when (state) {
            0 -> {
                size = if (t <= setupTime / 2) {
                    Interpolation.pow5Out.apply(0f, (1 - getTimePercentage()) * maxSize * 2, t / (0f + setupTime))
                } else {
                    Interpolation.pow5In.apply(
                        (1 - getTimePercentage()) * maxSize * 2,
                        (1 - getTimePercentage()) * maxSize,
                        t / (0f + setupTime)
                    )
                }

                if (t == setupTime) {
                    state = 1
                    t = 0
                }
            }
            1 -> {
                size = lerp(maxSize, 0f, getTimePercentage())
            }
            2 -> {
                size = Interpolation.pow5.apply(tmp, 0f, t / (0f + setupTime))
                if (t == setupTime) {
                    visible = false
                }
            }
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (!visible) {
            return
        }

        //TODO @Zzzyt
        if (batch != shapeDrawer.batch) {
            shapeDrawer = ShapeDrawer(batch, texture)
            shapeDrawer.pixelSize = 0.5f
        }
        shapeDrawer.setColor(0f, 0f, 1f, 0.5f)
        shapeDrawer.circle(x, y, size, 5f)
    }
}