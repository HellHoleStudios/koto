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

import com.hhs.koto.stg.drawable.Enemy
import com.hhs.koto.stg.addTask
import com.hhs.koto.stg.bullet.Bullet
import com.hhs.koto.stg.bullet.BulletGroup
import kotlinx.coroutines.*
import ktx.collections.GdxMap
import ktx.collections.set
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class CoroutineTask(
    index: Int = 0,
    obj: Any? = null,
    block: suspend CoroutineScope.() -> Unit
) : Task {
    companion object {
        val scope = CoroutineScope(CoroutineTaskDispatcher)
    }

    object CoroutineTaskDispatcher : MainCoroutineDispatcher() {
        override val immediate: MainCoroutineDispatcher = this
        private val map = GdxMap<CoroutineContext, Runnable>()

        fun tick(job: Job) {
            map[job].run()
        }

        fun remove(job: Job) {
            map.remove(job)
        }

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            map[context.job] = block
        }
    }

    class CoroutineTaskElement(
        var frame: Int = 0,
        val index: Int = 0,
    ) : AbstractCoroutineContextElement(CoroutineTaskElement) {
        companion object Key : CoroutineContext.Key<CoroutineTaskElement>

        lateinit var obj: Any
    }

    private val element = CoroutineTaskElement(index = index).apply {
        if (obj != null) this.obj = obj
    }
    private val job = scope.launch(element, block = block)
    override var alive: Boolean = true

    override fun tick() {
        CoroutineTaskDispatcher.tick(job.job)
        if (job.isCompleted) {
            alive = false
            CoroutineTaskDispatcher.remove(job.job)
        }
        element.frame++
    }

    override fun kill(): Boolean {
        alive = false
        job.cancel()
        CoroutineTaskDispatcher.remove(job.job)
        return true
    }
}

val CoroutineScope.frame: Int
    get() = coroutineContext[CoroutineTask.CoroutineTaskElement]!!.frame
val CoroutineScope.index: Int
    get() = coroutineContext[CoroutineTask.CoroutineTaskElement]!!.index
val CoroutineScope.bullet: Bullet
    get() = coroutineContext[CoroutineTask.CoroutineTaskElement]!!.obj as Bullet
val CoroutineScope.enemy: Enemy
    get() = coroutineContext[CoroutineTask.CoroutineTaskElement]!!.obj as Enemy
val CoroutineScope.group: BulletGroup
    get() = coroutineContext[CoroutineTask.CoroutineTaskElement]!!.obj as BulletGroup

suspend fun wait(frameCount: Int = 1) {
    repeat(frameCount) {
        yield()
    }
}

suspend fun waitFor(predicate: () -> Boolean) {
    while (!predicate()) {
        yield()
    }
}

suspend fun Task.waitForFinish() {
    while (alive) {
        yield()
    }
}

fun task(
    index: Int = 0,
    obj: Any? = null,
    block: suspend CoroutineScope.() -> Unit
): Task {
    val task = CoroutineTask(index, obj, block)
    addTask(task)
    return task
}