package com.hhs.koto.stg.replay
import com.badlogic.gdx.utils.Array
import com.hhs.koto.stg.GameMode
import com.hhs.koto.util.*

/**
 * Handles game replay. Need to be injected to the game using [ReplayLayer]
 *
 * @author XGN
 */
class Replay {
    /**
     * Saved replay name
     */
    var user = "Untitled"

    /**
     * [SystemFlag.name]
     */
    var name = ""

    /**
     * [SystemFlag.difficulty]
     */
    var difficulty = ""

    /**
     * [SystemFlag.player]
     */
    var player = ""

    /**
     * [SystemFlag.gamemode]
     */
    var mode = GameMode.STORY

    /**
     * Replay save date
     */
    var date = 0L

    /**
     * Key mask for each frame
     */
    var mask = Array<Int>()

    var startPoint = Array<StartPoint>()

}