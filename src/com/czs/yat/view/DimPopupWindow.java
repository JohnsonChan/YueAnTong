package com.czs.yat.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.czs.yat.R;

/**
 * 自定义的popupwindow 可以实现背景变暗的行为
 * 
 * @author asus
 * 
 */
public class DimPopupWindow implements Runnable {
    public interface WindwoDismissListener {
        void onWindowDismiss(DimPopupWindow window);
    }

    private PopupViewContainer container;
    private WindowManager mWindowManager;
    protected Context context;
    private float dimAmount;
    private int windowAnimation;
    private int gravity;
    private WindwoDismissListener listener;
    private boolean backDismiss;
    private boolean outsideDissmiss;
    private int windowHeight = WindowManager.LayoutParams.WRAP_CONTENT;
    private int windowWidth = WindowManager.LayoutParams.FILL_PARENT;
    private Handler handler;
    private Activity activityParent;

    public DimPopupWindow(Context context) {
        this(context, 0.5f, R.style.popwin_animation, Gravity.LEFT
                | Gravity.BOTTOM);
    }

    public DimPopupWindow(Context context, float dimAmount) {
        this(context, dimAmount, R.style.popwin_animation, Gravity.LEFT
                | Gravity.BOTTOM);
    }

    public DimPopupWindow(Context context, int windowAnimation, int gravity) {
        this(context, 0.5f, windowAnimation, gravity);
    }

    public DimPopupWindow(Context context, float dimAmount,
            int windowAnimation, int gravity) {
        super();
        backDismiss = true;
        outsideDissmiss = true;
        mWindowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        container = new PopupViewContainer(context);
        this.context = context;
        this.dimAmount = dimAmount;
        this.windowAnimation = windowAnimation;
        this.gravity = gravity;
        handler = new Handler();
    }

    public void setBackDismiss(boolean backDismiss) {
        this.backDismiss = backDismiss;
    }

    public void setListener(WindwoDismissListener listener) {
        this.listener = listener;
    }

    public void setOutsideDissmiss(boolean outsideDissmiss) {
        this.outsideDissmiss = outsideDissmiss;
    }

    public void setContentView(View contentView) {
        container.removeAllViews();
        ViewGroup.LayoutParams lp = contentView.getLayoutParams();
        if (lp == null) {
            lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.FILL_PARENT,
                    FrameLayout.LayoutParams.FILL_PARENT, Gravity.CENTER);
        }
        container.addView(contentView, lp);
    }

    public void dismiss() {
        handler.removeCallbacks(this);
        if (container != null && container.getParent() != null) {
            this.mWindowManager.removeView(container);
        }
        if (listener != null) {

            listener.onWindowDismiss(this);
        }
    }

    public void setWindowAnimation(int windowAnimation) {
        this.windowAnimation = windowAnimation;
    }

    @Override
    public void run() {
        show(activityParent);
    }

    public void show(Activity parent) {
        try {
            if (parent.isFinishing()) {
                return;
            }
            activityParent = parent;
            View view = parent.findViewById(Window.ID_ANDROID_CONTENT);
            IBinder token = view.getWindowToken();
            if (token == null) {
                handler.post(this);
                return;
            }

            InputMethodManager imm = (InputMethodManager) parent
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(token, 0);

            WindowManager.LayoutParams layoutParams = createLayout(token, 0, 0);
            if (container.getParent() != null) {
                this.mWindowManager.removeView(container);
            }

            this.mWindowManager.addView(container, layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getDimAmount() {
        return dimAmount;
    }

    public void setDimAmount(float dimAmount) {
        this.dimAmount = dimAmount;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    private WindowManager.LayoutParams createLayout(IBinder token, int x, int y) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = gravity;

        int k = layoutParams.flags;
        int m = computeFlags(k);
        layoutParams.flags = m;
        layoutParams.x = x;
        layoutParams.y = y;
        layoutParams.dimAmount = dimAmount;

        layoutParams.width = windowWidth;
        layoutParams.height = windowHeight;
        layoutParams.format = android.graphics.PixelFormat.TRANSLUCENT;
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        layoutParams.token = token;
        layoutParams.windowAnimations = windowAnimation;
        return layoutParams;
    }

    private int computeFlags(int curFlags) {
        curFlags &= ~(WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        curFlags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        curFlags |= WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES;
        curFlags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        // curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        return curFlags;
    }

    protected class PopupViewContainer extends FrameLayout {

        public PopupViewContainer(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            DispatcherState state = getKeyDispatcherState();
            if (state == null) {
                return super.dispatchKeyEvent(event);
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getRepeatCount() == 0) {
                    state.startTracking(event, this);
                    return true;
                } else if (event.getAction() == KeyEvent.ACTION_UP
                        && state.isTracking(event) && !event.isCanceled()) {
                    if (backDismiss) {
                        dismiss();
                    }
                    return true;
                }
                return super.dispatchKeyEvent(event);
            } else {
                return super.dispatchKeyEvent(event);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();

            if ((event.getAction() == MotionEvent.ACTION_DOWN)
                    && ((x < 0) || (x >= getWidth()) || (y < 0) || (y >= getHeight()))) {
                if (outsideDissmiss) {

                    dismiss();
                }
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                if (outsideDissmiss) {

                    dismiss();
                }
                return true;
            } else {
                return super.onTouchEvent(event);
            }
        }

        @Override
        public void sendAccessibilityEvent(int eventType) {
            // clinets are interested in the content not the container, make it
            // event source
            View contentView = this.getChildAt(0);
            if (contentView != null) {
                contentView.sendAccessibilityEvent(eventType);
            } else {
                super.sendAccessibilityEvent(eventType);
            }
        }
    }

}
