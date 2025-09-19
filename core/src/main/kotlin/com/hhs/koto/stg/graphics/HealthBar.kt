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

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils.PI2
import com.badlogic.gdx.math.MathUtils.HALF_PI
import com.badlogic.gdx.math.MathUtils.PI
import com.hhs.koto.stg.Drawable
import com.hhs.koto.stg.task.BasicSpell
import com.hhs.koto.util.*
import ktx.collections.GdxArray
import ktx.collections.lastIndex
import space.earlygrey.shapedrawer.ShapeDrawer

class HealthBar(
    val boss: Boss,
    val radius: Float = 50f,
    val borderColor: Color = RED_HSV,
    val barColor: Color = WHITE_HSV,
    override val zIndex: Int = 900,
) : Drawable {
    @Suppress("SetterBackingFieldAssignment", "UNUSED_PARAMETER")
    override var x: Float
        get() = boss.x
        set(value) = throw UnsupportedOperationException()

    @Suppress("SetterBackingFieldAssignment", "UNUSED_PARAMETER")
    override var y: Float
        get() = boss.y
        set(value) = throw UnsupportedOperationException()

    override var alive: Boolean = true
    var visible: Boolean = true
    val segments = GdxArray<Float>()
    var currentSegment: Int = 0
    var totalHealth: Float = 0f
    var currentHealth: Float = 0f
    var shapeDrawer = ShapeDrawer(game.batch, getRegion("ui/blank.png")).apply {
        pixelSize = 0.1f
    }
    val segmentDivider = getRegion("ui/segment_divider.png")
    var animationTimer = 0

    fun reset(){
        segments.clear()
        totalHealth=0f
        currentHealth=0f
        currentSegment=0
        animationTimer=0
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (visible && segments.size > 0) {
            if (batch != shapeDrawer.batch) {
                shapeDrawer = ShapeDrawer(batch, getRegion("ui/blank.png"))
                shapeDrawer.pixelSize = 0.5f
            }
            shapeDrawer.setColor(barColor)

            val anime = smoothstep(HALF_PI,PI2,animationTimer/30f)
            val realDis = min(anime,PI2 * currentTotalHealth() / totalHealth)

            shapeDrawer.arc(x, y, radius, PI / 2f, realDis, 3f)
            shapeDrawer.setColor(borderColor)
            shapeDrawer.circle(x, y, radius + 1.5f, 1f)
            shapeDrawer.circle(x, y, radius - 1.5f, 1f)

            if (currentSegment < segments.size) {
                var currentSum = segments.last()
                for (i in segments.size - 2 downTo currentSegment) {
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
    }

    override fun tick(){
        animationTimer++
    }

    fun addSegment(vararg health: Float) {
        if (segments.isEmpty) {
            currentHealth = health.first()
        }
        health.forEach {
            totalHealth += it
            segments.add(it)
        }
    }

    fun addSpell(vararg spell: BasicSpell<*>) {
        if (segments.isEmpty) {
            currentHealth = spell.first().health
        }
        spell.forEach {
            totalHealth += it.health
            segments.add(it.health)
        }
    }

    fun startWithSpell(vararg spell: BasicSpell<*>){
        reset()
        addSpell(*spell)
    }

    fun currentTotalHealth(): Float {
        var result = 0f
        for (i in currentSegment + 1 until segments.size) {
            result += segments[i]
        }
        return result + currentHealth
    }

    fun damage(damage: Float) {
        currentHealth = (currentHealth - damage).coerceAtLeast(0f)
    }

    fun currentSegmentDepleted(): Boolean = currentHealth <= 0

    fun nextSegment() {
        if (currentSegment < segments.lastIndex) {
            currentSegment++
            currentHealth = segments[currentSegment]
        } else {
            currentHealth = 0f
        }
    }
}