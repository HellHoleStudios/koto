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

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.hhs.koto.stg.AABBCollision
import com.hhs.koto.stg.CircleCollision
import com.hhs.koto.stg.CollisionShape
import com.hhs.koto.stg.NoCollision
import com.hhs.koto.util.app
import com.hhs.koto.util.json
import com.hhs.koto.util.safeIterator
import ktx.collections.GdxMap
import ktx.json.fromJson

class ShotSheet(val atlas: TextureAtlas, raw: ShotSheetLoader.RawShotSheet) {
    var data = GdxMap<Int, BulletData>()
    private var nameToId = GdxMap<String, Int>()

    init {
        for (i in raw.data.safeIterator()) {
            val tmp = BulletData(this, i)
            data.put(tmp.id, tmp)
            nameToId.put(tmp.name, tmp.id)
        }
    }

    constructor(internalSheetFile: String?) : this(Gdx.files.internal(internalSheetFile))
    constructor(sheetFile: FileHandle) : this(sheetFile, json.fromJson<ShotSheetLoader.RawShotSheet>(sheetFile))
    constructor(sheetFile: FileHandle, raw: ShotSheetLoader.RawShotSheet) : this(
        TextureAtlas(sheetFile.parent().child(raw.atlas!!)),
        raw,
    )

    fun getId(name: String): Int {
        val tmp = nameToId[name]
        if (tmp == null) {
            app.logger.error("Shot data of name\"$name\" not found!")
        }
        return tmp
    }

    fun findBullet(id: Int): BulletData {
        return data[id]
    }

    fun findBullet(name: String): BulletData {
        return data[nameToId[name]]
    }
}

class BulletData(parent: ShotSheet, raw: ShotSheetLoader.RawShotSheet.RawBulletData) {
    var id: Int = raw.id!!
    var name: String = raw.name!!
    var render: String = raw.render
    var texture: BulletTexture = BulletTexture(parent.atlas, name, raw.frames)
    var originX = raw.originX ?: texture.maxWidth / 2f
    var originY = raw.originY ?: texture.maxWidth / 2f
    var delayTexture: TextureRegion = parent.atlas.findRegion(raw.delaySrc!!)
    var delayColor: Color = Color.valueOf(raw.delayColor!!)
    var spinVelocity = raw.spinVelocity
    var collision: CollisionShape = when (raw.collisionMethod) {
        null -> NoCollision()
        "Circle" -> CircleCollision(raw.collisionData!![0])
        "Rectangle" -> AABBCollision(raw.collisionData!![0], raw.collisionData[1])
        else -> CircleCollision(raw.collisionData!![0]) // use circle as default
    }
    var rotation = raw.rotation
}