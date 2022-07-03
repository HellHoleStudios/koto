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

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Logger
import com.hhs.koto.app.Config
import ktx.collections.GdxMap
import ktx.collections.set

object SE {
    private val sounds = GdxMap<String, Sound>()
    private val fired = GdxMap<String, Boolean>()
    private val logger = Logger("SE", Config.logLevel)

    fun update() {
        if (Config.noDuplicateSE) {
            for (i in fired.safeKeys()) {
                fired[i] = false
            }
        }
    }

    fun play(name: String, volume: Float = 1f) {
        val sound = sounds[name]
        if (sound == null) {
            logger.error("SE with this name doesn't exist!")
        } else {
            if (Config.noDuplicateSE) {
                if (fired[name]) {
                    return
                }
                fired[name] = true
            }
            val id = sound.play(volume)
            sound.setVolume(id, options.SEVolume)
        }
    }

    fun pause() {
        sounds.safeValues().forEach {
            it.pause()
        }
    }

    fun resume() {
        sounds.safeValues().forEach {
            it.resume()
        }
    }

    fun stop() {
        sounds.safeValues().forEach {
            it.stop()
        }
    }

    fun register(name: String, path: String): Sound {
        logger.debug("Registering sound with alias: $name path: $path")
        val snd: Sound = A[path]
        sounds.put(name, snd)
        fired.put(name, false)
        return snd
    }
}
