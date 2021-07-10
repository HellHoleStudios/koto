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

package com.hhs.koto.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import com.badlogic.gdx.utils.ObjectMap
import com.hhs.koto.app.KotoApp
import com.hhs.koto.app.Options
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.GameMode
import ktx.collections.GdxArray
import ktx.collections.GdxMap
import ktx.graphics.copy

var global = GdxMap<String, Any>()

object SystemFlag {
    var redirect: String? = null
    var redirectDuration: Float? = null
    var gamemode: GameMode? = null
    var difficulty: GameDifficulty? = null
}

val json = Json().apply {
    setUsePrototypes(false)
    setOutputType(JsonWriter.OutputType.json)
}
lateinit var options: Options

lateinit var app: KotoApp

fun safeDeltaTime() = clamp(Gdx.graphics.deltaTime, 0f, 0.1f)

fun exitApp() {
    app.callbacks.restartCallback(false)
    Gdx.app.exit()
}

fun restartApp() {
    app.callbacks.restartCallback(true)
    Gdx.app.exit()
}

fun initAll() {
    initA()
    BGM.init()
    SE.init()
}

fun loadOptions() {
    options = app.callbacks.getOptions()
}

fun saveOptions() {
    app.callbacks.saveOptions(options)
}

fun <Type> GdxArray<Type>.safeIterator() = Array.ArrayIterator(this)

fun <K, V> GdxMap<K, V>.safeEntries() = ObjectMap.Entries(this)

fun <K, V> GdxMap<K, V>.safeKeys() = ObjectMap.Keys(this)

fun <K, V> GdxMap<K, V>.safeValues() = ObjectMap.Values(this)

operator fun Color.plus(other: Color): Color = this.copy().add(other)

operator fun Color.plusAssign(other: Color) {
    add(other)
}

operator fun Color.minus(other: Color): Color = this.copy().sub(other)

operator fun Color.minusAssign(other: Color) {
    sub(other)
}

operator fun Color.times(other: Color): Color = this.copy().mul(other)

operator fun Color.timesAssign(other: Color) {
    mul(other)
}
