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

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.util.*
import ktx.actors.plusAssign

class CreditScreen : BasicScreen(2, getRegion("bg/credit.png")) {

    val creditGroup = Group().apply {
        st += this
    }
    val nextDream = Label(
        bundle["ui.credit.nextDream"],
        Label.LabelStyle(getFont(120, bundle["font.subtitle"]), WHITE_HSV),
    ).apply {
        setSize(1440f, 120f)
        setAlignment(Align.center)
        st += this
    }

    init {
        val titleStyle = Label.LabelStyle(getFont(36, bundle["font.title"]), WHITE_HSV)
        val subtitleStyle = Label.LabelStyle(getFont(36, bundle["font.subtitle"]), WHITE_HSV)

        creditGroup.clear()
        creditGroup.addActor(Label(
            bundle["ui.credit.title"],
            Label.LabelStyle(getFont(120, bundle["font.title"]), WHITE_HSV),
        ).apply {
            setBounds(0f, 0f, 1440f, 120f)
            setAlignment(Align.center)
        })
        creditGroup.addActor(Label(bundle["ui.credit.text1"], subtitleStyle).apply {
            setPosition(400f, -100f)
        })
        creditGroup.addActor(Label(bundle["ui.credit.text2"], subtitleStyle).apply {
            setPosition(500f, -150f)
        })

        creditGroup.addActor(Label(bundle["ui.credit.text3"], titleStyle).apply {
            setPosition(400f, -250f)
        })

        creditGroup.addActor(Label(bundle["ui.credit.text4"], titleStyle).apply {
            setPosition(300f, -350f)
        })
        creditGroup.addActor(Label(bundle["ui.credit.text5"], subtitleStyle).apply {
            setPosition(400f, -400f)
        })

        creditGroup.addActor(Label(bundle["ui.credit.text6"], titleStyle).apply {
            setPosition(300f, -500f)
        })
        creditGroup.addActor(Label(bundle["ui.credit.text7"], subtitleStyle).apply {
            setPosition(400f, -550f)
        })

        creditGroup.addActor(Label(bundle["ui.credit.text8"], titleStyle).apply {
            setPosition(300f, -650f)
        })
        creditGroup.addActor(Label(bundle["ui.credit.text9"], subtitleStyle).apply {
            setPosition(400f, -700f)
        })
        creditGroup.addActor(Label(bundle["ui.credit.text10"], titleStyle).apply {
            setPosition(300f, -800f)
        })
        creditGroup.addActor(Label(bundle["ui.credit.text11"], subtitleStyle).apply {
            setPosition(400f, -850f)
        })
    }

    override fun fadeIn(oldScreen: KotoScreen?, duration: Float) {
        super.fadeIn(oldScreen, duration)
        nextDream.setPosition(0f, -1500f)
        nextDream.clearActions()
        nextDream.addAction(moveBy(0f, 2000f, 24f))

        creditGroup.setPosition(0f, -200f)
        creditGroup.clearActions()
        creditGroup.addAction(
            sequence(
                moveBy(0f, 2400f, 30f),
                delay(3f),
                Actions.run {
                    onQuit()
                },
            )
        )
    }

    override fun onQuit() {
        SystemFlag.redirect = "game"
        SystemFlag.redirectDuration = 1f
        app.setScreen("blank", 3f)
    }
}