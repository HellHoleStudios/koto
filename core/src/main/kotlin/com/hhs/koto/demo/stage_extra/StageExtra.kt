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

package com.hhs.koto.demo.stage_extra

import com.badlogic.gdx.graphics.Color
import com.hhs.koto.app.Config.worldOriginX
import com.hhs.koto.app.Config.worldW
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.graphics.BGMNameDisplay
import com.hhs.koto.stg.graphics.TileBackground
import com.hhs.koto.stg.pattern.interpolate
import com.hhs.koto.stg.task.BasicStage
import com.hhs.koto.stg.task.CoroutineTask
import com.hhs.koto.stg.task.attachAndWait
import com.hhs.koto.stg.task.wait
import com.hhs.koto.util.BGM
import com.hhs.koto.util.game
import com.hhs.koto.util.getRegion

object StageExtra : BasicStage() {
    override val availableDifficulties = GameDifficulty.EXTRA_AVAILABLE
    override val name = "stageExtra"
    override val isExtraStage: Boolean = true

    override fun stage() = CoroutineTask {
        game.background.addDrawable(
            TileBackground(
                getRegion("st1/water.png"),
                -10000,
                speedY = -4f,
                tileWidth = 128f,
                tileHeight = 256f,
            )
        )
        game.background.addDrawable(
            TileBackground(
                getRegion("st1/water_overlay.png"),
                -10000,
                speedY = -6f,
                tileWidth = 128f,
                tileHeight = 128f,
                color = Color(1f, 1f, 1f, 0.3f)
            )
        )
        game.background.addDrawable(
            TileBackground(
                getRegion("st1/shore_left.png"),
                -10000,
                speedY = -5f,
                tileWidth = 128f,
                tileHeight = 256f,
                width = 128f,
            )
        )
        game.background.addDrawable(
            TileBackground(
                getRegion("st1/shore_right.png"),
                -10000,
                speedY = -5f,
                x = worldW - 128f - worldOriginX,
                tileWidth = 128f,
                tileHeight = 256f,
                width = 128f,
            )
        )
        BGM.play(1, true)
        game.hud.addDrawable(BGMNameDisplay(1))

        interpolate(0f, 1f, 60) { game.background.alpha = it }

        MidStageExtra.build().attachAndWait()
        wait(240)
        defaultBonus(7)
    }
}