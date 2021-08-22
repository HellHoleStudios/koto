package com.hhs.koto.stg.replay

import com.badlogic.gdx.math.RandomXS128
import com.hhs.koto.util.KotoRuntimeException
import com.hhs.koto.util.SystemFlag
import com.hhs.koto.util.VK
import com.hhs.koto.util.replayKeyMask

class ReplayLayer {
    var replay = Replay()
    var frame = -1

    var rng = RandomXS128()

    /**
     * If not in replay mode, create a new start point with name
     *
     * Or load state in a start point
     */
    fun setOrLoadStartPoint(name: String) {
        if (SystemFlag.replay == true) { //using replay mode
            val start = replay.startPoint.first { it.name == name }
            rng = RandomXS128(start.rng)

            //TODO Load System State
        } else {
            //recording a replay
            val newRng = System.currentTimeMillis()
            rng = RandomXS128(newRng)
            replay.startPoint.add(StartPoint(frame, newRng, name))

            //TODO Save System State
        }
    }

    /**
     * Should be called each frame. Will update frame index and more.
     */
    fun tick() {
        frame++

        if (SystemFlag.replay == true) {
            //TODO using replay. Don't do anything?
        } else {
            var mask = 0
            for ((index, key) in replayKeyMask.withIndex()) {
                if (key.pressed()) {
                    mask = mask or (1 shl index)
                }
            }

            replay.mask.add(mask)

//            println("$frame = $mask")
            if (replay.mask.size - 1 != frame) {
                throw KotoRuntimeException("Replay Dislocation! Expected $frame but found ${replay.mask.size - 1}")
            }
        }
    }

    /**
     * Finish a replay recording
     */
    fun conclude() {
        if (SystemFlag.replay == true) {
            //TODO IDK What to put here
        } else {
            replay.date = System.currentTimeMillis()
            replay.user = "Leatherman"
        }
    }

    fun random(a: Float, b: Float): Float {
        return rng.nextFloat() * (b - a) + a
    }

    fun pressed(code: VK): Boolean {
        return pressed(replayKeyMask.indexOf(code))
    }

    fun pressed(code: Int): Boolean {
        return if (SystemFlag.replay == true) {
            (replay.mask[frame] and (1 shl code)) > 0
        } else {
            replayKeyMask[code].pressed()
        }
    }

    fun justPressed(code: Int): Boolean {
        return if (SystemFlag.replay == true) {
            (replay.mask[frame] and (1 shl code)) > 0 && (frame == 0 || (replay!!.mask[frame - 1] and (1 shl code)) == 0)
        } else {
            replayKeyMask[code].justPressed()
        }
    }
}