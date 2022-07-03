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

package com.hhs.koto.demo.stage2

import com.badlogic.gdx.math.Interpolation
import com.hhs.koto.stg.graphics.BasicEnemy
import com.hhs.koto.stg.graphics.BasicEnemyTexture
import com.hhs.koto.stg.pattern.angularVel
import com.hhs.koto.stg.pattern.move
import com.hhs.koto.stg.task.*
import com.hhs.koto.util.*

object MidStage2 : TaskBuilder {
    override fun build(): Task = CoroutineTask {
        task {
            repeat(20) {
                game.addEnemy(
                    BasicEnemy(
                        -250f,
                        250f,
                        BasicEnemyTexture(A["sprite/th10_fairy.atlas"], "th10_fairy_red"),
                        30f,
                        5,
                        5,
                    )
                ).task {
                    val enemy = enemy as BasicEnemy
                    move(enemy, 0f, 0f, 120, Interpolation.sine)
                    wait(120)
                    move(enemy, 250f, 250f, 120, Interpolation.sine)
                    enemy.kill()
                }.task {
                    val way = difficultySelect(10, 12, 16, 24)
                    repeat(5) {
                        repeat(way) {
                            towards(
                                "DS_BALL_S_RED",
                                enemy.x,
                                enemy.y,
                                playerX,
                                playerY,
                                2f,
                            ).apply {
                                angle += it * 360f / way + 360f / way / 2f
                            }.angularVel(1f)
                            wait(5)
                        }
                    }
                }
                wait(20)
            }
        }.waitForFinish()
        wait(300)
        game.bullets.forEach {
            it.destroy()
        }
    }
}