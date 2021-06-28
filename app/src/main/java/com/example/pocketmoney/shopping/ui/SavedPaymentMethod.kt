package com.example.pocketmoney.shopping.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentMyAccountHomeBinding
import com.example.pocketmoney.databinding.FragmentSavedPaymentMethodBinding
import com.example.pocketmoney.shopping.adapters.CustomMenuAdapter
import com.example.pocketmoney.shopping.adapters.SavedPaymentCardAdapter
import com.example.pocketmoney.shopping.model.ModelPaymentCard


class SavedPaymentMethod : Fragment() {

    // Ui
    private var _binding: FragmentSavedPaymentMethodBinding? = null
    private val binding get() = _binding!!

    //Adapter
    private lateinit var savedPaymentCardAdapter: SavedPaymentCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSavedPaymentMethodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvPaymentCardList()

        binding.fabAddNewCard.setOnClickListener {
            findNavController().navigate(R.id.action_savedPaymentMethod_to_addPaymentCard)
        }
    }

    fun generatePaymentCardList():MutableList<ModelPaymentCard>{
        val cardList = ArrayList<ModelPaymentCard>()

        cardList.add(ModelPaymentCard(getString(R.string.dummy_user_name),"1234567890123412",8,22))
        cardList.add(ModelPaymentCard(getString(R.string.dummy_user_name),"1234567890123412",8,22))

        return cardList

    }

    fun setupRvPaymentCardList(){
        savedPaymentCardAdapter = SavedPaymentCardAdapter()
        binding.apply {
            rvSavedCardList.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                savedPaymentCardAdapter.setCardList(generatePaymentCardList())
                adapter = savedPaymentCardAdapter
            }
        }
    }
}