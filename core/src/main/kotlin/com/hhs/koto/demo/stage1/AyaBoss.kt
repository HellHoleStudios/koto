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

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.hhs.koto.stg.graphics.BasicBoss
import com.hhs.koto.stg.graphics.StarGraphStateMachineTexture
import com.hhs.koto.stg.graphics.TileBackground
import com.hhs.koto.stg.pattern.interpolate
import com.hhs.koto.stg.task.CoroutineTask
import com.hhs.koto.stg.task.RunnableTask
import com.hhs.koto.stg.task.Task
import com.hhs.koto.util.A
import com.hhs.koto.util.TRANSPARENT_HSV
import com.hhs.koto.util.game
import com.hhs.koto.util.getRegion
import ktx.collections.set

class AyaBoss(
    x: Float = 300f,
    y: Float = 300f,
) : BasicBoss(x, y, 30f) {
    val textureStateMachine = StarGraphStateMachineTexture(
        A["sprite/th10_aya.atlas"], "th10_aya", "_center", 5
    ).apply {
        branches["left"] = StarGraphStateMachineTexture.Branch(
            edgeRegionName = "_movingLeft", edgeTransitionTime = 5
        )
        branches["right"] = StarGraphStateMachineTexture.Branch(
            edgeRegionName = "_movingRight", edgeTransitionTime = 5
        )
        branches["action"] = StarGraphStateMachineTexture.Branch(
            edgeRegionName = "_action", edgeTransitionTime = 5
        )
        build()
    }
    override val name: String = "aya"
    override val texture: TextureRegion
        get() = textureStateMachine.texture
    override val width: Float = 64f
    override val height: Float = 64f
    var usingAction: Boolean = false
    var oldX: Float = x
    lateinit var spellBackground: TileBackground
    lateinit var spellForeground: TileBackground

    override fun createSpellBackground(): Task = CoroutineTask {
        useDistortionEffect = false
        spellBackground = TileBackground(
            getRegion("st1/spell_background.png"),
            -100,
            color = TRANSPARENT_HSV,
        )
        spellForeground = TileBackground(
            getRegion("st1/spell_foreground.png"),
            -100,
            -1f,
            1f,
            color = TRANSPARENT_HSV,
        )
        game.background.addDrawable(spellBackground)
        game.background.addDrawable(spellForeground)
        interpolate(0f, 1f, 20) {
            spellBackground.color.a = it
            spellForeground.color.a = it / 2f
        }
    }

    override fun removeSpellBackground(): Task = RunnableTask {
        useDistortionEffect = true
        game.background.removeDrawable(spellBackground)
        game.background.removeDrawable(spellForeground)
    }

    override fun tick() {
        if (usingAction) {
            textureStateMachine.update("action")
        } else if (x < oldX) {
            textureStateMachine.update("left")
        } else if (x > oldX) {
            textureStateMachine.update("right")
        } else {
            textureStateMachine.update("")
        }
        oldX = x
        super.tick()
    }
}