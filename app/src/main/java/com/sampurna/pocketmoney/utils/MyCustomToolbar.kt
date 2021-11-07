package com.sampurna.pocketmoney.utils

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.sampurna.pocketmoney.R

class MyCustomToolbar(
        context: Context, attrs: AttributeSet
) : ConstraintLayout(context, attrs) {


    private var title: TextView
    private var subtitle: TextView
    private var logo: ImageView
    private var navIcon: ImageView
    private var menuIcon: ImageView
    private lateinit var mListener: MyCustomToolbarListener
    init {

        inflate(context, R.layout.layout_cutom_toolbar, this)

        navIcon = findViewById<ImageView>(R.id.iv_navigation_icon)
        logo = findViewById<ImageView>(R.id.iv_toolbar_logo)
        menuIcon = findViewById<ImageView>(R.id.iv_toolbar_menu_icon)
        title = findViewById<TextView>(R.id.tv_toolbar_title)
        subtitle = findViewById<TextView>(R.id.tv_toolbar_subtitle)


        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MyCustomToolbar)

        if (attributes.getDrawable(R.styleable.MyCustomToolbar_toolbarNavIcon) != null) {
            navIcon.setImageDrawable(attributes.getDrawable(R.styleable.MyCustomToolbar_toolbarNavIcon))
        }


        if (attributes.getDrawable(R.styleable.MyCustomToolbar_logoImage) != null) {
            logo.setImageDrawable(attributes.getDrawable(R.styleable.MyCustomToolbar_logoImage))
        }

        if (attributes.getDrawable(R.styleable.MyCustomToolbar_toolbarMenuIcon) != null) {
            menuIcon.setImageDrawable(attributes.getDrawable(R.styleable.MyCustomToolbar_toolbarMenuIcon))
        }

        title.text = attributes.getString(R.styleable.MyCustomToolbar_title)
        subtitle.text = attributes.getString(R.styleable.MyCustomToolbar_subtitle)

        val titleSize = attributes.getDimensionPixelSize(R.styleable.MyCustomToolbar_titleTextSize, 0);
        val subtitleSize = attributes.getDimensionPixelSize(R.styleable.MyCustomToolbar_subtitleTextSize, 0);

        if (titleSize > 0) {
            setTextSizeOfView(title, titleSize.toFloat())
        }
        if (subtitleSize > 0) {
            setTextSizeOfView(subtitle, subtitleSize.toFloat())
        }

        setVisibilityOfView(menuIcon, attributes.getBoolean(R.styleable.MyCustomToolbar_menuIconVisibility, false))
        setVisibilityOfView(logo, attributes.getBoolean(R.styleable.MyCustomToolbar_logoVisibility, false))
        setVisibilityOfView(title, attributes.getBoolean(R.styleable.MyCustomToolbar_titleVisibility, true))
        setVisibilityOfView(subtitle, attributes.getBoolean(R.styleable.MyCustomToolbar_subtitleVisibility, false))


        navIcon.setOnClickListener {
            mListener.onToolbarNavClick()
        }

        menuIcon.setOnClickListener {
            mListener.onMenuClick()
        }


        attributes.recycle()


    }

    private fun setVisibilityOfView(v: View, visibility: Boolean) {
        v.visibility = if (visibility) View.VISIBLE else View.GONE
    }

    private fun setTextSizeOfView(v: TextView, size: Float) {
        v.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }


    fun setToolbarTitle(text: String) {
        title.text = text
    }

    fun setToolbarSubtitle(text: String) {
        subtitle.text = text
    }

    fun setToolbarLogo(image: Int) {
        logo.setImageResource(image)
    }

    fun setToolbarNavIcon(image: Int) {
        navIcon.setImageResource(image)
    }

    fun setToolbarLogo(imageUrl: String) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_photo_library)
                .error(R.drawable.ic_error)
                .into(logo)
    }

    fun setCustomToolbarListener(mListener: MyCustomToolbarListener) {
        this.mListener = mListener

    }



    interface MyCustomToolbarListener {
        fun onToolbarNavClick()
        fun onMenuClick()
    }
}