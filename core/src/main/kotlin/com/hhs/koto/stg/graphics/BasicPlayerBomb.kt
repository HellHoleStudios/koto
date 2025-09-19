package com.hhs.koto.stg.graphics

import com.badlogic.gdx.graphics.g2d.Batch
import com.hhs.koto.stg.CircleCollision
import com.hhs.koto.stg.Collision
import com.hhs.koto.stg.Drawable
import com.hhs.koto.util.CYAN_HSV
import com.hhs.koto.util.RED_HSV
import com.hhs.koto.util.game
import com.hhs.koto.util.getRegion
import space.earlygrey.shapedrawer.ShapeDrawer

class BasicPlayerBomb(
    override var x: Float,
    override var y: Float,
    override var alive: Boolean = true,
    override val zIndex: Int = -201
) : Drawable {


    var shapeDrawer = ShapeDrawer(game.batch, getRegion("ui/blank.png")).apply {
        pixelSize = 0.1f
    }

    var radius = 10f
    var maxradius = 250f
    var alpha = 0.5f
    val collision = CircleCollision(radius)

    override fun draw(batch: Batch, parentAlpha: Float, subFrameTime: Float) {
        if (batch != shapeDrawer.batch) {
            shapeDrawer = ShapeDrawer(batch, getRegion("ui/blank.png"))
            shapeDrawer.pixelSize = 0.5f
        }
        shapeDrawer.filledCircle(x,y,radius, CYAN_HSV.apply { a=alpha })
    }

    override fun tick() {
        if(alpha<=0f){
            alive=false
        }

        if(radius>maxradius){
            alpha-=1/180f
            alpha=alpha.coerceAtLeast(0f)
        }else{
            radius += 5f
            collision.radius = radius
        }

        game.bullets.forEach {
            if (it.destroyable && Collision.collide(collision, x, y, it.collision, it.x, it.y)){
                it.destroy()
            }
        }
        game.enemies.forEach {
            if (it is BasicEnemy && Collision.collide(collision, x, y, it.bulletCollision, it.x, it.y)){
                it.onHit(16f,true)
            }
        }
        game.bosses.forEach {
            if (it is BasicBoss && Collision.collide(collision, x, y, it.bulletCollision, it.x, it.y)){
                it.onHit(16f,true)
            }
        }


        game.items.forEach {
            it.onCollect(x, y, true)
        }
    }
}