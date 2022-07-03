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

package com.hhs.koto.app.lwjgl3

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics
import com.hhs.koto.app.NativeGraphicsSystem
import org.lwjgl.glfw.GLFW

class Lwjgl3GraphicsSystem : NativeGraphicsSystem {
    override val borderlessAvailable: Boolean
        get() = true

    override fun setBorderless(): Pair<Int, Int> {
        // prevents incorrect desktop size caused by fullscreen
        if (Gdx.graphics.isFullscreen) {
            Gdx.graphics.setWindowedMode(100, 100)
        }

        val mode = Gdx.graphics.displayMode
        val window = (Gdx.graphics as Lwjgl3Graphics).window
        val monitor = Gdx.graphics.monitor
        Gdx.graphics.setWindowedMode(mode.width, mode.height)
        Gdx.graphics.setUndecorated(true)
        GLFW.glfwSetWindowPos(window.windowHandle, monitor.virtualX, monitor.virtualY)
        return mode.width to mode.height
    }
}