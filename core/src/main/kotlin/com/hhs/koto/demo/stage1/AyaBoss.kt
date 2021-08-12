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

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.hhs.koto.stg.drawable.BasicBoss
import com.hhs.koto.stg.drawable.StarGraphStateMachineTexture
import com.hhs.koto.util.A
import ktx.collections.set

class AyaBoss : BasicBoss(
    0f,
    0f,
    0f,
) {
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
    override val texture: TextureRegion
        get() = textureStateMachine.texture
    override val width: Float = 64f
    override val height: Float = 64f
    var oldX: Float = x

    override fun tick() {
        if (x < oldX) {
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