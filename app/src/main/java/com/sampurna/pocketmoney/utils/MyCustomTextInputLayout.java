package com.sampurna.pocketmoney.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.sampurna.pocketmoney.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MyCustomTextInputLayout extends ConstraintLayout {

    View bottomBorder;
    TextInputEditText inputEditText;
    TextInputLayout textInputLayout;
    public MyCustomTextInputLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.template_edit_text_with_back_arrow,this);

        bottomBorder = findViewById(R.id.bottom_border);
        inputEditText = findViewById(R.id.input_edit_text);
        textInputLayout = findViewById(R.id.text_input_layout);

        inputEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {bottomBorder.setBackground(null);
                bottomBorder.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));}
            }
        });


    }
}
