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

package com.hhs.koto.stg.pattern

import com.badlogic.gdx.math.Interpolation
import com.hhs.koto.stg.Movable
import com.hhs.koto.stg.task.self
import com.hhs.koto.stg.task.waitForFinish
import com.hhs.koto.util.completeInterpolation
import com.hhs.koto.util.dist
import com.hhs.koto.util.random
import kotlinx.coroutines.CoroutineScope

class Move(
    val target: Movable,
    val targetX: Float = target.x,
    val targetY: Float = target.y,
    duration: Int,
    val interpolation: Interpolation = Interpolation.smooth,
) : TemporalPattern(duration) {
    val startX: Float = target.x
    val startY: Float = target.y
    override fun action() {
        target.x = completeInterpolation(interpolation, startX, targetX, t, duration)
        target.y = completeInterpolation(interpolation, startY, targetY, t, duration)
    }
}

suspend fun CoroutineScope.move(
    target: Movable,
    targetX: Float = target.x,
    targetY: Float = target.y,
    duration: Int,
    interpolation: Interpolation = Interpolation.sine,
) {
    val task = Move(target, targetX, targetY, duration, interpolation)
    self.attachTask(task)
    task.waitForFinish()
}

suspend fun CoroutineScope.wander(
    target: Movable,
    duration: Int,
    targetMinX: Float = -150f,
    targetMaxX: Float = 150f,
    targetMinY: Float = 0f,
    targetMaxY: Float = 150f,
    interpolation: Interpolation = Interpolation.sine,
) {
    var targetX = target.x
    var targetY = target.y
    for (i in 0 until 10000) {
        targetX = random(targetMinX, targetMaxX)
        targetY = random(targetMinY, targetMaxY)
        if (dist(target.x, target.y, targetX, targetY) >= 50f) break
    }
    val task = Move(target, targetX, targetY, duration, interpolation)
    self.attachTask(task)
    task.waitForFinish()
}