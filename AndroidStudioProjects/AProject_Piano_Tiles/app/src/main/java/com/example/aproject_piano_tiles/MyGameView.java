package com.example.aproject_piano_tiles;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyGameView extends SurfaceView implements SurfaceHolder.Callback {
	private Context context = null;
	private SurfaceHolder holder = null;
	private MyThread myThread;
	
	private int fullWidth, fullHeight; // 화면의 가로 세로
	private int moveSpeedX;
	private int moveSpeedY;
	private MediaPlayer mp; // SoundPool 대신 MediaPlayer로 해볼꺼얌

	public MyGameView(Context context) {
		super(context);
		this.context = context;
		holder = getHolder();
		holder.addCallback(this);
		
// 게임에 필요한 여러가지 값 선언
		
		// 화면의 가로, 세로 구하기
		DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
		fullWidth = metrics.widthPixels;
		fullHeight = metrics.heightPixels;
		
		//
		moveSpeedX = 10;
		moveSpeedY = 15;
		
		
		// Thread 생성
		myThread = new MyThread();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		myThread.isRun(true); // while문 무한
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0); // 사운드 풀
		setSoundPieces(); // 소리 조각 저장(초기화)
		myThread.start(); // Thread 시작
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;

		myThread.isRun(false); // while문 반복중지

		while(retry) {
			try {
				myThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

// Thread

	private int littleStarSound[][] = { {R.raw.sound1,1},{R.raw.sound1,1},{R.raw.sound5,1},{R.raw.sound5,1}, // 소리 조각과 몇 박을 차지하는지 기록
										{R.raw.sound6,1},{R.raw.sound6,1},{R.raw.sound5,2},{R.raw.sound4,1},
										{R.raw.sound4,1},{R.raw.sound3,1},{R.raw.sound3,1},{R.raw.sound2,1},
										{R.raw.sound2,1},{R.raw.sound1,2}
	}; // 작은 별 건반 소리

	private int sound[] = { R.raw.flower_dance_1,R.raw.flower_dance_2,R.raw.flower_dance_3,
			                R.raw.flower_dance_4,R.raw.flower_dance_5,R.raw.flower_dance_6,
							R.raw.flower_dance_7,R.raw.flower_dance_8,R.raw.flower_dance_9,
							R.raw.flower_dance_10,R.raw.flower_dance_11,R.raw.flower_dance_12,
							R.raw.flower_dance_13,R.raw.flower_dance_14,R.raw.flower_dance_15,
							R.raw.flower_dance_16,R.raw.flower_dance_17,R.raw.flower_dance_18,
							R.raw.flower_dance_19,R.raw.flower_dance_20,R.raw.flower_dance_21,
							R.raw.flower_dance_22,R.raw.flower_dance_23,R.raw.flower_dance_24,
							R.raw.flower_dance_25,R.raw.flower_dance_26,R.raw.flower_dance_27,
							R.raw.flower_dance_28,R.raw.flower_dance_29,R.raw.flower_dance_30,
							R.raw.flower_dance_31,R.raw.flower_dance_32,R.raw.flower_dance_33,
							R.raw.flower_dance_34,R.raw.flower_dance_35,R.raw.flower_dance_36,
							R.raw.flower_dance_37,R.raw.flower_dance_38,R.raw.flower_dance_39,
							R.raw.flower_dance_40,R.raw.flower_dance_41,R.raw.flower_dance_42,
							R.raw.flower_dance_43,R.raw.flower_dance_44,R.raw.flower_dance_45,
							R.raw.flower_dance_46,R.raw.flower_dance_47,R.raw.flower_dance_48,
							R.raw.flower_dance_49,R.raw.flower_dance_50,R.raw.flower_dance_51,
							R.raw.flower_dance_52,R.raw.flower_dance_53,R.raw.flower_dance_54,
							R.raw.flower_dance_55,R.raw.flower_dance_56,R.raw.flower_dance_57,
							R.raw.flower_dance_58,R.raw.flower_dance_59,R.raw.flower_dance_60,
							R.raw.flower_dance_61,R.raw.flower_dance_62,R.raw.flower_dance_63,
							R.raw.flower_dance_64,R.raw.flower_dance_65,R.raw.flower_dance_66,
							R.raw.flower_dance_67,R.raw.flower_dance_68,R.raw.flower_dance_69,
							R.raw.flower_dance_70,R.raw.flower_dance_71,R.raw.flower_dance_72,
							R.raw.flower_dance_73,R.raw.flower_dance_74,R.raw.flower_dance_75,
							R.raw.flower_dance_76,R.raw.flower_dance_77,R.raw.flower_dance_78,
							R.raw.flower_dance_79,R.raw.flower_dance_80,R.raw.flower_dance_81,
							R.raw.flower_dance_82,R.raw.flower_dance_83,R.raw.flower_dance_84,
							R.raw.flower_dance_85,R.raw.flower_dance_86,R.raw.flower_dance_87,
							R.raw.flower_dance_88,R.raw.flower_dance_89,R.raw.flower_dance_90,
							R.raw.flower_dance_91,R.raw.flower_dance_92,R.raw.flower_dance_93,
	}; // 소리파일
//	private int soundId[] = new int[sound.length]; // 소리 아이디 저장
	private int soundId[] = new int[littleStarSound.length]; // 작은 별 소리 아이디 저장

	private Rect rect[][] = new Rect[5][4];
	private Point p[][][] = new Point[5][4][2];
	private Paint paint[][] = new Paint[5][4];
	private int blackTileCount = 0; // 검은 타일의 갯수를 카운트하여 소리 조각수와 동일한 양의 검은 타일을 만든다.
	private int continuityTiles = 0; // 1박 이상의 타일 일 경우

	class MyThread extends Thread {
		boolean running = false;
		Random ran = new Random();
		private int randomY = ran.nextInt(4);
		boolean isEndTile = false;
		int endTileRow; // 맨 아래 도달한 줄 (x값)
		int endTile; // 맨 아래 도달한 타일(y값)
		boolean firstTile = true; // 처음타일이 뭐든간에 랜덤으로 생성해야하기때문에

		public MyThread() {
			init();
		}

		private void init() { // 좌표를 rect배열 안에 넣기 (2개씩)
			for (int i = 0; i < rect.length; i++) {
				for (int i2 = 0; i2 < rect[i].length; i2++) {

				for (int j3 = 0; j3 < p[0][0].length; j3++) {
					for (int j = 0; j < p.length; j++) { // 좌표p배열안에 왼쪽위, 오른쪽아래 값을 넣기
						for (int j2 = 0; j2 < p[j].length; j2++) {	// (j+j3-1) 가로는 같지만 세로는 위 아래로 1줄씩 더 있으므로 처음시작은 ((1/4)*fullHeight) 부터 시작해야 하므로 이렇게 설정
							p[j][j2][j3] = new Point( (j2+j3)*fullWidth/p[j].length, (j+j3-1)*fullHeight/p[j].length );
						}
					}
				} // end of for p

					rect[i][i2] = new Rect(p[i][i2][0].x, p[i][i2][0].y, p[i][i2][1].x, p[i][i2][1].y);
					paint[i][i2] = new Paint();
					paint[i][i2].setColor(Color.WHITE);
				}
			} // end of for rect

		}

		@Override
		public void run() {

			while(running) { // 게임 동작 구현
				Canvas canvas = null;
				try {
					canvas = holder.lockCanvas(null);
					synchronized (holder) {

						// 배경색
						Paint pBack = new Paint();
						pBack.setColor(Color.WHITE);
						canvas.drawPaint(pBack);

						// 타일 이동
						scrollTiles();
						// 타일 색
						for (int j = 0; j < p.length; j++) {
							for (int j2 = 0; j2 < p[j].length; j2++) {
								canvas.drawRect(rect[j][j2], paint[j][j2]);
							}
						}

						// 랜덤으로 생성되는 위의 타일 색
						if(isEndTile) {
							int nextColorTile;
							while(true) { // 위 아래 연속적으로 타일이 나오지 못하게 처리
								if(endTileRow + 1 >= rect.length) {
									nextColorTile = 0;
								}else {
									nextColorTile = endTileRow + 1;
								}
								if(paint[nextColorTile][randomY].getColor() == Color.CYAN && littleStarSound[blackTileCount][1] != 1) {
								}else {
									randomY = ran.nextInt(4);
									firstTile = false;
								}
								if(paint[nextColorTile][randomY].getColor() == Color.CYAN && littleStarSound[blackTileCount][1] != 1) { // 아래 타일이 CYAN색이면서 자기가 CYAN을 가질 수 있는 박자라면 가져!
									break;
								}else if(paint[nextColorTile][randomY].getColor() == Color.BLACK) { // 아래 타일이 검은색이면 다시 반복해 (못나감)
								}else if(paint[nextColorTile][randomY].getColor() == Color.CYAN){ // // 아래 타일이 CYAN색이면 다시 반복해 (못나감)
								}else {
									break;
								}
							}
							if(littleStarSound[blackTileCount][1] == 1) {
								paint[endTileRow][randomY].setColor(Color.BLACK);
								blackTileCount++;// 검은타일의 갯수를 카운트
							}else  {
								paint[endTileRow][randomY].setColor(Color.CYAN);
								continuityTiles++;
								if(continuityTiles == littleStarSound[blackTileCount][1]) {
									blackTileCount++;
									continuityTiles = 0;
								}
							}
							canvas.drawRect(rect[endTileRow][randomY], paint[endTileRow][randomY]);

							isEndTile = false;
						}

					}
				}finally {
					if(canvas != null) {
						holder.unlockCanvasAndPost(canvas);
					}
				}
			} // end of while
		} // end of run

		public void isRun(boolean running) { // Thread 안의 while문 제어
			this.running = running;
		}


		private void scrollTiles() { // rect를 통해 사각형 이동

			for (int j = 0; j < p.length; j++) {
				for (int j2 = 0; j2 < p[j].length; j2++) {
					rect[j][j2].bottom += moveSpeedY;
					rect[j][j2].top += moveSpeedY;
					if(rect[j][j2].top > fullHeight) { // 더한 이유는 빈 공간을 제거하기 위해
						rect[j][j2].bottom = 0 + moveSpeedY;
						rect[j][j2].top = (-1 * fullHeight / 4) + moveSpeedY;
						if (blackTileCount < littleStarSound.length){ // 검은 타일 나온 수가 조각 수 보다 적으면
							isEndTile = true; // 타일 재생성
						}
						endTileRow = j;
						endTile = j2;
						if(paint[endTileRow][endTile].getColor() == Color.BLACK || paint[endTileRow][endTile].getColor() == Color.CYAN) { // 타일을 처리하지 못해 게임 오버
							myThread.isRun(false);
						}
						paint[endTileRow][endTile].setColor(Color.WHITE);
					}
				}
			}

		}

	} // end of MyGameThread

	@Override
	public boolean onTouchEvent(MotionEvent event) {

exit:	switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			for (int j = 0; j < rect.length; j++) {
				for (int j2 = 0; j2 < rect[j].length; j2++) {
					if( rect[j][j2].contains((int)event.getX(), (int)event.getY()) && (paint[j][j2].getColor() == Color.BLACK || paint[j][j2].getColor() == Color.CYAN)) { // 터치한 좌표의 사각형을 가져오기

						int nextTail;
						if(j+1 >= paint.length) { // 다음 타일을 비교할때 다음 타일이 작아질때 (5->0 과 같이)
							nextTail = 0;
						}else {
							nextTail = j+1;
						}
						for (int i = 0; i < paint[0].length; i++) {
							if(paint[nextTail][i].getColor() == Color.BLACK || paint[nextTail][j2].getColor() == Color.CYAN) { // 터치를 순서대로 안하면 그냥 switch문을 나가 누른 처리를 해주지 않음
								break exit;
							}
						}

						soundPlay(); // 소리 조각 재생
						paint[j][j2].setColor(Color.GRAY);
						paint[j][j2].setAlpha(40); // 투명도 설정
						break exit;
					}
				}
			}
			break;
	case MotionEvent.ACTION_MOVE:
		for (int j = 0; j < rect.length; j++) {
			for (int j2 = 0; j2 < rect[j].length; j2++) {
				if( rect[j][j2].contains((int)event.getX(), (int)event.getY()) && paint[j][j2].getColor() == Color.CYAN) { // 터치한 좌표의 사각형을 가져오기

					int nextTail;
					if(j+1 >= paint.length) { // 다음 타일을 비교할때 다음 타일이 작아질때 (5->0 과 같이)
						nextTail = 0;
					}else {
						nextTail = j+1;
					}
					for (int i = 0; i < paint[0].length; i++) {
						if(paint[nextTail][i].getColor() == Color.BLACK || paint[nextTail][j2].getColor() == Color.CYAN) { // 터치를 순서대로 안하면 그냥 switch문을 나가 누른 처리를 해주지 않음
							break exit;
						}
					}

					paint[j][j2].setColor(Color.GRAY);
					paint[j][j2].setAlpha(40); // 투명도 설정
					break exit;
				}
			}
		}
		break;

		default:
			break;
		}

		return true;
	}

	// 음원 파일
	private SoundPool soundPool;
	private int soundCount = 0;

	public void setSoundPieces() { // 소리 조각 저장
		for (int i = 0; i < littleStarSound.length; i ++) {
			soundId[i] = soundPool.load(context, littleStarSound[i][0],0);
		}
	}

	public void soundPlay() { // 소리 재생
		soundPool.play(soundId[soundCount],1.0f,10f,1,0,1.0f);
		soundCount++;
	}


} // end of MyGameView
