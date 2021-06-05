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

package com.hhs.koto.app.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.hhs.koto.app.KotoApp
import com.hhs.koto.util.config

object Lwjgl3Launcher {
    @JvmStatic
    fun main(args: Array<String>) {
        createApplication().logLevel = config.logLevel
    }

    private fun createApplication(): Lwjgl3Application {
        val configuration = Lwjgl3ApplicationConfiguration()
        configuration.setWindowedMode(config.startupWindowWidth, config.startupWindowHeight)
        configuration.useVsync(config.vsyncEnabled)
        configuration.setResizable(config.allowResize)
        configuration.setTitle(config.windowTitle)
        configuration.setWindowIcon("icon/icon_16x.png", "icon/icon_32x.png", "icon/icon_48x.png", "icon/icon_128x.png")
        return Lwjgl3Application(KotoApp(), configuration)
    }
}