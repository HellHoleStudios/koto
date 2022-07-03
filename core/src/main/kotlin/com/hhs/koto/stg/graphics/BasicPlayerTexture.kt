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

package com.hhs.koto.stg.graphics

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import ktx.collections.set

class BasicPlayerTexture(
    atlas: TextureAtlas,
    baseName: String,
    centerRegionName: String = "_center",
    leftRegionName: String = "_left",
    rightRegionName: String = "_right",
    movingLeftRegionName: String = "_movingLeft",
    movingRightRegionName: String = "_movingRight",
    centerTransitionTime: Int = 5,
    leftTransitionTime: Int = 5,
    rightTransitionTime: Int = 5,
    movingLeftTransitionTime: Int = 2,
    movingRightTransitionTime: Int = 2,
) : StarGraphStateMachineTexture(atlas, baseName, centerRegionName, centerTransitionTime) {

    init {
        branches["left"] =
            Branch(leftRegionName, leftTransitionTime, movingLeftRegionName, movingLeftTransitionTime)
        branches["right"] =
            Branch(rightRegionName, rightTransitionTime, movingRightRegionName, movingRightTransitionTime)
        build()
    }

    fun update(condition: Int) {
        when (condition) {
            0 -> super.update("")
            -1 -> super.update("left")
            1 -> super.update("right")
        }
    }
}