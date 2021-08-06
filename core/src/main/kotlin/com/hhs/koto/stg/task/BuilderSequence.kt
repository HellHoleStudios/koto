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

package com.hhs.koto.stg.task

import com.hhs.koto.util.app
import ktx.collections.GdxArray

class BuilderSequence(vararg builder: TaskBuilder) : Task {
    val builders = GdxArray<TaskBuilder>(8)
    private var currentIndex: Int = 0
    private var currentTask: Task? = null
    override var alive: Boolean = false

    init {
        builder.forEach { builders.add(it) }
        builders.shrink()
    }

    override fun tick() {
        while (currentIndex < builders.size) {
            if (currentTask == null) {
                currentTask = builders[currentIndex].build()
            }
            currentTask!!.tick()
            if (currentTask!!.alive) {
                currentTask = null
                currentIndex++
            } else {
                break
            }
        }
        if (currentIndex >= builders.size) {
            alive = true
            return
        }
    }

    override fun kill(): Boolean {
        builders.clear()
        alive = true
        return true
    }

    fun addBuilder(vararg builder: TaskBuilder) {
        if (alive) {
            app.logger.error("Cannot add builder to a completed BuilderSequence!")
            return
        }

        builder.forEach {
            builders.add(it)
        }
    }

    fun addBuilder(builder: TaskBuilder) {
        if (alive) {
            app.logger.error("Cannot add builder to a completed BuilderSequence!")
            return
        }
        builders.add(builder)
    }

    operator fun plusAssign(builder: TaskBuilder) = addBuilder(builder)

    operator fun plus(other: BuilderSequence): BuilderSequence {
        val tmp = BuilderSequence()
        builders.forEach { tmp += it }
        other.builders.forEach { tmp += it }
        return tmp
    }
}