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

package com.hhs.koto.stg.task

import com.hhs.koto.util.KotoRuntimeException
import com.hhs.koto.util.removeNull
import ktx.collections.GdxArray

class ParallelTask(vararg task: Task) : Task {
    val tasks = GdxArray<Task>(8)
    override var alive: Boolean = true

    init {
        task.forEach { tasks.add(it) }
        tasks.shrink()
    }

    override fun tick() {
        for (i in 0 until tasks.size) {
            if (!tasks[i].alive) {
                tasks[i] = null
                continue
            }
            tasks[i].tick()
            if (!tasks[i].alive) {
                tasks[i] = null
            }
        }
        tasks.removeNull()
        alive = tasks.size != 0
    }

    override fun kill(): Boolean {
        tasks.forEach {
            if (it.alive) it.kill()
        }
        tasks.clear()
        alive = false
        return true
    }

    fun addTask(vararg task: Task) {
        if (!alive) {
            throw KotoRuntimeException("Cannot add task to a completed ParallelTask!")
        }

        task.forEach {
            tasks.add(it)
        }
    }

    fun addTask(task: Task) {
        if (!alive) {
            throw KotoRuntimeException("Cannot add task to a completed ParallelTask!")
        }
        tasks.add(task)
    }

    operator fun plusAssign(task: Task) = addTask(task)

    operator fun plus(other: ParallelTask): ParallelTask {
        val tmp = ParallelTask()
        tasks.forEach { tmp += it }
        other.tasks.forEach { tmp += it }
        return tmp
    }
}