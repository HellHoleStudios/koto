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

import com.badlogic.gdx.graphics.Color
import com.hhs.koto.app.Config
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.addEnemy
import com.hhs.koto.stg.background.TileBackground
import com.hhs.koto.stg.pattern.Interpolate
import com.hhs.koto.stg.pattern.move
import com.hhs.koto.stg.task.*
import com.hhs.koto.util.findBoss
import com.hhs.koto.util.game
import com.hhs.koto.util.getRegion

class Stage1 : StageBuilder {
    override val availableDifficulties = GameDifficulty.REGULAR_AVAILABLE
    override val name = "stage1"

    override fun build() = BuilderSequence(
        taskBuilder {
            RunnableTask {
                game.background.addDrawable(
                    TileBackground(
                        getRegion("st3_water.png"),
                        0,
                        speedY = -4f,
                        tileWidth = 128f,
                        tileHeight = 256f,
                    )
                )
                game.background.addDrawable(
                    TileBackground(
                        getRegion("st3_water_overlay.png"),
                        0,
                        speedY = -6f,
                        tileWidth = 128f,
                        tileHeight = 128f,
                        color = Color(1f, 1f, 1f, 0.3f)
                    )
                )
                game.background.addDrawable(
                    TileBackground(
                        getRegion("st3_shore_left.png"),
                        0,
                        speedY = -5f,
                        tileWidth = 128f,
                        tileHeight = 256f,
                        width = 128f,
                    )
                )
                game.background.addDrawable(
                    TileBackground(
                        getRegion("st3_shore_right.png"),
                        0,
                        speedY = -5f,
                        x = Config.worldW - 128f - Config.originX,
                        tileWidth = 128f,
                        tileHeight = 256f,
                        width = 128f,
                    )
                )
            }
        },
        taskBuilder { Interpolate(0f, 1f, 60) { game.background.alpha = it } },
        MidStage1(),
        taskBuilder { DelayTask(240) },
        taskBuilder {
            CoroutineTask {
                val boss = addEnemy(AyaBoss())
                boss.healthBar.addSpell(Spell1(), Spell1(), Spell1())
                boss.creationTask().waitForFinish()
            }
        },
        Spell1(),
        Spell1(),
        Spell1(),
        taskBuilder {
            CoroutineTask {
                val boss = findBoss(AyaBoss::class.java)!!
                move(boss, -300f, 300f, 120)
            }
        }
    ).build()
}
