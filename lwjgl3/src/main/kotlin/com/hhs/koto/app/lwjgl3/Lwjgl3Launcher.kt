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

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.hhs.koto.app.Config
import com.hhs.koto.app.KotoApp
import com.hhs.koto.app.Options
import com.hhs.koto.util.getTrueFPSMultiplier

object Lwjgl3Launcher {

    @JvmStatic
    lateinit var lwjgl: Lwjgl3Application

    @JvmStatic
    fun main(args: Array<String>) {
        val lwjgl3FileSystem = Lwjgl3FileSystem()
        val lwjgl3GraphicsSystem = Lwjgl3GraphicsSystem()
        val options = lwjgl3FileSystem.loadOptions()
        Lwjgl3Application(KotoApp(lwjgl3FileSystem, lwjgl3GraphicsSystem), getConfiguration(options))
    }

    private fun getConfiguration(options: Options): Lwjgl3ApplicationConfiguration {
        val configuration = Lwjgl3ApplicationConfiguration()
        configuration.setResizable(Config.allowResize)
        configuration.setTitle(Config.windowTitle)
        configuration.setWindowIcon(
            "icon/koto-icon_16x.png",
            "icon/koto-icon_32x.png",
            "icon/koto-icon_48x.png",
            "icon/koto-icon_128x.png",
        )
        if (options.displayMode != "fullscreen" && options.displayMode != "borderless") {
            options.displayMode = "windowed"
        }
        configuration.setWindowedMode(options.windowWidth,options.windowHeight) // will be overwritten after launch
        configuration.useVsync(options.vsyncEnabled)
        configuration.setForegroundFPS((options.fps * getTrueFPSMultiplier(options.fpsMultiplier)).toInt())
        return configuration
    }
}