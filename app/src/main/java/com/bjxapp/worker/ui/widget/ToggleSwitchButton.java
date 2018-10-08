package com.bjxapp.worker.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bjxapp.worker.R;

public class ToggleSwitchButton extends LinearLayout implements View.OnClickListener {

    View button, slot;
    boolean isChecked;
    boolean enabled = true;
    private TextView mTextTv;

    public interface OnCheckedChangeListener {
        void onCheckedChanged(View view, boolean isChecked);
    }

    OnCheckedChangeListener checkedChangeListener;

    public ToggleSwitchButton(Context context) {
        this(context, null);
    }

    public ToggleSwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleSwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        /*if (attrs != null) {
            boolean isChecked = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/android", "checked", false);
            setChecked(isChecked, false);
        }*/
        setChecked(true , false);
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.toggle_switch_button, this);
        button = findViewById(R.id.switch_check_button);
        slot = findViewById(R.id.switch_check_slot);
        mTextTv = findViewById(R.id.switch_status_tv);
        this.setOnClickListener(this);
    }

    private void setChecked(boolean isChecked, boolean enableCallback) {
        if (!enabled) {
            return;
        }
        synchronized (this) {
            if (isChecked) {
                button.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.toggle_switch_btn_on_default));
                slot.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.toggle_switch_slot_on_default));
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) button.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                button.setLayoutParams(params);

                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) mTextTv.getLayoutParams();
                params1.setMargins(DimenUtils.dp2px(6 , getContext()), 0, 0, 0);
                mTextTv.setLayoutParams(params1);
                mTextTv.setText("接单");

            } else {
                button.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.toggle_switch_btn_off));
                slot.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.toggle_switch_slot_off));
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) button.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                button.setLayoutParams(params);

                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) mTextTv.getLayoutParams();
                params1.setMargins(DimenUtils.dp2px(20, getContext()), 0, 0, 0);
                mTextTv.setLayoutParams(params1);
                mTextTv.setText("不接单");
            }

            if (!enabled) {
                button.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.toggle_switch_btn_off));
                slot.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.toggle_switch_slot_off));
            }
            if ((this.isChecked != isChecked) && (enableCallback)) {
                if (checkedChangeListener != null) {
                    checkedChangeListener.onCheckedChanged(this, isChecked);
                }
            }

            this.isChecked = isChecked;
        }
    }

    public void setChecked(boolean isChecked) {
        setChecked(isChecked, true);
    }

    public void setCheckedWithoutCallback(boolean isChecked) {
        setChecked(isChecked, false);
    }

    public boolean isChecked() {
        synchronized (this) {
            return this.isChecked;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean e) {
        enabled = e;
        setChecked(this.isChecked);

        // handle disabled state
        if (!enabled) {

            button.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.toggle_switch_btn_off));
            slot.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.toggle_switch_slot_off));
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        checkedChangeListener = listener;
    }

    public interface OnToggleSwitchListener {
        void onClick(boolean isChecked);
    }

    public OnToggleSwitchListener mToggleSwitchListener;

    public void setToggleSwitchListener(OnToggleSwitchListener listener) {
        this.mToggleSwitchListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (!enabled) {
            return;
        }

        if (mToggleSwitchListener != null) {
            mToggleSwitchListener.onClick(isChecked);
        } else {
            setChecked(!isChecked);
        }
    }
}
