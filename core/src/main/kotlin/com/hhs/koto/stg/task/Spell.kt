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

import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.drawable.Enemy
import com.hhs.koto.util.game
import kotlinx.coroutines.yield
import ktx.collections.GdxArray

abstract class Spell<T : Enemy>(
    override val name: String,
    override val availableDifficulties: GdxArray<GameDifficulty>,
    val bossClass: Class<T>,
    val maxTime: Int,
    val health: Int,
) : SpellBuilder {
    abstract suspend fun createBoss()
    abstract suspend fun spell(boss: T)
    abstract suspend fun terminate(boss: T, time: Int)

    override fun build(): Task {
        val task = CoroutineTask {
            if (findBoss(bossClass) == null) {
                createBoss()
            }
            val boss: T = findBoss(bossClass)!!
            var time = 0
            task {
                while (true) {
                    yield()
                    time++
                }
            }
            task {
                wait(maxTime)
                terminate(boss, time)
            }
            spell(boss)
        }
        return task
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <T : Enemy> findBoss(clazz: Class<T>): T? {
            game.enemies.forEach {
                if (it.javaClass == clazz) {
                    return it as T
                }
            }
            return null
        }
    }
}