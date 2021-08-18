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

package com.hhs.koto.stg.task

import com.hhs.koto.stg.drawable.BasicBoss
import com.hhs.koto.stg.drawable.Boss
import com.hhs.koto.util.SystemFlag
import com.hhs.koto.util.game
import com.hhs.koto.util.gameData
import com.hhs.koto.util.saveGameData
import kotlinx.coroutines.yield
import kotlin.math.roundToLong

abstract class BasicSpell<T : Boss>(protected val bossClass: Class<T>) : SpellBuilder {
    abstract val health: Float
    abstract val maxTime: Int
    abstract val bonus: Long
    open val isNonSpell: Boolean = false
    open val isSurvival: Boolean = false

    abstract fun spell(): Task
    open fun terminate(): Task = EmptyTask()

    companion object {
        fun defaultBonus(stage: Int): Long = (2 * stage + 3 * SystemFlag.difficulty!!.ordinal) * 1000000L
    }

    fun getBoss(): T = game.findBoss(bossClass)!!

    override fun build(): Task =
        CoroutineTask {
            val spellTask = spell()
            val boss = getBoss()
            var t = 0
            var failedBonus = false
            val bombListener = game.addListener("player.bomb") {
                failedBonus = true
            }
            val deathListener = game.addListener("player.death") {
                failedBonus = true
            }
            gameData.currentElement.spell[name].totalAttempt++
            saveGameData()
            task {
                while (true) {
                    if (t >= maxTime || boss.healthBar.currentSegmentDepleted()) {
                        if (spellTask.alive) spellTask.kill()
                        break
                    }
                    yield()
                    t++
                    game.spellTimer.tickTime()
                }
            }
            if (!isNonSpell && boss is BasicBoss) {
                self.attachTask(boss.createSpellBackground())
            }
            if (!isNonSpell) game.bossNameDisplay.nextSpell()
            game.spellTimer.show(maxTime)

            attachAndWait(spellTask)

            boss.healthBar.nextSegment()
            game.bullets.forEach {
                it.destroy()
            }
            game.enemies.forEach {
                it.destroy()
            }
            if (!isNonSpell) {
                if (boss is BasicBoss) {
                    self.attachTask(boss.removeSpellBackground())
                }
                if (!failedBonus) {
                    // TODO Spell Bonus animation
                    gameData.currentElement.spell[name].successfulAttempt++
                    saveGameData()
                    game.score += getBonus(t)
                }
            }
            game.spellTimer.hide()
            game.removeListener("player.bomb", bombListener)
            game.removeListener("player.death", deathListener)

            attachAndWait(terminate())
        }

    open fun getBonus(time: Int): Long = if (isSurvival) {
        bonus
    } else {
        if (time <= 300) {
            bonus
        } else {
            val factor = 1 - (time - 300).toFloat() / (maxTime - 300) * 2f / 3f
            (bonus * factor).roundToLong()
        }
    }

    fun <T : BasicBoss> buildSpellPractice(bossBuilder: () -> T): Task = CoroutineTask {
        val boss = bossBuilder()
        game.addBoss(boss)
        game.bossNameDisplay.show(boss, 1)
        boss.healthBar.addSpell(this@BasicSpell)
        attachAndWait(boss.creationTask())
        attachAndWait(this@BasicSpell.build())
        game.bossNameDisplay.hide()
        boss.healthBar.visible = false
        game.end()
    }
}