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

import com.badlogic.gdx.utils.Array
import com.hhs.koto.util.SystemFlag

/**
 * Handles game replay. Need to be injected to the game using [ReplayLayer]
 *
 * @author XGN
 */
class Replay {
    /**
     * Saved replay name
     */
    var user = "Untitled"

    /**
     * [SystemFlag.name]
     */
    var name = ""

    /**
     * [SystemFlag.difficulty]
     */
    var difficulty = ""

    /**
     * [SystemFlag.player]
     */
    var player = ""

    /**
     * [SystemFlag.gamemode]
     */
    var mode = GameMode.STORY

    /**
     * Replay save date
     */
    var date = 0L

    /**
     * Key mask for each frame
     */
    var mask = Array<Int>()

    var startPoint = Array<StartPoint>()

}

/**
 * Specifies a start point
 * @author XGN
 */
class StartPoint(var frame: Int, var rng: Long, var name: String) {
    val mp = HashMap<String, String>()

    operator fun get(index: String): String {
        return mp[index]!!
    }

    operator fun set(index: String, value: String) {
        mp[index] = value
    }
}