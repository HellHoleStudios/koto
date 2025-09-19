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

package com.hhs.koto.demo.stage1

import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.graphics.Cutin
import com.hhs.koto.stg.pattern.cast
import com.hhs.koto.stg.pattern.cast2
import com.hhs.koto.stg.pattern.wander
import com.hhs.koto.stg.task.*
import com.hhs.koto.util.*
import ktx.collections.GdxArray

object Stage1Spell1 : BasicSpell<AyaBoss>(AyaBoss::class.java) {
    override val name: String = "stage1.spell1"
    override val availableDifficulties: GdxArray<GameDifficulty> = GameDifficulty.REGULAR_AVAILABLE
    override val health: Float
        get() = difficultySelect(1000f, 1200f, 1400f, 1600f)
    override val maxTime: Int = 1200
    override val bonus: Long
        get() = defaultBonus(1)

    override fun terminate(): Task {
        return object : Task {
            override var alive = true
            override fun tick() {
                game.shaking = 0
                alive = false
            }
        }
    }

    override fun spell(): Task = CoroutineTask {
        val boss = getBoss()

        //Create spellcard cutin effect
        game.stage.addDrawable(Cutin(getRegion("portrait/aya/attack.png")))
        repeat(20) {
            wander(boss, 120) //wander for 120 frames. Touhou bosses love to do this.
            cast(boss.x, boss.y) //cast effect
            wait(90)

            cast2(boss.x, boss.y) //another cast effect
            wait(30)

            boss.usingAction = true //use action animation instead of idle
            repeat(3) {
                ring( //creating patterns is simple!
                    "DS_BALL_M_A_BLUE", //asset name
                    boss.x,
                    boss.y,
                    50f, //radius
                    difficultySelect(8, 12, 16, 20), //number of bullets, easily scaled by difficulty
                    startAngle = random(0f, 360f), //angle
                    speed = 5f, //speed
                )
                wait(20)
            }
            boss.usingAction = false
            wait(30)
        }
    }

    override fun buildSpellPractice(): Task = buildSpellPractice(3) { AyaBoss() }
}