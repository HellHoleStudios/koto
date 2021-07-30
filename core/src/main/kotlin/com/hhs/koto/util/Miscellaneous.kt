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
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.Array
import com.hhs.koto.app.KotoApp
import com.hhs.koto.app.Options
import com.hhs.koto.stg.GameDifficulty
import com.hhs.koto.stg.GameMode
import ktx.collections.GdxArray
import ktx.collections.GdxMap
import java.util.*

var global = GdxMap<String, Any>()

object SystemFlag {
    var redirect: String? = null
    var redirectDuration: Float? = null
    var gamemode: GameMode? = null
    var difficulty: GameDifficulty? = null
    var player: String? = null
}

val json = Json().apply {
    setUsePrototypes(false)
    setOutputType(JsonWriter.OutputType.json)
    setSerializer(Locale::class.java, object : Json.Serializer<Locale> {
        override fun write(json: Json, obj: Locale, knownType: Class<*>?) {
            json.writeArrayStart()
            json.writeValue(obj.language)
            json.writeValue(obj.country)
            json.writeValue(obj.variant)
            json.writeArrayEnd()
        }

        override fun read(json: Json, jsonData: JsonValue, type: Class<*>?): Locale {
            return Locale(
                jsonData[0].asString(),
                jsonData[1].asString(),
                jsonData[2].asString(),
            )
        }
    })
}

private val prettyPrintSettings = JsonValue.PrettyPrintSettings().apply {
    outputType = JsonWriter.OutputType.json
    singleLineColumns = 80
    wrapNumericArrays = false
}

fun prettyPrintJson(obj: Any): String {
    return json.prettyPrint(obj, prettyPrintSettings)
}

fun prettyPrintJson(file: FileHandle, obj: Any) {
    file.writeString(prettyPrintJson(obj), false)
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

fun loadOptions() {
    options = app.callbacks.getOptions()
}

fun saveOptions() {
    app.callbacks.saveOptions(options)
}

fun <T> GdxArray<T>.removeNull() {
    var j = 0
    for (i in 0 until size) {
        if (this[i] != null) {
            this[j] = this[i]
            j++
        }
    }
    truncate(j)
}

fun <Type> GdxArray<Type>.safeIterator() = Array.ArrayIterator(this)

fun <K, V> GdxMap<K, V>.safeEntries() = ObjectMap.Entries(this)

fun <K, V> GdxMap<K, V>.safeKeys() = ObjectMap.Keys(this)

fun <K, V> GdxMap<K, V>.safeValues() = ObjectMap.Values(this)

fun getTrueFPSMultiplier(fpsMultiplier: Int): Float {
    if (fpsMultiplier == 0) return 1f
    if (fpsMultiplier < 0) return 1f / -fpsMultiplier
    return fpsMultiplier.toFloat()
}

fun <T> tri(condition: Boolean, a: T, b: T) = if (condition) {
    a
} else {
    b
}