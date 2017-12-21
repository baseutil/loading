package com.hk.agg.kotlintedt

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.*

/**
 * Created by wz on 2017/12/15.
 */
class LoadView : View {

    //淡白色
    val WHITE_COLOR = 0xfffde399
    //白色
    val WHITE_TEXT_COLOR = 0xffffffff
    //橙黄色
    val ORANGE_COLOR = 0xffffa800
    //文字大小
    val TEXT_SIZE = 25

    var swing: Float = 1.0F;//振幅
    var swing_gap: Float = 1.0F//振幅差距
    var rotate_peed: Float = 1.0F//树叶旋转速度
    var translation_peed: Float = 1.0F//树叶移动速度
    var mLeafFloatTime = 3000//叶子飘动周期 控制移动速度
    var mLeafRotateTime = 2000//叶子旋转周期 控制旋转速度
    val mMiddleAmplitude = 13
    val mAmplitudeDisparity = 5
    val mFanRotateTime = 2000//风扇旋转周期 控制旋转速度
    var mFanStartTime = 0L//风扇启动时间
    var mFanScaleTime = 0//控制风扇缩放
    var mFanTextSize = 0F//控制文字缩放

    var lastProgress: Int = 0
    var oldProgress: Int = 0
    var progress: Int = 0
//        set(value) {
//        progress = value
//        postInvalidate()
//    }


    var mOutBitmap: Bitmap?= null
    //绘制区域
    var mOuterSrcRect: Rect? = null
    var mOutHeight: Int = 50
    var mOutwidth: Int = 150

    //绘制屏幕所在区域
    var mOuterDestRect: Rect? = null
    var mOuterDestHeight: Int = 0
    var mOuterDestWidth: Int = 0

    var mWhiteRectF: RectF? = null
    var mOrangeRectF: RectF? = null
    var mArcRectF: RectF? = null

    var mLeafBitmap: Bitmap? = null
    var mLeafHeight: Int = 10
    var mLeafWidth: Int = 50

    var mFanBitmap: Bitmap? = null
    var mFanHeight: Int = 40
    var mFanWidth: Int = 40

    var mFanTextHeight: Int = 0

    var mResources: Resources?= null
    var mProgressWidth: Int = 300//进度条宽度
    var mCurrentProgressPosition: Int = 0//进度条宽度
    var mLeftMagin: Int = 0//控制上下左间距
    var mRightMagin: Int = 0//控制进度条距离右边距离
    var mArcRadius: Int = 0//弧的半径

    var mBitmapPaint: Paint? = null
    var mSpacePaint: Paint? = null
    var mProgressPaint: Paint? = null
    var mFanTextPaint: Paint? = null

