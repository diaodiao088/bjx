package com.bjxapp.worker.ui.view.activity.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bjx.master.R;
import com.bjxapp.worker.App;
import com.bjxapp.worker.ui.widget.DimenUtils;;

/**
 * Created by zi on 15/6/8.
 */
public class CustomLayoutDialog implements DialogInterface {

    public interface OnClickListener {
        void onClick(CustomLayoutDialog dialog, int which);
    }

    public interface ShowingStrategy {
        void show(CustomLayoutDialog dialog, View view);

        // margin means left and right margin
        void showAtPosition(CustomLayoutDialog dialog, View view, int gravity, int offsetX, int offsetY, int margin);

        void showFullScreenLayer(int backgroundColor, View view);

        void dismiss(CustomLayoutDialog dialog);

        boolean isShowing();

        void setOnShowListener(OnShowListener listener);

        void setOnCancelListener(OnCancelListener listener);

        void setOnDismissListener(OnDismissListener listener);

        void setOnKeyListener(OnKeyListener listener);

        void setWindowType(int windowType);

        void setOnClickListener(DialogInterface.OnClickListener listener);

        void setCanceledOnTouchOutside(boolean cancel);

        void setCancelable(boolean cancelable);

        void setOrientation(int orientation);

        Dialog getDialog(View view);
    }

