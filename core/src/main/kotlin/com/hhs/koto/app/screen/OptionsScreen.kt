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

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.Graphics.DisplayMode
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.I18NBundle
import com.hhs.koto.app.Config
import com.hhs.koto.app.Config.resolutionModes
import com.hhs.koto.app.Options
import com.hhs.koto.app.ui.ConstrainedGrid
import com.hhs.koto.app.ui.Grid
import com.hhs.koto.app.ui.GridButton
import com.hhs.koto.app.ui.register
import com.hhs.koto.util.*
import ktx.actors.alpha
import ktx.actors.plusAssign
import ktx.collections.GdxArray
import java.util.*
import kotlin.math.roundToInt

class OptionsScreen : BasicScreen(Config.uiBgm, getRegion("bg/title.png")) {
    private val grid = ConstrainedGrid(
        -2048f, 72f, 5536f, 880f, animationDuration = 0.5f, interpolation = Interpolation.sine
    ).register(st, input)
    private val restartNotification = Label(
        bundle["ui.options.restartRequired"],
        getUILabelStyle(36, RED_HSV),
    ).apply {
        grid += this
        setPosition(950f, 550f)
        alpha = 0f
    }
    private val languageGrid = Grid(0, -9, false).apply { input.addProcessor(this) }
    private val musicVolumeGrid = Grid(0, -8, false).apply { input.addProcessor(this) }
    private val SEVolumeGrid = Grid(0, -7, false).apply { input.addProcessor(this) }
    private val vsyncGrid = Grid(0, -6, false).apply { input.addProcessor(this) }
    private val resolutionGrid = Grid(0, -5, false).register(st, input)
    private val displayModeGrid = Grid(0, -4, true).apply { input.addProcessor(this) }
    private var oldOptions: Options? = null

    private val oldLocale: Locale = options.locale

