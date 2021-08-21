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

package com.hhs.koto.app.screen

import com.hhs.koto.app.ui.GridKeyboard
import com.hhs.koto.app.ui.register
import com.hhs.koto.stg.GameData
import com.hhs.koto.util.*

class SaveScreen : BasicScreen("", getRegion("bg/generic.png")) {
    val keyboard = GridKeyboard {
        val saveObject = SystemFlag.saveObject!!
        if (saveObject is GameData.ScoreEntry) {
            saveObject.name = it
            gameData.currentElement.score.add(saveObject)
            SystemFlag.saveObject = null
            saveGameData()
            onQuit()
        }
    }.register(st, input).apply {
        setPosition(330f, 550f)
    }

    override fun onQuit() {
        super.onQuit()
        app.setScreen("game", 0.5f)
    }
}