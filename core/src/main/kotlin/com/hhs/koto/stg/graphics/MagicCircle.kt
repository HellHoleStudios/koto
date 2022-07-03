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

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector3
import com.hhs.koto.app.Config.worldH
import com.hhs.koto.app.Config.worldOriginX
import com.hhs.koto.app.Config.worldOriginY
import com.hhs.koto.app.Config.worldW
import com.hhs.koto.util.BlendingMode
import com.hhs.koto.util.cos
import com.hhs.koto.util.getRegion
import com.hhs.koto.util.setBlending

class MagicCircle(
    val size: Float = 256f,
    var color: Color = Color(0f, 1f, 1f, 0.5f),
) {
    val texture = getRegion("particle/magic_circle.png")
    val vertices = FloatArray(20)
    var camera = OrthographicCamera(worldW, worldH)
    var t: Int = 0

    companion object {
        val tmpVector = Vector3()
    }

    init {
        vertices[Batch.U1] = texture.u
        vertices[Batch.V1] = texture.v2
        vertices[Batch.U2] = texture.u
        vertices[Batch.V2] = texture.v
        vertices[Batch.U3] = texture.u2
        vertices[Batch.V3] = texture.v
        vertices[Batch.U4] = texture.u2
        vertices[Batch.V4] = texture.v2
    }

    fun tick() {
        t = (t + 1) % 2160
        camera.position.set(200f, 0f, 0f)
        camera.direction.set(-1f, 0f, 0f)
        camera.up.set(0f, 0f, 1f)
        camera.rotateAround(Vector3.Zero, Vector3.Y, 60f + 30f * cos(t / 3f))
        camera.rotateAround(Vector3.Zero, Vector3.Z, t * 2f)
        camera.rotate(t / 2f)
        camera.update()
    }

    fun draw(batch: Batch, parentAlpha: Float, x: Float, y: Float) {
        if (color.a < 0.001f) return

        val tmpColor = color.cpy()
        color.a *= parentAlpha
        vertices[Batch.C1] = tmpColor.toFloatBits()
        vertices[Batch.C2] = tmpColor.toFloatBits()
        vertices[Batch.C3] = tmpColor.toFloatBits()
        vertices[Batch.C4] = tmpColor.toFloatBits()

        batch.setBlending(BlendingMode.ADD)

        tmpVector.set(-size / 2f, -size / 2f, 0f)
        camera.project(tmpVector, -worldW/2f, -worldH/2f, worldW, worldH)
        vertices[Batch.X1] = tmpVector.x + x
        vertices[Batch.Y1] = tmpVector.y + y

        tmpVector.set(-size / 2f, size / 2f, 0f)
        camera.project(tmpVector, -worldW/2f, -worldH/2f, worldW, worldH)
        vertices[Batch.X2] = tmpVector.x + x
        vertices[Batch.Y2] = tmpVector.y + y

        tmpVector.set(size / 2f, size / 2f, 0f)
        camera.project(tmpVector, -worldW/2f, -worldH/2f, worldW, worldH)
        vertices[Batch.X3] = tmpVector.x + x
        vertices[Batch.Y3] = tmpVector.y + y

        tmpVector.set(size / 2f, -size / 2f, 0f)
        camera.project(tmpVector, -worldW/2f, -worldH/2f, worldW, worldH)
        vertices[Batch.X4] = tmpVector.x + x
        vertices[Batch.Y4] = tmpVector.y + y

        batch.draw(texture.texture, vertices, 0, 20)
        batch.setBlending(BlendingMode.ALPHA)
    }
}