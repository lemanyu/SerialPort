package com.hsap.test2.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.hsap.test2.R;


/**
 * Created by Dongsh on 2018/1/30.
 */
public class CircleSeekBar extends View {

    private float outermostRadius;
    private float innerRadius;

    private int crackColor;
    private int crackBgColor;
    private int centerColor;

    private float textSize;
    private int textColor;

    private Paint crackPaint;
    private Paint crackBgPaint;
    private Paint centerPaint;
    private Paint textPaint;

    private int mXCenter;
    private int mYCenter;

    private int Size;

    public CircleSeekBar(Context context) {
        super(context);
    }

    public CircleSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initAttrs(context, attrs);

        initPaint();
    }

    private void initPaint() {
        crackPaint = new Paint();
        crackPaint.setAntiAlias(true);
        crackPaint.setStrokeWidth(outermostRadius - innerRadius);
        crackPaint.setStyle(Paint.Style.STROKE);
        crackPaint.setColor(crackColor);

        crackBgPaint = new Paint();
        crackBgPaint.setAntiAlias(true);
        crackBgPaint.setStrokeWidth(outermostRadius - innerRadius);
        crackBgPaint.setStyle(Paint.Style.FILL);
        crackBgPaint.setColor(crackBgColor);

        centerPaint = new Paint();
        centerPaint.setAntiAlias(true);
        centerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        centerPaint.setColor(centerColor);

        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleSeekBar, 0, 0);

        outermostRadius = typedArray.getDimension(R.styleable.CircleSeekBar_OutermostRadius, 0);

        innerRadius = typedArray.getDimension(R.styleable.CircleSeekBar_InnerRadius, 0);

        crackBgColor = typedArray.getColor(R.styleable.CircleSeekBar_CrackBgColor, 0xFFFFFFFF);
        crackColor = typedArray.getColor(R.styleable.CircleSeekBar_CrackColor, 0xFFFFFFFF);
        centerColor = typedArray.getColor(R.styleable.CircleSeekBar_CenterColor, 0xFFFFFFFF);

        textSize = typedArray.getDimension(R.styleable.CircleSeekBar_textSize, 0);
        textColor = typedArray.getColor(R.styleable.CircleSeekBar_textColor, 0xFFFFFFFF);

        Size = typedArray.getInt(R.styleable.CircleSeekBar_Size, 0);
    }

    public CircleSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mXCenter = getWidth() >> 1;
        mYCenter = getHeight() >> 1;
        canvas.drawCircle(mXCenter, mYCenter, outermostRadius - 1, crackBgPaint);

        canvas.drawCircle(mXCenter, mYCenter, innerRadius, centerPaint);

        float oi = (outermostRadius - innerRadius) / 2;
        float x = mXCenter - outermostRadius + oi;
        float y = mYCenter - outermostRadius + oi;
        RectF rectF = new RectF(x, y, getWidth() - x, getHeight() - y);
        canvas.drawArc(rectF, -90, Size / 5 * 18, false, crackPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setTextSize(int size) {
        Size = size;
        invalidate();
    }

}
