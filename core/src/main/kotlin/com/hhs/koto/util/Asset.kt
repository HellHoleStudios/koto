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
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.hhs.koto.app.Config
import com.hhs.koto.stg.shot.ShotSheet
import com.hhs.koto.stg.shot.ShotSheetLoader
import ktx.assets.load
import ktx.collections.GdxArray
import ktx.collections.GdxMap
import ktx.collections.contains
import ktx.freetype.registerFreeTypeFontLoaders
import ktx.json.fromJson

lateinit var A: AssetManager
private lateinit var textureReflect: GdxMap<Texture, String>
private lateinit var fontCache: GdxMap<FreeTypeFontParameterWrapper, BitmapFont>
private var charset: String = ""

fun initA() {
    textureReflect = GdxMap()
    fontCache = GdxMap()
    A = AssetManager()
    A.setLoader(ShotSheet::class.java, ShotSheetLoader(A.fileHandleResolver))
    A.registerFreeTypeFontLoaders()
    A.logger.level = Config.logLevel
    for (i in json.fromJson<GdxArray<String>>(Gdx.files.internal(".charset.json")).safeIterator()) {
        charset += i
    }
}

fun getTexture(fileName: String): Texture {
    return A[fileName]
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
    borderColor: Color? = null,
    borderOutside: Boolean = true
): BitmapFont {
    val parameter = FreeTypeFontParameter()
    parameter.size = size
    parameter.color = color
    if (borderWidth != 0f && borderColor != null) {
        parameter.borderWidth = borderWidth
        parameter.borderColor = borderColor
        if (borderOutside) {
            parameter.spaceX = -borderWidth.toInt()
            parameter.spaceY = -borderWidth.toInt()
        }
    }
    parameter.characters = charset
    parameter.minFilter = Config.textureMinFilter
    parameter.magFilter = Config.textureMagFilter
    parameter.genMipMaps = Config.genMipMaps
    val key = FreeTypeFontParameterWrapper(parameter)
    if (key in fontCache) {
        return fontCache[key]
    }
    A.logger.debug("Font not found in cache. Generating: $name")
    val generator: FreeTypeFontGenerator = A[name]
    val font = generator.generateFont(parameter)
    fontCache.put(key, font)
    return font
}

private class FreeTypeFontParameterWrapper(parameter0: FreeTypeFontParameter) {
    val parameter = FreeTypeFontParameter()

    init {
        parameter.size = parameter0.size
        parameter.mono = parameter0.mono
        parameter.hinting = parameter0.hinting
        parameter.color = parameter0.color
        parameter.gamma = parameter0.gamma
        parameter.renderCount = parameter0.renderCount
        parameter.borderWidth = parameter0.borderWidth
        parameter.borderColor = parameter0.borderColor
        parameter.borderStraight = parameter0.borderStraight
        parameter.borderGamma = parameter0.borderGamma
        parameter.shadowOffsetX = parameter0.shadowOffsetX
        parameter.shadowOffsetY = parameter0.shadowOffsetY
        parameter.shadowColor = parameter0.shadowColor
        parameter.spaceX = parameter0.spaceX
        parameter.spaceY = parameter0.spaceY
        parameter.padTop = parameter0.padTop
        parameter.padLeft = parameter0.padLeft
        parameter.padBottom = parameter0.padBottom
        parameter.padRight = parameter0.padRight
        parameter.characters = parameter0.characters
        parameter.kerning = parameter0.kerning
        parameter.packer = parameter0.packer
        parameter.flip = parameter0.flip
        parameter.genMipMaps = parameter0.genMipMaps
        parameter.minFilter = parameter0.minFilter
        parameter.magFilter = parameter0.magFilter
        parameter.incremental = parameter0.incremental
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FreeTypeFontParameterWrapper

        if (parameter.size != other.parameter.size) return false
        if (parameter.mono != other.parameter.mono) return false
        if (parameter.hinting != other.parameter.hinting) return false
        if (parameter.color != other.parameter.color) return false
        if (parameter.gamma != other.parameter.gamma) return false
        if (parameter.renderCount != other.parameter.renderCount) return false
        if (parameter.borderWidth != other.parameter.borderWidth) return false
        if (parameter.borderColor != other.parameter.borderColor) return false
        if (parameter.borderStraight != other.parameter.borderStraight) return false
        if (parameter.borderGamma != other.parameter.borderGamma) return false
        if (parameter.shadowOffsetX != other.parameter.shadowOffsetX) return false
        if (parameter.shadowOffsetY != other.parameter.shadowOffsetY) return false
        if (parameter.shadowColor != other.parameter.shadowColor) return false
        if (parameter.spaceX != other.parameter.spaceX) return false
        if (parameter.spaceY != other.parameter.spaceY) return false
        if (parameter.padTop != other.parameter.padTop) return false
        if (parameter.padLeft != other.parameter.padLeft) return false
        if (parameter.padBottom != other.parameter.padBottom) return false
        if (parameter.padRight != other.parameter.padRight) return false
        if (parameter.characters != other.parameter.characters) return false
        if (parameter.kerning != other.parameter.kerning) return false
        if (parameter.packer != other.parameter.packer) return false
        if (parameter.flip != other.parameter.flip) return false
        if (parameter.genMipMaps != other.parameter.genMipMaps) return false
        if (parameter.minFilter != other.parameter.minFilter) return false
        if (parameter.magFilter != other.parameter.magFilter) return false
        if (parameter.incremental != other.parameter.incremental) return false

        return true
    }

