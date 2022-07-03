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

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Interpolation
import com.hhs.koto.stg.Drawable
import com.hhs.koto.util.*
import ktx.math.vec2

/**
 * aka. Timer Circle
 *
 * Change size according to boss timer
 */
class SpellAttackCircle(
    val boss: Boss,
    val maxSize: Float = 128f,
    val setupTime: Int = 60,
    override val zIndex: Int = 800,
) : Drawable {
    override val blending: BlendingMode
        get() = BlendingMode.ADD
    override var x = 0f
    override var y = 0f
    override var alive = true

    var visible = false
    var size = 0f
    var rotation = 0f

    /**
     * Animation State. 0 = expanding 1 = normal 2 = shrinking
     */
    var state = 0

    var tmpSize = -1f

    /**
     * Frame counter
     */
    var t = 0

    var maxTime = 1
    var nowTime = 1

    val texture = getRegion("particle/spell_attack_circle.png")
    private val vertexCount = 32
    private val vertices = FloatArray((vertexCount + 1) * 10)

    // copied from SpriteBatch
    val mesh: Mesh = Mesh(
        false, (vertexCount + 1) * 2, vertexCount * 6,
        VertexAttribute(
            VertexAttributes.Usage.Position, 2,
            ShaderProgram.POSITION_ATTRIBUTE,
        ),
        VertexAttribute(
            VertexAttributes.Usage.ColorPacked, 4,
            ShaderProgram.COLOR_ATTRIBUTE,
        ),
        VertexAttribute(
            VertexAttributes.Usage.TextureCoordinates, 2,
            ShaderProgram.TEXCOORD_ATTRIBUTE + "0",
        )
    )

    init {
        val indices = ShortArray(vertexCount * 6)
        for (i in 0 until vertexCount) {
            indices[i * 6] = (i * 2).toShort()
            indices[i * 6 + 1] = (i * 2 + 1).toShort()
            indices[i * 6 + 2] = (i * 2 + 2).toShort()
            indices[i * 6 + 3] = (i * 2 + 1).toShort()
            indices[i * 6 + 4] = (i * 2 + 2).toShort()
            indices[i * 6 + 5] = (i * 2 + 3).toShort()
        }
        mesh.setIndices(indices)
        val color = Color(0f, 0f, 1f, 0.5f).toFloatBits()
        for (i in 0 until vertexCount + 1) {
            val u = texture.u + (texture.u2 - texture.u) * i.toFloat() / vertexCount
            vertices[i * 10 + 2] = color
            vertices[i * 10 + 3] = u
            vertices[i * 10 + 4] = texture.v2
            vertices[i * 10 + 7] = color
            vertices[i * 10 + 8] = u
            vertices[i * 10 + 9] = texture.v
        }
    }

    fun reset(maxTime: Int) {
        state = 0
        x = boss.x
        y = boss.y
        tmpSize = -1f
        t = 0
        visible = true
        this.maxTime = maxTime
    }

    private fun getTimePercentage() = nowTime.toFloat() / maxTime

    fun end() {
        state = 2 //mark end
        t = 0
        tmpSize = size
    }

    override fun tick() {
        if (!boss.alive) {
            alive = false
            return
        }

        val a = vec2(boss.x - x, boss.y - y).limit(3f)
        x += a.x
        y += a.y
        t++
        rotation = (rotation + 20f) % 360f

        when (state) {
            0 -> {
                size = if (t <= setupTime / 2) {
                    Interpolation.pow5Out.apply(
                        0f,
                        (1 - getTimePercentage()) * maxSize * 2,
                        t.toFloat() / setupTime,
                    )
                } else {
                    Interpolation.pow5In.apply(
                        (1 - getTimePercentage()) * maxSize * 2,
                        (1 - getTimePercentage()) * maxSize,
                        t.toFloat() / setupTime,
                    )
                }
                if (t == setupTime) {
                    state = 1
                    t = 0
                }
            }
            1 -> {
                size = lerp(maxSize, 0f, getTimePercentage())
            }
            2 -> {
                size = Interpolation.pow5.apply(tmpSize, 0f, t.toFloat() / setupTime)
                if (t == setupTime) {
                    visible = false
                }
            }
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (!visible) return

        batch.flush()
        texture.texture.bind()
        for (i in 0 until vertexCount + 1) {
            val angle = 360f * i / vertexCount + rotation
            vertices[i * 10] = x + (size + 20f) * cos(angle)
            vertices[i * 10 + 1] = y + (size + 20f) * sin(angle)
            vertices[i * 10 + 5] = x + size * cos(angle)
            vertices[i * 10 + 6] = y + size * sin(angle)
        }
        mesh.setVertices(vertices)
        batch.setBlending(BlendingMode.ADD, immediately = true)
        mesh.render(batch.shader, GL20.GL_TRIANGLES)
    }
}