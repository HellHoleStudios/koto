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

package com.hhs.koto.app.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions.color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.util.darken
import com.hhs.koto.util.getUILabelStyle
import ktx.collections.GdxArray

class GridKeyboard(val lengthLimit: Int = 10, val onConfirmation: (String) -> Unit) : Grid() {
    companion object {
        val rows = GdxArray.with(
            "ABCDEFGHIJKLM",
            "NOPQRSTUVWXYZ",
            "abcdefghijklm",
            "nopqrstuvwxyz",
            "0123456789+-=",
            "`~!?@#$%^&*_|",
            "()[]{}<>,./\\;",
            ":'\"",
        )

        fun GridButton.setStyle(): GridButton {
            activeAction = {
                color(Color.WHITE)
            }
            inactiveAction = {
                color(darken(Color.WHITE, 0.5f))
            }
            setAlignment(Align.center)
            width = 50f
            return this
        }
    }

    var text = ""
    private val textLabel = Label("", getUILabelStyle(48)).apply {
        setBounds(0f, 150f, 780f, 50f)
        addActor(this)
    }
    private var counter: Float = 0f

    init {
        for (row in 0..6) {
            for (i in 0 until 13) {
                val character = rows[row][i].toString()
                add(
                    GridButton(character, 48, i, row) {
                        addText(character)
                    }.setStyle()
                )
            }
        }
        for (i in 0..2) {
            val character = rows[7][i].toString()
            add(
                GridButton(character, 48, i, 7) {
                    addText(character)
                }.setStyle()
            )
        }
        add(
            GridButton("␣", 48, 3, 7) {
                addText(" ")
            }.setStyle()
        )
        add(
            GridButton("⇦", 48, 11, 7) {
                text = text.dropLast(1)
            }.setStyle()
        )
        add(
            GridButton("OK", 48, 12, 7) {
                onConfirmation(text)
            }.setStyle().apply {
                setAlignment(Align.left)
            }
        )
        arrange(0f, 0f, 60f, -60f)
        selectFirst()
    }

    fun addText(newText: String) {
        text = (text + newText).take(lengthLimit)
    }

    override fun act(delta: Float) {
        super.act(delta)
        counter += delta
        if (counter >= 1f) {
            counter = 0f
        }
        val newText = if (counter > 0.5f) {
            text + "_"
        } else {
            text
        }
        if (textLabel.text.toString() != newText) {
            textLabel.setText(newText)
        }
    }

}
