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
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonWriter
import com.badlogic.gdx.utils.ObjectMap
import com.hhs.koto.app.Config
import com.hhs.koto.app.KotoApp
import com.hhs.koto.app.Options
import ktx.collections.GdxArray
import ktx.collections.removeAll
import ktx.json.fromJson

lateinit var global: ObjectMap<String, Any>

val json = Json().apply {
    setUsePrototypes(false)
    setOutputType(JsonWriter.OutputType.json)
}
lateinit var options: Options

lateinit var koto: KotoApp

fun safeDeltaTime() = clamp(Gdx.graphics.deltaTime, 0f, 0.1f);

fun exitApp() {
    koto.restartCallback(false)
    Gdx.app.exit()
}

fun restartApp() {
    koto.restartCallback(true)
    Gdx.app.exit()
}

fun initAll() {
    global = ObjectMap<String, Any>()
    initA()
    BGM.init()
    SE.init()
}

fun loadOptions() {
    val file = Gdx.files.external(Config.optionsPath)
    if (file.exists()) {
        Gdx.app.log("Main", "Reading options from file")
        options = json.fromJson(file)
    } else {
        options = Options()
        Gdx.files.external(Config.optionsPath).parent().mkdirs()
        Gdx.app.log("Main", "Creating options file")
        json.toJson(options, file)
    }
}

fun matchKey(keycode: Int, key: GdxArray<Int>): Boolean {
    if (koto.blocker.isBlocking) return false
    return keycode in key
}

fun checkKey(key: GdxArray<Int>): Boolean {
    if (koto.blocker.isBlocking) return false
    for (i in key.safeIterator()) {
        if (Gdx.input.isKeyPressed(i)) return true
    }
    return false
}

fun <Type> GdxArray<Type>.safeIterator() = Array.ArrayIterator(this)