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

package com.hhs.koto.demo.stage1

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.hhs.koto.demo.portrait.AyaPortrait
import com.hhs.koto.demo.portrait.MarisaPlayerPortrait
import com.hhs.koto.demo.portrait.ReimuPlayerPortrait
import com.hhs.koto.stg.dialog.Dialog
import com.hhs.koto.stg.graphics.BGMNameDisplay
import com.hhs.koto.stg.task.CoroutineTask
import com.hhs.koto.stg.task.Task
import com.hhs.koto.stg.task.TaskBuilder
import com.hhs.koto.util.BGM
import com.hhs.koto.util.bundle
import com.hhs.koto.util.game

object Stage1Dialog1 : TaskBuilder {
    override fun build(): Task = CoroutineTask {
        val dialog = Dialog()
        dialog.addPortrait(ReimuPlayerPortrait())
        dialog.addPortrait(AyaPortrait())
        dialog.addPortrait(MarisaPlayerPortrait())
        dialog.start()

        dialog.setVariant("reimuPlayer", "laugh")
        dialog.show("reimuPlayer")
        dialog.setTextAndWait("reimuPlayer", bundle["game.dialog.stage1.dialog1"])

        dialog.setVariant("aya", "smile")
        dialog.show("aya")
        dialog.setVariant("reimuPlayer", "smile")
        dialog.setTextAndWait("aya", bundle["game.dialog.stage1.dialog2"])

        dialog.setTextAndWait("reimuPlayer", bundle["game.dialog.stage1.dialog3"])

        dialog.setVariant("aya", "laugh")
        dialog.setTextAndWait("aya", bundle["game.dialog.stage1.dialog4"])

        dialog.setVariant("marisaPlayer", "smile")
        dialog.setVariant("reimuPlayer", "surprise")
        dialog.show("marisaPlayer")
        dialog.setTextAndWait("marisaPlayer", bundle["game.dialog.stage1.dialog5"])

        dialog.setTextAndWait("reimuPlayer", bundle["game.dialog.stage1.dialog6"])

        dialog.setVariant("marisaPlayer", "laugh")
        dialog.setTextAndWait("marisaPlayer", bundle["game.dialog.stage1.dialog7"])

        dialog.setTextAndWait("aya", bundle["game.dialog.stage1.dialog8"])

        BGM.play(3, true)
        game.hud.addDrawable(BGMNameDisplay(3).apply {
            x=0f
        })

        dialog.setTextAndWait("aya", bundle["game.dialog.stage1.dialog9"])
        dialog.setTextAndWait("aya", bundle["game.dialog.stage1.dialog10"])

        dialog.end()
    }
}