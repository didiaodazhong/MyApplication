package com.peixing.myapplication.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.peixing.myapplication.R;


/**
 *
 */
public class LoginEditText extends android.support.v7.widget.AppCompatEditText {

    Drawable drawable;

    public LoginEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        boolean useDark = false;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Crossed, 0, 0);
        try {
            useDark = a.getBoolean(R.styleable.Crossed_dark, false);
        } finally {
            a.recycle();
        }
        int crossedRes = useDark ? R.mipmap.delete_edit_login_black : R.mipmap.delete_edit_login_black;
        drawable = getResources().getDrawable(crossedRes);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 3, drawable.getIntrinsicHeight() / 3);
        addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                displayDelete(s.length() > 0);
            }
        });
    }

    private void displayDelete(boolean show) {
        if (show) {
            setDrawableRight(drawable);
        } else {
            setDrawableRight(null);
        }
    }

    private void setDrawableRight(Drawable drawable) {
        setCompoundDrawables(null, null, drawable, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable) {
                    this.setText("");
                }
            }
        }

        return super.onTouchEvent(event);
    }
}