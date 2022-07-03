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

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.crashinvaders.vfx.VfxManager
import com.hhs.koto.stg.Drawable

class VfxOutputDrawable(
    override var x: Float = 0f,
    override var y: Float = 0f,
    val width: Float,
    val height: Float,
    override val zIndex: Int = Int.MIN_VALUE,
) : Drawable {
    constructor(
        vfxManager: VfxManager,
        x: Float = 0f,
        y: Float = 0f,
        width: Float,
        height: Float,
        zIndex: Int = Int.MIN_VALUE,
    ) : this(x, y, width, height, zIndex) {
        this.vfxManager = vfxManager
    }

    override var alive: Boolean = true
    private val vfxOutput = TextureRegion()

    lateinit var vfxManager: VfxManager

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        vfxOutput.setRegion(vfxManager.resultBuffer.texture)
        vfxOutput.flip(false, true)
        val tmpColor = batch.color.cpy()
        batch.setColor(1f, 1f, 1f, parentAlpha)
        batch.draw(vfxOutput, x, y, width, height)
        batch.color = tmpColor
    }

    override fun tick() = Unit
}