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

package com.hhs.koto.stg.bullet

import com.badlogic.gdx.graphics.Color
import com.hhs.koto.stg.Drawable
import com.hhs.koto.stg.Entity
import com.hhs.koto.stg.task.Task
import kotlinx.coroutines.CoroutineScope

interface Bullet : Entity, Drawable {
    var speed: Float
    var angle: Float
    var rotation: Float
    var tint: Color

    /**
     * Whether this bullet is deleted upon bombing or hitting the player
     *
     * Take this into consideration when designing bombs!
     */
    var destroyable: Boolean

    fun onGraze()
    fun attachTask(task: Task): Bullet
    fun task(index: Int, block: suspend CoroutineScope.() -> Unit): Bullet
    fun task(block: suspend CoroutineScope.() -> Unit): Bullet = task(0, block)
    fun destroy()
}