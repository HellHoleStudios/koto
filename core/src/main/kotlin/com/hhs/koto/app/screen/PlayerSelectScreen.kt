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

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.app.ui.Grid
import com.hhs.koto.app.ui.GridImage
import com.hhs.koto.app.ui.GridLabel
import com.hhs.koto.app.ui.register
import com.hhs.koto.stg.GameMode
import com.hhs.koto.util.*

class PlayerSelectScreen : BasicScreen("mus/E0120.ogg", getRegion("bg/generic.png")) {
    val portraits = Grid().register(st, input)
    val descriptions = Grid().register(st, input)

    val selectedPlayer: String? = null

    companion object {
        fun generatePortrait(
            selectPortrait: TextureRegion,
            gridX: Int,
            gridY: Int,
            staticX: Float,
            staticY: Float,
            width: Float = selectPortrait.regionWidth.toFloat(),
            height: Float = width / selectPortrait.regionWidth * selectPortrait.regionHeight,
        ) = Grid(staticX = staticX, staticY = staticY, gridX = gridX, gridY = gridY).add(
            GridImage(
                selectPortrait,
                width = width,
                height = height,
                tint = Color(1f, 1f, 0f, 0.8f),
            ).apply {
                activeAction = {
                    Actions.sequence(
                        Actions.moveTo(this.staticX, this.staticY),
                        Actions.moveTo(this.staticX + 30f, this.staticY, 1f, Interpolation.pow5Out),
                    )
                }
            }
        ).add(
            GridImage(selectPortrait, width = width, height = height).apply {
                activeAction = {
                    Actions.sequence(
                        Actions.moveTo(this.staticX, this.staticY),
                        Actions.moveTo(this.staticX - 30f, this.staticY, 1f, Interpolation.pow5Out),
                    )
                }
            }
        ).apply {
            activeAction = { Actions.show() }
            inactiveAction = { Actions.hide() }
        }.selectFirst()

        fun generateDescription(
            title: String,
            name: String,
            description: String,
            gridX: Int,
            gridY: Int,
            staticX: Float,
            staticY: Float,
            color: Color,
        ) = Grid(staticX = staticX, staticY = staticY, gridX = gridX, gridY = gridY).add(
            GridLabel(
                title,
                width = 600f,
                height = 50f,
                staticY = 500f,
                activeStyle = Label.LabelStyle(
                    getFont(
                        bundle["font.boldItalic"],
                        36,
                        Color.WHITE,
                        borderColor = Color.BLACK
                    ), Color.WHITE
                ),
            ).apply {
                setAlignment(Align.bottom)
            }
        ).add(
            GridLabel(
                name,
                width = 600f,
                height = 100f,
                staticY = 400f,
                activeStyle = Label.LabelStyle(
                    getFont(
                        bundle["font.boldRegular"],
                        72,
                        color,
                        borderColor = Color.BLACK
                    ), Color.WHITE
                ),
            ).apply {
                setAlignment(Align.center)
            }
        ).add(
            GridLabel(description, 24, width = 600f, height = 400f).apply {
                setAlignment(Align.center)
            }
        ).apply {
            activeAction = { Actions.show() }
            inactiveAction = { Actions.hide() }
        }
    }

    init {
        val switchTarget: String = when (SystemFlag.gamemode) {
            GameMode.SPELL_PRACTICE -> "spellSelect"
            GameMode.STAGE_PRACTICE -> "stageSelect"
            else -> "game"
        }

        portraits.add(
            generatePortrait(
                getRegion("portrait/reimu/select.png"), 0, 0, 830f, 50f, 560f
            )
        )
        portraits.add(
            generatePortrait(
                getRegion("portrait/marisa/select.png"), 1, 0, 830f, 50f, 560f
            )
        )
        portraits.add(
            generatePortrait(
                getRegion("portrait/sakuya/select.png"), 2, 0, 880f, 50f, 460f
            )
        )
        portraits.add(
            generatePortrait(
                getRegion("portrait/youmu/select.png"), 3, 0, 780f, 50f, 720f
            )
        )
        portraits.add(
            generatePortrait(
                getRegion("portrait/sanae/select.png"), 4, 0, 750f, 50f, 680f
            )
        )
        portraits.selectFirst()

        descriptions.add(
            generateDescription(
                bundle["ui.playerSelect.reimu.title"],
                bundle["ui.playerSelect.reimu.name"],
                bundle["ui.playerSelect.reimu.description"],
                0,
                0,
                150f,
                250f,
                Color(1f, 0f, 0f, 1f)
            )
        )
        descriptions.add(
            generateDescription(
                bundle["ui.playerSelect.marisa.title"],
                bundle["ui.playerSelect.marisa.name"],
                bundle["ui.playerSelect.marisa.description"],
                1,
                0,
                150f,
                250f,
                Color(1f, 1f, 0f, 1f)
            )
        )
        descriptions.add(
            generateDescription(
                bundle["ui.playerSelect.sakuya.title"],
                bundle["ui.playerSelect.sakuya.name"],
                bundle["ui.playerSelect.sakuya.description"],
                2,
                0,
                150f,
                250f,
                Color(1f, 1f, 1f, 1f)
            )
        )
        descriptions.add(
            generateDescription(
                bundle["ui.playerSelect.youmu.title"],
                bundle["ui.playerSelect.youmu.name"],
                bundle["ui.playerSelect.youmu.description"],
                3,
                0,
                150f,
                250f,
                Color(0.3f, 1f, 0.5f, 1f)
            )
        )
        descriptions.add(
            generateDescription(
                bundle["ui.playerSelect.sanae.title"],
                bundle["ui.playerSelect.sanae.name"],
                bundle["ui.playerSelect.sanae.description"],
                4,
                0,
                150f,
                250f,
                Color(0f, 0.5f, 1f, 1f)
            )
        )
        descriptions.selectFirst()
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        portraits.clearActions()
        portraits.setPosition(portraits.staticX + 400f, portraits.staticY)
        portraits.addAction(Actions.moveTo(portraits.staticX, portraits.staticY, duration, Interpolation.pow5Out))

        descriptions.clearActions()
        descriptions.setPosition(descriptions.staticX - 400f, descriptions.staticY)
        descriptions.addAction(
            Actions.moveTo(
                descriptions.staticX,
                descriptions.staticY,
                duration,
                Interpolation.pow5Out
            )
        )
    }

    override fun fadeOut(newScreen: KotoScreen?, duration: Float) {
        super.fadeOut(newScreen, duration)
        portraits.clearActions()
        portraits.setPosition(portraits.staticX, portraits.staticY)
        portraits.addAction(
            Actions.moveTo(
                portraits.staticX + 400f,
                portraits.staticY,
                duration,
                Interpolation.pow5Out
            )
        )

        descriptions.clearActions()
        descriptions.setPosition(descriptions.staticX, descriptions.staticY)
        descriptions.addAction(
            Actions.moveTo(
                descriptions.staticX - 400f,
                descriptions.staticY,
                duration,
                Interpolation.pow5Out
            )
        )
    }

    override fun onQuit() {
        super.onQuit()
        app.setScreen("difficultySelect", 1f)
    }
}