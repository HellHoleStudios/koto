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

package com.hhs.koto.stg.bullet

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array

class BulletTexture(atlas: TextureAtlas, name: String, frames: Array<Int>) {

    private var frames: Array<out TextureRegion> = atlas.findRegions(name)
    private var frameTime: Array<Int> = Array()

    init {
        for (i in 0 until frames.size) {
            if (i == 0) {
                frameTime.add(frames[0])
            } else {
                frameTime.add(frameTime[i - 1] + frames[0])
            }
        }
    }

    fun getFrame(frame: Int): TextureRegion {
        val tmp = frame % frameTime.last()
        for (i in 0 until frameTime.size) {
            if (tmp < frameTime[i]) {
                return frames[i]
            }
        }
        return frames[0]
    }

    val maxWidth: Int
        get() {
            var ret = 0
            frames.forEach {
                ret = ret.coerceAtLeast(it.regionWidth)
            }
            return ret
        }

    val maxHeight: Int
        get() {
            var ret = 0
            frames.forEach {
                ret = ret.coerceAtLeast(it.regionHeight)
            }
            return ret
        }
}