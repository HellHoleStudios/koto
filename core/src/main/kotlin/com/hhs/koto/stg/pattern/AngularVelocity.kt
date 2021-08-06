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

package com.hhs.koto.stg.pattern

import com.hhs.koto.stg.addTask
import com.hhs.koto.stg.bullet.Bullet
import com.hhs.koto.stg.bullet.BulletGroup

class AngularVelocity(
    val bullet: Bullet,
    var omega: Float = 0f,
    duration: Int = Int.MAX_VALUE,
) : TemporalPattern(duration) {
    override var alive: Boolean = false

    override fun action() {
        bullet.angle += omega
    }
}

fun <T : Bullet> T.angularVel(omega: Float = 0f, duration: Int = Int.MAX_VALUE): T {
    addTask(AngularVelocity(this, omega, duration))
    return this
}

fun BulletGroup.angularVel(omega: Float = 0f, duration: Int = Int.MAX_VALUE): BulletGroup {
    forEach {
        addTask(AngularVelocity(it, omega, duration))
    }
    return this
}