package com.hhs.koto.util

/**
 * This is a helper class to lasers
 *
 * This is a semi-replay for random calls used in lasers
 */
class RNGBank<A> {
    private val mp = HashMap<A,Float>()

    fun random(index: A, l: Float, r: Float):Float{
        if(index in mp){
            return mp[index]!!
        }
        return random(l,r).apply {
            mp[index]=this
        }
    }

}