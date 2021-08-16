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

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction
import com.badlogic.gdx.utils.I18NBundle
import com.hhs.koto.app.Config.resolutionModes
import com.hhs.koto.app.Options
import com.hhs.koto.app.ui.ConstrainedGrid
import com.hhs.koto.app.ui.Grid
import com.hhs.koto.app.ui.GridButton
import com.hhs.koto.app.ui.register
import com.hhs.koto.util.*
import kotlin.math.roundToInt

class OptionsScreen : BasicScreen("mus/E0120.ogg", getRegion("bg/title.png")) {
    private val grid = ConstrainedGrid(
        -2048f,
        72f,
        5536f,
        880f,
        animationDuration = 0.5f,
        interpolation = Interpolation.sine
    ).register(st, input)
    private val language = Grid(0, -9, false).register(st, input)
    private val musicVolume = Grid(0, -8, false).register(st, input)
    private val SEVolume = Grid(0, -7, false).register(st, input)
    private val vsync = Grid(0, -6, false).register(st, input)
    private val resolution = Grid(0, -5, false).register(st, input)
    private val fullscreen = Grid(0, -4, false).register(st, input)
    private var oldOptions: Options? = null

    companion object {
        fun getAlternativeActiveAction(button: GridButton, vararg actions: () -> Action): () -> Action = {
            val ret = ParallelAction()
            ret.addAction(
                sequence(
                    show(),
                    color(Color.WHITE),
                    moveTo(button.staticX + 2, button.staticY, 0.03f, Interpolation.sine),
                    moveTo(button.staticX - 4, button.staticY, 0.06f, Interpolation.sine),
                    moveTo(button.staticX, button.staticY, 0.03f, Interpolation.sine),
                )
            )
            ret.addAction(
                forever(
                    sequence(
                        color(darken(Color.WHITE, 0.9f), 0.5f),
                        color(Color.WHITE, 0.5f),
                    )
                )
            )
            for (action in actions) {
                ret.addAction(action())
            }
            ret
        }

        fun getAlternativeInactiveAction(button: GridButton, vararg actions: () -> Action): () -> Action = {
            val ret = ParallelAction()
            ret.addAction(hide())
            for (action in actions) {
                ret.addAction(action())
            }
            ret
        }
    }

