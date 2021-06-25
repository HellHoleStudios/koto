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
import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.BitmapFontLoader
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.ObjectMap
import com.hhs.koto.app.Config
import com.hhs.koto.app.ui.GridButtonBase
import com.hhs.koto.stg.shot.ShotSheet
import com.hhs.koto.stg.shot.ShotSheetLoader
import ktx.assets.load
import ktx.collections.GdxArray
import ktx.freetype.registerFreeTypeFontLoaders
import ktx.json.fromJson

lateinit var A: AssetManager
private lateinit var textureReflect: ObjectMap<Texture, String>
private lateinit var fontCache: ObjectMap<String, BitmapFont>
private var charset: String = ""

fun initA() {
    textureReflect = ObjectMap<Texture, String>()
    fontCache = ObjectMap<String, BitmapFont>()
    A = AssetManager()
    A.setLoader(ShotSheet::class.java, ShotSheetLoader(A.fileHandleResolver))
    A.registerFreeTypeFontLoaders()
    A.logger.level = Config.logLevel
    for (i in json.fromJson<GdxArray<String>>(Gdx.files.internal(".charset.json")).safeIterator()) {
        charset += i
    }
}

fun getTexture(fileName: String): Texture {
    return A.get(fileName)
}

fun getRegion(fileName: String): TextureRegion {
    return when (val tmp: Any = A[fileName]) {
        is TextureRegion -> tmp
        is Texture -> TextureRegion(tmp as Texture)
        else -> {
            A.logger.error("getRegion() requires a Texture-like asset!")
            tmp as TextureRegion
        }
    }
}

fun getFont(
    name: String,
    size: Int,
    color: Color = Color.BLACK,
    borderWidth: Float = 0f,
    borderColor: Color? = null
): BitmapFont {
    val tmp = StringBuilder(64)
    tmp.append(name).append(':').append(size).append(':').append(color).append(':')
    if (borderWidth != 0f) {
        tmp.append(borderWidth).append(':').append(borderColor.toString())
    }
    val key = tmp.toString()
    if (fontCache.containsKey(key)) {
        return fontCache.get(key)
    }
    val generator: FreeTypeFontGenerator = A[name]
    val parameter = FreeTypeFontParameter()
    parameter.size = size
    parameter.color = color
    if (borderWidth != 0f && borderColor != null) {
        parameter.borderWidth = borderWidth
        parameter.borderColor = borderColor
    }
    parameter.characters = charset
    parameter.minFilter = options.textureMinFilter
    parameter.magFilter = options.textureMagFilter
    val font = generator.generateFont(parameter)
    fontCache.put(key, font)
    return font
}

fun getUILabelStyle(fontSize: Int): LabelStyle {
    return LabelStyle(
        getFont(
            Config.UIFont, fontSize, Config.UIFontColor,
            Config.UIFontBorderWidth, Config.UIFontBorderColor
        ), Color.WHITE
    )
}

fun getButtonActivateAction(button: GridButtonBase, vararg actions: () -> Action): () -> Action {
    return {
        val ret = ParallelAction()
        ret.addAction(
            Actions.sequence(
                Actions.color(Color.WHITE),
                Actions.moveTo(button.staticX - 10, button.staticY, 1f, Interpolation.pow5Out)
            )
        )
        ret.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.color(Color(0.9f, 0.9f, 0.9f, 1f), 0.5f),
                    Actions.color(Color.WHITE, 0.5f)
                )
            )
        )
        for (action in actions) {
            ret.addAction(action())
        }
        ret
    }
}

fun getButtonDeactivateAction(button: GridButtonBase, vararg actions: () -> Action): () -> Action {
    return {
        val ret = ParallelAction()
        ret.addAction(
            Actions.alpha(1f),
        )
        ret.addAction(
            Actions.moveTo(button.staticX, button.staticY, 1f, Interpolation.pow5Out),
        )
        ret.addAction(
            Actions.color(Color(0.7f, 0.7f, 0.7f, 1f))
        )
        for (action in actions) {
            ret.addAction(action())
        }
        ret
    }
}

fun loadAssetIndex(file: FileHandle) {
    A.logger.debug("Loading asset index from file $file")
    for (i in json.fromJson<Array<String>>(file)) {
        loadSmart(i)
    }
}

fun loadSmart(fileName: String) {
    with(A) {
        when (fileHandleResolver.resolve(fileName).extension()) {
            "png", "jpg", "jpeg", "bmp", "gif" -> load<Texture>(fileName, defaultTextureParameter())
            "wav" -> if (Config.wavMusic) {
                load<Music>(fileName)
            } else {
                load<Sound>(fileName)
            }
            "mp3", "ogg" -> load<Music>(fileName)
            "atlas" -> load<TextureAtlas>(fileName)
            "fnt" -> load<BitmapFont>(fileName, defaultBitmapFontParameter())
            "ttf", "otf" -> load<FreeTypeFontGenerator>(fileName)
            "shot" -> load<ShotSheet>(fileName)
            "p" -> load<ParticleEffect>(fileName)
            else -> Unit
        }
    }
}

fun putTextureReflect(texture: Texture, fileName: String) {
    A.logger.debug("Texture reflect info: ${texture.hashCode()} <- $fileName")
    textureReflect.put(texture, fileName)
}

class TextureReflectCallback() : LoadedCallback {
    private var original: LoadedCallback? = null

    constructor(original: LoadedCallback?) : this() {
        this.original = original
    }

    override fun finishedLoading(assetManager: AssetManager, fileName: String, type: Class<*>?) {
        putTextureReflect(assetManager.get(fileName), fileName)
        original?.finishedLoading(assetManager, fileName, type)
    }
}

fun defaultTextureParameter() = TextureLoader.TextureParameter().apply {
    minFilter = options.textureMinFilter
    magFilter = options.textureMagFilter
    loadedCallback = TextureReflectCallback()
}

fun defaultBitmapFontParameter() = BitmapFontLoader.BitmapFontParameter().apply {
    minFilter = options.textureMinFilter
    magFilter = options.textureMagFilter
}