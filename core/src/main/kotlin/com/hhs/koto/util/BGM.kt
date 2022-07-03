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

package com.hhs.koto.util

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.utils.Logger
import com.hhs.koto.app.Config
import ktx.collections.GdxMap

object BGM {
    private var bgm: LoopingMusic? = null
    private val bgms = GdxMap<String, LoopingMusic>()
    val logger = Logger("BGM", Config.logLevel)

    fun play(id: Int?, forceRestart: Boolean = false) {
        if (id == null) {
            stop()
        } else {
            gameData.musicUnlocked[id] = true
            saveGameData()
            play(bundle["music.$id.file"], forceRestart)
        }
    }

    fun play(name: String?, forceRestart: Boolean = false) {
        if (name == null) {
            stop()
            return
        }
        if (bgm != null) {
            if (bgm!!.name == name && !forceRestart) {
                logger.debug("Same BGM as before. No changing.")
                return
            }
            stop()
        }
        logger.debug("Playing \"$name\".")
        bgm = bgms[name]
        if (bgm == null) {
            logger.error("BGM with this name doesn't exist!")
        } else {
            bgm!!.play()
        }
    }

    fun register(music: LoopingMusic): LoopingMusic {
        bgms.put(music.name, music)
        return music
    }

    fun update() = bgm?.update()

    fun stop() {
        if (bgm != null) {
            logger.debug("Stopping \"${bgm!!.name}\".")
            bgm!!.stop()
            bgm = null
        }
    }

    fun dispose() {
        if (bgm != null) {
            bgm!!.stop()
        }
        bgms.forEach {
            it.value.dispose()
        }
    }

    fun pause() = bgm?.pause()

    fun resume() = bgm?.resume()

    fun setVolume(volume: Float) = bgm?.setVolume(volume)
}

class LoopingMusic(val name: String) {
    private var music: Music = A[name]
    private var isPlaying = false
    private var isLooping = false
    var loopStart = 0f
    var loopEnd = 0f

    init {
        music.setOnCompletionListener { music ->
            if (isLooping) {
                music.volume = options.musicVolume
                music.play()
                music.position = loopStart
            }
        }
    }

    constructor(name: String, loopStart: Float, loopEnd: Float) : this(name) {
        isLooping = true
        isPlaying = false
        this.loopStart = loopStart
        this.loopEnd = loopEnd
    }

    fun stop() {
        isPlaying = false
        music.stop()
    }

    fun dispose() {
        BGM.logger.debug("Disposing music file \"$name\".")
        music.dispose()
    }

    fun play() {
        isPlaying = true
        music.isLooping = false
        music.volume = options.musicVolume
        music.play()
    }

    fun pause() {
        isPlaying = false
        music.pause()
    }

    fun resume() {
        isPlaying = true
        music.volume = options.musicVolume
        music.play()
    }

    fun update() {
        if (isPlaying && isLooping) {
            if (!music.isPlaying) {
                music.position = loopStart
                music.volume = options.musicVolume
                music.play()
            } else if (music.position >= loopEnd) {
                music.position = loopStart + (music.position - loopEnd)
            }
        }
    }

    fun setVolume(volume: Float) {
        music.volume = volume
    }
}