    init {
        grid.add(language)
        grid.add(GridButton(bundle["ui.options.language"], 36, 0, -9, triggerSound = null))
        for (i in 0 until options.locales.size) {
            val tmpBundle = I18NBundle.createBundle(Gdx.files.internal("locale/locale"), options.locales[i])
            val button = GridButton(
                tmpBundle["locale.name"], 36, i, 0, triggerSound = null, ignoreParent = true
            )
            language.add(button)
            button.activeAction = getAlternativeActiveAction(button, {
                Actions.run {
                    options.locale = options.locales[i]
                }
            })
            button.inactiveAction = getAlternativeInactiveAction(button)
        }

        grid.add(musicVolume)
        grid.add(GridButton(bundle["ui.options.musicVolume"], 36, 0, -8, triggerSound = null))
        for (i in 0..20) {
            val button = GridButton(
                (i * 5).toString() + "%", 36, i, 0, triggerSound = null, ignoreParent = true
            )
            musicVolume.add(button)
            button.activeAction = getAlternativeActiveAction(button, {
                Actions.run {
                    options.musicVolume = i / 20f
                    BGM.setVolume(i / 20f)
                }
            })
            button.inactiveAction = getAlternativeInactiveAction(button)
        }

        grid.add(SEVolume)
        val tmpButton1 = GridButton(bundle["ui.options.SEVolume"], 36, 0, -7, triggerSound = null)
        tmpButton1.activeAction = tmpButton1.getActiveAction({
            forever(
                sequence(
                    delay(1.5f),
                    Actions.run {
                        SE.play("pldead")
                    },
                )
            )
        })
        grid.add(tmpButton1)
        for (i in 0..20) {
            val button = GridButton(
                (i * 5).toString() + "%", 36, i, 0, triggerSound = null, ignoreParent = true
            )
            SEVolume.add(button)
            button.activeAction = getAlternativeActiveAction(button, {
                Actions.run {
                    options.SEVolume = i / 20f
                }
            })
            button.inactiveAction = getAlternativeInactiveAction(button)
        }

        grid.add(vsync)
        grid.add(GridButton(bundle["ui.options.vsync"], 36, 0, -6, triggerSound = null))
        val vsyncDisableButton = GridButton(
            bundle["ui.no"], 36, 0, 0, triggerSound = null, ignoreParent = true
        )
        vsync.add(vsyncDisableButton)
        vsyncDisableButton.activeAction = getAlternativeActiveAction(
            vsyncDisableButton,
            {
                Actions.run {
                    options.vsyncEnabled = false
                    Gdx.graphics.setVSync(false)
                }
            },
        )
        vsyncDisableButton.inactiveAction = getAlternativeInactiveAction(vsyncDisableButton)

        val vsyncEnableButton = GridButton(
            bundle["ui.yes"], 36, 1, 0, triggerSound = null, ignoreParent = true
        )
        vsync.add(vsyncEnableButton)
        vsyncEnableButton.activeAction = getAlternativeActiveAction(
            vsyncEnableButton,
            {
                Actions.run {
                    options.vsyncEnabled = true
                    Gdx.graphics.setVSync(true)
                }
            },
        )
        vsyncEnableButton.inactiveAction = getAlternativeInactiveAction(vsyncEnableButton)

        grid.add(resolution)
        grid.add(GridButton(bundle["ui.options.resolution"], 36, 0, -5, triggerSound = null))
        for (i in 0 until resolutionModes.size) {
            val mode = resolutionModes[i]
            val button = GridButton(mode.name, 36, i, 0, triggerSound = null, ignoreParent = true)
            resolution.add(button)
            button.activeAction = getAlternativeActiveAction(button, {
                Actions.run {
                    mode.apply(options)
                    if (!options.fullScreen) Gdx.graphics.setWindowedMode(options.windowWidth, options.windowHeight)
                }
            })
            button.inactiveAction = getAlternativeInactiveAction(button)
        }
        val customButton = GridButton(
            bundle["ui.options.resolution.custom"],
            36,
            resolutionModes.size,
            0,
            triggerSound = null,
            ignoreParent = true,
        )
        resolution.add(customButton)
        customButton.activeAction = getAlternativeActiveAction(customButton, {
            Actions.run {
                options.windowWidth = oldOptions!!.windowWidth
                options.windowHeight = oldOptions!!.windowHeight
                options.frameWidth = oldOptions!!.frameWidth
                options.frameHeight = oldOptions!!.frameHeight
                if (!options.fullScreen) Gdx.graphics.setWindowedMode(options.windowWidth, options.windowHeight)
            }
        })
        customButton.inactiveAction = getAlternativeInactiveAction(customButton)
        customButton.deactivate()

        grid.add(fullscreen)
        grid.add(GridButton(bundle["ui.options.fullscreen"], 36, 0, -4, triggerSound = null))
        val fullscreenDisableButton = GridButton(
            bundle["ui.no"], 36, 0, 0, triggerSound = null, ignoreParent = true
        )
        fullscreen.add(fullscreenDisableButton)
        fullscreenDisableButton.activeAction = getAlternativeActiveAction(
            fullscreenDisableButton,
            {
                Actions.run {
                    if (options.fullScreen) {
                        options.fullScreen = false
                        if (!resolution.grid.last().enabled) {
                            val modeIndex = ResolutionMode.findOptimalIndex(Gdx.graphics.displayMode)
                            resolutionModes[modeIndex].apply(options)
                            resolution.select(modeIndex, 0, true)
                        }
                        Gdx.graphics.setWindowedMode(options.windowWidth, options.windowHeight)
                    }
                }
            },
        )
        fullscreenDisableButton.inactiveAction = getAlternativeInactiveAction(fullscreenDisableButton)

        val fullscreenEnableButton = GridButton(
            bundle["ui.yes"], 36, 1, 0, triggerSound = null, ignoreParent = true
        )
        fullscreen.add(fullscreenEnableButton)
        fullscreenEnableButton.activeAction = getAlternativeActiveAction(
            fullscreenEnableButton,
            {
                Actions.run {
                    if (!options.fullScreen) {
                        options.fullScreen = true
                        val displayMode = Gdx.graphics.displayMode
                        Gdx.graphics.setFullscreenMode(displayMode)
                        if (!resolution.grid.last().enabled) {
                            var modeIndex = resolutionModes.indexOfFirst {
                                displayMode.width <= it.windowWidth && displayMode.height <= it.windowHeight
                            }
                            if (modeIndex == -1) modeIndex = resolutionModes.size - 1
                            resolutionModes[modeIndex].apply(options)
                            resolution.select(modeIndex, 0, true)
                        }
                    }
                }
            },
        )
        fullscreenEnableButton.inactiveAction = getAlternativeInactiveAction(fullscreenEnableButton)

        grid.add(GridButton(bundle["ui.options.keyConfig"], 36, 0, -3, enabled = false))
        grid.add(GridButton(bundle["ui.options.reset"], 36, 0, -2) {
            options = Options()
            ResolutionMode.findOptimal(Gdx.graphics.displayMode).apply(options)
            BGM.setVolume(options.musicVolume)
            Gdx.graphics.setVSync(options.vsyncEnabled)
            reset()
        })
        grid.add(GridButton(bundle["ui.options.cancel"], 36, 0, -1, triggerSound = null) {
            super.onQuit()
            app.setScreen("title", 0.5f)
        })
        grid.add(GridButton(bundle["ui.options.quit"], 36, 0, 0, triggerSound = null) {
            onQuit()
        })

        reset()

        grid.arrange(950f, 100f, 0f, -45f)
        language.x = 1200f
        musicVolume.x = 1200f
        SEVolume.x = 1200f
        vsync.x = 1200f
        resolution.x = 1200f
        fullscreen.x = 1200f

        grid.selectFirst()
    }