    override fun hashCode(): Int {
        var result = parameter.size
        result = 31 * result + parameter.mono.hashCode()
        result = 31 * result + parameter.hinting.hashCode()
        result = 31 * result + (parameter.color?.hashCode() ?: 0)
        result = 31 * result + parameter.gamma.hashCode()
        result = 31 * result + parameter.renderCount
        result = 31 * result + parameter.borderWidth.hashCode()
        result = 31 * result + (parameter.borderColor?.hashCode() ?: 0)
        result = 31 * result + parameter.borderStraight.hashCode()
        result = 31 * result + parameter.borderGamma.hashCode()
        result = 31 * result + parameter.shadowOffsetX
        result = 31 * result + parameter.shadowOffsetY
        result = 31 * result + parameter.shadowColor.hashCode()
        result = 31 * result + parameter.spaceX
        result = 31 * result + parameter.spaceY
        result = 31 * result + parameter.padTop
        result = 31 * result + parameter.padLeft
        result = 31 * result + parameter.padBottom
        result = 31 * result + parameter.padRight
        result = 31 * result + parameter.characters.hashCode()
        result = 31 * result + parameter.kerning.hashCode()
        result = 31 * result + (parameter.packer?.hashCode() ?: 0)
        result = 31 * result + parameter.flip.hashCode()
        result = 31 * result + parameter.genMipMaps.hashCode()
        result = 31 * result + parameter.minFilter.hashCode()
        result = 31 * result + parameter.magFilter.hashCode()
        result = 31 * result + parameter.incremental.hashCode()
        return result
    }
}

fun getUILabelStyle(fontSize: Int): LabelStyle {
    return LabelStyle(
        getFont(
            Config.UIFont, fontSize, Config.UIFontColor,
            Config.UIFontBorderWidth, Config.UIFontBorderColor
        ), Color.WHITE
    )
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
        putTextureReflect(assetManager[fileName], fileName)
        original?.finishedLoading(assetManager, fileName, type)
    }
}

fun defaultTextureParameter() = TextureLoader.TextureParameter().apply {
    minFilter = Config.textureMinFilter
    magFilter = Config.textureMagFilter
    genMipMaps = Config.genMipMaps
    loadedCallback = TextureReflectCallback()
}

fun defaultBitmapFontParameter() = BitmapFontLoader.BitmapFontParameter().apply {
    minFilter = Config.textureMinFilter
    magFilter = Config.textureMagFilter
    genMipMaps = Config.genMipMaps
}