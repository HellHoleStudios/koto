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


data class Config(
    var logLevel: Int = Logger.DEBUG,
    var autoload: Boolean = false,
    var fpsMultiplier: Int = 0,
    var fpsLimit: Int = 60,
    var vsyncEnabled: Boolean = false,
    var windowTitle: String = "Koto",
    var screenWidth: Float = 1280f,
    var screenHeight: Float = 960f,
    var startupWindowWidth: Int = 1280,
    var startupWindowHeight: Int = 960,
    var frameWidth: Int = 768,
    var frameHeight: Int = 896,
    var frameOffsetX: Float = 64f,
    var frameOffsetY: Float = 32f,
    var allowFullScreen: Boolean = true,
    var allowResize: Boolean = true,
    var startupFullScreen: Boolean = false,
    var windowScaling: Scaling = Scaling.fit,
    var textureMinFilter: TextureFilter = TextureFilter.Linear,
    var textureMagFilter: TextureFilter = TextureFilter.Linear,

    var w: Float = 384f,
    var h: Float = 448f,
    var originX: Float = w / 2,
    var originY: Float = h,
    var deleteDistance: Float = 1024f,
    var safeDistance: Float = 16f,
    var orthoCircleCollision: Boolean = true,
    var invulnerable: Boolean = false,
    var cleanupBulletCount: Int = 0,
    var cleanupBlankCount: Int = 0,
    var defaultShotSheet: String = "default_shot.shot",
    var allowSpeedUpOutOfReplay: Boolean = true,
    var speedUpMultiplier: Int = 0,
    var musicVolume: Float = 0f,
    var SEVolume: Float = 0f,

    var keyDown: List<Int> = listOf(Keys.DOWN),
    var keyUp: List<Int> = listOf(Keys.UP),
    var keyLeft: List<Int> = listOf(Keys.LEFT),
    var keyRight: List<Int> = listOf(Keys.RIGHT),
    var keySelect: List<Int> = listOf(Keys.Z, Keys.ENTER),
    var keyCancel: List<Int> = listOf(Keys.X, Keys.ESCAPE),
    var keyShot: List<Int> = listOf(Keys.Z),
    var keySlow: List<Int> = listOf(Keys.SHIFT_LEFT),
    var keyBomb: List<Int> = listOf(Keys.X),
    var keyPause: List<Int> = listOf(Keys.ESCAPE),
    var keyCustom: List<Int> = listOf(Keys.C),

    var UIFont: String = "font/SongSC.ttf",
    var UIFontColor: Color = Color.WHITE,
    var UIFontBorderWidth: Float = 4f,
    var UIFontBorderColor: Color = Color.BLACK,
    var debugActorLayout: Boolean = false
)