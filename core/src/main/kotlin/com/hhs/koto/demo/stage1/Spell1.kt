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

import com.badlogic.gdx.math.MathUtils.random
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.graphics.Cutin
import com.hhs.koto.stg.pattern.wander
import com.hhs.koto.stg.task.BasicSpell
import com.hhs.koto.stg.task.CoroutineTask
import com.hhs.koto.stg.task.Task
import com.hhs.koto.stg.task.wait
import com.hhs.koto.util.difficultySelect
import com.hhs.koto.util.game
import com.hhs.koto.util.getRegion
import com.hhs.koto.util.ring
import ktx.collections.GdxArray

class Spell1 : BasicSpell<AyaBoss>(AyaBoss::class.java) {
    override val name: String = "stage1.spell1"
    override val availableDifficulties: GdxArray<GameDifficulty> = GameDifficulty.REGULAR_AVAILABLE
    override val health: Float
        get() = difficultySelect(1000f, 1200f, 1400f, 1600f)
    override val maxTime: Int = 1200
    override val bonus: Long
        get() = defaultBonus(1)

    override fun spell(): Task = CoroutineTask {
        val boss = getBoss()
        game.stage.addDrawable(Cutin(getRegion("portrait/aya/attack.png")))
        repeat(20) {
            wander(boss, 120)
            wait(30)
            boss.usingAction = true
            repeat(3) {
                ring(
                    "DS_BALL_M_A_BLUE",
                    boss.x,
                    boss.y,
                    50f,
                    difficultySelect(8, 12, 16, 20),
                    startAngle = game.layer.random(0f, 360f),
                    speed = 5f,
                )
                wait(20)
            }
            boss.usingAction = false
            wait(30)
        }
    }

    override fun buildSpellPractice(): Task = buildSpellPractice { AyaBoss() }
}