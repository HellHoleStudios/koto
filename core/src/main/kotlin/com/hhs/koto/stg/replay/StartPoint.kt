package com.hhs.koto.stg.replay

/**
 * Specifies a start point
 * @author XGN
 */
class StartPoint(var frame: Int,var rng: Long, var name: String) {
    val mp = HashMap<String,String>()

    operator fun get(index: String):String {
        return mp[index]!!
    }

    operator fun set(index: String, value: String){
        mp[index]=value
    }
}
