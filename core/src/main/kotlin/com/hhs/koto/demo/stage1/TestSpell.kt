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

package com.hhs.koto.demo.stage1

import com.badlogic.gdx.math.Interpolation
import com.hhs.koto.stg.*
import com.hhs.koto.stg.item.PointItem
import com.hhs.koto.stg.item.PowerItem
import com.hhs.koto.stg.task.*
import com.hhs.koto.util.*
import kotlinx.coroutines.yield
import ktx.collections.GdxArray

class TestSpell : SpellBuilder {
    override val name = "stage1.spell1"
    override val availableDifficulties: GdxArray<GameDifficulty> = GameDifficulty.REGULAR_AVAILABLE

    override fun build(): Task = CoroutineTask {
        task {
            while (true) {
                addEnemy(
                    object : BasicEnemy(
                        -250f,
                        100f,
                        30f,
                        BasicEnemyTexture(A["sprite/th10_fairy.atlas"], "th10_fairy_red"),
                        10f,
                    ) {
                        override fun death() {
                            super.death()
                            ringCloud(x, y, 5) { x, y ->
                                addItem(PowerItem(x, y))
                            }
                            ringCloud(x, y, 5) { x, y ->
                                addItem(PointItem(x, y))
                            }
                        }
                    }
                ).task {
                    val enemy = enemy as BasicEnemy
                    task {
                        repeat(100) {
                            enemy.x = Interpolation.sine.apply(-250f, 0f, frame / 100f)
                            yield()
                        }
                    }.waitForFinish()
                    wait(120)
                    task {
                        repeat(100) {
                            enemy.x = Interpolation.sine.apply(0f, 250f, frame / 100f)
                            yield()
                        }
                    }.waitForFinish()
                    enemy.kill()
                }.task {
                    while (true) {
                        towards(B["DS_BALL_S_BLUE"], enemy.x, enemy.y, playerX, playerY, 3f)
                        wait(30)
                    }
                }
                wait(130)
            }
        }
//        while (true) {
//            ringCloud(random(-100f, 100f), 100f, 5) { x, y ->
//                addItem(PowerItem(x, y))
//            }
//            ringCloud(random(-100f, 100f), 100f, 5) { x, y ->
//                addItem(PointItem(x, y))
//            }
//            ring(B["DS_BALL_S_RED"], 0f, 0f, 50f, 7 until 367 step 15)
//                .accelerate(0.2f, 20)
//            wait(10)
//        }
    }
}