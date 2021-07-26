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
import com.hhs.koto.app.ui.Grid
import com.hhs.koto.app.ui.GridImage
import com.hhs.koto.stg.GameMode
import com.hhs.koto.util.SystemFlag
import com.hhs.koto.util.app
import com.hhs.koto.util.getRegion

class PlayerSelectScreen : BasicScreen("mus/E0120.ogg", getRegion("bg/generic.png")) {
    val portraits = Grid().apply {
        st.addActor(this)
        input.addProcessor(this)
    }

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
                        Actions.moveTo(this.staticX + 50f, this.staticY, 1f, Interpolation.pow5Out),
                    )
                }
            }
        ).add(
            GridImage(selectPortrait, width = width, height = height)
        ).apply {
            activeAction = { Actions.show() }
            inactiveAction = { Actions.hide() }
        }.selectFirst()
    }

    init {
        val switchTarget: String = when (SystemFlag.gamemode) {
            GameMode.SPELL_PRACTICE -> "spellSelect"
            GameMode.STAGE_PRACTICE -> "stageSelect"
            else -> "playerSelect"
        }

        portraits.add(
            generatePortrait(
                getRegion("portrait/reimu/select.png"),
                0,
                0,
                800f,
                50f,
                560f
            )
        )
        portraits.add(
            generatePortrait(
                getRegion("portrait/marisa/select.png"),
                1,
                0,
                800f,
                50f,
                560f
            )
        )
        portraits.add(
            generatePortrait(
                getRegion("portrait/sakuya/select.png"),
                2,
                0,
                850f,
                50f,
                460f
            )
        )
        portraits.add(
            generatePortrait(
                getRegion("portrait/youmu/select.png"),
                3,
                0,
                750f,
                50f,
                700f
            )
        )
        portraits.add(
            generatePortrait(
                getRegion("portrait/sanae/select.png"),
                4,
                0,
                720f,
                50f,
                680f
            )
        )
        portraits.selectFirst()
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        portraits.clearActions()
        portraits.setPosition(portraits.staticX + 400f, portraits.staticY)
        portraits.addAction(Actions.moveTo(portraits.staticX, portraits.staticY, duration, Interpolation.pow5Out))
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
    }

    override fun onQuit() {
        super.onQuit()
        app.setScreen("difficultySelect", 1f)
    }
}