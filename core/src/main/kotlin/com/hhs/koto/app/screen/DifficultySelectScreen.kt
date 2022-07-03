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

package com.hhs.koto.app.screen

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.Config
import com.hhs.koto.app.ui.*
import com.hhs.koto.stg.GameBuilder
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.util.*
import ktx.collections.lastIndex

class DifficultySelectScreen : BasicScreen(Config.uiBgm, getRegion(Config.uiBackground)) {
    val grid = ScrollingGrid(
        staticX = 420f,
        staticY = 360f,
        animationDuration = 1f,
        interpolation = Interpolation.pow5Out
    ).register(st, input)

    companion object {
        fun difficultyColor(difficulty: GameDifficulty): Color = when (difficulty) {
            GameDifficulty.EASY -> Color(0.29f, 1f, 0.72f, 1f)
            GameDifficulty.NORMAL -> Color(0.5f, 1f, 0.72f, 1f)
            GameDifficulty.HARD -> Color(0.6f, 1f, 0.72f, 1f)
            GameDifficulty.LUNATIC -> Color(0.74f, 1f, 0.72f, 1f)
            GameDifficulty.EXTRA -> Color(0f, 1f, 0.72f, 1f)
            GameDifficulty.PHANTASM -> Color(0.88f, 0.7f, 0.9f, 1f)
        }

        fun generateButton(
            difficulty: GameDifficulty,
            gridX: Int,
            gridY: Int,
        ) = Grid(gridX = gridX, gridY = gridY, width = 600f, height = 240f).add(
            GridImage(
                getRegion("ui/bg.png"),
                width = 600f,
                height = 240f,
                tint = difficultyColor(difficulty),
            ) {
                SystemFlag.difficulty = difficulty
                app.setScreen("playerSelect", 0.5f)
            }
        ).add(
            GridButton(
                bundle["ui.difficultySelect.${difficulty.name.lowercase()}.text"],
                140,
                staticY = 60f,
                width = 600f,
                height = 180f,
                triggerSound = null,
            ).apply {
                setAlignment(Align.bottom)
            }
        ).add(
            GridButton(
                bundle["ui.difficultySelect.${difficulty.name.lowercase()}.description"],
                staticY = 30f,
                width = 600f,
                height = 30f,
                activeStyle = Label.LabelStyle(
                    getFont(48, bundle["font.subtitle"], borderColor = null), WHITE_HSV
                ),
                triggerSound = null,
            ).apply {
                activeAction = getActiveAction()
                inactiveAction = getInactiveAction()
                setAlignment(Align.center)
            }
        ).apply {
            setOrigin(Align.center)
            activeAction = { scaleTo(1f, 1f, 0.5f, Interpolation.pow5Out) }
            inactiveAction = { scaleTo(0.6f, 0.6f, 0.5f, Interpolation.pow5Out) }
        }.selectFirst()
    }

    init {
        for (i in 0 until GameBuilder.usedDifficulties.size) {
            grid.add(
                generateButton(
                    GameBuilder.usedDifficulties[i],
                    0,
                    -(GameBuilder.usedDifficulties.lastIndex - i),
                )
            )
        }
        grid.arrange(0f, 0f, 0f, -220f)
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

        grid.update()

        if (grid[1].enabled) {
            grid.select(grid[1], true)
        } else {
            grid.selectFirst()
        }
        if (SystemFlag.difficulty != null) {
            val buttonIndex = SystemFlag.difficulty!!.ordinal
            if (buttonIndex < grid.grid.size && grid[buttonIndex].enabled) grid.select(grid[buttonIndex], true)
        }

        grid.finishAnimation()
    }

    override fun onQuit() {
        super.onQuit()
        app.setScreen("title", 0.5f)
    }
}