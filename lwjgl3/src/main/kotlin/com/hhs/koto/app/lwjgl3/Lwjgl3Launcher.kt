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
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle
import com.badlogic.gdx.files.FileHandle
import com.esotericsoftware.kryo.io.ByteBufferInput
import com.esotericsoftware.kryo.io.ByteBufferOutput
import com.hhs.koto.app.Config
import com.hhs.koto.app.KotoApp
import com.hhs.koto.app.KotoCallbacks
import com.hhs.koto.app.Options
import com.hhs.koto.stg.GameData
import com.hhs.koto.stg.Replay
import com.hhs.koto.util.*
import ktx.collections.GdxArray
import ktx.json.fromJson
import java.text.SimpleDateFormat
import java.util.*

object Lwjgl3Launcher {

    @JvmStatic
    fun main(args: Array<String>) {
        val optionsFile = getFile("options.json")
        val gameDataFile = getFile("game_data.json")
        var options = readOptions(optionsFile)
        var restart0 = false

        val callbacks = object : KotoCallbacks {
            val dateFormat = SimpleDateFormat("yyyy_MM_dd")

            override fun restartCallback(restart: Boolean) {
                restart0 = restart
            }

            override fun loadOptions(): Options = options

            override fun saveOptions(options: Options) {
                app.logger.info("Writing options to file...")
                if (!optionsFile.exists()) {
                    optionsFile.parent().mkdirs()
                }
                prettyPrintJson(options, optionsFile)
            }

            override fun loadGameData(): GameData? = if (gameDataFile.exists()) {
                app.logger.info("Reading game data from file...")
                json.fromJson(gameDataFile)
            } else {
                null
            }

            override fun saveGameData(gameData: GameData) {
                app.logger.info("Writing game data to file...")
                if (!gameDataFile.exists()) {
                    gameDataFile.parent().mkdirs()
                }
                json.toJson(gameData, gameDataFile)
            }

            override fun loadReplays(): GdxArray<Replay> {
                val result = GdxArray<Replay>()
                val replayFolder = getFile("replay")
                if (!replayFolder.exists()) replayFolder.mkdirs()
                replayFolder.list().forEach {
                    if (it.extension() == "ktr") {
                        val input = ByteBufferInput(it.read())
                        result.add(kryo.readObject(input, Replay::class.java))
                        input.close()
                    }
                }
                return result
            }

            override fun saveReplay(replay: Replay) {
                val replayFolder = getFile("replay")
                var replayName = Config.replayPrefix + "_" + dateFormat.format(replay.date)
                if (replayFolder.child("$replayName.ktr").exists()) {
                    for (i in 1..1000) {
                        if (!replayFolder.child("${replayName}_$i.ktr").exists()) {
                            replayName = "${replayName}_$i"
                            break
                        }
                    }
                }
                val replayFile = replayFolder.child("$replayName.ktr")
                val output = ByteBufferOutput(replayFile.write(false))
                kryo.writeObject(output, replay)
                output.close()
            }
        }

        val configuration = Lwjgl3ApplicationConfiguration()
        configure(configuration, options)

        Lwjgl3Application(KotoApp(callbacks), configuration)
        while (restart0) {
            restart0 = false
            options = readOptions(optionsFile)
            configure(configuration, options)
            Lwjgl3Application(KotoApp(callbacks), configuration)
        }
    }

    private fun configure(configuration: Lwjgl3ApplicationConfiguration, options: Options) {
        configuration.setResizable(Config.allowResize)
        configuration.setTitle(Config.windowTitle)
        configuration.setWindowIcon(
            "icon/koto-icon_16x.png",
            "icon/koto-icon_32x.png",
            "icon/koto-icon_48x.png",
            "icon/koto-icon_128x.png",
        )
        if (options.fullScreen) {
            configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode())
        } else {
            configuration.setWindowedMode(options.windowWidth, options.windowHeight)
        }
        configuration.useVsync(options.vsyncEnabled)
        configuration.setForegroundFPS((options.fps * getTrueFPSMultiplier(options.fpsMultiplier)).toInt())
    }

    private fun getFile(fileName: String): FileHandle {
        val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
        return when {
            "windows" in osName -> {
                // Windows
                Lwjgl3FileHandle("AppData/Roaming/koto/$fileName", Files.FileType.External)
            }
            "linux" in osName -> {
                // Linux
                Lwjgl3FileHandle(".koto/$fileName", Files.FileType.External)
            }
            "mac os x" in osName -> {
                // MacOS
                // TODO is this appropriate?
                Lwjgl3FileHandle("Library/Application Support/koto/$fileName", Files.FileType.External)
            }
            else -> {
                // what??
                Lwjgl3FileHandle(".koto/$fileName", Files.FileType.External)
            }
        }
    }

    private fun readOptions(optionsFile: FileHandle): Options = if (optionsFile.exists()) {
        println("[Main] Reading options from file...")
        json.fromJson(optionsFile)
    } else {
        val options = Options()
        ResolutionMode.findOptimal(Lwjgl3ApplicationConfiguration.getDisplayMode()).apply(options)
        optionsFile.parent().mkdirs()
        println("[Main] Creating default options file...")
        prettyPrintJson(options, optionsFile)
        options
    }
}