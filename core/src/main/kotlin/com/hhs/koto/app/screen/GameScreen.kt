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

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut
import com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
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
import java.util.*

class GameScreen : BasicScreen(null, null) {

    val blurVfxManager = VfxManager(Pixmap.Format.RGBA8888, options.frameBufferWidth, options.frameBufferHeight)
    val blurEffect = GaussianBlurEffect()
    val gameFrame = VfxOutput().apply {
        setBounds(72f, 36f, Config.frameWidth, Config.frameHeight)
        st += this
    }
    val blurredGameFrame = VfxOutput().apply {
        setBounds(72f, 36f, Config.frameWidth, Config.frameHeight)
        st += this
    }
    var gameStatus: GameStatus? = null

    var rng = Random()

    init {
        val gameBackground = Image(getRegion("bg/game.png"))
        gameBackground.setBounds(0f, 0f, Config.screenWidth, Config.screenHeight)
        st += gameBackground

        blurVfxManager.addEffect(blurEffect)
        blurEffect.amount = 10f
    }

    private val pauseMenu = PauseMenu(this, st, input)

    private var target: ScreenTarget = ScreenTarget.RUNNING

    private enum class ScreenTarget {
        RUNNING, PAUSE, RETRY
    }

    private var pauseCounter: Int = 0
    private val pauseTime: Int = 30
    private var deltaTimeCounter: Float = 0f
    private var oldOverlayY: Float = 0f
    private var oldOverlayAlpha: Float = 1f

    override fun render(delta: Float) {
        if (state != ScreenState.SHOWN) {
            super.render(delta)
            return
        }

        if (pauseCounter == 0 && game.state != GameState.RUNNING) {
            pauseGame()
        }
        if (target == ScreenTarget.PAUSE || pauseCounter != 0) {
            deltaTimeCounter += delta
        } else {
            deltaTimeCounter = 0f
        }

        if (target == ScreenTarget.PAUSE) {
            if (pauseCounter >= pauseTime &&
                VK.PAUSE.justPressed() && !blurredGameFrame.hasActions() && !pauseMenu.saveMenu.active
            ) {
                resumeGame()
            } else if (SystemFlag.gamemode!!.isPractice() && VK.RESTART.justPressed()) {
                if (retryGame()) SE.play("ok")
            } else {
                while (deltaTimeCounter > 0f) {
                    deltaTimeCounter = (deltaTimeCounter - 1 / 60f).coerceAtLeast(0f)
                    if (pauseCounter < pauseTime) {
                        if (pauseCounter == 0) {
                            blurVfxManager.useAsInput(game.postVfx.resultBuffer.texture)
                        } else {
                            blurVfxManager.useAsInput(blurVfxManager.resultBuffer.texture)
                        }
                        blurVfxManager.applyEffects()
                        game.overlay.root.y = oldOverlayY - Interpolation.pow5Out.apply(
                            0f, 300f,
                            pauseCounter.toFloat() / pauseTime,
                        )
                        game.overlay.alpha = Interpolation.pow5Out.apply(
                            oldOverlayAlpha, 0f,
                            pauseCounter.toFloat() / pauseTime,
                        )
                        pauseCounter++
                    }
                }
            }
        } else {
            while (deltaTimeCounter > 0f) {
                deltaTimeCounter = (deltaTimeCounter - 1 / 60f).coerceAtLeast(0f)
                if (pauseCounter > 0) {
                    pauseCounter--
                    if (target == ScreenTarget.RUNNING) {
                        if (pauseCounter == 0) {
                            game.overlay.root.y = oldOverlayY
                            game.overlay.alpha = oldOverlayAlpha
                        } else {
                            game.overlay.root.y = oldOverlayY - Interpolation.pow5In.apply(
                                0f, 300f,
                                pauseCounter.toFloat() / pauseTime,
                            )
                            game.overlay.alpha = Interpolation.pow5In.apply(
                                oldOverlayAlpha, 0f,
                                pauseCounter.toFloat() / pauseTime,
                            )
                        }
                    }
                }
            }
            if (pauseCounter == 0) {
                if (target == ScreenTarget.RETRY) {
                    game.dispose()
                    reset()
                } else {
                    game.state = GameState.RUNNING
                    SE.resume()
                    BGM.resume()
                }
            }
        }

        if (pauseCounter == 0) {
            if (SystemFlag.replay == null) {
                if (SystemFlag.gamemode!!.isPractice()) {
                    gameData.practiceTime += delta
                } else {
                    gameData.playTime += delta
                }
            }
            game.update()
        }
        //shake game frame :3
        if (game.shaking != 0 && pauseCounter==0) {
            gameFrame.setPosition(
                72f + rng.nextFloat() * game.shaking * 2 - game.shaking,
                36f + rng.nextFloat() * game.shaking * 2 - game.shaking
            )
        } else {
            gameFrame.setPosition(72f, 36f)
        }

        super.render(delta)
        game.overlay.draw()
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        if (SystemFlag.ending == null) {
            reset()
        }
    }

    fun retryGame(): Boolean {
        if (pauseCounter != pauseTime) return false

        target = ScreenTarget.RETRY

        blurredGameFrame.clearActions()
        blurredGameFrame.addAction(fadeOut(0.5f, Interpolation.sine))
        pauseMenu.deactivate()
        gameFrame.alpha = 0f

        return true
    }

    fun resumeGame(): Boolean {
        if (pauseCounter != pauseTime) return false

        target = ScreenTarget.RUNNING

        blurredGameFrame.clearActions()
        blurredGameFrame.addAction(fadeOut(0.5f, Interpolation.sine))
        pauseMenu.deactivate()

        return true
    }

    fun pauseGame(): Boolean {
        if (pauseCounter != 0) return false

        target = ScreenTarget.PAUSE

        SE.pause()
        SE.play("pause")
        BGM.pause()
        pauseMenu.activate()
        blurredGameFrame.clearActions()
        blurredGameFrame.alpha = 1f
        oldOverlayY = game.overlay.root.y
        oldOverlayAlpha = game.overlay.alpha

        return true
    }

    fun reset() {
        SE.stop()
        pauseMenu.reset()

        SystemFlag.ending = null
        GameBuilder.build()
        gameStatus?.remove()
        gameStatus = GameStatus(game)
        st += gameStatus!!

        gameFrame.vfxManager = game.postVfx
        gameFrame.alpha = 1f

        blurredGameFrame.vfxManager = blurVfxManager
        blurredGameFrame.alpha = 0f

        pauseMenu.clearActions()
        pauseMenu.alpha = 0f

        target = ScreenTarget.RUNNING
        pauseCounter = 0
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
        blurVfxManager.dispose()
        blurEffect.dispose()
    }
}