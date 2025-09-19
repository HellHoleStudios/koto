package com.hhs.koto.demo.player

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.hhs.koto.stg.Drawable
import com.hhs.koto.stg.PlayerState
import com.hhs.koto.stg.bullet.ShotSheet
import com.hhs.koto.util.*
import ktx.math.div
import ktx.math.vec2

class ReimuBall(
    val player: ReimuPlayer,
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float,
):Drawable {
    override var x: Float = playerX+x1
    override var y: Float = playerY+y1
    override var alive: Boolean = true

    override val zIndex: Int = -201

    val shotSheet: ShotSheet = A["player/th10_player.shot"]

    var frame = 0

    val sprite = Sprite(A.get<Texture>("player/reimuBall.png"))

    override val recyclable: Boolean = true

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        sprite.draw(batch)
    }

    override fun tick() {
        sprite.setPosition(x-7f,y-7f)
        sprite.setOriginCenter()
        sprite.rotate(1f)
        frame++

        if(player.playerState!=PlayerState.RESPAWNING) {
            val vec = (if(game.pressed(VK.SLOW)) {
                vec2(playerX + x2 - x, playerY + y2 - y)
            }else{
                vec2(playerX + x1 - x, playerY + y1 - y)
            }/10).limit(15f)

            x += vec.x
            y += vec.y
        }

        if (player.playerState != PlayerState.RESPAWNING && !game.inDialog && game.pressed(VK.SHOT)) {
            if (frame % 4 == 0) {
                SE.play("shoot")
                game.playerBullets.add(HomingAmulet(x, y, 3f, shotSheet, A["player/th10_player.atlas"]))
            }
        }
    }
}