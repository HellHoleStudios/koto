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

package com.hhs.koto.stg

import com.hhs.koto.util.KotoRuntimeException
import com.hhs.koto.util.SystemFlag
import com.hhs.koto.util.VK
import com.hhs.koto.util.app
import java.util.*

class Replay {
    lateinit var name: String
    lateinit var date: Date
    lateinit var stage: String
    var sessionName: String? = null
    var gameMode: GameMode? = null
    var difficulty: GameDifficulty? = null
    var shottype: String? = null

    @Transient
    private val keyHistory = ArrayList<BooleanArray>()
    var frameCount: Int = 0
    private lateinit var encodedHistory: ArrayList<KeyChangeEvent>

    @Transient
    var decoded: Boolean = false

    val checkpoints = ArrayList<Checkpoint>()

    data class KeyChangeEvent(
        val frame: Int,
        val vk: Byte,
        val change: Boolean,
    ) {
        @Suppress("unused")
        private constructor() : this(0, 0, false)
    }

    fun copy(): Replay {
        val result = Replay()
        result.name = name
        result.date = date
        result.stage = stage
        result.sessionName = sessionName
        result.gameMode = gameMode
        result.difficulty = difficulty
        result.shottype = shottype
        result.keyHistory.addAll(keyHistory)
        result.frameCount = frameCount
        result.decoded = decoded
        result.checkpoints.addAll(checkpoints)
        return result
    }

    fun saveSystemFlags() {
        sessionName = SystemFlag.sessionName
        gameMode = SystemFlag.gamemode
        difficulty = SystemFlag.difficulty
        shottype = SystemFlag.shottype
    }

    fun applySystemFlags() {
        SystemFlag.sessionName = sessionName
        SystemFlag.gamemode = gameMode
        SystemFlag.difficulty = difficulty
        SystemFlag.shottype = shottype
    }

    fun logKeys() {
        val pressed = BooleanArray(VK.values().size)
        VK.values().forEach {
            pressed[it.ordinal] = app.input.pressed(it)
        }
        keyHistory.add(pressed)
        frameCount++
    }

    fun encodeKeys() {
        val vkCount = VK.values().size
        var currentState = BooleanArray(vkCount)
        encodedHistory = ArrayList()
        for (frame in 0 until frameCount) {
            for (vk in 0 until vkCount) {
                if (keyHistory[frame][vk] != currentState[vk]) {
                    encodedHistory.add(KeyChangeEvent(frame, vk.toByte(), keyHistory[frame][vk]))
                }
            }
            currentState = keyHistory[frame]
        }
    }

    fun decodeKeys() {
        if (decoded) return
        val vkCount = VK.values().size
        val currentState = BooleanArray(vkCount)
        var i = 0
        keyHistory.clear()
        for (frame in 0 until frameCount) {
            while (i < encodedHistory.size && frame >= encodedHistory[i].frame) {
                currentState[encodedHistory[i].vk.toInt()] = encodedHistory[i].change
                i++
            }
            keyHistory.add(currentState.copyOf())
        }
        decoded = true
    }

    fun createCheckpoint(game: KotoGame, name: String) {
        checkpoints.add(
            Checkpoint(
                name,
                game.frame,
                game.pointValue,
                game.maxPointValue,
                game.pointValueHeight,
                game.score,
                game.highScore,
                game.highScoreAchieved,
                game.maxCredit,
                game.creditCount,
                game.initialLife.copy(),
                game.initialBomb.copy(),
                game.life.copy(),
                game.bomb.copy(),
                game.maxPower,
                game.power,
                game.graze,
                game.inDialog,
                game.random.getState(0),
                game.random.getState(1),
            )
        )
    }

    fun pressed(vk: VK, frame: Int): Boolean {
        if (frame < 0) return false
        if (frame >= keyHistory.size) throw KotoRuntimeException("frame $frame has not been recorded!")
        return keyHistory[frame][vk.ordinal]
    }

    fun justPressed(vk: VK, frame: Int): Boolean {
        return pressed(vk, frame) && (!pressed(vk, frame - 1))
    }
}

data class Checkpoint(
    var name: String,
    var frame: Int,
    var pointValue: Long,
    var maxPointValue: Long,
    var pointValueHeight: Float,
    var score: Long,
    var highScore: Long,
    var highScoreAchieved: Boolean,
    var maxCredit: Int,
    var creditCount: Int,
    var initialLife: FragmentCounter,
    var initialBomb: FragmentCounter,
    var life: FragmentCounter,
    var bomb: FragmentCounter,
    var maxPower: Float,
    var power: Float,
    var graze: Int,
    var inDialog: Boolean,
    var randomSeed0: Long,
    var randomSeed1: Long,
) {
    @Suppress("unused")
    private constructor() : this(
        "",
        0,
        0L,
        0L,
        0f,
        0L,
        0L,
        false,
        0,
        0,
        FragmentCounter(),
        FragmentCounter(),
        FragmentCounter(),
        FragmentCounter(),
        0f,
        0f,
        0,
        false,
        0L,
        0L,
    )

    fun apply(game: KotoGame) {
        game.frame = frame
        game.pointValue = pointValue
        game.maxPointValue = maxPointValue
        game.pointValueHeight = pointValueHeight
        game.score = score
        game.highScore = highScore
        game.highScoreAchieved = highScoreAchieved
        game.maxCredit = maxCredit
        game.creditCount = creditCount
        game.initialLife.set(initialLife)
        game.initialBomb.set(initialBomb)
        game.life.set(life)
        game.bomb.set(bomb)
        game.maxPower = maxPower
        game.power = power
        game.graze = graze
        game.inDialog = inDialog
        game.random.setState(randomSeed0, randomSeed1)
    }
}