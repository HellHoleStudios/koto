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

package com.hhs.koto.stg

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector3
import com.hhs.koto.app.Config
import com.hhs.koto.util.*

class MagicCircle(
    val size: Float = 256f,
    var color: Color = Color.WHITE,
) {
    val texture = getRegion("particle/magic_circle.png")
    val vertices = FloatArray(20)
    var camera = OrthographicCamera(Config.worldW, Config.worldH)
    var t: Int = 0

    companion object {
        val tmpVector = Vector3()
    }

    init {
        vertices[Batch.U1] = texture.u
        vertices[Batch.V1] = texture.v2
        vertices[Batch.C1] = Color.WHITE_FLOAT_BITS
        vertices[Batch.U2] = texture.u
        vertices[Batch.V2] = texture.v
        vertices[Batch.C2] = Color.WHITE_FLOAT_BITS
        vertices[Batch.U3] = texture.u2
        vertices[Batch.V3] = texture.v
        vertices[Batch.C3] = Color.WHITE_FLOAT_BITS
        vertices[Batch.U4] = texture.u2
        vertices[Batch.V4] = texture.v2
        vertices[Batch.C4] = Color.WHITE_FLOAT_BITS
    }

    fun tick() {
        t = (t + 1) % 2160
        camera.position.set(200f, 0f, 0f)
        camera.direction.set(-1f, 0f, 0f)
        camera.up.set(0f, 0f, 1f)
        camera.rotateAround(Vector3.Zero, Vector3.Y, 60f + 30f * cos(t / 3f))
        camera.rotateAround(Vector3.Zero, Vector3.Z, t.toFloat())
        camera.rotate(t.toFloat() / 2f)
        camera.update()
    }

    fun draw(batch: Batch, parentAlpha: Float, x: Float, y: Float) {
        tmpColor.set(batch.color)
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)
        batch.setBlending(BlendingMode.ADD)

        tmpVector.set(-size / 2f, -size / 2f, 0f)
        camera.project(tmpVector, -Config.originX, -Config.originY, Config.worldW, Config.worldH)
        vertices[Batch.X1] = tmpVector.x + x
        vertices[Batch.Y1] = tmpVector.y + y

        tmpVector.set(-size / 2f, size / 2f, 0f)
        camera.project(tmpVector, -Config.originX, -Config.originY, Config.worldW, Config.worldH)
        vertices[Batch.X2] = tmpVector.x + x
        vertices[Batch.Y2] = tmpVector.y + y

        tmpVector.set(size / 2f, size / 2f, 0f)
        camera.project(tmpVector, -Config.originX, -Config.originY, Config.worldW, Config.worldH)
        vertices[Batch.X3] = tmpVector.x + x
        vertices[Batch.Y3] = tmpVector.y + y

        tmpVector.set(size / 2f, -size / 2f, 0f)
        camera.project(tmpVector, -Config.originX, -Config.originY, Config.worldW, Config.worldH)
        vertices[Batch.X4] = tmpVector.x + x
        vertices[Batch.Y4] = tmpVector.y + y

        batch.draw(texture.texture, vertices, 0, 20)
        batch.setBlending(BlendingMode.ALPHA)
        batch.color = tmpColor
    }
}