package com.sampurna.pocketmoney.mlm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.mlm.model.ModelOnBoardingItem

class OnBoardingAdapter(private val onBoardingItems: List<ModelOnBoardingItem>) :
    RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        return OnBoardingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.template_onboarding_screen, parent, false)
        )
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        holder.bind(onBoardingItems[position])
    }

    override fun getItemCount(): Int {
        return onBoardingItems.size
    }


    inner class OnBoardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imageOnBoarding = view.findViewById<ImageView>(R.id.img_onboarding)
        private val tvTitle = view.findViewById<TextView>(R.id.title_onboarding)
        private val tvSubtitle = view.findViewById<TextView>(R.id.subtitle_onboarding)

        fun bind(item: ModelOnBoardingItem) {
            imageOnBoarding.setImageResource(item.imageUrl)
            tvTitle.text = item.title
            tvSubtitle.text = item.description
        }
    }
}