    public static final ShowingStrategy USE_FLOATING_WINDOW = new ShowingStrategy() {
        private OnKeyListener mOnKeyListener;

        @Override
        public void show(final CustomLayoutDialog dialog, View view) {
            showAtPosition(dialog, view, Gravity.CENTER, 0, 0, 0);
        }

        @Override
        public void showAtPosition(final CustomLayoutDialog dialog, View view, int gravity, int x, int y, int margin) {
            view.setFocusableInTouchMode(true);
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                        dismiss(dialog);
                        if (null != mOnKeyListener) {
                            mOnKeyListener.onKey(dialog, keyCode, event);
                        }
                        return true;
                    }
                    return false;
                }
            });
            DialogWindow.getIns().showAtPosition(view, gravity, x, y);
        }

        @Override
        public void showFullScreenLayer(int backgroundColor, View view) {
        }

        @Override
        public void dismiss(CustomLayoutDialog dialog) {
            DialogWindow.getIns().hide(dialog);
        }

        @Override
        public boolean isShowing() {
            return DialogWindow.getIns().isShowing();
        }

        @Override
        public void setOnDismissListener(OnDismissListener listener) {
            DialogWindow.getIns().setOnDismissListener(listener);
        }

        @Override
        public void setOnKeyListener(OnKeyListener listener) {
            mOnKeyListener = listener;
        }

        @Override
        public void setWindowType(int windowType) {
        }

        @Override
        public void setOnClickListener(DialogInterface.OnClickListener listener) {
        }

        @Override
        public void setOnCancelListener(OnCancelListener listener) {
        }

        @Override
        public void setOnShowListener(OnShowListener listener) {
        }

        @Override
        public void setCanceledOnTouchOutside(boolean cancel) {

        }

        @Override
        public void setCancelable(boolean cancelable) {
        }

        @Override
        public void setOrientation(int orientation) {
            DialogWindow.getIns().setOrientation(orientation);
        }

        @Override
        public Dialog getDialog(View view) {
            return null;
        }
    };

    public static class DialogShowingStrategy implements ShowingStrategy {
        private ShowDialog mDialog = null;
        private Context mContext;
        private int mWindowType = -1;
        private OnShowListener mOnShowListener;
        private OnCancelListener mOnCancelListener;
        private OnDismissListener mOnDismissListener;
        private OnKeyListener mOnKeyListener;
        private DialogInterface.OnClickListener mClickListener;
        private boolean mCanceledOnTouchOutside;
        private boolean mCancelable = true;

        public DialogShowingStrategy(Context context) {
            mContext = context;
        }

        @Override
        public void setWindowType(int windowType) {
            mWindowType = windowType;
        }

        @Override
        public void setOnClickListener(DialogInterface.OnClickListener listener) {
            mClickListener = listener;
        }

        @Override
        public void show(CustomLayoutDialog dialog, View view) {
            showAtPosition(dialog, view, Gravity.CENTER, 0, 0, DimenUtils.dp2px(20, App.getInstance()));
        }

        protected ShowDialog createDialog(Context context, int theme, View view, boolean isDismiss) {
            return new ShowDialog(context, theme, view, isDismiss);
        }

        @Override
        public void showAtPosition(CustomLayoutDialog dialog, View view, int gravity, int offsetX, int offsetY, int margin) {
            if (mDialog == null) {
                mDialog = createDialog(mContext, R.style.dialog, view, true);
            }
            mDialog.setDialogMargin(margin);
            mDialog.setPosition(gravity, offsetX, offsetY);

            if (-1 != mWindowType) {
                try {
                    mDialog.setWindowType(mWindowType);
                } catch (IllegalArgumentException e) {
                    return;
                }
            }

            if (mOnDismissListener != null) {
                mDialog.setOnDismissListener(mOnDismissListener);
            }
            if (mOnShowListener != null) {
                mDialog.setOnShowListener(mOnShowListener);
            }
            if (mOnCancelListener != null) {
                mDialog.setOnCancelListener(mOnCancelListener);
            }
            if (null != mOnKeyListener) {
                mDialog.setOnKeyListener(mOnKeyListener);
            }
            mDialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
            mDialog.setCancelable(mCancelable);
            try {
                if (mDialog != null) {
                    if (!isActivityFinishing()) {
                        mDialog.show();
                    }
                }
            } catch (final IllegalArgumentException pE) {
                pE.printStackTrace();
            }
        }

        @Override
        public void showFullScreenLayer(int backgroundColor, View view) {
            if (mDialog == null) {
                mDialog = createDialog(mContext, R.style.dialog, view, true);
            }

            mDialog.setFullScreenLayer(backgroundColor);
        }

        protected boolean isActivityFinishing() {
            if (mContext instanceof Activity) {
                Activity activity = (Activity) mContext;
                return activity.isFinishing();
            }

            return false;
        }

        @Override
        public void dismiss(CustomLayoutDialog dialog) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
        }

        @Override
        public boolean isShowing() {
            if (mDialog == null) {
                return false;
            }
            return mDialog.isShowing();
        }

        @Override
        public void setOnCancelListener(OnCancelListener listener) {
            if (mDialog != null) mDialog.setOnCancelListener(listener);
            mOnCancelListener = listener;
        }

        @Override
        public void setOnShowListener(OnShowListener listener) {
            if (mDialog != null) mDialog.setOnShowListener(listener);
            mOnShowListener = listener;
        }

        @Override
        public void setOnDismissListener(OnDismissListener listener) {
            mOnDismissListener = listener;
            if (null != mDialog) mDialog.setOnDismissListener(listener);
        }

        @Override
        public void setOnKeyListener(OnKeyListener listener) {
            if (mDialog != null) mDialog.setOnKeyListener(listener);
            mOnKeyListener = listener;
        }

        @Override
        public void setCanceledOnTouchOutside(boolean cancel) {
            mCanceledOnTouchOutside = cancel;
        }

        @Override
        public void setCancelable(boolean cancelable) {
            mCancelable = cancelable;
        }

        @Override
        public void setOrientation(int orientation) {
        }

        @Override
        public Dialog getDialog(View view) {
            if (mDialog == null) {
                mDialog = createDialog(mContext, R.style.dialog, view, true);
            }

            return mDialog;
        }
    }

    private Context mContext;
    private View mView;
    private ShowingStrategy mStrategy;

    public CustomLayoutDialog(Context context, int layoutId) {
        this(context, layoutId, new DialogShowingStrategy(context));
    }

    public CustomLayoutDialog(Context context, int layoutId, ShowingStrategy strategy) {
        mContext = context;
        mStrategy = strategy;
        initView(layoutId);
    }

    private void initView(int layoutId) {
        try {
            mView = LayoutInflater.from(mContext).inflate(layoutId, null);
        } catch (InflateException ife) {

        }
        if (mView == null) {

        }
    }

    public View getView() {
        return mView;
    }

    public View getViewById(int id) {
        if (mView == null) {
            return null;
        }
        return mView.findViewById(id);
    }

    public TextView getTextViewById(int id) {
        return (TextView) getViewById(id);
    }

    public void setOrientation(int orientation) {
        mStrategy.setOrientation(orientation);
    }

    public void show() {
        if (isActivityFinishing()) {
            return;
        }
        mStrategy.show(this, mView);
    }

    protected boolean isActivityFinishing() {
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            return activity.isFinishing();
        }

        return false;
    }

    public void showAtPosition(int gravity, int offsetX, int offsetY, int margin) {
        mStrategy.showAtPosition(this, mView, gravity, offsetX, offsetY, margin);
    }

    public boolean isShowing() {
        return mStrategy.isShowing();
    }

    @Override
    public void cancel() {
        if (mStrategy.isShowing()) {
            mStrategy.dismiss(this);
        }
    }

    @Override
    public void dismiss() {
        mStrategy.dismiss(this);
    }

    public void setOnDismissListener(OnDismissListener listener) {
        mStrategy.setOnDismissListener(listener);
    }

    public void setOnKeyListener(OnKeyListener listener) {
        mStrategy.setOnKeyListener(listener);
    }

    public void setOnCancelListener(OnCancelListener listener) {
        mStrategy.setOnCancelListener(listener);
    }

    public void setOnShowListener(OnShowListener listener) {
        mStrategy.setOnShowListener(listener);
    }

    public void setWindowType(int windowType) {
        mStrategy.setWindowType(windowType);
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        mStrategy.setCanceledOnTouchOutside(cancel);
    }

    public void setCancelable(boolean cancelable) {
        mStrategy.setCancelable(cancelable);
    }

    public Window getWindow() {
        Dialog dig = getDialog();
        if (dig != null) {
            return dig.getWindow();
        }
        return null;
    }

    public void showFullScreenLayer(int backgroundColor) {
        mStrategy.showFullScreenLayer(backgroundColor, mView);
    }

    public Dialog getDialog() {
        return mStrategy.getDialog(mView);
    }
}
