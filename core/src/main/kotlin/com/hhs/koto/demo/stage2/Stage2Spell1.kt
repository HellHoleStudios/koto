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

import com.hhs.koto.demo.stage1.AyaBoss
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.graphics.Cutin
import com.hhs.koto.stg.pattern.wander
import com.hhs.koto.stg.task.BasicSpell
import com.hhs.koto.stg.task.CoroutineTask
import com.hhs.koto.stg.task.Task
import com.hhs.koto.stg.task.wait
import com.hhs.koto.util.*
import ktx.collections.GdxArray

object Stage2Spell1 : BasicSpell<AyaBoss>(AyaBoss::class.java) {
    override val name: String = "stage2.spell1"
    override val availableDifficulties: GdxArray<GameDifficulty> = GameDifficulty.REGULAR_AVAILABLE

    override val health: Float
        get() = difficultySelect(100f, 150f, 200f, 250f)
    override val maxTime: Int = 3000
    override val bonus: Long
        get() = defaultBonus(1)

    override fun spell(): Task = CoroutineTask {
        val boss = getBoss()
        game.stage.addDrawable(Cutin(getRegion("portrait/aya/attack.png")))
        repeat(20) {
            wander(boss, 60)
            val hash = ((playerX.hashCode() + boss.x.hashCode() + game.frame.hashCode()) and 0xf) + 1
            repeat(18) {
                if (it != hash) {
                    create("DS_BALL_M_A_BLUE", -180f + 20f * it, 0f, 2f, -90f)
                }
            }
            wait(30)
        }
    }

    override fun buildSpellPractice(): Task = buildSpellPractice { AyaBoss() }

}