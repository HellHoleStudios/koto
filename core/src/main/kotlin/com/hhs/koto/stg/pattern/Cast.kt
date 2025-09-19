package com.hhs.koto.stg.pattern

import com.hhs.koto.stg.Movable
import com.hhs.koto.stg.particle.CastParticle
import com.hhs.koto.stg.particle.CastParticle2
import com.hhs.koto.stg.particle.DeathParticle
import com.hhs.koto.stg.particle.Explosion
import com.hhs.koto.stg.task.Task
import com.hhs.koto.stg.task.self
import com.hhs.koto.stg.task.waitForFinish
import com.hhs.koto.util.SE
import com.hhs.koto.util.game
import com.hhs.koto.util.random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.yield

/**
 * declare a cast animation. This does not need cast posture to be on to use (though it is better on)
 *
 * It sucks particles(?)
 */
suspend fun CoroutineScope.cast(x: Float, y: Float, intensity:Int = 10, length: Int = 10){
    val task=object: TemporalPattern(length){
        override fun action() {
            repeat(intensity){
                val ax=random(-1000f,1000f)
                val ay=random(-1000f,1000f)
                game.addParticle(CastParticle(ax,ay,x,y))
            }
        }
    }
    SE.play("charge")
    self.attachTask(task)
    task.waitForFinish()
}

/**
 * declare a cast2 animation. This does not need cast posture to be on to use (though it is better on)
 *
 * It releases sucked particles(?)
 */
suspend fun CoroutineScope.cast2(x: Float, y: Float, intensity:Int = 10, length: Int = 10){
    val task=object: TemporalPattern(length){
        override fun action() {
            repeat(intensity){
                val ax=random(-1000f,1000f)
                val ay=random(-1000f,1000f)
                game.addParticle(CastParticle2(x,y,ax,ay))
            }
        }
    }
    SE.play("charge2")
    self.attachTask(task)
    task.waitForFinish()
}


/**
 * declare an explosion animation. It is boss explode animation
 *
 * It releases sucked particles and create colorful circles
 */
suspend fun CoroutineScope.cast3(x: Float, y: Float, intensity:Int = 3, length: Int = 180){
    val task=object: TemporalPattern(length) {
        override fun action() {
            repeat(intensity) {
                val ax = random(-300f, 300f)
                val ay = random(-300f, 300f)
                game.addParticle(CastParticle2(x, y, ax, ay))
            }

            if(t%10==0){
                game.addParticle(
                    Explosion(
                        x, y, 32f, 64f, 16f, 256f, random(0f, 360f), 30
                    )
                )
                repeat(5) {
                    game.addParticle(DeathParticle(x, y, 10f, random(0f, 360f), 24f, 0f, 20))
                }
            }
        }

    }

    SE.play("bossdead")
    self.attachTask(task)
    task.waitForFinish()
}

