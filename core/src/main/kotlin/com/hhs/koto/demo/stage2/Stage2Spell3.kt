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

package com.hhs.koto.demo.stage2

import com.hhs.koto.demo.stage1.AyaBoss
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.bullet.BasicBullet
import com.hhs.koto.stg.bullet.StaticLaser
import com.hhs.koto.stg.graphics.Cutin
import com.hhs.koto.stg.pattern.wander
import com.hhs.koto.stg.task.*
import com.hhs.koto.util.*
import kotlinx.coroutines.yield
import ktx.collections.GdxArray

object Stage2Spell3 : BasicSpell<AyaBoss>(AyaBoss::class.java) {
    override val name: String = "stage2.spell3"
    override val availableDifficulties: GdxArray<GameDifficulty> = GameDifficulty.REGULAR_AVAILABLE

    override val health: Float
        get() = 10000f
    override val maxTime: Int = 30000
    override val bonus: Long
        get() = defaultBonus(2)

    override fun spell(): Task = CoroutineTask {
        val boss = getBoss()
        game.stage.addDrawable(Cutin(getRegion("portrait/aya/attack.png")))
        var base = 0f
        while (true) {
//
            staticLaser(defaultShotSheet["DS_BALL_M_A_BLUE"],boss.x,boss.y,350f,18f,base,style=1).task{
                var cnt=0f
                while(true){
                    bullet.angle+=sin(cnt)*1.5f
                    yield()
                    cnt++
                }
            }

            wait(120)
            base += 12.34f
        }
    }

    override fun buildSpellPractice(): Task = buildSpellPractice(1) { AyaBoss() }

}