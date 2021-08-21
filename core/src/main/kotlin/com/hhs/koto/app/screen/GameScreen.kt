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

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.crashinvaders.vfx.VfxManager
import com.crashinvaders.vfx.effects.GaussianBlurEffect
import com.hhs.koto.app.Config
import com.hhs.koto.app.ui.*
import com.hhs.koto.stg.GameBuilder
import com.hhs.koto.stg.GameData
import com.hhs.koto.stg.GameMode
import com.hhs.koto.stg.GameState
import com.hhs.koto.util.*
import ktx.actors.alpha
import ktx.actors.plusAssign
import ktx.actors.then
import java.util.*

class GameScreen : BasicScreen(null, null) {

    val vfxManager = VfxManager(Pixmap.Format.RGBA8888, options.frameWidth, options.frameHeight)
    val blurEffect = GaussianBlurEffect()
    val gameFrame = VfxOutput().apply {
        setBounds(72f, 36f, 864f, 1008f)
        st += this
    }
    val blurredGameFrame = VfxOutput().apply {
        setBounds(72f, 36f, 864f, 1008f)
        st += this
    }
    var gameStatus: GameStatus? = null

    init {
        val gameBackground = Image(getRegion("bg/game.png"))
        gameBackground.setBounds(0f, 0f, Config.screenWidth, Config.screenHeight)
        st += gameBackground

        vfxManager.addEffect(blurEffect)
        blurEffect.amount = 10f
    }

    private val confirmationMenu = ConfirmationMenu(staticX = 680f, staticY = 450f).register(st, input).apply {
        alpha = 0f
        deactivate()
        noRunnable = {
            deactivate()
            input.addProcessor(pauseMenu)
        }
        exitRunnable = noRunnable
    }

    private val pauseMenu = Grid(staticX = 150f, staticY = 400f).register(st, input).apply {
        alpha = 0f
        deactivate()
        activeAction = {
            setPosition(staticX - 200f, staticY)
            parallel(
                fadeIn(0.5f, Interpolation.pow5Out),
                moveTo(staticX, staticY, 0.5f, Interpolation.pow5Out),
            )
        }
        inactiveAction = {
            parallel(
                fadeOut(0.5f, Interpolation.pow5Out),
                moveTo(staticX - 200f, staticY, 0.5f, Interpolation.pow5Out),
            )
        }
    }

    init {
        pauseMenu.add(GridButton(bundle["ui.game.pauseMenu.resume"], 36, gridX = 0, gridY = 1) {
            resumeGame()
        })
        pauseMenu.add(GridButton(bundle["ui.game.pauseMenu.continue"], 36, gridX = 0, gridY = 1) {
            resumeGame()
        }.apply {
            disable()
            isVisible = false
        })
        pauseMenu.add(GridButton(bundle["ui.game.pauseMenu.saveScore"], 36, gridX = 0, gridY = 1) {
            SystemFlag.saveObject = game.createScoreEntry()
            app.setScreen("save", 0.5f)
        }.apply {
            disable()
            isVisible = false
        })
        pauseMenu.add(GridButton(bundle["ui.game.pauseMenu.saveReplay"], 36, gridX = 0, gridY = 2) {
            // TODO
        }.apply {
            disable()
        })
        pauseMenu.add(GridButton(bundle["ui.game.pauseMenu.restart"], 36, gridX = 0, gridY = 3) {
            confirmationMenu.activate()
            confirmationMenu.selectLast()
            input.removeProcessor(pauseMenu)
            confirmationMenu.yesRunnable = {
                confirmationMenu.deactivate()
                pauseMenu.deactivate()
                blurredGameFrame.addAction(
                    sequence(
                        fadeOut(0.5f, Interpolation.sine),
                        Actions.run {
                            game.dispose()
                            reset()
                        },
                    )
                )
                gameFrame.alpha = 0f
            }
        })
        pauseMenu.add(GridButton(bundle["ui.game.pauseMenu.quit"], 36, gridX = 0, gridY = 4) {
            confirmationMenu.activate()
            confirmationMenu.selectLast()
            input.removeProcessor(pauseMenu)
            confirmationMenu.yesRunnable = { quit() }
        })
        pauseMenu.arrange(0f, 200f, 0f, -55f)
        pauseMenu.add(
            GridLabel(
                "", 48, gridX = 0, gridY = 0, staticX = -20f, staticY = 200f
            )
        )
        pauseMenu.selectFirst()
    }

    private var paused: Boolean = false
    private var passCounter: Int = 0
    private var deltaTimeCounter: Float = 0f

