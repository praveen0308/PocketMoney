package com.example.pocketmoney.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.LayoutLoadingButtonBinding

class LoadingButton @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr){

    private var binding = LayoutLoadingButtonBinding.inflate(LayoutInflater.from(context))

    init {
        addView(binding.root)
        loadAttributes(attrs, defStyleAttr)
    }

    private fun loadAttributes(attrs: AttributeSet?,defStyleAttr: Int){
        val attr = context.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            defStyleAttr,
            0
        )

        val btnText = attr.getString(R.styleable.LoadingButton_android_text)

        binding.btnAction.text = btnText

        attr.recycle()
    }

    fun setButtonClick(onClickListener: OnClickListener){
        binding.btnAction.setOnClickListener(onClickListener)
    }

    fun setState(state:LoadingStates,mText:String="",msg:String=""){
        when(state){
            LoadingStates.DISABLED->{
                binding.apply {
                    btnAction.isEnabled = false
                    btnAction.setBackgroundColor(ContextCompat.getColor(context,R.color.Silver))
                    btnAction.setCompoundDrawables(null,null,null,null)
                    btnAction.text = mText
                    progressStatus.isVisible = false
                    tvStatus.text = msg
                }
            }
            LoadingStates.NORMAL->{
                binding.apply {
                    btnAction.isEnabled = true
                    btnAction.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary))
                    btnAction.setCompoundDrawables(null,null,null,null)
                    btnAction.text = mText
                    progressStatus.isVisible = false
                    tvStatus.text = msg
                }
            }
            LoadingStates.LOADING->{
                binding.apply {
                    btnAction.isEnabled = false
                    btnAction.setCompoundDrawables(null,null,null,null)
                    btnAction.text = mText
                    progressStatus.isVisible = true
                    tvStatus.isVisible = true
                    tvStatus.text = msg
                }
            }
            LoadingStates.FAILED->{
                binding.apply {
                    btnAction.setBackgroundColor(ContextCompat.getColor(context,R.color.CreamyRed))
                    btnAction.setCompoundDrawables(ContextCompat.getDrawable(context,R.drawable.ic_error),null,null,null)
                    btnAction.text = mText
                    progressStatus.isVisible = false
                    tvStatus.isVisible = false
                    tvStatus.text = msg
                }
            }
            LoadingStates.SUCCESS->{
                binding.apply {
                    btnAction.setBackgroundColor(ContextCompat.getColor(context,R.color.Green))
                    btnAction.setCompoundDrawables(ContextCompat.getDrawable(context,R.drawable.ic_round_check_24),null,null,null)
                    btnAction.text = mText
                    progressStatus.isVisible = false
                    tvStatus.isVisible = false
                    tvStatus.text = msg
                }
            }
            LoadingStates.RETRY->{
                binding.apply {
                    btnAction.isEnabled= true
                btnAction.setBackgroundColor(ContextCompat.getColor(context,R.color.Orange))
                btnAction.setCompoundDrawables(ContextCompat.getDrawable(context, R.drawable.ic_baseline_replay_24),null,null,null)
                btnAction.text = mText
                progressStatus.isVisible = false
                tvStatus.isVisible = false
                tvStatus.text = msg
            }}
        }
    }

    enum class LoadingStates{
        LOADING,NORMAL,FAILED,SUCCESS,RETRY,DISABLED
    }
    interface onButtonClick{
        fun onClick()
    }
}