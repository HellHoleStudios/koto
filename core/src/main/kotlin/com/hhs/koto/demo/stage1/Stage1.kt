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

import com.badlogic.gdx.scenes.scene2d.actions.Actions.forever
import com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.task.RunnableTask
import com.hhs.koto.stg.task.SequenceTask
import com.hhs.koto.stg.task.StageBuilder
import com.hhs.koto.util.game
import com.hhs.koto.util.getRegion
import ktx.actors.plusAssign
import ktx.actors.then

class Stage1 : StageBuilder {
    override val availableDifficulties = GameDifficulty.REGULAR_AVAILABLE
    override val name = "stage1"

    override fun build() = SequenceTask(
        RunnableTask {
            game.st += Image(getRegion("icon/koto-icon_128x.png")).apply {
                setBounds(-150f, -200f, 128f, 128f)
            }
            game.st += Image(getRegion("ui/blank.png")).apply {
                setBounds(-10f, -150f, 20f, 20f)
                addAction(
                    forever(
                        moveTo(-10f, 150f, 30f)
                                then moveTo(-10f, -150f, 30f)
                    )
                )
            }
        },
        TestSpell().build(),
    )
}