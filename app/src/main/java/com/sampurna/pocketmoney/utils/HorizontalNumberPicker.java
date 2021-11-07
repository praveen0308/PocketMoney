package com.sampurna.pocketmoney.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sampurna.pocketmoney.R;


public class HorizontalNumberPicker extends LinearLayout {
    private TextView tv_number;
    private int min, max;
    private HorizontalNumberPickerListener mListener;

    public HorizontalNumberPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.number_picker_horizontal, this);

        tv_number = findViewById(R.id.tv_number);

        final ImageButton btn_less = findViewById(R.id.btn_less);

        btn_less.setOnClickListener(v -> {
            int newValue = getValue() - 1;
            if (newValue < min) {
                newValue = min;
            } else if (newValue > max) {
                newValue = max;
            }
            tv_number.setText(String.valueOf(newValue));
            if (mListener!=null){
                mListener.onMinusClick();
            }
        });

        final ImageButton btn_more = findViewById(R.id.btn_more);

        btn_more.setOnClickListener(v -> {
            int newValue = getValue() + 1;
            if (newValue < min) {
                newValue = min;
            } else if (newValue > max) {
                newValue = max;
            }
            tv_number.setText(String.valueOf(newValue));
            if (mListener!=null){
                mListener.onAddClick();
            }
        });

    }

    /***
     * HANDLERS
     **/

    public void setHorizontalNumberPickerListener(HorizontalNumberPickerListener horizontalNumberPickerListener){
        mListener = horizontalNumberPickerListener;
    }

//    private class AddHandler implements OnClickListener, OnLongClickListener {
//        final int diff;
//
//        public AddHandler(int diff) {
//            this.diff = diff;
//        }
//
//        @Override
//        public void onClick(View v) {
//            int newValue = getValue() + diff;
//            if (newValue < min) {
//                newValue = min;
//            } else if (newValue > max) {
//                newValue = max;
//            }
//            tv_number.setText(String.valueOf(newValue));
//            if (mListener!=null){
//                mListener.onAddClick();
//            }
//        }
//
//        @Override
//        public boolean onLongClick(View view) {
//            int newValue = getValue() + diff;
//            if (newValue < min) {
//                newValue = min;
//            } else if (newValue > max) {
//                newValue = max;
//            }
//            tv_number.setText(String.valueOf(newValue));
//            return false;
//        }
//    }

    /***
     * GETTERS & SETTERS
     */

    public int getValue() {
        if (tv_number != null) {
            try {
                final String value = tv_number.getText().toString();
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                Log.e("HorizontalNumberPicker", ex.toString());
                return 0;
            }
        }
        else return 0;

    }

    public void setValue(final int value) {
        if (tv_number != null) {
            tv_number.setText(String.valueOf(value));
        }
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }


    public interface HorizontalNumberPickerListener{
        public void onAddClick();
        public void onMinusClick();
    }
}