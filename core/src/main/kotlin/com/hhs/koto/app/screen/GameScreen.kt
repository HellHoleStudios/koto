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

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.hhs.koto.app.Config
import com.hhs.koto.app.Config.h
import com.hhs.koto.app.Config.w
import com.hhs.koto.stg.GameMode
import com.hhs.koto.stg.KotoGame
import com.hhs.koto.util.SystemFlag
import com.hhs.koto.util.app
import com.hhs.koto.util.game
import com.hhs.koto.util.getRegion
import ktx.actors.plusAssign

class GameScreen : BasicScreen(null, null) {
    init {
        game = KotoGame()
        val gameFrame = Image(game.fboTextureRegion)
        gameFrame.setBounds(
            Config.frameOffsetX, Config.frameOffsetY,
            Config.frameWidth, Config.frameHeight
        )
        st += gameFrame
        val gameBackground = Image(getRegion("bg/game.png"))
        gameBackground.setBounds(0f, 0f, Config.screenWidth, Config.screenHeight)
        st += gameBackground
    }

    override fun render(delta: Float) {
        game.update()
        game.draw(st.viewport)
        super.render(delta)
    }

    override fun onQuit() {
        super.onQuit()
        SystemFlag.redirect = when (SystemFlag.gamemode!!) {
            GameMode.STAGE_PRACTICE -> "stageSelect"
            GameMode.SPELL_PRACTICE -> "spellSelect"
            else -> "playerSelect"
        }
        SystemFlag.redirectDuration = 0.5f
        app.setScreen("blank", 0.5f)
    }
}