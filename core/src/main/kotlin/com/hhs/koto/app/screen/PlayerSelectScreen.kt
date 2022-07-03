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
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.Config
import com.hhs.koto.app.ui.*
import com.hhs.koto.stg.GameMode
import com.hhs.koto.util.*
import ktx.actors.alpha
import ktx.actors.plusAssign
import ktx.collections.GdxArray

class PlayerSelectScreen : BasicScreen(Config.uiBgm, getRegion(Config.uiBackground)) {
    private lateinit var difficultyLabel: Grid
    private val portraits = Grid(selectSound = null).register(st, input)
    private val descriptions = Grid().register(st, input)

    private val shottypes = Group().apply {
        st += this
        alpha = 0f
    }
    private val shottypeBackground = Image(getRegion("ui/bg.png")).apply {
        setSize(850f, 250f)
        shottypes += this
    }
    private val shottypeGrid = Grid().apply { shottypes += this }

    private var switchTarget: String? = null
    var selectedPlayer: String? = null

    companion object {
        fun playerColor(name: String): Color = when (name) {
            "reimu" -> Color(1f, 0f, 0f, 1f)
            "marisa" -> Color(1f, 1f, 0f, 1f)
            "sakuya" -> Color(1f, 1f, 1f, 1f)
            "youmu" -> Color(0.3f, 0.8f, 0.4f, 1f)
            "sanae" -> Color(0f, 0.5f, 1f, 1f)
            else -> Color.WHITE
        }
    }

    private fun generatePlayer(
        screen: PlayerSelectScreen,
        name: String,
        selectPortrait: TextureRegion,
        shottypes: GdxArray<String>,
        gridX: Int,
        gridY: Int,
        portraitStaticX: Float,
        portraitStaticY: Float,
        portraitWidth: Float = selectPortrait.regionWidth.toFloat(),
        portraitHeight: Float = portraitWidth / selectPortrait.regionWidth * selectPortrait.regionHeight,
        descriptionStaticX: Float = 150f,
        descriptionStaticY: Float = 300f,
    ) {
        if (SystemFlag.gamemode!! == GameMode.EXTRA) {
            var extraUnlocked = false
            shottypes.forEach {
                extraUnlocked = extraUnlocked || gameData.data[it].extraUnlocked
            }
            if (!extraUnlocked) return
        }
        portraits.add(
            Grid(staticX = portraitStaticX, staticY = portraitStaticY, gridX = gridX, gridY = gridY).add(
                GridImage(
                    selectPortrait,
                    width = portraitWidth,
                    height = portraitHeight,
                    tint = Color(1f, 1f, 0f, 0.8f),
                ) {
                    if (shottypes.size == 0) {
                        SystemFlag.shottype = name
                        screen.switch()
                    } else {
                        screen.selectedPlayer = name
                        screen.showShotType(name, shottypes)
                    }
                }.apply {
                    activeAction = {
                        sequence(
                            moveTo(this.staticX, this.staticY),
                            moveTo(this.staticX + 30f, this.staticY, 1f, Interpolation.pow5Out),
                        )
                    }
                }
            ).add(
                GridImage(selectPortrait, width = portraitWidth, height = portraitHeight, triggerSound = null).apply {
                    activeAction = {
                        sequence(
                            moveTo(this.staticX, this.staticY),
                            moveTo(this.staticX - 30f, this.staticY, 1f, Interpolation.pow5Out),
                        )
                    }
                }
            ).apply {
                activeAction = { Actions.show() }
                inactiveAction = { Actions.hide() }
            }.selectFirst()
        )
        descriptions.add(
            Grid(staticX = descriptionStaticX, staticY = descriptionStaticY, gridX = gridX, gridY = gridY).add(
                GridImage(
                    getRegion("ui/arrow.png"),
                    width = 48f,
                    staticX = -80f,
                    staticY = 525f,
                    activeAction = null,
                    triggerSound = null,
                )
            ).add(
                GridImage(
                    getRegion("ui/arrow.png"),
                    width = 48f,
                    staticX = 680f,
                    staticY = 525f,
                    activeAction = null,
                    triggerSound = null,
                ).apply {
                    setScale(-1f, 1f)
                }
            ).add(
                GridLabel(
                    bundle["ui.playerSelect.player.$name.title"],
                    width = 600f,
                    height = 50f,
                    staticY = 600f,
                    style = Label.LabelStyle(
                        getFont(
                            36,
                            bundle["font.subtitle"],
                            Color.RED,
                            borderColor = Color.BLACK
                        ), WHITE_HSV
                    ),
                ).apply {
                    setAlignment(Align.bottom)
                }
            ).add(
                GridLabel(
                    bundle["ui.playerSelect.player.$name.name"],
                    width = 600f,
                    height = 100f,
                    staticY = 500f,
                    style = Label.LabelStyle(
                        getFont(
                            72,
                            bundle["font.title"],
                            Color.RED,
                            borderColor = Color.BLACK,
                        ), playerColor(name).toHSVColor()
                    ),
                ).apply {
                    setAlignment(Align.center)
                }
            ).add(
                GridLabel(bundle["ui.playerSelect.player.$name.description"], 24, width = 600f, height = 500f).apply {
                    setAlignment(Align.center)
                }
            ).apply {
                activeAction = { Actions.show() }
                inactiveAction = { Actions.hide() }
            }.selectFirst()
        )
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)

