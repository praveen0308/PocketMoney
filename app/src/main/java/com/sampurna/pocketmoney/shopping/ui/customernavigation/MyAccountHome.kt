package com.sampurna.pocketmoney.shopping.ui.customernavigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.FragmentMyAccountHomeBinding
import com.sampurna.pocketmoney.shopping.adapters.CustomMenuAdapter
import com.sampurna.pocketmoney.shopping.model.ModelCustomMenuItem
import com.sampurna.pocketmoney.shopping.ui.checkoutorder.AddressArgs
import com.sampurna.pocketmoney.utils.myEnums.MenuEnum
import com.sampurna.pocketmoney.utils.myEnums.ShoppingEnum

class MyAccountHome : Fragment(), CustomMenuAdapter.CustomMenuListener {

    // Ui
    private var _binding: FragmentMyAccountHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController:NavController
    //Adapter
    private lateinit var customMenuAdapter:CustomMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMyAccountHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        customMenuAdapter = CustomMenuAdapter(this)

        binding.apply {
            rvMyAccountMenuList.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                customMenuAdapter.setMenuList(generateMyAccountMenuItems())
                adapter = customMenuAdapter
            }
        }

    }

    private fun generateMyAccountMenuItems():MutableList<ModelCustomMenuItem>{
        val menuItemList = ArrayList<ModelCustomMenuItem>()

        menuItemList.add(ModelCustomMenuItem("My Orders","having 0 orders",MenuEnum.MY_ORDERS))
        menuItemList.add(ModelCustomMenuItem("My Addresses","having 0 saved address",MenuEnum.MY_ADDRESSES))
//        menuItemList.add(ModelCustomMenuItem("Payment Methods","No methods yet",MenuEnum.PAYMENT_METHODS))
        menuItemList.add(ModelCustomMenuItem("My Reviews","Reviewed 0 products",MenuEnum.MY_REVIEWS))
        menuItemList.add(ModelCustomMenuItem("Settings","Notifications,passwords",MenuEnum.SETTING))
        return menuItemList
    }

    override fun onMenuItemClick(action: MenuEnum) {
        when(action){
            MenuEnum.MY_ADDRESSES->{
            navController.navigate(R.id.action_myAccountHome_to_address,AddressArgs(ShoppingEnum.MY_ACCOUNT).toBundle())
            }
            MenuEnum.MY_ORDERS->{
                navController.navigate(R.id.action_myAccountHome_to_myOrders)
            }

            MenuEnum.PAYMENT_METHODS->navController.navigate(R.id.action_myAccountHome_to_savedPaymentMethod)
            else->{
                Toast.makeText(context, "WORKING !!!", Toast.LENGTH_SHORT).show()


            }
        }
    }
}