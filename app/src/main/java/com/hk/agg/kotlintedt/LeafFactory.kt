package com.hk.agg.kotlintedt

import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by wz on 2017/12/19.
 */
class LeafFactory {
    val MAX_NUM = 8
    //使叶子出现时间错开
    var LEAF_FLOAT_TIME = 3000
    val random = Random()

    fun buildLeaf(): Leaf{
        val leaf = Leaf()
        val randomType = random.nextInt(3)
        when(randomType){
            0 -> leaf.startType = "TITLE"
            1 -> leaf.startType = "MIDDLE"
            2 -> leaf.startType = "BIG"
        }

        leaf.rotateDirection = random.nextInt(2)
        leaf.varrotateAngle = random.nextInt(360)
        val mAddTime = random.nextInt(((LEAF_FLOAT_TIME * 1.5).toInt()))
        leaf.startTime = System.currentTimeMillis() + mAddTime

        return leaf
    }

    fun getLeafs(): ArrayList<Leaf>{
        var leafs = arrayListOf<Leaf>()
        for (i in 1..MAX_NUM){
            leafs.add(buildLeaf())
        }
        return leafs
    }
}