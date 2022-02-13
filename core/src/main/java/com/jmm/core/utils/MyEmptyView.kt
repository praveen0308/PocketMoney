package com.jmm.core.utils

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.jmm.core.R
import com.jmm.core.databinding.TemplateEmptyViewBinding

class MyEmptyView @kotlin.jvm.JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var binding = TemplateEmptyViewBinding.inflate(LayoutInflater.from(context))
    init {
        addView(binding.root)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MyEmptyView)

        if (attributes.getDrawable(R.styleable.MyEmptyView_MEVImage) != null) {
            binding.imageView.setImageDrawable(attributes.getDrawable(R.styleable.MyEmptyView_MEVImage))
        }


        binding.title.text = attributes.getString(R.styleable.MyEmptyView_MEVTitle)

        val titleSize = attributes.getDimensionPixelSize(R.styleable.MyEmptyView_MEVTitleTextSize, 0);


        if (titleSize > 0) {
            setTextSizeOfView(binding.title, titleSize.toFloat())
        }
        attributes.recycle()
    }

    private fun setTextSizeOfView(v: TextView, size: Float) {
        v.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    fun setMessage(msg:String) {
        binding.title.text = msg
    }
}