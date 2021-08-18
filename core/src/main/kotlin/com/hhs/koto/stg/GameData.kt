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

package com.hhs.koto.stg

import com.hhs.koto.util.SystemFlag
import ktx.collections.GdxArray
import ktx.collections.GdxMap
import java.util.*

data class GameData(
    var playTime: Int = 0,
    var playCount: Int = 0,
    var practiceTime: Int = 0,
    var practiceCount: Int = 0,
    var missCount: Int = 0,
    var clearCount: Int = 0,
    val data: GdxMap<String, GdxMap<String, GameDataElement>> = GdxMap(),
) {
    val currentElement: GameDataElement
        get() = data[SystemFlag.player!!][SystemFlag.difficulty!!.name]

    data class GameDataElement(
        val score: GdxArray<ScoreEntry> = GdxArray(),
        val spell: GdxMap<String, SpellHistory> = GdxMap(),
    )

    data class ScoreEntry(
        val name: String,
        val time: Date,
        val score: Long,
        val retryCount: Int,
        val percentage: Float,
    )

    data class SpellHistory(
        var totalAttempt: Int = 0,
        var successfulAttempt: Int = 0,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameData

        if (playTime != other.playTime) return false
        if (playCount != other.playCount) return false
        if (clearCount != other.clearCount) return false
        if (data != other.data) return false

        return true
    }
}