    private companion object {
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
        grid.add(languageGrid)
        grid.add(GridButton(bundle["ui.options.language"], 36, 0, -9, triggerSound = null))
        for (i in 0 until options.locales.size) {
            val tmpBundle = I18NBundle.createBundle(Gdx.files.internal("locale/locale"), options.locales[i])
            val button = GridButton(
                tmpBundle["locale.name"], 36, i, 0, triggerSound = null, ignoreParent = true
            )
            languageGrid.add(button)
            button.activeAction = getAlternativeActiveAction(button, {
                Actions.run {
                    options.locale = options.locales[i]
                }
            })
            button.inactiveAction = getAlternativeInactiveAction(button)
        }

        grid.add(musicVolumeGrid)
        grid.add(GridButton(bundle["ui.options.musicVolume"], 36, 0, -8, triggerSound = null))
        for (i in 0..20) {
            val button = GridButton(
                (i * 5).toString() + "%", 36, i, 0, triggerSound = null, ignoreParent = true
            )
            musicVolumeGrid.add(button)
            button.activeAction = getAlternativeActiveAction(button, {
                Actions.run {
                    options.musicVolume = i / 20f
                    BGM.setVolume(i / 20f)
                }
            })
            button.inactiveAction = getAlternativeInactiveAction(button)
        }

        grid.add(SEVolumeGrid)
        grid.add(GridButton(bundle["ui.options.SEVolume"], 36, 0, -7, triggerSound = null).apply {
            activeAction = getActiveAction({
                forever(
                    sequence(
                        delay(1.5f),
                        Actions.run {
                            SE.play("pldead")
                        },
                    )
                )
            })
        })
        for (i in 0..20) {
            val button = GridButton(
                (i * 5).toString() + "%", 36, i, 0, triggerSound = null, ignoreParent = true
            )
            SEVolumeGrid.add(button)
            button.activeAction = getAlternativeActiveAction(button, {
                Actions.run {
                    options.SEVolume = i / 20f
                }
            })
            button.inactiveAction = getAlternativeInactiveAction(button)
        }

        grid.add(vsyncGrid)
        grid.add(GridButton(bundle["ui.options.vsync"], 36, 0, -6, triggerSound = null))
        val vsyncDisableButton = GridButton(
            bundle["ui.no"], 36, 0, 0, triggerSound = null, ignoreParent = true
        )
        vsyncGrid.add(vsyncDisableButton)
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
        vsyncGrid.add(vsyncEnableButton)
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

        grid.add(resolutionGrid)
        grid.add(GridButton(bundle["ui.options.resolution"], 36, 0, -5, triggerSound = null))

        grid.add(displayModeGrid)
        grid.add(GridButton(bundle["ui.options.display"], 36, 0, -4, triggerSound = null))

        val windowedButton = GridButton(
            bundle["ui.options.display.windowed"], 36, 0, 0, triggerSound = null, ignoreParent = true
        )
        displayModeGrid.add(windowedButton)
        windowedButton.activeAction = getAlternativeActiveAction(
            windowedButton,
            {
                Actions.run {
                    // prevents incorrect desktop size caused by fullscreen
                    if (Gdx.graphics.isFullscreen) {
                        Gdx.graphics.setWindowedMode(100, 100)
                    }

                    options.displayMode = "windowed"
                    resolutionGrid.clearChildren()

                    var modeIndex = -1
                    for (i in 0 until resolutionModes.size) {
                        val mode = resolutionModes[i]
                        if (mode.windowWidth == options.windowWidth &&
                            mode.windowHeight == options.windowHeight &&
                            mode.frameBufferWidth == options.frameBufferWidth &&
                            mode.frameBufferHeight == options.frameBufferHeight
                        ) {
                            modeIndex = i
                        }
                        val button = GridButton(mode.name, 36, i, 0, triggerSound = null, ignoreParent = true)
                        resolutionGrid.add(button)
                        button.activeAction = getAlternativeActiveAction(button, {
                            Actions.run {
                                mode.saveTo(options)
                                Gdx.graphics.setUndecorated(false)
                                Gdx.graphics.setWindowedMode(options.windowWidth, options.windowHeight)
                            }
                        })
                        button.inactiveAction = getAlternativeInactiveAction(button)
                    }
                    if (modeIndex == -1) {
                        val customButton = GridButton(
                            bundle["ui.options.resolution.custom"],
                            36,
                            resolutionModes.size,
                            0,
                            triggerSound = null,
                            ignoreParent = true,
                        )
                        resolutionGrid.add(customButton)
                        customButton.activeAction = getAlternativeActiveAction(customButton, {
                            Actions.run {
                                options.windowWidth = oldOptions!!.windowWidth
                                options.windowHeight = oldOptions!!.windowHeight
                                options.frameBufferWidth = oldOptions!!.frameBufferWidth
                                options.frameBufferHeight = oldOptions!!.frameBufferHeight
                                Gdx.graphics.setUndecorated(false)
                                Gdx.graphics.setWindowedMode(options.windowWidth, options.windowHeight)
                            }
                        })
                        customButton.inactiveAction = getAlternativeInactiveAction(customButton)
                        resolutionGrid.selectLast()
                    } else {
                        resolutionGrid.select(modeIndex, 0, true)
                    }
                }
            },
        )
        windowedButton.inactiveAction = getAlternativeInactiveAction(windowedButton)

        val fullscreenButton = GridButton(
            bundle["ui.options.display.fullscreen"], 36, 1, 0, triggerSound = null, ignoreParent = true
        )
        displayModeGrid.add(fullscreenButton)
        fullscreenButton.activeAction = getAlternativeActiveAction(
            fullscreenButton,
            {
                Actions.run {
                    options.displayMode = "fullscreen"

                    val displayModes = GdxArray<DisplayMode>()
                    Gdx.graphics.displayModes.forEach {
                        for ((i, mode) in displayModes.withIndex()) {
                            if (it.width == mode.width && it.height == mode.height) {
                                if (it.refreshRate > mode.refreshRate) {
                                    displayModes[i] = it
                                }
                                return@forEach
                            }
                        }
                        displayModes.add(it)
                    }
                    resolutionGrid.clearChildren()

                    var selectedDisplayMode = 0
                    for ((i, displayMode) in displayModes.withIndex()) {
                        if (displayMode.height <= options.windowHeight) {
                            selectedDisplayMode = i
                        }

                        var resolutionMode: ResolutionMode? = null
                        for (mode in resolutionModes.safeIterator()) {
                            if (mode.windowHeight >= displayMode.height) {
                                resolutionMode = mode
                                break
                            }
                        }
                        if (resolutionMode == null) {
                            resolutionMode = resolutionModes.last()
                        }
                        val button = GridButton(
                            "${displayMode.width}x${displayMode.height}",
                            36,
                            i,
                            0,
                            triggerSound = null,
                            ignoreParent = true
                        )
                        resolutionGrid.add(button)
                        button.activeAction = getAlternativeActiveAction(button, {
                            Actions.run {
                                resolutionMode!!.saveTo(options)
                                Gdx.graphics.setFullscreenMode(displayMode)
                            }
                        })
                        button.inactiveAction = getAlternativeInactiveAction(button)
                    }

                    resolutionGrid.select(selectedDisplayMode, 0, true)
                }
            },
        )
        fullscreenButton.inactiveAction = getAlternativeInactiveAction(fullscreenButton)

        if (app.graphicsSystem.borderlessAvailable) {
            val borderlessButton = GridButton(
                bundle["ui.options.display.borderless"], 36, 2, 0, triggerSound = null, ignoreParent = true
            )
            displayModeGrid.add(borderlessButton)
            borderlessButton.activeAction = getAlternativeActiveAction(
                borderlessButton,
                {
                    Actions.run {
                        options.displayMode = "borderless"
                        val (width, height) = app.graphicsSystem.setBorderless()
                        resolutionGrid.clearChildren()
                        val button = GridButton(
                            "${width}x${height}", 36, 0, 0, triggerSound = null, ignoreParent = true
                        )
                        var modeExists = false
                        for (mode in resolutionModes.safeIterator()) {
                            if (mode.windowHeight >= height) {
                                mode.saveTo(options)
                                modeExists = true
                                break
                            }
                        }
                        if (!modeExists) {
                            resolutionModes.last().saveTo(options)
                        }

                        button.activeAction = getAlternativeActiveAction(button)
                        button.inactiveAction = getAlternativeInactiveAction(button)
                        resolutionGrid.add(button)
                        resolutionGrid.selectFirst()
                    }
                },
            )
            borderlessButton.inactiveAction = getAlternativeInactiveAction(borderlessButton)
        }

        grid.add(GridButton(bundle["ui.options.keyConfig"], 36, 0, -3, enabled = false))
        grid.add(GridButton(bundle["ui.options.reset"], 36, 0, -2) {
            options = Options()
            ResolutionMode.findOptimal(Gdx.graphics.displayMode).saveTo(options)
            BGM.setVolume(options.musicVolume)
            Gdx.graphics.setVSync(options.vsyncEnabled)
            reset()
        })
        grid.add(GridButton(bundle["ui.options.cancel"], 36, 0, -1, triggerSound = null) {
            options = oldOptions!!
            if (options.displayMode == "fullscreen") {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
            } else {
                Gdx.graphics.setWindowedMode(options.windowWidth, options.windowHeight)
            }
            BGM.setVolume(options.musicVolume)
            Gdx.graphics.setVSync(options.vsyncEnabled)
            reset()
            super.onQuit()
            app.setScreen("title", 0.5f)
        })
        grid.add(GridButton(bundle["ui.options.quit"], 36, 0, 0, triggerSound = null) {
            onQuit()
        })

        reset()

        grid.arrange(950f, 100f, 0f, -45f)
        languageGrid.x = 1200f
        musicVolumeGrid.x = 1200f
        SEVolumeGrid.x = 1200f
        vsyncGrid.x = 1200f
        resolutionGrid.x = 1200f
        displayModeGrid.x = 1200f

        grid.select(0, -1, true)
        grid.updateComponent()
    }

