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
import com.hhs.koto.stg.graphics.Cutin
import com.hhs.koto.stg.pattern.wander
import com.hhs.koto.stg.task.*
import com.hhs.koto.util.*
import kotlinx.coroutines.yield
import ktx.collections.GdxArray

object Stage2Spell2 : BasicSpell<AyaBoss>(AyaBoss::class.java) {
    override val name: String = "stage2.spell2"
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
//            for(i in 0 until 5){
//            wander(boss, 60)
            val tx = boss.x
            val ty = boss.y

            val b1 = RNGBank<Int>()
            laser(self, 1f, 150f) {
                BasicBullet(tx, ty, 3f, base, defaultShotSheet["DS_BALL_M_A_BLUE"]).apply{destroyable=false}.task {
                    wait(30)
                    var cnt=0
                    var omega=0f
                    while(true){
                        cnt++
                        bullet.angle+=omega
                        if(cnt%10==0){
                            omega=b1.random(cnt,-5f,5f)
//                            omega=random(-5f,5f) NEVER USE THIS
                        }
                        yield()
                    }
                } as BasicBullet
            }

//            laser(self, 8f, 150f, color = BLUE_HSV) {
//                BasicBullet(boss.x, boss.y, 3f, base, defaultShotSheet["DS_BALL_M_A_BLUE"]).task {
//                    wait(30)
//                    while (true) {
//                        bullet.angle--
//                        yield()
//                    }
//                } as BasicBullet
//            }
//            }

            wait(120)
            base += 12.34f
        }
    }

    override fun buildSpellPractice(): Task = buildSpellPractice(1) { AyaBoss() }

}