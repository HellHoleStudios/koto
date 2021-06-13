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

package com.hhs.koto.app

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.utils.Logger
import com.badlogic.gdx.utils.Scaling
import ktx.collections.GdxArray

data class Options(
    var fpsMultiplier: Int = 0,
    var fpsLimit: Int = 60,
    var vsyncEnabled: Boolean = false,
    var startupWindowWidth: Int = 960,
    var startupWindowHeight: Int = 720,
    var startupFullScreen: Boolean = false,
    var textureMinFilter: TextureFilter = TextureFilter.Linear,
    var textureMagFilter: TextureFilter = TextureFilter.Linear,
    var invulnerable: Boolean = false,
    var speedUpMultiplier: Int = 4,
    var musicVolume: Float = 1f,
    var SEVolume: Float = 1f,
    var keyDown: GdxArray<Int> = GdxArray.with(Keys.DOWN),
    var keyUp: GdxArray<Int> = GdxArray.with(Keys.UP),
    var keyLeft: GdxArray<Int> = GdxArray.with(Keys.LEFT),
    var keyRight: GdxArray<Int> = GdxArray.with(Keys.RIGHT),
    var keySelect: GdxArray<Int> = GdxArray.with(Keys.Z, Keys.ENTER),
    var keyCancel: GdxArray<Int> = GdxArray.with(Keys.X, Keys.ESCAPE),
    var keyShot: GdxArray<Int> = GdxArray.with(Keys.Z),
    var keySlow: GdxArray<Int> = GdxArray.with(Keys.SHIFT_LEFT),
    var keyBomb: GdxArray<Int> = GdxArray.with(Keys.X),
    var keyPause: GdxArray<Int> = GdxArray.with(Keys.ESCAPE),
    var keyCustom: GdxArray<Int> = GdxArray.with(Keys.C),
)

object Config {
    var logLevel = Logger.DEBUG
    const val optionsPath = ".config/koto/options.json"
    const val windowTitle = "Koto"
    const val screenWidth = 1280f
    const val screenHeight = 960f
    const val frameWidth = 768
    const val frameHeight = 896
    const val frameOffsetX = 64f
    const val frameOffsetY = 32f
    const val allowFullScreen = true
    const val allowResize = true
    val windowScaling: Scaling = Scaling.fit
    const val defaultBlending = "ALPHA"
    const val w = 384f
    const val h = 448f
    const val originX = w / 2
    const val originY = h
    const val deleteDistance = 1024f
    const val safeDistance = 16f
    const val orthoCircleCollision = true
    const val cleanupBulletCount = 8192
    const val cleanupBlankCount = 512
    const val defaultShotSheet = "default_shot.shot"
    const val allowSpeedUpOutOfReplay = true
    const val wavMusic = false
    const val UIFont = "font/SongSC.ttf"
    var UIFontColor = Color.WHITE
    const val UIFontBorderWidth = 4f
    var UIFontBorderColor = Color.BLACK
    const val debugActorLayout = false
}