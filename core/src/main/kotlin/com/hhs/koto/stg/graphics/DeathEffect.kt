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
import com.hhs.koto.app.Config.worldW
import com.hhs.koto.util.A

class DeathEffect : ShaderVfxEffect(
    ShaderProgram(
        Gdx.files.classpath("gdxvfx/shaders/screenspace.vert").readString(),
        A.get("shader/death.frag"),
    )
), ChainVfxEffect {
    var playerPositionX: Float = 0f
    var playerPositionY: Float = 0f
    var time: Float = 0f
    var enabled: Boolean = false

    init {
        rebind()
    }

    fun start(playerX: Float, playerY: Float) {
        playerPositionX = playerX
        playerPositionY = playerY
        time = 0f
        enabled = true
        program.bind()
        program.setUniformf(
            "u_playerPosition",
            playerPositionX + worldW / 2,
            playerPositionY + worldH / 2,
        )
    }

    fun end() {
        enabled = false
        rebind()
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
        program.setUniformf("u_playerPosition", 2048f, 2048f)
        program.setUniformf("u_time", 0f)
    }

    override fun render(context: VfxRenderContext, buffers: VfxPingPongWrapper) {
        if (enabled) {
            buffers.srcBuffer.texture.bind(TEXTURE_HANDLE0)
            renderShader(context, buffers.dstBuffer)
        }
    }
}