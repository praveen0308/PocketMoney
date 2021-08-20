package com.example.pocketmoney.utils

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.pocketmoney.R

class ApplicationToolbar(
        context: Context, attrs: AttributeSet
) : FrameLayout(context, attrs) {

    // UI
    private var title: TextView
    private var tvMenuBadge: TextView
    private var navIcon: ImageView
    private var menuIcon: ImageView
    private var viewMenu: View


    // Variables
    private var badgeCount = 0

    private lateinit var mListener: ApplicationToolbarListener

    init {

        inflate(context, R.layout.toolbar_application_display, this)

        navIcon = findViewById<ImageView>(R.id.iv_navigation_icon)

        title = findViewById<TextView>(R.id.tv_toolbar_title)

        viewMenu = findViewById(R.id.layout_menu)

        menuIcon = viewMenu.findViewById(R.id.iv_toolbar_menu_icon)
        tvMenuBadge = viewMenu.findViewById(R.id.tv_cart_badge)


        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ApplicationToolbar)

        if (attributes.getDrawable(R.styleable.ApplicationToolbar_ATNavIcon) != null) {
            navIcon.setImageDrawable(attributes.getDrawable(R.styleable.ApplicationToolbar_ATNavIcon))
        }

        if (attributes.getDrawable(R.styleable.ApplicationToolbar_ATMenuIcon) != null) {
            menuIcon.setImageDrawable(attributes.getDrawable(R.styleable.ApplicationToolbar_ATMenuIcon))
        }

        when(attributes.getInt(R.styleable.ApplicationToolbar_ATTitleGravity,-1)){
            -1 -> title.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            1 -> title.gravity = Gravity.END or Gravity.CENTER_VERTICAL
            0 -> title.gravity = Gravity.CENTER
        }



        title.text = attributes.getString(R.styleable.ApplicationToolbar_ATTitle)

        val titleSize = attributes.getDimensionPixelSize(R.styleable.ApplicationToolbar_ATTitleTextSize, 0);


        if (titleSize > 0) {
            setTextSizeOfView(title, titleSize.toFloat())
        }

        setVisibilityOfView(viewMenu, attributes.getBoolean(R.styleable.ApplicationToolbar_ATMenuIconVisibility, false))
        setVisibilityOfView(title, attributes.getBoolean(R.styleable.ApplicationToolbar_ATTitleVisibility, true))


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

    private fun changeVisibilityOfBadge(){
        tvMenuBadge.visibility = if (badgeCount==0) View.GONE else View.VISIBLE
    }
    private fun setTextSizeOfView(v: TextView, size: Float) {
        v.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    fun setApplicationToolbarListener(applicationToolbarListener: ApplicationToolbarListener) {
        mListener = applicationToolbarListener

    }

    fun setToolbarTitle(text: String) {
        title.text = text
    }

    fun setToolbarNavIcon(image: Int) {
        navIcon.setImageResource(image)
    }

    fun setMenuBadgeCount(count:Int){
        badgeCount = count
        tvMenuBadge.text = count.toString()
        changeVisibilityOfBadge()
    }


    interface ApplicationToolbarListener {
        fun onToolbarNavClick()
        fun onMenuClick()
    }
}