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

package com.hhs.koto.stg.dialog

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.stg.task.wait
import com.hhs.koto.util.*
import kotlinx.coroutines.yield
import ktx.actors.plusAssign
import ktx.collections.GdxArray

class Dialog : Group() {

    companion object {
        val leftColor: Color = Color(0f, 0.5f, 1f, 1f)
        val rightColor: Color = Color(0.5f, 0.5f, 1f, 1f)
    }

    val portraits: GdxArray<DialogPortrait> = GdxArray()
    val portraitGroup: Group = Group().apply {
        this@Dialog += this
    }
    val textBackground: Image = Image(getRegion("ui/blank.png")).apply {
        this@Dialog += this
        setBounds(150f, 100f, 708f, 180f)
        color = Color(0f, 0f, 0f, 0f)
    }
    val textLabel: Label = Label(
        "",
        Label.LabelStyle(getFont(36), null),
    ).apply {
        this@Dialog += this
        setAlignment(Align.topLeft)
        setBounds(170f, 110f, 668f, 160f)
        wrap = true
    }

    fun start() {
        game.overlay.addActor(this)
        game.inDialog = true
        showText()
    }

    suspend fun end() {
        hideAll()
        hideText()
        wait(60)
        remove()
        game.inDialog = false
    }

    fun showText() {
        textLabel.addAction(fadeIn(60f, Interpolation.pow5Out))
        textBackground.addAction(alpha(0.8f, 60f, Interpolation.pow5Out))
    }

    fun hideText() {
        textLabel.addAction(fadeOut(60f, Interpolation.pow5Out))
        textBackground.addAction(fadeOut(60f, Interpolation.pow5Out))
    }

    fun setText(text: String, color: Color = WHITE_HSV) {
        textLabel.setText(text)
        textLabel.color = color
    }

    fun setText(portraitName: String, text: String) {
        setText(text, portraits.find { it.name == portraitName }!!.textColor)
    }

    suspend fun setTextAndWait(text: String, color: Color = WHITE_HSV, duration: Int = 240) {
        setText(text, color)
        for (i in 0 until duration) {
            yield()
            if (game.justPressed(VK.SELECT)) {
                SE.play("select")
                break
            }
        }
    }

    suspend fun setTextAndWait(portraitName: String, text: String, duration: Int = 240) {
        val portrait = portraits.find { it.portraitName == portraitName }!!
        activate(portraitName)
        setTextAndWait(text, portrait.textColor, duration)
    }

    fun addPortrait(portrait: DialogPortrait) {
        portraits.add(portrait)
        portraitGroup += portrait
    }

    fun setVariant(portraitName: String, variant: String) {
        portraits.find { it.portraitName == portraitName }!!.variant = variant
    }

    fun show(portraitName: String) {
        portraits.find { it.portraitName == portraitName }!!.show()
    }

    fun hide(portraitName: String) {
        portraits.find { it.portraitName == portraitName }!!.hide()
    }

    fun showAll() {
        portraits.forEach { it.show() }
    }

    fun hideAll() {
        portraits.forEach { it.hide() }
    }

    fun activate(portraitName: String) {
        portraits.forEach {
            if (it.portraitName == portraitName) {
                it.activate()
                it.toFront()
            } else if (it.state == DialogPortrait.DialogPortraitState.ACTIVE) {
                it.deactivate()
            }
        }
    }
}