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
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.crashinvaders.vfx.VfxRenderContext
import com.crashinvaders.vfx.effects.ChainVfxEffect
import com.crashinvaders.vfx.effects.ShaderVfxEffect
import com.crashinvaders.vfx.framebuffer.VfxPingPongWrapper
import com.hhs.koto.app.Config.worldH
import com.hhs.koto.app.Config.worldOriginX
import com.hhs.koto.app.Config.worldOriginY
import com.hhs.koto.app.Config.worldW
import com.hhs.koto.util.A

class BossDistortionEffect : ShaderVfxEffect(
    ShaderProgram(
        Gdx.files.classpath("gdxvfx/shaders/screenspace.vert").readString(),
        A.get("shader/boss_distortion.frag"),
    )
), ChainVfxEffect {
    var bossPositionX: Float = 0f
    var bossPositionY: Float = 0f
    var radius: Float = 50f
    var time: Float = 0f
    var enabled: Boolean = false

    init {
        rebind()
    }

    fun start() {
        time = 0f
        enabled = true
    }

    fun end() {
        enabled = false
        rebind()
    }

    fun tick(bossX: Float, bossY: Float, radius: Float = 80f) {
        bossPositionX = bossX
        bossPositionY = bossY
        this.radius = radius
        program.bind()
        program.setUniformf(
            "u_bossPosition",
            bossPositionX + worldOriginX,
            bossPositionY + worldOriginY,
        )
        program.setUniformf("u_radius", radius)
    }

    override fun update(delta: Float) {
        if (enabled) {
            time += delta
            program.bind()
            program.setUniformf("u_time", time)
        }
    }

    override fun rebind() {
        program.bind()
        program.setUniformi("u_texture", TEXTURE_HANDLE0)
        program.setUniformf("u_screenSize", worldW, worldH)
        program.setUniformf("u_bossPosition", 2048f, 2048f)
        program.setUniformf("u_radius", 50f)
        program.setUniformf("u_time", 0f)
    }

    override fun render(context: VfxRenderContext, buffers: VfxPingPongWrapper) {
        if (enabled) {
            buffers.srcBuffer.texture.bind(TEXTURE_HANDLE0)
            renderShader(context, buffers.dstBuffer)
        }
    }
}