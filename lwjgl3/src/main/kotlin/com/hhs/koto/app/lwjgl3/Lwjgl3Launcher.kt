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

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle
import com.badlogic.gdx.files.FileHandle
import com.hhs.koto.app.Config
import com.hhs.koto.app.KotoApp
import com.hhs.koto.app.KotoCallbacks
import com.hhs.koto.app.Options
import com.hhs.koto.util.json
import ktx.json.fromJson
import java.util.*

object Lwjgl3Launcher {

    @JvmStatic
    fun main(args: Array<String>) {
        val optionsFile = getOptionsFile()
        var options = readOptions(optionsFile)
        var restart0: Boolean = false

        val callbacks = object : KotoCallbacks {
            override fun restartCallback(restart: Boolean) {
                restart0 = restart
            }

            override fun getOptions(): Options = options

            override fun saveOptions(options: Options) {
                Gdx.app.log("Main", "Writing options to file")
                if (!optionsFile.exists()) {
                    optionsFile.parent().mkdirs()
                }
                json.toJson(options, optionsFile)
            }
        }

        val configuration = Lwjgl3ApplicationConfiguration()
        configuration.setResizable(Config.allowResize)
        configuration.setTitle(Config.windowTitle)
        configuration.setWindowIcon(
            "icon/koto-icon_16x.png",
            "icon/koto-icon_32x.png",
            "icon/koto-icon_48x.png",
            "icon/koto-icon_128x.png",
        )
        configuration.setWindowedMode(options.startupWindowWidth, options.startupWindowHeight)
        configuration.useVsync(options.vsyncEnabled)
        configuration.setForegroundFPS(options.fpsLimit)

        Lwjgl3Application(KotoApp(callbacks), configuration)
        while (restart0) {
            restart0 = false
            options = readOptions(optionsFile)
            configuration.setWindowedMode(options.startupWindowWidth, options.startupWindowHeight)
            configuration.useVsync(options.vsyncEnabled)
            configuration.setForegroundFPS(options.fpsLimit)
            Lwjgl3Application(KotoApp(callbacks), configuration)
        }
    }

    private fun getOptionsFile(): FileHandle {
        val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
        return when {
            "windows" in osName -> {
                // Windows
                Lwjgl3FileHandle("AppData/Roaming/koto/options.json", Files.FileType.External)
            }
            "linux" in osName -> {
                // Linux
                Lwjgl3FileHandle(".koto/options.json", Files.FileType.External)
            }
            "mac os x" in osName -> {
                // MacOS
                // TODO is this appropriate?
                Lwjgl3FileHandle("Library/Application Support/koto/options.json", Files.FileType.External)
            }
            else -> {
                // what??
                Lwjgl3FileHandle(".koto/options.json", Files.FileType.External)
            }
        }
    }

    private fun readOptions(optionsFile: FileHandle) = if (optionsFile.exists()) {
        println("Reading options from file...")
        json.fromJson(optionsFile)
    } else {
        val options = Options()
        optionsFile.parent().mkdirs()
        println("Creating default options file...")
        json.toJson(options, optionsFile)
        options
    }
}