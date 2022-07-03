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

package com.hhs.koto.stg.bullet

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.hhs.koto.stg.AABBCollision
import com.hhs.koto.stg.CircleCollision
import com.hhs.koto.stg.CollisionShape
import com.hhs.koto.stg.NoCollision
import com.hhs.koto.util.BlendingMode
import com.hhs.koto.util.KotoRuntimeException
import com.hhs.koto.util.json
import com.hhs.koto.util.toHSVColor
import ktx.collections.GdxMap
import ktx.json.fromJson

class ShotSheet(val atlas: TextureAtlas, raw: ShotSheetLoader.RawShotSheet) {
    var ids = GdxMap<Int, BulletData>()
    var names = GdxMap<String, BulletData>()

    init {
        raw.data.forEach {
            val tmp = BulletData.fromShotSheet(this, it)
            if (tmp.id != null) ids.put(tmp.id, tmp)
            if (tmp.name != null) names.put(tmp.name, tmp)
        }
    }

    constructor(internalSheetFile: String?) : this(Gdx.files.internal(internalSheetFile))
    constructor(sheetFile: FileHandle) : this(sheetFile, json.fromJson<ShotSheetLoader.RawShotSheet>(sheetFile))
    constructor(sheetFile: FileHandle, raw: ShotSheetLoader.RawShotSheet) : this(
        TextureAtlas(sheetFile.parent().child(raw.atlas!!)),
        raw,
    )

    operator fun get(id: Int): BulletData {
        return ids[id] ?: throw KotoRuntimeException("bullet with id $id not found!")
    }

    operator fun get(name: String): BulletData {
        return names[name] ?: throw KotoRuntimeException("bullet with name \"$name\" not found!")
    }
}

data class BulletData(
    var id: Int?,
    var name: String?,
    var color: Color,
    var blending: BlendingMode,
    var texture: BulletTexture,
    var originX: Float,
    var originY: Float,
    var width: Float,
    var height: Float,
    var delayTexture: TextureRegion,
    var delayColor: Color,
    var delayBlending: BlendingMode,
    var spinVelocity: Float,
    var collision: CollisionShape,
    var rotation: Float,
) {
    companion object {
        fun fromShotSheet(parent: ShotSheet, raw: ShotSheetLoader.RawShotSheet.RawBulletData): BulletData {
            val result = BulletData(
                raw.id,
                raw.name,
                Color.valueOf(raw.color ?: "ff0000").toHSVColor(),
                BlendingMode.forName(raw.blending),
                BulletTexture(parent.atlas, raw.region ?: raw.name!!, raw.frames),
                0f,
                0f,
                0f,
                0f,
                parent.atlas.findRegion(raw.delayRegion!!),
                Color.valueOf(raw.delayColor ?: "ff0000").toHSVColor(),
                BlendingMode.forName(raw.delayBlending ?: "ADD"),
                raw.spinVelocity,
                when (raw.collisionMethod) {
                    "none" -> NoCollision()
                    "circle" -> CircleCollision(raw.collisionData!![0])
                    "rectangle" -> AABBCollision(raw.collisionData!![0], raw.collisionData[1])
                    else -> CircleCollision(raw.collisionData!![0]) // use circle as default
                },
                raw.rotation,
            )
            result.originX = raw.originX ?: (result.texture.maxWidth / 2f)
            result.originY = raw.originY ?: (result.texture.maxHeight / 2f)
            result.width = raw.width ?: result.texture.maxWidth.toFloat()
            result.height = raw.height ?: result.texture.maxHeight.toFloat()
            return result
        }
    }
}