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

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.ui.Grid
import com.hhs.koto.app.ui.GridButton
import com.hhs.koto.app.ui.GridImage
import com.hhs.koto.app.ui.ScrollingGrid
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.GameMode
import com.hhs.koto.util.*

class DifficultySelectScreen : BasicScreen("mus/E0120.ogg", getRegion("bg/generic.png")) {
    val grid = ScrollingGrid(
        staticX = 350f,
        staticY = 300f,
        animationDuration = 1f,
        interpolation = Interpolation.pow5Out
    ).apply {
        st.addActor(this)
        input.addProcessor(this)
    }

    companion object {
        fun generateButton(
            switchTarget: String,
            difficulty: GameDifficulty,
            text: String,
            description: String,
            color: Color,
            gridX: Int,
            gridY: Int,
        ) = Grid(gridX = gridX, gridY = gridY, width = 720f, height = 360f).add(
            GridImage(
                getRegion("ui/diff_bg.png"),
                width = 720f,
                height = 360f,
                tint = color,
            ) {
                SystemFlag.difficulty = difficulty
                app.setScreen(switchTarget, 0.5f)
            }
        ).add(
            GridButton(text, 140, staticY = 150f, width = 720f, height = 200f).apply {
                setAlignment(Align.bottom)
            }
        ).add(
            GridButton(
                description,
                staticY = 50f,
                width = 720f,
                height = 130f,
                activeStyle = Label.LabelStyle(
                    getFont(bundle["font.boldItalic"], 48, borderColor = null), Color.WHITE
                ),
            ).apply {
                activeAction = getActiveAction()
                inactiveAction = getInactiveAction()
                setAlignment(Align.center)
            }
        ).apply {
            setOrigin(Align.center)
            activeAction = { Actions.scaleTo(1f, 1f, 0.5f, Interpolation.pow5Out) }
            inactiveAction = { Actions.scaleTo(0.6f, 0.6f, 0.5f, Interpolation.pow5Out) }
        }.selectFirst()
    }

    init {
        grid.add(
            generateButton(
                "playerSelect",
                GameDifficulty.EASY,
                bundle["ui.difficultySelect.easy.text"],
                bundle["ui.difficultySelect.easy.description"],
                Color(0.29f, 1f, 0.72f, 1f),
                0,
                -4,
            )
        )
        grid.add(
            generateButton(
                "playerSelect",
                GameDifficulty.NORMAL,
                bundle["ui.difficultySelect.normal.text"],
                bundle["ui.difficultySelect.normal.description"],
                Color(0.46f, 1f, 0.72f, 1f),
                0,
                -3,
            )
        )
        grid.add(
            generateButton(
                "playerSelect",
                GameDifficulty.HARD,
                bundle["ui.difficultySelect.hard.text"],
                bundle["ui.difficultySelect.hard.description"],
                Color(0.6f, 1f, 0.72f, 1f),
                0,
                -2,
            )
        )
        grid.add(
            generateButton(
                "playerSelect",
                GameDifficulty.LUNATIC,
                bundle["ui.difficultySelect.lunatic.text"],
                bundle["ui.difficultySelect.lunatic.description"],
                Color(0.74f, 1f, 0.72f, 1f),
                0,
                -1,
            )
        )
        grid.add(
            generateButton(
                "playerSelect",
                GameDifficulty.EXTRA,
                bundle["ui.difficultySelect.extra.text"],
                bundle["ui.difficultySelect.extra.description"],
                Color(0f, 1f, 0.72f, 1f),
                0,
                0,
            )
        )
        grid.arrange(0f, 0f, 0f, -250f)
        grid.selectFirst()
        grid.finishAnimation()
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        for (i in 0..3) {
            grid[i].active = SystemFlag.gamemode!!.hasRegularDifficulty()
            grid[i].enabled = SystemFlag.gamemode!!.hasRegularDifficulty()
            (grid[i] as Actor).isVisible = SystemFlag.gamemode!!.hasRegularDifficulty()
        }
        grid[4].active = SystemFlag.gamemode!!.hasExtraDifficulty()
        grid[4].enabled = SystemFlag.gamemode!!.hasExtraDifficulty()
        (grid[4] as Actor).isVisible = SystemFlag.gamemode!!.hasExtraDifficulty()

        if (SystemFlag.difficulty != null) {
            val buttonIndex = SystemFlag.difficulty!!.ordinal
            if (buttonIndex < grid.grid.size && grid[buttonIndex].enabled) grid.select(grid[buttonIndex])
            else grid.selectFirst()
        } else {
            grid.selectFirst()
        }
    }

    override fun onQuit() {
        super.onQuit()
        app.setScreen("title", 0.5f)
    }
}