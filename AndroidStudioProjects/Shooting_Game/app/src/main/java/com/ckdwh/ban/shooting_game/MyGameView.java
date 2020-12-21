package com.ckdwh.ban.shooting_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * Created by BAN on 2017-04-06.
 */

public class MyGameView extends SurfaceView implements SurfaceHolder.Callback {
    private Context context = null; // context
    private SurfaceHolder holder = null; // holder
    private MyThread myThread = null; // Thread

    private Point point = new Point(); // 해상도 getSize 에 넣을 때 사용했음.

    private int width, height; // 화면의 전체 폭
    private int x1,y1,x2,y2,y1Rest,y2Rest; // Viewport의 좌표 (y1,2Rest은
                                     // 배경을 그려주다 끝까지 갔을때 다시 아래로 돌려보내주기 위해 생성)
    private int cx,cy; // 전체 맵의 중심 좌표
    private int mx,my; // x,y 이동 속도 (move x,y)

    private int w, h; // 캐릭터의 가로, 세로 길이

    // 캐릭터 초기 위치 (가로: 가운데, 세로: 맨 아래)
    int characterPostionX;
    int characterPostionY;

    public MyGameView(Context context) {
        super(context);
        this.context = context;
        holder = getHolder();
        holder.addCallback(this);

        // 화면 해상도
        Display display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getSize(point);
        width = point.x;
        height = point.y;

        x1 = 0;
        y1 = y1Rest = (int)(height*3f/4);
        x2 = width;
        y2 = y2Rest = height;

        cx = width/2;
        cy = height/2;

        mx = 5;
        my = 5;

        myThread = new MyThread();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
         // Thread 시작
        myThread.setRunning(true);
        myThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean a = true;
        // Thread 종료
        myThread.setRunning(false);
        while(a) {
            try {
                myThread.join();
                a = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } // end of while
    }

// Thread

    class MyThread extends Thread {
        boolean isRun = false; // Thread while flag
        Bitmap backgroundImage; // 배경
        Bitmap touchImage; // 캐릭터
        int count = 0; // count를 if문 만족할 만큼 run()이 돌고나서, 스크롤을 1번 움직여라

        public MyThread() {
            backgroundImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
            backgroundImage = Bitmap.createScaledBitmap(backgroundImage, width,height, true);
            touchImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.image);
            touchImage = Bitmap.createScaledBitmap(touchImage, 300, 300, true);
            w = touchImage.getWidth();
            h = touchImage.getHeight();
            characterPostionX = cx-w/2;
            characterPostionY = height-h;
        }

        @Override
        public void run() {
            Canvas canvas = null; // canvas
            Rect src = new Rect(); // Viewport 좌표
            Rect dst = new Rect(); // View 좌표
            dst.set(0, 0, width, height); // View 설정(화면 전체 크기)

            while(isRun) {
                canvas = holder.lockCanvas(null); /// 버퍼 할당
                try {
                    synchronized (holder) { // 동기화 유지
                        scrollImage(); // Viewport 이동
                        src.set(x1, y1, x2, y2); // 이동한 Viewport 좌표 설정
                        canvas.drawBitmap(backgroundImage, src, dst, null); // 버퍼에 배경 그리기
                        canvas.drawBitmap(touchImage, characterPostionX, characterPostionY, null);
                    }
                }finally {
                    if(canvas != null) {
                        holder.unlockCanvasAndPost(canvas); // canvas의 내용을 view에 전송
                    }
                }
            } // end of while
        } // end of run

        public void setRunning(boolean isRun) { // Thread 루프 관리
            this.isRun = isRun;
        }

        private void scrollImage () {
            count++;
            if(count <= 2) {
                y1 -= my;
                y2 -= my;
                if(y1 < 0) {
                    y1 = y1Rest;
                    y2 = y2Rest;
                }
                count = 0;
            }
        } // end of scrollImage

    } // end of MyThread

    boolean touch = true; // 이미지를 건드림 여부를 통한 아래 MotionEvent 발생
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x, y; // 터치한 x,y 좌표
        Rect characterTouch = new Rect(); // 캐릭터 범위
        characterTouch.set(characterPostionX, characterPostionY, characterPostionX+w, characterPostionY+h); // 캐릭터를 기준으로 네모 모양의 범위 설정

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 캐릭터 범위 내부를 눌렀다면 캐릭터를 움직이게 한다.
                x = (int)event.getX();
                y = (int)event.getY();

                if(!characterTouch.contains(x,y)) { // 내부에 없다면 종료
                    touch = false;
                    break;
                }

                touch = true; // 내부에 있다면 무브와 업을 실행
            case MotionEvent.ACTION_MOVE:
                if(!touch) {
                    break;
                }
            case MotionEvent.ACTION_UP:
                if(!touch) {
                    break;
                }
                x = (int)event.getX();
                y = (int)event.getY();
                characterPostionX = x-w/2; // 눌렀을 때 캐릭터의 위치x
                characterPostionY = y-h/2; // 눌렀을 때 캐릭터의 위치y
            break;
        }
        return true;
    }
} // end of class
