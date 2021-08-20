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

package com.hhs.koto.app.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.screen.DifficultySelectScreen
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.KotoGame
import com.hhs.koto.util.*
import ktx.graphics.copy

class GameStatus(val game: KotoGame) : Group() {
    companion object {
        fun difficultyColor(difficulty: GameDifficulty): Color =
            DifficultySelectScreen.difficultyColor(difficulty).copy(blue = 1f)
    }

//    val maxScoreLabel: Label
//    val grazeLabel: Label

    init {
        addActor(Label(
            bundle["ui.game.status.difficulty.${SystemFlag.difficulty!!.name.lowercase()}"],
            Label.LabelStyle(
                getFont(
                    bundle["font.difficulty"],
                    32, Color.WHITE,
                    borderWidth = 3f, borderColor = Color(0.8f, 0f, 0f, 1f),
                ),
                difficultyColor(SystemFlag.difficulty!!),
            ),
        ).apply {
            setBounds(980f, 1020f, 410f, 40f);
            setAlignment(Align.bottom)
        })

        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(930f, 960f, 500f, 3f)
            color = Color(0f, 0f, 1f, 0.7f);
        })
        addActor(Label(
            bundle["ui.game.status.highScore"],
            Label.LabelStyle(
                getFont(bundle["font.ui"], 32, Color.RED),
                Color(0f, 0f, 0.7f, 1f),
            ),
        ).apply {
            setBounds(980f, 960f, 410f, 40f)
        })
        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(930f, 920f, 500f, 3f)
            color = Color(0f, 0f, 1f, 0.7f);
        })
        addActor(Label(
            bundle["ui.game.status.score"],
            Label.LabelStyle(getFont(bundle["font.ui"], 32, Color.RED), WHITE_HSV),
        ).apply {
            setBounds(980f, 920f, 410f, 40f)
        })

        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(930f, 810f, 500f, 3f)
            color = Color(0f, 0f, 1f, 0.7f);
        })
        addActor(Label(
            bundle["ui.game.status.life"],
            Label.LabelStyle(
                getFont(bundle["font.ui"], 32, Color.RED),
                Color(0.85f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(980f, 840f, 410f, 40f)
        })
        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(930f, 730f, 500f, 3f)
            color = Color(0f, 0f, 1f, 0.7f);
        })
        addActor(Label(
            bundle["ui.game.status.bomb"],
            Label.LabelStyle(
                getFont(bundle["font.ui"], 32, Color.RED),
                Color(0.4f, 0.3f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(980f, 760f, 410f, 40f)
        })

        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(1040f, 640f, 360f, 3f)
            color = Color(0f, 0f, 1f, 0.7f);
        })
        addActor(Label(
            bundle["ui.game.status.power"],
            Label.LabelStyle(
                getFont(bundle["font.ui"], 32, Color.RED),
                Color(0f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(1070f, 640f, 300f, 40f)
        })
        addActor(Image(getRegion("item/power.png")).apply {
            setBounds(1020f, 640f, 40f, 40f)
        })

        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(1040f, 600f, 360f, 3f)
            color = Color(0f, 0f, 1f, 0.7f);
        })
        addActor(Label(
            bundle["ui.game.status.score"],
            Label.LabelStyle(
                getFont(bundle["font.ui"], 32, Color.RED),
                Color(0.6f, 0.5f, 0.8f, 1f),
            ),
        ).apply {
            setBounds(1070f, 600f, 300f, 40f)
        })
        addActor(Image(getRegion("item/point.png")).apply {
            setBounds(1020f, 600f, 40f, 40f)
        })

        addActor(Image(getRegion("ui/bg.png")).apply {
            setBounds(1040f, 560f, 360f, 3f)
            color = Color(0f, 0f, 1f, 0.7f);
        })
        addActor(Label(
            bundle["ui.game.status.graze"],
            Label.LabelStyle(
                getFont(bundle["font.ui"], 32, Color.RED),
                Color(0f, 0f, 0.7f, 1f),
            ),
        ).apply {
            setBounds(1070f, 560f, 300f, 40f)
        })
    }
}