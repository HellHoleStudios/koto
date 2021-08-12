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

import ktx.collections.GdxArray

class HealthBar {
    val segments = GdxArray<Float>()
    var currentSegment: Int = -1
    var totalHealth: Float = 0f
    var currentHealth: Float = 0f

    fun addSegment(health: Float) {
        currentSegment++
        totalHealth += health
        currentHealth = health
        segments.add(health)
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
        currentSegment--
        currentHealth = segments[currentSegment]
    }
}