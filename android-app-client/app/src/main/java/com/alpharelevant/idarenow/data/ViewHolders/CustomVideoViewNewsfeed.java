package com.alpharelevant.idarenow.data.ViewHolders;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.VideoView;

/**
 * Created by Tabraiz on 3/5/2018.
 */

public class CustomVideoViewNewsfeed extends SurfaceView {
    public long startTime = 0;
    public CustomVideoViewNewsfeed(Context context) {
        super(context);
    }
    public CustomVideoViewNewsfeed(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }
    public CustomVideoViewNewsfeed(Context context, AttributeSet attributeSet, int defStyleAttr){
        super(context,attributeSet,defStyleAttr);
    }
    public int mForceHeight = 0;
    public int mForceWidth = 0;

    public void setDimensions(int w, int h) {
        this.mForceHeight = h;
        this.mForceWidth = w;

    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        Log.d("onTouchEven","test");
//
//
//        if(event.getAction()==MotionEvent.ACTION_DOWN){
//            Log.d("onTouchEvent","ACTION_DOWN");
//            return true;
//        }
//        if(event.getAction()==MotionEvent.ACTION_UP){
//            Log.d("onTouchEvent","ACTION_UP");
//            return true;
//        }
//        return false;
//    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = getDefaultSize(0, mForceWidth);
//        int height = getDefaultSize(0, mForceHeight);

        setMeasuredDimension(mForceWidth, mForceHeight);
    }
}