    var mLeafFactory: LeafFactory ?= null
    var mLeafs: ArrayList<Leaf> ?= null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        init(context,attrs,0)

    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init(context,attrs,defStyleAttr)
    }

    fun init(context: Context?,attrs: AttributeSet?,defStyleAttr: Int){
        val a: TypedArray = context!!.theme.obtainStyledAttributes(attrs, R.styleable.LoadView, defStyleAttr, 0)
        swing = a.getFloat(R.styleable.LoadView_swing,1.0F)
        swing_gap = a.getFloat(R.styleable.LoadView_swing_gap,1.0F)
        rotate_peed = a.getFloat(R.styleable.LoadView_rotate_peed,1.0F)
        translation_peed = a.getFloat(R.styleable.LoadView_translation_peed,1.0F)
        progress = a.getInt(R.styleable.LoadView_progress,0)
        mLeftMagin = a.getDimension(R.styleable.LoadView_mLeftMagin, 0F).toInt()
        mRightMagin = a.getDimension(R.styleable.LoadView_mRightMagin, 0F).toInt()
        a.recycle()

        mResources = resources
        initBitmap()
        initPaint()

        mLeafFactory = LeafFactory()
        mLeafs = mLeafFactory!!.getLeafs()

    }

    private fun initPaint() {
        mBitmapPaint = Paint()
        mBitmapPaint!!.isAntiAlias = true
        mBitmapPaint!!.isDither = true
        mBitmapPaint!!.isFilterBitmap = true

        mSpacePaint = Paint()
        mSpacePaint!!.isAntiAlias = true
        mSpacePaint!!.isDither = true
        mSpacePaint!!.color = WHITE_COLOR.toInt()

        mProgressPaint = Paint()
        mProgressPaint!!.color = ORANGE_COLOR.toInt()
        mProgressPaint!!.isDither = true
        mProgressPaint!!.isAntiAlias = true

        mFanTextPaint = Paint()
        mFanTextPaint!!.color = WHITE_TEXT_COLOR.toInt()
        mFanTextPaint!!.isDither = true
        mFanTextPaint!!.isAntiAlias = true
        mFanTextPaint!!.textSize = UiUtils.dipToPx(context,TEXT_SIZE).toFloat()
        mFanTextPaint!!.textAlign = Paint.Align.CENTER

        val fm = mFanTextPaint!!.fontMetricsInt
        mFanTextHeight = fm.bottom - fm.top + UiUtils.dipToPx(context,5)
    }

    private fun initBitmap() {
        mOutBitmap = (mResources!!.getDrawable(R.mipmap.leaf_kuang) as BitmapDrawable).bitmap
        mOutHeight = mOutBitmap!!.height
        mOutwidth = mOutBitmap!!.width
//        mProgressWidth = mOutwidth/5

        mLeafBitmap = BitmapFactory.decodeResource(mResources, R.mipmap.leaf)
        mLeafHeight = mLeafBitmap!!.height
        mLeafWidth = mLeafBitmap!!.width

        mFanBitmap = BitmapFactory.decodeResource(mResources,R.mipmap.fengshan)
        mFanHeight = mFanBitmap!!.height
        mFanWidth = mFanBitmap!!.width
        mFanScaleTime = mFanWidth

        mFanStartTime = System.currentTimeMillis()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthSpecMode === MeasureSpec.AT_MOST && heightSpecMode === MeasureSpec.AT_MOST){
            setMeasuredDimension(mOutwidth,mOutHeight)
        }else if(widthSpecMode === MeasureSpec.AT_MOST){
            setMeasuredDimension(mOutwidth,heightSpecSize)
        }else if (heightSpecMode === MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSpecSize,mOutHeight)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mOuterDestHeight = h
        mOuterDestWidth = w
        mProgressWidth = mOuterDestWidth - mLeftMagin - mRightMagin
         mArcRadius = (mOuterDestHeight - 2 * mLeftMagin)/2

        mOuterSrcRect = Rect(0,0,mOutwidth,mOutHeight)
        mOuterDestRect = Rect(0,0,mOuterDestWidth,mOuterDestHeight)

        mArcRectF = RectF(
                mLeftMagin.toFloat(),
                mLeftMagin.toFloat(),
                (mLeftMagin + 2 * mArcRadius).toFloat(),
                (mOuterDestHeight - mLeftMagin).toFloat()
        )

        mWhiteRectF = RectF(
                (mLeftMagin + mCurrentProgressPosition).toFloat(),
                mLeftMagin.toFloat(),
                (mOuterDestWidth - mRightMagin).toFloat(),
                (mOuterDestHeight - mLeftMagin).toFloat()
        )

        mOrangeRectF = RectF(
                (mLeftMagin + mArcRadius).toFloat(),
                mLeftMagin.toFloat(),
                (mOuterDestWidth - mRightMagin).toFloat(),
                (mOuterDestHeight - mLeftMagin).toFloat()
        )

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawProgress(canvas)

        canvas!!.drawBitmap(mOutBitmap,mOuterSrcRect,mOuterDestRect,mBitmapPaint)

        drawFan(canvas)

        postInvalidate()
    }

    //绘制风扇
    //此处适配可能有问题
    private fun drawFan(canvas: Canvas?) {
        val nowTime = System.currentTimeMillis()
        val progressDifference = if ((progress - oldProgress) > 0)  progress - oldProgress else 1
        canvas!!.save()
        val mFan = matrix

        mFan.postTranslate((UiUtils.dipToPx(context,5) + mProgressWidth - mFanWidth / 2).toFloat(), (mOutHeight / 2 - mFanHeight / 2).toFloat())

//        if(progress > lastProgress){
//            lastProgress = progress
            val mFanControlSpeed = ((nowTime - mFanStartTime) % mFanRotateTime) / mFanRotateTime.toDouble()
            val mFanRotate = mFanControlSpeed * 360 * progressDifference
            mFan.postRotate(mFanRotate.toFloat(),((UiUtils.dipToPx(context,5) + mProgressWidth)).toFloat(), (mOutHeight / 2 - mFanHeight / 2 + mFanHeight / 2).toFloat())
//        Log.e("TAG","out:${mOutHeight},mOuterDestHeight:${mOuterDestHeight}")

        if (progress >= 100 && mFanScaleTime > 0){
            mFanScaleTime--
            mFanTextSize++
            val mFanAngle = mFanScaleTime.toFloat() / mFanWidth.toFloat()
            mFan.postScale(mFanAngle, mFanAngle,((UiUtils.dipToPx(context,5) + mProgressWidth)).toFloat(), (mOutHeight / 2 - mFanHeight / 2 + mFanHeight / 2).toFloat())
//            mFanTextSize = mFanScaleTime.toFloat() / mFanWidth.toFloat()
            mFanTextPaint!!.textSize = UiUtils.dipToPx(context,TEXT_SIZE) * mFanTextSize / mFanWidth
            canvas.drawText("100%", (UiUtils.dipToPx(context,5) + mProgressWidth).toFloat(), mFanTextHeight .toFloat(),mFanTextPaint)
        }else if (progress >= 100 && mFanScaleTime <= 0){
            mFan.postScale(mFanWidth.toFloat(), mFanHeight.toFloat())
            mFanTextPaint!!.textSize = UiUtils.dipToPx(context,TEXT_SIZE).toFloat()
            canvas.drawText("100%", (UiUtils.dipToPx(context,5) + mProgressWidth).toFloat(), mFanTextHeight.toFloat(),mFanTextPaint)
        }


        canvas.drawBitmap(mFanBitmap,mFan,mBitmapPaint)
        canvas.restore()
    }

    //绘制进度条
    private fun drawProgress(canvas: Canvas?) {
        if (progress >= 100)
            progress = 100
        mCurrentProgressPosition = mProgressWidth * progress / 100

        if (mCurrentProgressPosition < mArcRadius){

            canvas!!.drawArc(mArcRectF, 90F, 180F,false,mSpacePaint)

            mWhiteRectF!!.left = (mLeftMagin + mArcRadius).toFloat()
            canvas.drawRect(mWhiteRectF,mSpacePaint)

            val angle = Math.toDegrees(Math.acos(((mArcRadius.toDouble() - mCurrentProgressPosition.toDouble()) / mArcRadius.toDouble())))
            val sweep = 2 * angle

            drawLeafs(canvas)

            canvas.drawArc(mArcRectF, (180 - angle).toFloat(), sweep.toFloat(),false,mProgressPaint)

        }else{

            mWhiteRectF!!.left = (mCurrentProgressPosition + mLeftMagin).toFloat()
            canvas!!.drawRect(mWhiteRectF,mSpacePaint)

            drawLeafs(canvas)
            canvas.drawArc(mArcRectF, 90F, 180F,false,mProgressPaint)

            mOrangeRectF!!.right = (mCurrentProgressPosition + mLeftMagin).toFloat()
            canvas.drawRect(mOrangeRectF,mProgressPaint)
        }
    }

    //绘制叶子
    private fun drawLeafs(canvas: Canvas?) {
        val nowTime = System.currentTimeMillis()
        for (item in this!!.mLeafs!!){
            if (nowTime > item.startTime && item.startTime != 0L){
                getLeafLocaltion(item,nowTime)
                canvas!!.save()
                val m = Matrix()
                val c = Camera()
                val m3d = Matrix()

                val tranx = mLeftMagin + item.x
                val trany = mLeftMagin + item.y
                m.postTranslate(tranx.toFloat(), trany.toFloat())

                //绑定mLeafFloatTime 可以控制旋转速度
                val rotateChangeSpeed = ((nowTime - item.startTime) % mLeafRotateTime) / mLeafRotateTime.toFloat()
                val angle = rotateChangeSpeed * 360
                val rotate = if (item.rotateDirection == 0) angle + item.varrotateAngle else -angle - item.varrotateAngle
                Log.e("TAG","rotate:${rotate},rotateChangeSpeed:${rotateChangeSpeed},angle:${angle}")
                m.postRotate(rotate, (tranx + mLeafWidth / 2).toFloat(), (trany + mLeafHeight / 2).toFloat())

                c.save()
                c.rotate(angle,0F,0F)
                c.getMatrix(m3d)
                c.restore()

                m3d.preTranslate((-tranx - mLeafWidth / 2).toFloat(), (-trany - mLeafHeight / 2).toFloat())
                m3d.postTranslate(tranx + mLeafWidth.toFloat(), trany + mLeafHeight.toFloat())
                canvas.concat(m3d)

                canvas.drawBitmap(mLeafBitmap,m,mBitmapPaint)
                canvas.restore()

            }else{
                continue
            }

        }
    }

    //获取／设置叶子位置
    private fun getLeafLocaltion(leaf: Leaf,nowTime: Long) {
        val intervalTime = nowTime - leaf.startTime
        if (intervalTime < 0)
            return
        else if (intervalTime > mLeafFloatTime){
            leaf.startTime = System.currentTimeMillis() + Random().nextInt(mLeafFloatTime)
        }
        leaf.x = mProgressWidth - (mProgressWidth * (intervalTime.toDouble() / mLeafFloatTime.toDouble()) + mLeftMagin).toInt()
        leaf.y = getLeafLocaltionY(leaf)
    }

    // 通过叶子信息获取当前叶子的Y值
    private fun getLeafLocaltionY(leaf: Leaf): Int {
        // y = A(wx+Q)+h
        val w = 2 * Math.PI / mProgressWidth
        var a = mMiddleAmplitude
        when(leaf.startType){
            "TITLE" -> a = mMiddleAmplitude - mAmplitudeDisparity
            "MIDDLE" -> a = mMiddleAmplitude
            "BIG" -> a = mMiddleAmplitude + mAmplitudeDisparity
        }
        Log.i("TAG", "---a = " + a + "---w = " + w + "--leaf.x = " + leaf.x)
        return ((a * Math.sin(w * leaf.x)) + mArcRadius * 2 / 3).toInt()
    }

    fun setNewProgress(pro: Int){
        oldProgress = progress
        progress = pro
        postInvalidate()
    }
}
