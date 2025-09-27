package com.hhs.koto.util

import com.badlogic.gdx.math.Vector2
import java.util.Deque

class Distancer(val length: Float) {
    data class NodeSnapshot(val x: Float, val y: Float, val rot: Float){
        fun dst(other: NodeSnapshot): Float {
            return Vector2.dst(x,y,other.x,other.y)
        }
    }

    val dq= ArrayDeque<NodeSnapshot>()
    var totalLength=0f
    var next: Distancer?=null

    fun add(now: NodeSnapshot){
        if(dq.isEmpty()){
            dq.add(now)
            return
        }

        if(now==dq.first()) return

        totalLength+=dq.first().dst(now)
        dq.addFirst(now)

        while(totalLength>length){
            val last=dq.removeLast()
            val newLast=dq.last()
            val dist=last.dst(newLast)
            totalLength-=dist
            next?.add(last)
        }
    }

    fun get(default: NodeSnapshot) = dq.lastOrNull() ?: default
}