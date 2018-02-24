package com.hsap.test2.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.hsap.test2.R;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Dongsh on 2018/2/1.
 */
public class TableView extends View {

    private String[] stateNameArray;
    private int stateSize;
    private int stateStart;

    public int startHour;
    public int startMinute;

    private int timeSize;
    //中间间隔
    private int minuteCrack;

    private int textColor;
    private float textSize;
    private int textHeight;

    private Paint linePaint;
    private Paint textPaint;
    private Paint textTopPaint;
    private Paint pathPaint;

    private float[] positionX;
    private float[] positionY;

    private float max;
    private float min;

    private float X;
    private float Y;

    private List<Point> pointList;

    public TableView(Context context) {
        super(context);
    }

    public TableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initPaint();
    }

    private void initPaint() {
        linePaint = new Paint();
        linePaint.setColor(Color.argb(255, 97, 97, 97));
        linePaint.setStrokeWidth(1);

        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setTextAlign(Paint.Align.LEFT);

        textTopPaint = new Paint();
        textTopPaint.setTextSize(textSize);
        textTopPaint.setColor(textColor);
        textTopPaint.setTextAlign(Paint.Align.CENTER);

        pathPaint = new Paint();
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(2);
        pathPaint.setColor(Color.WHITE);

        positionX = new float[timeSize + 2];
        positionY = new float[stateSize];

    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TableView, 0, 0);
        CharSequence stateName = typedArray.getText(R.styleable.TableView_stateName);
        if (stateName != null && !stateName.equals("")) {
            stateNameArray = stateName.toString().split(",");
        } else {
            stateNameArray = new String[0];
        }

        stateSize = typedArray.getInt(R.styleable.TableView_stateSize, 0);
        stateStart = typedArray.getInt(R.styleable.TableView_stateStart, 0);

        startHour = typedArray.getInt(R.styleable.TableView_startHour, 0);
        startMinute = typedArray.getInt(R.styleable.TableView_startMinute, 0);

        timeSize = typedArray.getInt(R.styleable.TableView_timeSize, 0);
        minuteCrack = typedArray.getInt(R.styleable.TableView_minuteCrack, 0);

        textSize = typedArray.getDimension(R.styleable.TableView_textSize, 0);
        textColor = typedArray.getColor(R.styleable.TableView_textColor, 0xFFFFFFFF);

        max = typedArray.getFloat(R.styleable.TableView_max, 100);
        min = typedArray.getFloat(R.styleable.TableView_min, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackGround(canvas);
        drawPath(canvas);
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
        invalidate();
    }

    private void drawPath(Canvas canvas) {
        /*pointList = new LinkedList<>();
        pointList.add(new Point(0, 50));
        pointList.add(new Point(1, 100));
        pointList.add(new Point(2, 30));
        pointList.add(new Point(3, 100));
        pointList.add(new Point(4, 40));
        pointList.add(new Point(5, 100));
        pointList.add(new Point(6, 20));
        pointList.add(new Point(7, 100));
        pointList.add(new Point(8, 10));
        pointList.add(new Point(9, 80));
        pointList.add(new Point(10, 65));
        pointList.add(new Point(11, 70));
        pointList.add(new Point(12, 25));
        pointList.add(new Point(13, 35));
        pointList.add(new Point(14, 160));
        pointList.add(new Point(15, 130));
        pointList.add(new Point(16, 180));
        pointList.add(new Point(17, 190));
        pointList.add(new Point(18, 200));
        pointList.add(new Point(19, 170));
        pointList.add(new Point(20, 120));
        pointList.add(new Point(21, 70));
        pointList.add(new Point(22, 25));
        pointList.add(new Point(23, 35));
        pointList.add(new Point(24, 160));
        pointList.add(new Point(25, 130));
        pointList.add(new Point(26, 180));
        pointList.add(new Point(27, 190));
        pointList.add(new Point(28, 200));
        pointList.add(new Point(29, 170));
        pointList.add(new Point(30, 120));
        pointList.add(new Point(31, 70));
        pointList.add(new Point(32, 25));
        pointList.add(new Point(33, 35));
        pointList.add(new Point(34, 160));
        pointList.add(new Point(35, 130));
        pointList.add(new Point(36, 180));
        pointList.add(new Point(37, 190));
        pointList.add(new Point(38, 200));
        pointList.add(new Point(39, 170));
        pointList.add(new Point(40, 120));
        pointList.add(new Point(41, 70));
        pointList.add(new Point(42, 25));
        pointList.add(new Point(43, 35));
        pointList.add(new Point(44, 160));
        pointList.add(new Point(45, 130));
        pointList.add(new Point(46, 180));
        pointList.add(new Point(47, 190));
        pointList.add(new Point(48, 200));
        pointList.add(new Point(49, 170));
        pointList.add(new Point(50, 120));
        pointList.add(new Point(51, 70));
        pointList.add(new Point(52, 25));
        pointList.add(new Point(53, 35));
        pointList.add(new Point(54, 160));
        pointList.add(new Point(55, 130));
        pointList.add(new Point(56, 180));
        pointList.add(new Point(57, 190));
        pointList.add(new Point(58, 200));
        pointList.add(new Point(59, 170));
        pointList.add(new Point(60, 120));
        pointList.add(new Point(61, 70));
        pointList.add(new Point(62, 25));
        pointList.add(new Point(63, 35));
        pointList.add(new Point(64, 160));
        pointList.add(new Point(65, 130));
        pointList.add(new Point(66, 180));
        pointList.add(new Point(67, 190));
        pointList.add(new Point(68, 200));
        pointList.add(new Point(69, 170));
        pointList.add(new Point(70, 120));*/



        if (pointList != null && pointList.size() != 0) {
            X = (positionX[positionX.length - 1] - positionX[0]) / (minuteCrack * (timeSize + 1));
            Y = (positionY[positionY.length - 1] - positionY[0]) / (max - min);
            Point point1;
            Point point3;
            for (int i = 0; i < pointList.size() - 1; i++) {
                point1 = pointList.get(i);
                point3 = pointList.get(i + 1);

                canvas.drawLine(i * X + positionX[0], (max - point1.y) * Y + positionY[0], (i + 1) * X + positionX[0], (max - point3.y) * Y + positionY[0], pathPaint);

                if (i == minuteCrack * (timeSize + 1)) {
                    break;
                }
            }
        }
    }

    private void drawBackGround(Canvas canvas) {
        int height = getMeasuredHeight() - (getMeasuredHeight() >> 5);
        int width = getMeasuredWidth() - (getMeasuredWidth() >> 5);

        int maxTextWidth = measureText();
        int crackHeight = height / stateSize;
        int crackWidth = (width - maxTextWidth) / (timeSize + 1);

        for (int i = 1; i < stateSize + 1; i++) {
            positionY[i - 1] = i * crackHeight;
            canvas.drawLine(maxTextWidth, positionY[i - 1], maxTextWidth + crackWidth * (timeSize + 1), positionY[i - 1], linePaint);
            if (i <= stateNameArray.length) {
                canvas.drawText(stateNameArray[i - 1], maxTextWidth >> 4, (i + stateStart) * crackHeight + (textHeight >> 2), textPaint);
            }
        }
        for (int i = 0, hour = startHour, minute = startMinute; i < timeSize + 2; i++) {
            positionX[i] = maxTextWidth + crackWidth * i;
            canvas.drawLine(positionX[i], crackHeight, positionX[i], stateSize * crackHeight, linePaint);
            if (minuteCrack != 0) {
                canvas.drawText((hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute), maxTextWidth + crackWidth * i, crackHeight - (textHeight), textTopPaint);
                minute += minuteCrack;
                while (minute >= 60) {
                    minute -= 60;
                    hour++;
                }
                if (hour == 25) {
                    hour = 0;
                }
            }
        }
        for (int i = 0; i < stateSize; i++) {
            String string = "";
            for (int j = 0; j < timeSize + 2; j++) {
                string += "(" + positionX[j] + "," + positionY[i] + ")  ";
            }
            Log.d(TAG, "drawBackGround: " + string);
        }

    }

    private String TAG = "TableView";

    private int measureText() {
        int width = 0;
        if (stateNameArray != null && stateNameArray.length != 0) {
            String stateName;
            Rect rect = new Rect();
            for (int i = 0; i < stateNameArray.length; i++) {
                stateName = stateNameArray[i];
                textPaint.getTextBounds(stateName, 0, stateName.length(), rect);
                width = Math.max(width, rect.right - rect.left);
                textHeight = rect.bottom - rect.top;
            }
        }
        return width + (width >> 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