        portraits.clear()
        descriptions.clear()
        generatePlayer(
            this, "reimu", getRegion("portrait/reimu/smile.png"),
            GdxArray.with("reimuA", "reimuB", "reimu"),
            0, 0, 850f, 50f, 560f,
        )
        generatePlayer(
            this, "marisa", getRegion("portrait/marisa/smile.png"),
            GdxArray.with("marisaA", "marisaB", "marisa"),
            1, 0, 850f, 50f, 580f,
        )
        generatePlayer(
            this, "sakuya", getRegion("portrait/sakuya/calm.png"),
            GdxArray.with("sakuyaA", "sakuyaB", "sakuya"),
            2, 0, 880f, 50f, 460f,
        )
        generatePlayer(
            this, "youmu", getRegion("portrait/youmu/smile.png"),
            GdxArray.with("youmuA", "youmuB", "youmu"),
            3, 0, 740f, 20f, 750f,
        )
        generatePlayer(
            this, "sanae", getRegion("portrait/sanae/smile.png"),
            GdxArray.with("sanaeA", "sanaeB", "sanae"),
            4, 0, 750f, 50f, 660f,
        )
        portraits.selectFirst()
        descriptions.selectFirst()

        switchTarget = when (SystemFlag.gamemode) {
            GameMode.SPELL_PRACTICE -> "spellSelect"
            GameMode.STAGE_PRACTICE -> "stageSelect"
            else -> "game"
        }

