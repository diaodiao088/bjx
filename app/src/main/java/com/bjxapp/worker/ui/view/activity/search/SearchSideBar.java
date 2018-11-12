package com.bjxapp.worker.ui.view.activity.search;

import com.bjx.master.R;;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SearchSideBar extends View {
	// declare touch event 
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	// 26 english letters
	public static String[] letters = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z", "#" };
	private int choose = -1;
	private Paint paint = new Paint();

	private TextView mTextDialog;

    /** 
     * 为SideBar设置显示字母的TextView 
     * @param mTextDialog 
     */
	public void setTextView(TextView mTextDialog) {
		this.mTextDialog = mTextDialog;
	}
	
	public void setLetters(String[] inLetters){
		letters = inLetters;
		this.refreshDrawableState();
	}

	public SearchSideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SearchSideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SearchSideBar(Context context) {
		super(context);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//获取每一个字母的高度
		int height = getHeight();
		int width = getWidth();
		int singleHeight = height / letters.length;

		for (int i = 0; i < letters.length; i++) {
			paint.setColor(Color.DKGRAY);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			
			paint.setTextSize(24);
			
			//选中
			if (i == choose) {
				paint.setColor(Color.BLUE);
				paint.setFakeBoldText(true);
			}
			
			//x坐标等于中间-字符串宽度的一半
			float xPos = width / 2 - paint.measureText(letters[i]) / 2;
			float yPos = singleHeight * i + singleHeight / 2;
			canvas.drawText(letters[i], xPos, yPos, paint);
			paint.reset();
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		//获取点击的Y坐标
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		//点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数
		final int c = (int) (y / getHeight() * letters.length);

		switch (action) {
		case MotionEvent.ACTION_UP:
			setBackgroundDrawable(new ColorDrawable(0x00000000));
			choose = -1;
			invalidate();
			if (mTextDialog != null) {
				mTextDialog.setVisibility(View.INVISIBLE);
			}
			break;

		default:
			setBackgroundResource(R.drawable.search_side_bar_bg);
			if (oldChoose != c) {
				if (c >= 0 && c < letters.length) {
					if (listener != null) {
						listener.onTouchingLetterChanged(letters[c]);
					}
					if (mTextDialog != null) {
						mTextDialog.setText(letters[c]);
						mTextDialog.setVisibility(View.VISIBLE);
					}
					
					choose = c;
					invalidate();
				}
			}

			break;
		}
		return true;
	}

	/**
	 * 外部调用接口
	 * @param onTouchingLetterChangedListener
	 */
	public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}