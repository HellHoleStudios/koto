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

package com.hhs.koto.app.ui

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.hhs.koto.stg.GameData
import com.hhs.koto.stg.Replay
import com.hhs.koto.util.*
import ktx.actors.alpha
import ktx.actors.plusAssign
import ktx.collections.GdxArray
import java.util.*

class SaveMenu(
    st: Stage,
    val input: InputMultiplexer,
    val staticX: Float,
    val staticY: Float,
    var lengthLimit: Int = 10,
    var onFinish: () -> Unit,
) : Group() {
    companion object {
        @Suppress("SpellCheckingInspection")
        val rows: GdxArray<String> = GdxArray.with(
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
            width = 40f
            return this
        }
    }

    var text = ""
    private val keyboardGrid = Grid().apply { this@SaveMenu += this }
    private val textLabel = Label("", getUILabelStyle(36)).apply {
        setBounds(10f, 100f, 500f, 50f)
        addActor(this)
    }
    private var counter: Float = 0f
    lateinit var saveObject: Any
    var active: Boolean = false

    init {
        setPosition(staticX, staticY - 300f)
        st += this

        textLabel.alpha = 0f
        keyboardGrid.alpha = 0f
        keyboardGrid.activeAction = { fadeIn(0.5f, Interpolation.pow5Out) }
        keyboardGrid.inactiveAction = { fadeOut(0.5f, Interpolation.pow5Out) }

        for (row in 0..6) {
            @Suppress("SpellCheckingInspection")
            for (i in 0 until 13) {
                val character = rows[row][i].toString()
                keyboardGrid.add(
                    GridButton(character, 36, i, row) {
                        addText(character)
                    }.setStyle()
                )
            }
        }
        for (i in 0..2) {
            val character = rows[7][i].toString()
            keyboardGrid.add(
                GridButton(character, 36, i, 7) {
                    addText(character)
                }.setStyle()
            )
        }
        keyboardGrid.add(
            GridButton("\u2423", 36, 3, 7) {
                addText(" ")
            }.setStyle()
        )
        keyboardGrid.add(
            GridButton("\u21e6", 36, 11, 7, triggerSound = "cancel") {
                text = text.dropLast(1)
            }.setStyle()
        )
        keyboardGrid.add(
            GridButton("OK", 36, 12, 7) {
                save()
            }.setStyle().apply {
                setAlignment(Align.left)
            }
        )
        keyboardGrid.arrange(0f, 0f, 40f, -40f)
        deactivate()
    }

    fun addText(newText: String) {
        text = (text + newText).take(lengthLimit)
    }

    fun activate(saveObject: Any) {
        this.saveObject = saveObject
        keyboardGrid.selectFirst()
        keyboardGrid.activate()
        textLabel.addAction(fadeIn(0.5f, Interpolation.pow5Out))
        addAction(moveTo(staticX, staticY, 0.5f, Interpolation.pow5Out))
        input.addProcessor(keyboardGrid)
        active = true
    }

    fun deactivate() {
        keyboardGrid.deactivate()
        textLabel.addAction(fadeOut(0.5f, Interpolation.pow5Out))
        addAction(moveTo(staticX, staticY - 300f, 0.5f, Interpolation.pow5Out))
        input.removeProcessor(keyboardGrid)
        active = false
    }

    fun save() {
        val saveObject = saveObject
        if (saveObject is GameData.ScoreEntry) {
            saveObject.name = text
            gameData.currentElement.score.add(saveObject)
            saveGameData()
        } else if (saveObject is Replay) {
            saveObject.name = text
            saveObject.date = Date()
            saveReplay(saveObject)
        }
        SE.play("extend")
        deactivate()
        onFinish()
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (active && VK.CANCEL.justPressed()) {
            SE.play("cancel")
            if (text.isNotEmpty()) {
                text = text.dropLast(1)
            } else {
                deactivate()
                onFinish()
            }
        }
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