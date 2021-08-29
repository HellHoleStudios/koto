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

package com.hhs.koto.demo.stage1

import com.hhs.koto.demo.portrait.AyaPortrait
import com.hhs.koto.demo.portrait.ReimuPortraitLeft
import com.hhs.koto.stg.dialog.Dialog
import com.hhs.koto.stg.task.CoroutineTask
import com.hhs.koto.stg.task.Task
import com.hhs.koto.stg.task.TaskBuilder
import com.hhs.koto.util.bundle

object Stage1Dialog1 : TaskBuilder {
    override fun build(): Task = CoroutineTask {
        val dialog = Dialog()
        dialog.addPortrait(ReimuPortraitLeft())
        dialog.addPortrait(AyaPortrait())
        dialog.setVariant("reimuLeft", "laugh")
        dialog.show("reimuLeft")
        dialog.activate("reimuLeft")
        dialog.start()

        dialog.setTextAndWait(bundle["game.dialog.stage1.dialog1"], Dialog.leftColor)

        dialog.setVariant("aya", "smile")
        dialog.show("aya")
        dialog.activate("aya")
        dialog.setVariant("reimuLeft", "smile")
        dialog.setTextAndWait(bundle["game.dialog.stage1.dialog2"], Dialog.rightColor)

        dialog.activate("reimuLeft")
        dialog.setTextAndWait(bundle["game.dialog.stage1.dialog3"], Dialog.leftColor)

        dialog.setVariant("aya", "laugh")
        dialog.activate("aya")
        dialog.setTextAndWait(bundle["game.dialog.stage1.dialog4"], Dialog.rightColor)

        dialog.end()
    }
}