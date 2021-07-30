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

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.crashinvaders.vfx.VfxManager
import com.hhs.koto.app.Config
import com.hhs.koto.app.ui.VfxOutput
import com.hhs.koto.stg.task.ParallelTask
import com.hhs.koto.util.keyPressed
import com.hhs.koto.util.options
import ktx.app.clearScreen
import java.lang.Float.min

class KotoGame(private val appViewport: Viewport) : Disposable {
    val vfx = VfxManager(Pixmap.Format.RGBA8888, Config.fw, Config.fh)
    val postVfx = VfxManager(Pixmap.Format.RGBA8888, Config.fw, Config.fh)
    val tasks = ParallelTask()
    val cam = OrthographicCamera().apply {
        position.x = Config.w / 2f - Config.originX
        position.y = Config.h / 2f - Config.originY
        zoom = min(Config.w / Config.fw, Config.h / Config.fh)
    }
    val st = Stage().apply {
        viewport.worldWidth = vfx.width.toFloat()
        viewport.worldHeight = vfx.height.toFloat()
        viewport.camera = cam
        viewport.update(vfx.width, vfx.height)
    }
    val hud = Stage(
        ScalingViewport(
            Scaling.stretch, postVfx.width.toFloat(), postVfx.height.toFloat(), OrthographicCamera()
        ),
        SpriteBatch()
    ).apply {
        addActor(VfxOutput(vfx))
    }
    var frame: Int = 0
    var speedUpMultiplier: Int = 1
    val frameScheduler = FrameScheduler(this)

    fun update() {
        speedUpMultiplier = if (keyPressed(options.keySpeedUp)) {
            options.speedUpMultiplier
        } else {
            1
        }
        frameScheduler.update()
    }

    fun draw() {
        st.viewport.apply()

        vfx.beginInputCapture()
        clearScreen(0f, 0f, 0f, 1f)
        st.draw()
        vfx.endInputCapture()
        vfx.applyEffects()

        postVfx.beginInputCapture()
        clearScreen(0f, 0f, 0f, 1f)
        hud.draw()
        postVfx.endInputCapture()
        postVfx.applyEffects()

        appViewport.apply()
    }

    override fun dispose() {
        vfx.dispose()
        postVfx.dispose()
    }
}