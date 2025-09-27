// kotlin.script.experimental.api.ScriptTemplateWithArgs=com.hhs.koto.scripting.SpellScript
import com.hhs.koto.stg.graphics.Cutin
import com.hhs.koto.stg.task.CoroutineTask
import com.hhs.koto.stg.task.bullet
import com.hhs.koto.stg.task.wait
import com.hhs.koto.util.*
import kotlinx.coroutines.yield

// This is an example script of BasicScriptedSpell

// You should always return a Task. This is the last statement in your script.
CoroutineTask {
    val boss = getBoss() //You can assume `this` is an object of BasicSpell
    game.stage.addDrawable(Cutin(getRegion("portrait/aya/attack.png")))
    var base = 0f
    for(angle in 0 until 360 step 30){
        newLaser(defaultShotSheet["DS_SCALE_BLUE"], 100f, boss.x, boss.y, 20, 2f, angle.toFloat()).task {
            while(bullet.speed>0){
                bullet.speed-=0.02f
                bullet.speed=bullet.speed.coerceAtLeast(0f)
                yield()
            }

            while(true) {
                wait(120)
                repeat(60) {
                    val targetFor=atan2(bullet.x, bullet.y, playerX+50*cos(angle.toFloat()), playerY+50*sin(angle.toFloat()))
                    bullet.angle+=(targetFor-bullet.angle)*0.1f
                    bullet.speed += 3 / 60f
                    yield()
                }
                repeat(60) {
                    val targetFor=atan2(bullet.x, bullet.y, playerX+50*cos(angle.toFloat()), playerY+50*sin(angle.toFloat()))
                    bullet.angle+=(targetFor-bullet.angle)*0.1f
                    bullet.speed -= 3 / 60f
                    bullet.speed=bullet.speed.coerceAtLeast(0f)
                    yield()
                }
            }

        }
    }

    wait(30000)
}