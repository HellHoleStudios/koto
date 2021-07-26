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
import com.hhs.koto.app.ui.*
import com.hhs.koto.stg.GameMode
import com.hhs.koto.util.*
import ktx.scene2d.actors

class PlayerSelectScreen : BasicScreen("mus/E0120.ogg", getRegion("bg/generic.png")) {
    var difficultyLabel: Grid? = null
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
            name: String,
            gridX: Int,
            gridY: Int,
            staticX: Float = 150f,
            staticY: Float = 250f,
        ) = Grid(staticX = staticX, staticY = staticY, gridX = gridX, gridY = gridY).add(
            GridImage(
                getRegion("ui/arrow.png"),
                width = 48f,
                staticX = -80f,
                staticY = 425f,
                activeAction = null,
            )
        ).add(
            GridImage(
                getRegion("ui/arrow.png"),
                width = 48f,
                staticX = 680f,
                staticY = 425f,
                activeAction = null,
            ).apply {
                setScale(-1f, 1f)
            }
        ).add(
            GridLabel(
                bundle["ui.playerSelect.$name.title"],
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
                bundle["ui.playerSelect.$name.name"],
                width = 600f,
                height = 100f,
                staticY = 400f,
                activeStyle = Label.LabelStyle(
                    getFont(
                        bundle["font.boldRegular"],
                        72,
                        Color.valueOf(bundle["ui.playerSelect.$name.color"]),
                        borderColor = Color.BLACK
                    ), Color.WHITE
                ),
            ).apply {
                setAlignment(Align.center)
            }
        ).add(
            GridLabel(bundle["ui.playerSelect.$name.description"], 24, width = 600f, height = 400f).apply {
                setAlignment(Align.center)
            }
        ).apply {
            activeAction = { Actions.show() }
            inactiveAction = { Actions.hide() }
        }.selectFirst()
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

        descriptions.add(generateDescription("reimu", 0, 0))
        descriptions.add(generateDescription("marisa", 1, 0))
        descriptions.add(generateDescription("sakuya", 2, 0))
        descriptions.add(generateDescription("youmu", 3, 0))
        descriptions.add(generateDescription("sanae", 4, 0))
        descriptions.selectFirst()
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)

        difficultyLabel?.remove()
        difficultyLabel = DifficultySelectScreen.generateButton(SystemFlag.difficulty!!, 0, 0)
        st.addActor(difficultyLabel)
        difficultyLabel!!.staticX = 150f
        difficultyLabel!!.staticY = 25f
        difficultyLabel!!.setScale(0.5f)
        difficultyLabel!!.clearActions()
        difficultyLabel!!.setPosition(difficultyLabel!!.staticX, difficultyLabel!!.staticY - 200f)
        difficultyLabel!!.addAction(
            Actions.moveTo(
                difficultyLabel!!.staticX,
                difficultyLabel!!.staticY,
                duration,
                Interpolation.pow5Out
            )
        )

        portraits.clearActions()
        portraits.setPosition(portraits.staticX + 800f, portraits.staticY)
        portraits.addAction(Actions.moveTo(portraits.staticX, portraits.staticY, duration, Interpolation.pow5Out))

        descriptions.clearActions()
        descriptions.setPosition(descriptions.staticX - 800f, descriptions.staticY)
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

        difficultyLabel!!.clearActions()
        difficultyLabel!!.addAction(
            Actions.moveTo(
                difficultyLabel!!.staticX,
                difficultyLabel!!.staticY - 200f,
                duration,
                Interpolation.pow5Out
            )
        )

        portraits.clearActions()
        portraits.addAction(
            Actions.moveTo(
                portraits.staticX + 800f,
                portraits.staticY,
                duration,
                Interpolation.pow5Out
            )
        )

        descriptions.clearActions()
        descriptions.addAction(
            Actions.moveTo(
                descriptions.staticX - 800f,
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