        if (selectedPlayer == null) {
            if (this::difficultyLabel.isInitialized) difficultyLabel.remove()
            difficultyLabel = DifficultySelectScreen.generateButton(SystemFlag.difficulty!!, 0, 0)
            st += difficultyLabel
            difficultyLabel.staticX = 150f
            difficultyLabel.staticY = 50f
            difficultyLabel.setScale(0.5f)
            difficultyLabel.clearActions()
            difficultyLabel.setPosition(difficultyLabel.staticX, difficultyLabel.staticY - 300f)
            difficultyLabel.addAction(
                moveTo(
                    difficultyLabel.staticX,
                    difficultyLabel.staticY,
                    duration,
                    Interpolation.pow5Out
                )
            )

            portraits.clearActions()
            portraits.setPosition(portraits.staticX + 800f, portraits.staticY)
            portraits.addAction(moveTo(portraits.staticX, portraits.staticY, duration, Interpolation.pow5Out))

            descriptions.clearActions()
            descriptions.setPosition(descriptions.staticX - 800f, descriptions.staticY)
            descriptions.addAction(
                moveTo(
                    descriptions.staticX,
                    descriptions.staticY,
                    duration,
                    Interpolation.pow5Out
                )
            )
        }
    }

    override fun fadeOut(newScreen: KotoScreen?, duration: Float) {
        super.fadeOut(newScreen, duration)

        if (selectedPlayer == null) {
            difficultyLabel.clearActions()
            difficultyLabel.addAction(
                moveTo(
                    difficultyLabel.staticX,
                    difficultyLabel.staticY - 300f,
                    duration,
                    Interpolation.pow5Out
                )
            )

            portraits.clearActions()
            portraits.addAction(
                moveTo(
                    portraits.staticX + 800f,
                    portraits.staticY,
                    duration,
                    Interpolation.pow5Out
                )
            )

            descriptions.clearActions()
            descriptions.addAction(
                moveTo(
                    descriptions.staticX - 800f,
                    descriptions.staticY,
                    duration,
                    Interpolation.pow5Out
                )
            )
        }
    }

    private fun showShotType(name: String, shottypes: GdxArray<String>) {
        this.shottypes.clearActions()
        this.shottypes.addAction(fadeIn(0.5f, Interpolation.pow5Out))
        input.addProcessor(shottypeGrid)

        descriptions.clearActions()
        descriptions.addAction(fadeOut(0.5f, Interpolation.pow5Out))
        input.removeProcessor(descriptions)
        input.removeProcessor(portraits)

        shottypeGrid.clear()

        val baseY = (250f * shottypes.size - 50f) / 2f + 600f
        shottypeBackground.color = playerColor(name).toHSVColor()
        shottypeBackground.y = baseY - 180f

        for (i in 0 until shottypes.size) {
            val extraUnlocked = SystemFlag.gamemode!! != GameMode.EXTRA || gameData.data[shottypes[i]].extraUnlocked
            shottypeGrid.add(
                GridButton(
                    bundle["ui.playerSelect.shottype.${shottypes[i]}.name"],
                    48,
                    0,
                    i,
                    staticX = 100f,
                    staticY = baseY - i * 250f,
                ) {
                    SystemFlag.shottype = shottypes[i]
                    switch()
                }.apply {
                    activeAction = getActiveAction({
                        Actions.run {
                            shottypeBackground.clearActions()
                            shottypeBackground.addAction(
                                moveTo(
                                    0f,
                                    baseY - i * 250f - 180f,
                                    1f,
                                    Interpolation.pow5Out
                                )
                            )
                        }
                    })
                    enabled = extraUnlocked
                }
            )
            shottypeGrid.add(
                GridButton(
                    bundle["ui.playerSelect.shottype.${shottypes[i]}.description"],
                    28,
                    0,
                    i,
                    staticX = 100f,
                    staticY = baseY - i * 250f - 170f,
                    height = 150f,
                    triggerSound = null,
                ).apply {
                    setAlignment(Align.left)
                    enabled = extraUnlocked
                }
            )
        }
        shottypeGrid.selectFirst()
    }

    private fun hideShotType() {
        shottypes.clearActions()
        shottypes.addAction(fadeOut(0.5f, Interpolation.pow5Out))
        input.removeProcessor(shottypeGrid)

        descriptions.clearActions()
        descriptions.setPosition(descriptions.staticX, descriptions.staticY)
        descriptions.addAction(fadeIn(0.5f, Interpolation.pow5Out))
        input.addProcessor(descriptions)
        input.addProcessor(portraits)
    }

    private fun switch() {
        if (switchTarget == "game") {
            SystemFlag.redirect = switchTarget
            SystemFlag.redirectDuration = 0.5f
            SystemFlag.replay = null
            SystemFlag.checkpoint = null
            SystemFlag.ending = null
            app.setScreen("blank", 0.5f)
        } else {
            app.setScreen(switchTarget, 0.5f)
        }
    }

    override fun onQuit() {
        if (selectedPlayer == null) {
            super.onQuit()
            app.setScreen("difficultySelect", 1f)
        } else {
            SE.play("cancel")
            selectedPlayer = null
            hideShotType()
        }
    }
}