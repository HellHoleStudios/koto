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

package com.hhs.koto.demo.player

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.hhs.koto.stg.PlayerState
import com.hhs.koto.stg.bullet.ShotSheet
import com.hhs.koto.stg.graphics.BasicPlayer
import com.hhs.koto.stg.graphics.BasicPlayerTexture
import com.hhs.koto.util.A
import com.hhs.koto.util.SE
import com.hhs.koto.util.VK
import com.hhs.koto.util.game

open class MarisaPlayer : BasicPlayer(
    BasicPlayerTexture(A["player/th10_player.atlas"], "th10_marisa"),
    (A.get<TextureAtlas>("player/th10_player.atlas")).findRegion("hitbox"),
    3.5f,
    5f,
    2f,
    10,
) {
    protected val shotSheet: ShotSheet = A["player/th10_player.shot"]

    override fun tick() {
        if (playerState != PlayerState.RESPAWNING && !game.inDialog && game.pressed(VK.SHOT)) {
            if (frame % 4 == 0) {
                SE.play("shoot")
                game.playerBullets.add(HomingAmulet(x - 10, y, 3f, shotSheet, A["player/th10_player.atlas"]))
                game.playerBullets.add(HomingAmulet(x + 10, y, 3f, shotSheet, A["player/th10_player.atlas"]))
            }
        }
        super.tick()
    }
}