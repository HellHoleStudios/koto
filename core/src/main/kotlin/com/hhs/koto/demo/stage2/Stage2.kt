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
import com.hhs.koto.stg.graphics.BGMNameDisplay
import com.hhs.koto.stg.graphics.TileBackground
import com.hhs.koto.stg.pattern.interpolate
import com.hhs.koto.stg.pattern.move
import com.hhs.koto.stg.task.BasicStage
import com.hhs.koto.stg.task.CoroutineTask
import com.hhs.koto.stg.task.attachAndWait
import com.hhs.koto.stg.task.wait
import com.hhs.koto.util.BGM
import com.hhs.koto.util.game
import com.hhs.koto.util.getRegion

object Stage2 : BasicStage() {
    override val availableDifficulties = GameDifficulty.REGULAR_AVAILABLE
    override val name = "stage2"
    override val isFinalStage: Boolean = true

    override fun stage() = CoroutineTask {
        game.background.addDrawable(
            TileBackground(
                getRegion("st2/sunset.png"),
                -10000,
            )
        )
        BGM.play(1, true)
        game.hud.addDrawable(BGMNameDisplay(1))

        interpolate(0f, 1f, 60) { game.globalAlpha = it }

        attachAndWait(MidStage2.build())

        val boss = game.addBoss(AyaBoss())
        game.bossNameDisplay.show(boss, 1)
        boss.healthBar.addSpell(Stage2Spell1)
        attachAndWait(boss.creationTask())

        attachAndWait(Stage2Spell1.build())

        boss.healthBar.visible = false
        game.bossNameDisplay.hide()
        move(boss, -300f, 300f, 120)
        boss.kill()
        defaultBonus(2)
        wait(60)
    }
}