package com.hhs.koto.stg.bullet

import com.badlogic.gdx.graphics.g2d.Batch
import com.hhs.koto.stg.CollisionShape
import com.hhs.koto.stg.NoCollision
import com.hhs.koto.stg.SLCollision
import com.hhs.koto.stg.particle.GrazeParticle
import com.hhs.koto.util.*

/**
 * Remember the last collision state to avoid creating new collision shapes every frame
 */
class RememberedCollisionState {
    var angle: Float = 0f
    var length: Float = 0f
    var laserWidth: Float = 0f
    var headHit: Float = 0f
    var widthHit: Float = 0f
    var collisionShape: CollisionShape? = null

    fun update(angle: Float, length: Float, laserWidth: Float, headHit: Float, widthHit: Float) {
        this.angle = angle
        this.length = length
        this.laserWidth = laserWidth
        this.headHit = headHit
        this.widthHit = widthHit
        this.collisionShape = SLCollision(angle, length, laserWidth, headHit, widthHit)
    }

    fun isChanged(angle: Float, length: Float, laserWidth: Float, headHit: Float, widthHit: Float): Boolean {
        return this.angle != angle
                || this.length != length
                || this.laserWidth != laserWidth
                || this.headHit != headHit
                || this.widthHit != widthHit
                || this.collisionShape == null
    }
}

/**
 * Static Laser: laser that has only 1 rectangle collision
 *
 * @author XGN
 */
class StaticLaser(
    override var x: Float,
    override var y: Float,
    var length: Float,
    var laserWidth: Float,
    override var delay: Int,
    data: BulletData,
    var headHit: Float = 0.6f,
    var widthHit: Float = 0.7f,
    /**
     * See [LUASTG_STYLE] and [DANMAKUFU_STYLE]
     */
    var style: Int = LUASTG_STYLE,
) : BasicBullet(x, y, data = data, destroyable = false) {

    companion object {
        /**
         * The laser becomes thicker over time during preparation
         */
        const val LUASTG_STYLE = 0

        /**
         * The laser shows an expanding danger line during preparation
         */
        const val DANMAKUFU_STYLE = 1
    }

    override val collision: CollisionShape
        get() = if (t >= delay) getRememberedCollisionShape() else NoCollision()

    private var rememberedCollisionState = RememberedCollisionState()
    private fun getRememberedCollisionShape(): SLCollision {
        if(rememberedCollisionState.isChanged(angle, length, laserWidth, headHit, widthHit)) {
            rememberedCollisionState.update(angle, length, laserWidth, headHit, widthHit)
        }

        return rememberedCollisionState.collisionShape as SLCollision
    }

    override fun onGraze() {
        if (t % 5 == 0) {
            game.graze++
            game.pointValue = (game.pointValue + 1L).coerceAtMost(game.maxPointValue)
            SE.play("graze")
            game.addParticle(GrazeParticle(x, y))
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        val texture = data.texture.getFrame(t)
        tmpColor.set(batch.color)
        val bulletColor = data.color.tintHSV(tint)
        bulletColor.a *= parentAlpha
        batch.color = bulletColor

        if (t >= delay || style == LUASTG_STYLE) {

            val scaleX = length / data.width
            val scaleY = smoothstep(0.1f, laserWidth / data.height, t * 1f / delay)
            batch.draw(
                texture,
                x - data.originX + length * cos(angle) / 2,
                y - data.originY + length * sin(angle) / 2,
                data.originX,
                data.originY,
                data.width,
                data.height,
                scaleX,
                scaleY,
                angle,
            )
        } else {
            //draw danger line

            game.drawer.line(
                x,
                y,
                x + smoothstep(0f, length, t * 1f / max(1f, delay - 20f)) * cos(angle),
                y + smoothstep(0f, length, t * 1f / max(1f, delay - 20f)) * sin(angle)
            )
        }

//
//
//        //debug show hitbox
//
//        run {
//            val vlt = vec2(length * cos(angle), length * sin(angle))
//            val vlr = vlt.cpy().setLength(vlt.len() * (1 - headHit) / 2)
//            val vlro = vlt.cpy().rotate90(1).setLength(laserWidth * widthHit / 2)
//            val xx = x + cos(angle) * length
//            val yy = y + sin(angle) * length
//            val v1 = vec2(x, y) + vlr + vlro
//            val v2 = vec2(x, y) + vlr - vlro
//            val v3 = vec2(xx, yy) - vlr - vlro
//            val v4 = vec2(xx, yy) - vlr + vlro
//            game.drawer.polygon(Polygon(floatArrayOf(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y, v4.x, v4.y)))
//        }
    }
}