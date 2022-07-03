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

import ktx.collections.GdxArray

class BuilderSequence(vararg builder: TaskBuilder) : TaskBuilder {
    private val builders = GdxArray<TaskBuilder>(8)

    init {
        builder.forEach { builders.add(it) }
        builders.shrink()
    }

    override fun build(): Task = object : Task {
        private var currentIndex: Int = 0
        private var currentTask: Task? = null
        override var alive: Boolean = true

        override fun tick() {
            while (currentIndex < builders.size) {
                if (currentTask == null) {
                    currentTask = builders[currentIndex].build()
                }
                currentTask!!.tick()
                if (!currentTask!!.alive) {
                    currentTask = null
                    currentIndex++
                } else {
                    break
                }
            }
            if (currentIndex >= builders.size) {
                alive = false
                return
            }
        }

        override fun kill(): Boolean {
            builders.clear()
            alive = false
            return true
        }
    }

    fun add(vararg builder: TaskBuilder) {
        builder.forEach {
            builders.add(it)
        }
    }

    fun add(builder: TaskBuilder) {
        builders.add(builder)
    }

    operator fun plusAssign(builder: TaskBuilder) = add(builder)

    operator fun plus(other: BuilderSequence): BuilderSequence {
        val tmp = BuilderSequence()
        builders.forEach { tmp += it }
        other.builders.forEach { tmp += it }
        return tmp
    }
}