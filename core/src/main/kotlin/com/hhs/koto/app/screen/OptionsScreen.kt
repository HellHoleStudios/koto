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
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.I18NBundle
import com.hhs.koto.app.Options
import com.hhs.koto.app.ui.ConstrainedGrid
import com.hhs.koto.app.ui.Grid
import com.hhs.koto.app.ui.GridButton
import com.hhs.koto.util.*
import kotlin.math.roundToInt

class OptionsScreen : BasicScreen("mus/E.0120.ogg", getRegion("bg/title.png")) {
    private val grid = ConstrainedGrid(
        -2048f,
        72f,
        5536f,
        880f,
        animationDuration = 0.5f,
        interpolation = Interpolation.sine
    ).apply {
        st.addActor(this)
        input.addProcessor(this)
    }
    private val language = Grid(0, -7, false).apply {
        st.addActor(this)
        input.addProcessor(this)
    }
    private val musicVolume = Grid(0, -6, false).apply {
        st.addActor(this)
        input.addProcessor(this)
    }
    private val SEVolume = Grid(0, -5, false).apply {
        st.addActor(this)
        input.addProcessor(this)
    }
    private val Vsync = Grid(0, -4, false).apply {
        st.addActor(this)
        input.addProcessor(this)
    }
    var oldOptions: Options? = null

