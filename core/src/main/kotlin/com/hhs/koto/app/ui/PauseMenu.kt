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

package com.hhs.koto.app.ui

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.hide
import com.badlogic.gdx.scenes.scene2d.actions.Actions.show
import com.hhs.koto.app.screen.GameScreen
import com.hhs.koto.stg.GameState
import com.hhs.koto.util.SystemFlag
import com.hhs.koto.util.bundle
import com.hhs.koto.util.game
import ktx.actors.alpha
import ktx.actors.then


class PauseMenu(val screen: GameScreen, val st: Stage, val input: InputMultiplexer) : Group() {

    // confirmationMenu must be registered before pauseMenu!!
    val confirmationMenu = ConfirmationMenu(staticX = 680f, staticY = 450f).register(st, input).apply {
        alpha = 0f
        deactivate()
        noRunnable = {
            deactivate()
            input.addProcessor(pauseMenu)
        }
        exitRunnable = noRunnable
    }
    val pauseMenu = Grid(staticX = 150f, staticY = 400f).register(st, input).apply {
        alpha = 0f
        deactivate()
        activeAction = {
            setPosition(staticX - 200f, staticY)
            Actions.parallel(
                show() then Actions.fadeIn(0.5f, Interpolation.pow5Out),
                Actions.moveTo(staticX, staticY, 0.5f, Interpolation.pow5Out),
            )
        }
        inactiveAction = {
            Actions.parallel(
                Actions.fadeOut(0.5f, Interpolation.pow5Out) then hide(),
                Actions.moveTo(staticX - 200f, staticY, 0.5f, Interpolation.pow5Out),
            )
        }
    }
    val saveMenu = SaveMenu(st, input, 240f, 400f) {
        pauseMenu.activate()
    }

    init {
        pauseMenu.add(GridButton(bundle["ui.game.pauseMenu.resume"], 36, gridX = 0, gridY = 1) {
            screen.resumeGame()
        })
        pauseMenu.add(GridButton(bundle["ui.game.pauseMenu.continue"], 36, gridX = 0, gridY = 1) {
            screen.resumeGame()
        }.apply {
            disable()
            isVisible = false
        })
        pauseMenu.add(GridButton(bundle["ui.game.pauseMenu.saveScore"], 36, gridX = 0, gridY = 1) {
            saveMenu.activate(game.createScoreEntry())
            pauseMenu.deactivate()
        }.apply {
            disable()
            isVisible = false
        })
        pauseMenu.add(GridButton(bundle["ui.game.pauseMenu.saveReplay"], 36, gridX = 0, gridY = 2) {
            saveMenu.activate(game.replay)
            pauseMenu.deactivate()
        }.apply {
            disable()
        })
        pauseMenu.add(GridButton(bundle["ui.game.pauseMenu.restart"], 36, gridX = 0, gridY = 3) {
            confirmationMenu.activate()
            confirmationMenu.selectLast()
            input.removeProcessor(pauseMenu)
            confirmationMenu.yesRunnable = {
                if (screen.retryGame()) confirmationMenu.deactivate()
            }
        })
        pauseMenu.add(GridButton(bundle["ui.game.pauseMenu.quit"], 36, gridX = 0, gridY = 4) {
            confirmationMenu.activate()
            confirmationMenu.selectLast()
            input.removeProcessor(pauseMenu)
            confirmationMenu.yesRunnable = { screen.quit() }
        })
        pauseMenu.arrange(0f, 200f, 0f, -55f)
        pauseMenu.add(
            GridLabel(
                "", 48, gridX = 0, gridY = 0, staticX = -20f, staticY = 200f
            )
        )
        pauseMenu.selectFirst()
    }

    fun activate() {
        val resumeButton = pauseMenu[0] as GridButton
        val continueButton = pauseMenu[1] as GridButton
        val saveScoreButton = pauseMenu[2] as GridButton
        val saveReplayButton = pauseMenu[3] as GridButton
        resumeButton.enabled = game.state == GameState.PAUSED
        resumeButton.isVisible = game.state == GameState.PAUSED
        continueButton.enabled = game.state == GameState.GAME_OVER && SystemFlag.replay == null
        continueButton.isVisible = game.state == GameState.GAME_OVER || game.state == GameState.GAME_OVER_NO_CREDIT
        saveScoreButton.enabled =
            game.state == GameState.FINISH && SystemFlag.replay == null && SystemFlag.difficulty!!.isRegular()
        saveScoreButton.isVisible = game.state == GameState.FINISH || game.state == GameState.FINISH_PRACTICE
        saveReplayButton.enabled = game.creditCount == 0 && SystemFlag.replay == null

        (pauseMenu.grid.last() as GridLabel).setText(
            if (SystemFlag.replay == null) {
                when (game.state) {
                    GameState.PAUSED -> bundle["ui.game.pauseMenu.paused"]
                    GameState.GAME_OVER, GameState.GAME_OVER_NO_CREDIT -> bundle["ui.game.pauseMenu.gameOver"]
                    GameState.FINISH -> bundle["ui.game.pauseMenu.finish"]
                    GameState.FINISH_PRACTICE -> bundle["ui.game.pauseMenu.finishPractice"]
                    else -> ""
                }
            } else {
                if (game.state == GameState.PAUSED) {
                    bundle["ui.game.pauseMenu.paused"]
                } else {
                    bundle["ui.game.pauseMenu.finishReplay"]
                }
            }
        )
        pauseMenu.selectFirst()
        pauseMenu.activate()
    }

    fun deactivate() {
        pauseMenu.deactivate()
    }

    fun reset() {
        pauseMenu.selectFirst()
        pauseMenu.alpha = 0f
        pauseMenu.setPosition(pauseMenu.staticX - 200f, pauseMenu.staticY)
        pauseMenu.deactivate()
        if (pauseMenu !in input.processors) input.addProcessor(pauseMenu)

        confirmationMenu.alpha = 0f
        confirmationMenu.setPosition(confirmationMenu.staticX - 200f, confirmationMenu.staticY)
        confirmationMenu.deactivate()
    }
}