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

package com.hhs.koto.stg.shot

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.hhs.koto.util.json
import ktx.collections.GdxArray
import ktx.json.fromJson

class ShotSheetLoader(resolver: FileHandleResolver) :
    SynchronousAssetLoader<ShotSheet, ShotSheetLoader.ShotSheetParameters>(resolver) {

    private lateinit var raw: RawShotSheet

    override fun getDependencies(
        fileName: String,
        sheetFile: FileHandle,
        parameter: ShotSheetParameters?,
    ): GdxArray<AssetDescriptor<*>> {
        val imgDir = sheetFile.parent()
        raw = json.fromJson(sheetFile)
        val dependencies = GdxArray<AssetDescriptor<*>>()
        dependencies.add(AssetDescriptor(imgDir.child(raw.atlas), TextureAtlas::class.java))
        return dependencies
    }

    override fun load(
        assetManager: AssetManager,
        fileName: String,
        file: FileHandle,
        parameter: ShotSheetParameters?,
    ) = ShotSheet(assetManager[raw.atlas, TextureAtlas::class.java], raw)

    data class RawShotSheet(val atlas: String, val data: GdxArray<RawBulletData>) {

        constructor() : this("", GdxArray())

        data class RawBulletData(
            val id: Int,
            val name: String,
            val frames: GdxArray<Int>,
            val render: String?,
            val delaySrc: String,
            val delayColor: String,
            val collisionMethod: String?,
            val collisionData: GdxArray<Float>?,
            val rotation: Float,
            val spinVelocity: Float,
            val originX: Float?,
            val originY: Float?,
        ) {
            constructor() : this(0, "", GdxArray.with(1), null, "", "", null, null, 0f, 0f, null, null)
        }
    }

    class ShotSheetParameters : AssetLoaderParameters<ShotSheet>()
}