    init {
        grid.add(language)
        grid.add(GridButton(bundle["ui.options.language"], 36, 0, -7, triggerSound = null))
        for (i in 0 until options.locales.size) {
            val tmpBundle = I18NBundle.createBundle(Gdx.files.internal("locale/locale"), options.locales[i])
            println(options.locales[i])
            println(tmpBundle["locale.name"])
            val button = GridButton(tmpBundle["locale.name"], 36, i, 0, triggerSound = null, ignoreParent = true)
            language.add(button)
            button.activeAction = {
                Actions.parallel(
                    Actions.sequence(
                        Actions.show(),
                        Actions.color(Color.WHITE),
                        Actions.moveTo(button.staticX + 2, button.staticY, 0.03f, Interpolation.sine),
                        Actions.moveTo(button.staticX - 4, button.staticY, 0.06f, Interpolation.sine),
                        Actions.moveTo(button.staticX, button.staticY, 0.03f, Interpolation.sine),
                    ),
                    Actions.forever(
                        Actions.sequence(
                            Actions.color(darken(Color.WHITE, 0.9f), 0.5f),
                            Actions.color(Color.WHITE, 0.5f),
                        )
                    ),
                    Actions.run {
                        options.locale = options.locales[i]
                    },
                )
            }
            button.inactiveAction = {
                Actions.hide()
            }
        }
        language.select(options.locales.indexOf(options.locale, false), 0, true)

        grid.add(musicVolume)
        grid.add(GridButton(bundle["ui.options.musicVolume"], 36, 0, -6, triggerSound = null))
        for (i in 0..20) {
            val button = GridButton((i * 5).toString() + "%", 36, i, 0, triggerSound = null, ignoreParent = true)
            musicVolume.add(button)
            button.activeAction = {
                Actions.parallel(
                    Actions.sequence(
                        Actions.show(),
                        Actions.color(Color.WHITE),
                        Actions.moveTo(button.staticX + 2, button.staticY, 0.03f, Interpolation.sine),
                        Actions.moveTo(button.staticX - 4, button.staticY, 0.06f, Interpolation.sine),
                        Actions.moveTo(button.staticX, button.staticY, 0.03f, Interpolation.sine),
                    ),
                    Actions.forever(
                        Actions.sequence(
                            Actions.color(darken(Color.WHITE, 0.9f), 0.5f),
                            Actions.color(Color.WHITE, 0.5f),
                        )
                    ),
                    Actions.run {
                        options.musicVolume = i / 20f
                        BGM.setVolume(i / 20f)
                    },
                )
            }
            button.inactiveAction = {
                Actions.hide()
            }
        }
        musicVolume.select(clamp((options.musicVolume * 20).roundToInt(), 0, 20), 0, true)

        grid.add(SEVolume)
        val tmpButton1 = GridButton(bundle["ui.options.SEVolume"], 36, 0, -5, triggerSound = null)
        tmpButton1.activeAction = tmpButton1.getActiveAction({
            Actions.forever(
                Actions.sequence(
                    Actions.delay(1.5f),
                    Actions.run {
                        SE.play("pldead")
                    },
                )
            )
        })
        grid.add(tmpButton1)
        for (i in 0..20) {
            val button = GridButton((i * 5).toString() + "%", 36, i, 0, triggerSound = null, ignoreParent = true)
            SEVolume.add(button)
            button.activeAction = {
                Actions.parallel(
                    Actions.sequence(
                        Actions.show(),
                        Actions.color(Color.WHITE),
                        Actions.moveTo(button.staticX + 2, button.staticY, 0.03f, Interpolation.sine),
                        Actions.moveTo(button.staticX - 4, button.staticY, 0.06f, Interpolation.sine),
                        Actions.moveTo(button.staticX, button.staticY, 0.03f, Interpolation.sine),
                    ),
                    Actions.forever(
                        Actions.sequence(
                            Actions.color(darken(Color.WHITE, 0.9f), 0.5f),
                            Actions.color(Color.WHITE, 0.5f),
                        )
                    ),
                    Actions.run {
                        options.SEVolume = i / 20f
                    },
                )
            }
            button.inactiveAction = { Actions.hide() }
        }
        SEVolume.select(clamp((options.SEVolume * 20).roundToInt(), 0, 20), 0, true)

        grid.add(Vsync)
        grid.add(GridButton(bundle["ui.options.vsync"], 36, 0, -4, triggerSound = null))
        val VsyncDisableButton = GridButton(bundle["ui.no"], 36, 0, 0, triggerSound = null, ignoreParent = true)
        Vsync.add(VsyncDisableButton)
        VsyncDisableButton.activeAction = {
            Actions.parallel(
                Actions.sequence(
                    Actions.show(),
                    Actions.color(Color.WHITE),
                    Actions.moveTo(
                        VsyncDisableButton.staticX + 2,
                        VsyncDisableButton.staticY,
                        0.03f,
                        Interpolation.sine,
                    ),
                    Actions.moveTo(
                        VsyncDisableButton.staticX - 4,
                        VsyncDisableButton.staticY,
                        0.06f,
                        Interpolation.sine,
                    ),
                    Actions.moveTo(VsyncDisableButton.staticX, VsyncDisableButton.staticY, 0.03f, Interpolation.sine),
                ),
                Actions.forever(
                    Actions.sequence(
                        Actions.color(darken(Color.WHITE, 0.9f), 0.5f),
                        Actions.color(Color.WHITE, 0.5f),
                    )
                ),
                Actions.run {
                    options.vsyncEnabled = false
                    Gdx.graphics.setVSync(false)
                },
            )
        }

        val VsyncEnableButton = GridButton(bundle["ui.yes"], 36, 1, 0, triggerSound = null, ignoreParent = true)
        Vsync.add(VsyncEnableButton)
        VsyncEnableButton.activeAction = {
            Actions.parallel(
                Actions.sequence(
                    Actions.show(),
                    Actions.color(Color.WHITE),
                    Actions.moveTo(VsyncEnableButton.staticX + 2, VsyncEnableButton.staticY, 0.03f, Interpolation.sine),
                    Actions.moveTo(VsyncEnableButton.staticX - 4, VsyncEnableButton.staticY, 0.06f, Interpolation.sine),
                    Actions.moveTo(VsyncEnableButton.staticX, VsyncEnableButton.staticY, 0.03f, Interpolation.sine),
                ),
                Actions.forever(
                    Actions.sequence(
                        Actions.color(darken(Color.WHITE, 0.9f), 0.5f),
                        Actions.color(Color.WHITE, 0.5f),
                    )
                ),
                Actions.run {
                    options.vsyncEnabled = true
                    Gdx.graphics.setVSync(true)
                },
            )
        }
        VsyncEnableButton.inactiveAction = { Actions.hide() }
        VsyncDisableButton.inactiveAction = { Actions.hide() }
        Vsync.select(if (options.vsyncEnabled) 1 else 0, 0, true)

        grid.add(GridButton(bundle["ui.options.keyConfig"], 36, 0, -3, enabled = false))
        grid.add(GridButton(bundle["ui.options.reset"], 36, 0, -2) {
            options = Options()
            BGM.setVolume(options.musicVolume)
            Gdx.graphics.setVSync(options.vsyncEnabled)
            musicVolume.select(clamp((options.musicVolume * 20).roundToInt(), 0, 20), 0)
            SEVolume.select(clamp((options.SEVolume * 20).roundToInt(), 0, 20), 0)
            Vsync.select(if (options.vsyncEnabled) 1 else 0, 0, true)
        })
        grid.add(GridButton(bundle["ui.options.cancel"], 36, 0, -1, triggerSound = null) {
            super.onQuit()
            app.setScreen("title", 0.5f)
        })
        grid.add(GridButton(bundle["ui.options.quit"], 36, 0, 0, triggerSound = null) {
            onQuit()
        })

        grid.arrange(950f, 100f, 0f, -45f)
        language.x = 1200f
        musicVolume.x = 1200f
        SEVolume.x = 1200f
        Vsync.x = 1200f

        grid.selectFirst()
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        oldOptions = options.copy()
        grid.clearActions()
        grid.setPosition(grid.staticX + 400f, grid.staticY)
        grid.addAction(Actions.moveTo(grid.staticX, grid.staticY, duration, Interpolation.pow5Out))
    }

    override fun fadeOut(newScreen: KotoScreen?, duration: Float) {
        super.fadeOut(newScreen, duration)
        grid.clearActions()
        grid.addAction(Actions.moveTo(grid.staticX + 400f, grid.staticY, duration, Interpolation.sineOut))
    }

    override fun onQuit() {
        if (grid.selectedY == 0) {
            super.onQuit()
            saveOptions()
            if (oldOptions!!.fpsMultiplier != options.fpsMultiplier || oldOptions!!.locale != options.locale) {
                restartApp()
            } else {
                app.setScreen("title", 0.5f)
            }
        } else {
            grid.select(0, 0)
        }
    }
}