    private fun reset() {
        language.select(options.locales.indexOf(options.locale, false), 0, true)
        musicVolume.select(clamp((options.musicVolume * 20).roundToInt(), 0, 20), 0, true)
        SEVolume.select(clamp((options.SEVolume * 20).roundToInt(), 0, 20), 0, true)
        vsync.select(if (options.vsyncEnabled) 1 else 0, 0, true)
        val modeIndex = resolutionModes.indexOfFirst {
            options.windowWidth == it.windowWidth &&
                    options.windowHeight == it.windowHeight &&
                    options.frameWidth == it.frameWidth &&
                    options.frameHeight == it.frameHeight
        }
        if (modeIndex != -1) {
            resolution.grid.last().disable()
            resolution.select(modeIndex, 0, true)
        } else {
            resolution.grid.last().enable()
            resolution.selectLast()
        }
        fullscreen.select(if (options.fullScreen) 1 else 0, 0, true)
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        oldOptions = options.copy()
        grid.clearActions()
        grid.setPosition(grid.staticX + 400f, grid.staticY)
        grid.addAction(moveTo(grid.staticX, grid.staticY, duration, Interpolation.pow5Out))
    }

    override fun fadeOut(newScreen: KotoScreen?, duration: Float) {
        super.fadeOut(newScreen, duration)
        grid.clearActions()
        grid.addAction(moveTo(grid.staticX + 400f, grid.staticY, duration, Interpolation.sineOut))
    }

    override fun onQuit() {
        if (grid.selectedY == 0) {
            super.onQuit()
            saveOptions()
            if (oldOptions!!.fpsMultiplier != options.fpsMultiplier ||
                oldOptions!!.locale != options.locale
            ) {
                restartApp()
            } else {
                app.setScreen("title", 0.5f)
            }
        } else {
            grid.select(0, 0)
        }
    }
}