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
import com.hhs.koto.stg.GameMode
import com.hhs.koto.util.*
import ktx.actors.alpha
import ktx.actors.plusAssign
import ktx.actors.then

class GameScreen : BasicScreen(null, null) {

    val vfxManager = VfxManager(Pixmap.Format.RGBA8888, Config.fw, Config.fh)
    val blurEffect = GaussianBlurEffect()
    val gameFrame = VfxOutput().apply { st += this }
    val blurredGameFrame = VfxOutput().apply { st += this }

    init {
        val gameBackground = Image(getRegion("bg/game.png"))
        gameBackground.setBounds(0f, 0f, Config.screenWidth, Config.screenHeight)
        st += gameBackground

        vfxManager.addEffect(blurEffect)
        blurEffect.amount = 10f
    }

    private val confirmationMenu = ConfirmationMenu(staticX = 650f, staticY = 450f).register(st, input).apply {
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
        pauseMenu.add(GridButton(bundle["ui.game.resume"], 36, gridX = 0, gridY = 1) {
            resumeGame()
        })
        pauseMenu.add(GridButton(bundle["ui.game.restart"], 36, gridX = 0, gridY = 2) {
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
        pauseMenu.add(GridButton(bundle["ui.game.quit"], 36, gridX = 0, gridY = 3) {
            confirmationMenu.activate()
            confirmationMenu.selectLast()
            input.removeProcessor(pauseMenu)
            confirmationMenu.yesRunnable = { quit() }
        })
        pauseMenu.arrange(0f, 200f, 0f, -55f)
        pauseMenu.add(GridLabel(bundle["ui.game.paused"], 48, gridX = 0, gridY = 0, staticX = -20f, staticY = 200f))
        pauseMenu.selectFirst()
    }

    private var tryPause: Boolean = false
    private var paused: Boolean = false
    private var passCounter: Int = 0
    private var deltaTimeCounter: Float = 0f

    override fun render(delta: Float) {
        if (state != ScreenState.SHOWN) {
            super.render(delta)
            return
        }
        if (!paused && keyJustPressed(options.keyPause)) {
            tryPause = true
        }
        if (tryPause && game.frameScheduler.canPause) {
            tryPause = false
            pauseGame()
        }
        if (paused) {
            deltaTimeCounter += delta
            if (passCounter >= 30 && keyJustPressed(options.keyPause) && blurredGameFrame.actions.isEmpty) {
                resumeGame()
            } else {
                if (deltaTimeCounter >= 1 / 60f) {
                    if (passCounter < 30) {
                        if (passCounter == 0) {
                            vfxManager.useAsInput(game.postVfx.resultBuffer.texture)
                        } else {
                            vfxManager.useAsInput(vfxManager.resultBuffer.texture)
                        }
                        vfxManager.applyEffects()
                        passCounter++
                    }
                    deltaTimeCounter = 0f
                }
            }
        } else {
            game.update()
        }
        super.render(delta)
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        reset()
    }

    fun resumeGame() {
        blurredGameFrame.addAction(
            fadeOut(0.5f, Interpolation.sine) then Actions.run { paused = false }
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
        pauseMenu.activate()
        blurredGameFrame.alpha = 1f
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

        gameFrame.vfxManager = game.postVfx
        gameFrame.setBounds(
            Config.frameOffsetX, Config.frameOffsetY,
            Config.frameWidth, Config.frameHeight
        )
        gameFrame.alpha = 1f

        blurredGameFrame.vfxManager = vfxManager
        blurredGameFrame.setBounds(
            Config.frameOffsetX, Config.frameOffsetY,
            Config.frameWidth, Config.frameHeight
        )
        blurredGameFrame.alpha = 0f

        paused = false
        tryPause = false
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