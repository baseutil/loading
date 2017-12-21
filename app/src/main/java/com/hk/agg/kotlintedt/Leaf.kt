package com.hk.agg.kotlintedt

/**
 * Created by wz on 2017/12/19.
 */
class Leaf {
    var x = 0
    var y = 0
    //叶子振幅
    var startType = ""//LITTLE, MIDDLE, BIG
    //旋转角度
    var varrotateAngle = 0
    //旋转方向 0顺时针 1逆时针
    var rotateDirection = 0
    //启始时间
    var startTime: Long = 0

}