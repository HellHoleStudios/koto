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

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.crashinvaders.vfx.VfxManager
import com.hhs.koto.stg.IndexedActor

class VfxOutput() : Image(), IndexedActor {
    override val z: Int = Int.MIN_VALUE

    constructor(vfxManager: VfxManager) : this() {
        this.vfxManager = vfxManager
    }

    private val vfxOutput = TextureRegionDrawable(TextureRegion())

    lateinit var vfxManager: VfxManager

    init {
        drawable = vfxOutput
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (vfxManager.resultBuffer == null) return
        setSize(vfxManager.width.toFloat(), vfxManager.height.toFloat())
        vfxOutput.region.setRegion(vfxManager.resultBuffer.texture)
        vfxOutput.region.flip(false, true)
        super.draw(batch, parentAlpha)
    }
}