    override fun render(delta: Float) {
        if (state != ScreenState.SHOWN) {
            super.render(delta)
            return
        }
        if (!paused && game.state != GameState.RUNNING) {
            pauseGame()
        }
        if (paused) {
            deltaTimeCounter += delta
            if (game.state == GameState.PAUSED &&
                passCounter >= 30 && VK.PAUSE.justPressed() && !blurredGameFrame.hasActions()
            ) {
                resumeGame()
            } else if (VK.RESTART.justPressed()) {
                //same as above :) It's duplication!! Quick give me C!!
                SE.play("ok")
                confirmationMenu.deactivate()
                pauseMenu.deactivate()
                blurredGameFrame.addAction(
                    sequence(
                        fadeOut(0.5f, Interpolation.sine),
                        Actions.run {
                            game.dispose()
                            reset()
                        },
                    )
                )
                gameFrame.alpha = 0f
            } else if (deltaTimeCounter >= 1 / 60f) {
                if (passCounter < 30) {
                    if (passCounter == 0) {
                        blurredGameFrame.alpha = 1f
                        vfxManager.useAsInput(game.postVfx.resultBuffer.texture)
                    } else {
                        vfxManager.useAsInput(vfxManager.resultBuffer.texture)
                    }
                    vfxManager.applyEffects()
                    passCounter++
                }
                deltaTimeCounter = 0f
            }

        } else {
            // TODO replay check
            if (SystemFlag.gamemode!!.isPractice()) {
                gameData.practiceTime += delta
            } else {
                gameData.playTime += delta
            }
            game.update()
        }
        super.render(delta)
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        if (oldScreen !is SaveScreen) reset()
    }

    fun resumeGame() {
        blurredGameFrame.addAction(
            fadeOut(0.5f, Interpolation.sine) then Actions.run {
                paused = false
                game.state = GameState.RUNNING
            }
        )
        pauseMenu.deactivate()
        SE.resume()
    }

    fun pauseGame() {
        SE.pause()
        SE.play("pause")
        paused = true
        passCounter = 0
        deltaTimeCounter = 0f
        val resumeButton = pauseMenu[0] as GridButton
        val continueButton = pauseMenu[1] as GridButton
        val saveScoreButton = pauseMenu[2] as GridButton
        val saveReplayButton = pauseMenu[3] as GridButton
        resumeButton.enabled = game.state == GameState.PAUSED
        resumeButton.isVisible = game.state == GameState.PAUSED
        continueButton.enabled = game.state == GameState.GAME_OVER
        continueButton.isVisible = game.state == GameState.GAME_OVER || game.state == GameState.GAME_OVER_NO_CREDIT
        saveScoreButton.enabled = game.state == GameState.FINISH
        saveScoreButton.isVisible = game.state == GameState.FINISH || game.state == GameState.FINISH_PRACTICE
        saveReplayButton.enabled = game.creditCount == 0

        (pauseMenu.grid.last() as GridLabel).setText(
            when (game.state) {
                GameState.PAUSED -> bundle["ui.game.pauseMenu.paused"]
                GameState.GAME_OVER, GameState.GAME_OVER_NO_CREDIT -> bundle["ui.game.pauseMenu.gameOver"]
                GameState.FINISH -> bundle["ui.game.pauseMenu.finish"]
                GameState.FINISH_PRACTICE -> bundle["ui.game.pauseMenu.finishPractice"]
                else -> ""
            }
        )
        pauseMenu.selectFirst()
        pauseMenu.activate()
    }

    fun reset() {
        SE.stop()
        pauseMenu.selectFirst()
        pauseMenu.alpha = 0f
        pauseMenu.setPosition(pauseMenu.staticX - 200f, pauseMenu.staticY)
        pauseMenu.deactivate()
        if (pauseMenu !in input.processors) input.addProcessor(pauseMenu)

        confirmationMenu.alpha = 0f
        confirmationMenu.setPosition(confirmationMenu.staticX - 200f, confirmationMenu.staticY)
        confirmationMenu.deactivate()

        GameBuilder.build()
        gameStatus?.remove()
        gameStatus = GameStatus(game)
        st += gameStatus!!

        gameFrame.vfxManager = game.postVfx
        gameFrame.alpha = 1f

        blurredGameFrame.vfxManager = vfxManager
        blurredGameFrame.alpha = 0f

        paused = false
    }

    private fun quit() {
        SE.stop()
        SE.play("cancel")
        game.dispose()
        SystemFlag.redirect = when (SystemFlag.gamemode!!) {
            GameMode.STAGE_PRACTICE -> "stageSelect"
            GameMode.SPELL_PRACTICE -> "spellSelect"
            else -> "playerSelect"
        }
        SystemFlag.redirectDuration = 0.5f
        app.setScreen("blank", 0.5f)
    }

    override fun onQuit() = Unit

    override fun dispose() {
        super.dispose()
        vfxManager.dispose()
        blurEffect.dispose()
    }
}