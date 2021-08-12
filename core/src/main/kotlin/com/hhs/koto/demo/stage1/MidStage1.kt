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
import com.badlogic.gdx.math.MathUtils.random
import com.hhs.koto.stg.addEnemy
import com.hhs.koto.stg.drawable.BasicEnemy
import com.hhs.koto.stg.drawable.BasicEnemyTexture
import com.hhs.koto.stg.pattern.interpolate
import com.hhs.koto.stg.pattern.move
import com.hhs.koto.stg.task.*
import com.hhs.koto.util.A

class MidStage1 : TaskBuilder {
    override fun build(): Task = CoroutineTask {
        task {
            addEnemy(AyaBoss()).task {
                while (true) {
                    move(enemy, random(-150f, 150f), random(-150f, 150f), 120)
                    wait(120)
                }
            }
            while (true) {
                addEnemy(
                    BasicEnemy(
                        -250f,
                        100f,
                        BasicEnemyTexture(A["sprite/th10_fairy.atlas"], "th10_fairy_red"),
                        30f,
                        5,
                        5,
                    )
                ).task {
                    val enemy = enemy as BasicEnemy
                    interpolate(-250f, 0f, 100, Interpolation.sine) {
                        enemy.x = it
                    }
                    wait(120)
                    interpolate(0f, 250f, 100, Interpolation.sine) {
                        enemy.x = it
                    }
                    enemy.kill()
                }.task {
                    while (true) {
//                        repeat(10) {
//                            towards(
//                                "DS_BALL_S_RED",
//                                enemy.x,
//                                enemy.y,
//                                playerX,
//                                playerY,
//                                3f,
//                            ).angle += it * 36f
//                        }
                        wait(10)
                    }
                }
                wait(130)
            }
        }.waitForFinish()
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