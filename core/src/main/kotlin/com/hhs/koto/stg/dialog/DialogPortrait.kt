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
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.hhs.koto.util.NO_TINT_HSV
import com.hhs.koto.util.TRANSPARENT_HSV
import com.hhs.koto.util.safeValues
import ktx.actors.alpha
import ktx.actors.then
import ktx.collections.GdxMap
import ktx.collections.set

open class DialogPortrait(
    val portraitName: String,
    val rightSide: Boolean,
    val textColor: Color = if (rightSide) Dialog.rightColor else Dialog.leftColor,
    val inactiveOffsetX: Float = 80f,
    val inactiveOffsetY: Float = 50f,
    val hiddenOffsetX: Float = 200f,
    val hiddenOffsetY: Float = 100f,
    val staticX: Float = 0f,
    val staticY: Float = 0f,
    val duration: Float = 30f,
    val interpolation: Interpolation = Interpolation.pow5Out,
    val inactiveColor: Color = Color(0f, 1f, 0.5f, 1f),
) : Group() {
    var variant: String? = null
        set(value) {
            variants.safeValues().forEach {
                it.isVisible = false
            }
            variants[value].toFront()
            variants[value].isVisible = true
            field = value
        }
    val variants: GdxMap<String, Image> = GdxMap()
    var state: DialogPortraitState = DialogPortraitState.HIDDEN

    enum class DialogPortraitState {
        HIDDEN, INACTIVE, ACTIVE
    }

    init {
        alpha = 0f
        setPosition(offset(staticX, hiddenOffsetX), staticY - hiddenOffsetY)
    }

    private fun offset(x: Float, offset: Float): Float = if (rightSide) {
        x + offset
    } else {
        x - offset
    }

    fun addVariant(
        name: String,
        texture: TextureRegion,
        x: Float,
        y: Float,
        width: Float,
        height: Float = width / texture.regionWidth * texture.regionHeight,
    ) {
        val image = Image(texture)
        image.setBounds(x, y, width, height)
        image.color = TRANSPARENT_HSV
        image.isVisible = false
        variants[name] = image
        addActor(image)
    }

    fun show() {
        state = DialogPortraitState.INACTIVE
        isVisible = true
        addAction(
            moveTo(
                offset(staticX, inactiveOffsetX),
                staticY - inactiveOffsetY,
                duration,
                interpolation,
            )
        )
        addAction(fadeIn(duration, interpolation))
    }

    fun hide() {
        state = DialogPortraitState.HIDDEN
        addAction(
            moveTo(
                offset(staticX, hiddenOffsetX),
                staticY - hiddenOffsetY,
                duration,
                interpolation,
            )
        )
        addAction(fadeOut(duration, interpolation) then Actions.hide())
    }

    fun activate() {
        state = DialogPortraitState.ACTIVE
        addAction(
            moveTo(
                staticX,
                staticY,
                duration,
                interpolation,
            )
        )
        children.forEach {
            it.addAction(color(NO_TINT_HSV, duration, interpolation))
        }
    }

    fun deactivate() {
        state = DialogPortraitState.INACTIVE
        addAction(
            moveTo(
                offset(staticX, inactiveOffsetX),
                staticY - inactiveOffsetY,
                duration,
                interpolation,
            )
        )
        children.forEach {
            it.addAction(color(inactiveColor, duration, interpolation))
        }
    }
}