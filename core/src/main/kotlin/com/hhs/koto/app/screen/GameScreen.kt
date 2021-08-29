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
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut
import com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.crashinvaders.vfx.VfxManager
import com.crashinvaders.vfx.effects.GaussianBlurEffect
import com.hhs.koto.app.Config
import com.hhs.koto.app.ui.GameStatus
import com.hhs.koto.app.ui.PauseMenu
import com.hhs.koto.app.ui.VfxOutput
import com.hhs.koto.stg.GameBuilder
import com.hhs.koto.stg.GameMode
import com.hhs.koto.stg.GameState
import com.hhs.koto.util.*
import ktx.actors.alpha
import ktx.actors.plusAssign
import ktx.actors.then

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

    private val pauseMenu = PauseMenu(this, st, input)

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
                SE.play("ok")
                retryGame()
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
            if (SystemFlag.replay == null) {
                if (SystemFlag.gamemode!!.isPractice()) {
                    gameData.practiceTime += delta
                } else {
                    gameData.playTime += delta
                }
            }
            game.update()
        }
        super.render(delta)
        if (!paused) {
            game.overlay.draw()
        }
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        reset()
    }

    fun retryGame() {
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

    fun resumeGame() {
        blurredGameFrame.addAction(
            fadeOut(0.5f, Interpolation.sine) then Actions.run {
                paused = false
                game.state = GameState.RUNNING
                SE.resume()
                BGM.resume()
            }
        )
        pauseMenu.deactivate()
    }

    fun pauseGame() {
        SE.pause()
        SE.play("pause")
        BGM.pause()
        paused = true
        passCounter = 0
        deltaTimeCounter = 0f
        pauseMenu.activate()
    }

    fun reset() {
        SE.stop()
        pauseMenu.reset()

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

    fun quit() {
        SE.stop()
        SE.play("cancel")
        game.dispose()
        SystemFlag.redirect = if (SystemFlag.replay == null) when (SystemFlag.gamemode!!) {
            GameMode.STAGE_PRACTICE -> "stageSelect"
            GameMode.SPELL_PRACTICE -> "spellSelect"
            else -> "playerSelect"
        }
        else "replay"
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