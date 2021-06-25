package com.example.pocketmoney.utils.MyCustomNavigationDrawer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.databinding.TemplateUserNavigationDrawerBinding
import com.example.pocketmoney.utils.myEnums.NavigationEnum

class MyNavigationDrawer @kotlin.jvm.JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), MyNavigationChildAdapter.MyNavigationChildInterface, MyNavigationParentAdapter.MyNavigationParentAdapterInterface {
    private var binding = TemplateUserNavigationDrawerBinding.inflate(LayoutInflater.from(context))
    private lateinit var myNavigationParentAdapter: MyNavigationParentAdapter
    private var myNavigationDrawerInterface: MyNavigationDrawerInterface? = null

    init {
        addView(binding.root)
        setupRecyclerview()

    }

    private fun setupRecyclerview() {
        myNavigationParentAdapter = MyNavigationParentAdapter(this,this)
        binding.apply {
            rvNavigationItems.apply {
                setHasFixedSize(true)
                val layoutManager = LinearLayoutManager(context)
                val dividerItemDecoration = DividerItemDecoration(context,
                        layoutManager.orientation)
                addItemDecoration(dividerItemDecoration)
                setLayoutManager(layoutManager)

                adapter = myNavigationParentAdapter
            }
        }
    }

    fun setNavigationItemList(itemList: List<ModelItem>) {
        myNavigationParentAdapter.setNavigationItemList(itemList)
    }

    fun setNavigationListener(myNavigationDrawerInterface: MyNavigationDrawerInterface) {
        this.myNavigationDrawerInterface = myNavigationDrawerInterface
    }

    override fun onSubItemClick(action: NavigationEnum) {
        if (myNavigationDrawerInterface != null) {
            myNavigationDrawerInterface!!.onNavigationItemClick(action)
        }
    }


    interface MyNavigationDrawerInterface {
        fun onNavigationItemClick(action: NavigationEnum)
    }

    override fun onParentNavItemClick(action: NavigationEnum) {
        if (myNavigationDrawerInterface != null) {
            myNavigationDrawerInterface!!.onNavigationItemClick(action)
        }
    }
}