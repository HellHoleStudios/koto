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

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils.PI
import com.badlogic.gdx.math.MathUtils.PI2
import com.hhs.koto.stg.drawable.Boss
import com.hhs.koto.stg.task.BasicSpell
import com.hhs.koto.util.*
import ktx.collections.GdxArray
import space.earlygrey.shapedrawer.ShapeDrawer

class HealthBar(
    val boss: Boss,
    val radius: Float = 50f,
    val borderColor: Color = Color.RED.toHSVColor(),
    val barColor: Color = Color.WHITE.toHSVColor(),
) : Drawable {
    override val x: Float
        get() = boss.x
    override val y: Float
        get() = boss.y
    override var alive: Boolean = true
    val segments = GdxArray<Float>()
    var currentSegment: Int = -1
    var totalHealth: Float = 0f
    var currentHealth: Float = 0f
    var shapeDrawer = ShapeDrawer(game.batch, getRegion("ui/blank.png")).apply {
        pixelSize = 0.5f
    }
    val segmentDivider = getRegion("ui/segment_divider.png")

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (batch != shapeDrawer.batch) {
            shapeDrawer = ShapeDrawer(batch, getRegion("ui/blank.png"))
            shapeDrawer.pixelSize = 0.5f
        }
        shapeDrawer.setColor(barColor)
        shapeDrawer.arc(x, y, radius, PI / 2f, PI2 * currentTotalHealth() / totalHealth, 3f)
        shapeDrawer.setColor(borderColor)
        shapeDrawer.circle(x, y, radius + 1.5f, 1f)
        shapeDrawer.circle(x, y, radius - 1.5f, 1f)

        if (currentSegment > 0) {
            var currentSum = segments[0]
            for (i in 1..currentSegment) {
                val angle = currentSum / totalHealth * 360f + 90f
                batch.draw(
                    segmentDivider,
                    x + radius * cos(angle) - 4f,
                    y + radius * sin(angle) - 4f,
                    4f,
                    4f,
                    8f,
                    8f,
                    1f,
                    1f,
                    angle,
                )
                currentSum += segments[i]
            }
        }
    }

    override fun tick() = Unit

    fun addSegment(vararg health: Float) {
        health.forEach {
            currentSegment++
            totalHealth += it
            segments.add(it)
        }
        currentHealth = health.last()
    }

    fun addSpell(vararg spell: BasicSpell<*>) {
        spell.forEach {
            currentSegment++
            totalHealth += it.health
            segments.add(it.health)
        }
        currentHealth = spell.last().health
    }

    fun currentTotalHealth(): Float {
        var result = 0f
        for (i in 0 until currentSegment) {
            result += segments[i]
        }
        return result + currentHealth
    }

    fun damage(damage: Float) {
        currentHealth = (currentHealth - damage).coerceAtLeast(0f)
    }

    fun currentSegmentDepleted(): Boolean = currentHealth <= 0

    fun nextSegment() {
        if (currentSegment > 0) {
            currentSegment--
            currentHealth = segments[currentSegment]
        } else {
            currentHealth = 0f
        }
    }
}