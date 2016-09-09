package com.example.administrator.a2048;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by Administrator on 2016/9/7.
 */
public class Fangkuai extends View {
    private static final String TAG = "Fangkuai";
    private Paint paint = new Paint();
    private int num = 0;
    private String strnum;
    Rect rect;

    public Fangkuai(Context context) {
        this(context,null);
    }
    public Fangkuai(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Fangkuai(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        DisplayMetrics metrics = new DisplayMetrics();
        getDisplay().getMetrics(metrics);
        int width = metrics.widthPixels/4;
        setMeasuredDimension(width,width);
    }

    public void setNum(int num) {
        this.num = num;
        strnum = num+"";
        paint.setTextSize(100);
        rect = new Rect();
        paint.getTextBounds(strnum,0,strnum.length(),rect);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        String mBgColor = "#EA7821";
        switch (num){
            case 0:mBgColor = "#CCC0B3";
                break;
            case 2:mBgColor = "#EEE4DA";
                break;
            case 4:mBgColor = "#EDE0C8";
                break;
            case 8:mBgColor = "#F2B179";
                break;
            case 16:mBgColor = "#F49563";
                break;
            case 32:mBgColor = "#F5794D";
                break;
            case 64:mBgColor = "#F55D37";
                break;
            case 128:mBgColor = "#EEE863";
                break;
            case 256: mBgColor = "#EDB04D";
                break;
            case 512:mBgColor = "#ECB04D";
                break;
            case 1024:mBgColor = "#EB9437";
                break;
            case 2048: mBgColor = "#EA7821";
                break;
        }
        paint.setColor(Color.parseColor(mBgColor));
        canvas.drawRect(5,5,getWidth()-5,getWidth()-5, paint);
        if (num != 0){
            drawText(canvas);
        }
    }

    private void drawText(Canvas canvas){
        paint.setColor(Color.BLACK);
        float x = (getWidth()-rect.width())/2;
        float y = getHeight()/2+rect.height()/2;
        canvas.drawText(strnum,x-strnum.length(),y,paint);
    }

    public int getNum() {
        return num;
    }
}