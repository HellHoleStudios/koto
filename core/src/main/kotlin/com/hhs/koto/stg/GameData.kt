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

import com.hhs.koto.util.SystemFlag
import ktx.collections.GdxArray
import ktx.collections.GdxMap
import java.util.*

data class GameData(
    var playTime: Double = 0.0,
    var playCount: Int = 0,
    var practiceTime: Double = 0.0,
    var practiceCount: Int = 0,
    var deathCount: Int = 0,
    var bombCount: Int = 0,
    var clearCount: Int = 0,
    var spellPracticeUnlocked: Boolean = false,
    var musicUnlocked: GdxArray<Boolean> = GdxArray(),
    val data: GdxMap<String, ShottypeElement> = GdxMap(),
) {
    val currentElement: GameDataElement
        get() = data[SystemFlag.shottype!!].data[SystemFlag.difficulty!!.name]

    data class ShottypeElement(
        var extraUnlocked: Boolean = false,
        val data: GdxMap<String, GameDataElement> = GdxMap(),
    )

    data class GameDataElement(
        val score: GdxArray<ScoreEntry> = GdxArray(),
        val practiceUnlocked: GdxMap<String, Boolean> = GdxMap(),
        val practiceHighScore: GdxMap<String, Long> = GdxMap(),
        val spell: GdxMap<String, SpellEntry> = GdxMap(),
    )

    data class ScoreEntry(
        var name: String,
        var date: Date,
        var score: Long,
        var creditCount: Int,
    ) {
        @Suppress("unused")
        private constructor() : this("", Date(), 0L, 0)
    }

    data class SpellEntry(
        var practiceUnlocked: Boolean = false,
        var highScore: Long = 0L,
        var totalAttempt: Int = 0,
        var successfulAttempt: Int = 0,
    )
}