    override fun render(delta: Float) {
        if (oldLocale != options.locale) {
            restartNotification.alpha = 1f
        } else {
            restartNotification.alpha = 0f
        }
        super.render(delta)
    }

    private fun reset() {
        languageGrid.select(options.locales.indexOf(options.locale, false), 0, true)
        musicVolumeGrid.select(clamp((options.musicVolume * 20).roundToInt(), 0, 20), 0, true)
        SEVolumeGrid.select(clamp((options.SEVolume * 20).roundToInt(), 0, 20), 0, true)
        vsyncGrid.select(if (options.vsyncEnabled) 1 else 0, 0, true)
        displayModeGrid.select(
            when (options.displayMode) {
                "windowed" -> 0
                "fullscreen" -> 1
                "borderless" -> 2
                else -> throw KotoRuntimeException("Invalid display mode: ${options.displayMode}")
            }, 0, true
        )
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        oldOptions = options.copy()
        grid.clearActions()
        grid.setPosition(grid.staticX + 500f, grid.staticY)
        grid.addAction(moveTo(grid.staticX, grid.staticY, duration, Interpolation.pow5Out))
    }

    override fun fadeOut(newScreen: KotoScreen?, duration: Float) {
        super.fadeOut(newScreen, duration)
        grid.clearActions()
        grid.addAction(moveTo(grid.staticX + 500f, grid.staticY, duration, Interpolation.sineOut))
    }

    override fun onQuit() {
        if (grid.selectedY == 0) {
            super.onQuit()
            saveOptions()
            app.setScreen("title", 0.5f)
        } else {
            grid.select(0, 0)
        }
    }
}