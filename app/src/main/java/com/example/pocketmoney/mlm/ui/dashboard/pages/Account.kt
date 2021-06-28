package com.example.pocketmoney.mlm.ui.dashboard.pages

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentAccountBinding
import com.example.pocketmoney.databinding.TemplateUserQrPopupBinding
import com.example.pocketmoney.mlm.adapters.AccountSettingChildAdapter
import com.example.pocketmoney.mlm.adapters.AccountSettingParentAdapter
import com.example.pocketmoney.mlm.model.ModelMenuItem
import com.example.pocketmoney.mlm.model.ModelParentMenu
import com.example.pocketmoney.mlm.ui.MainActivity
import com.example.pocketmoney.mlm.ui.customergrowth.CustomerGrowthNCommission
import com.example.pocketmoney.mlm.ui.dashboard.*
import com.example.pocketmoney.mlm.viewmodel.AccountViewModel
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.myEnums.NavigationEnum
import com.example.pocketmoney.utils.setAmount
import com.google.zxing.WriterException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Account : BaseFragment<FragmentAccountBinding>(FragmentAccountBinding::inflate),
    AccountSettingChildAdapter.AccountSettingChildInterface {

    //ViewModels
    private val viewModel by viewModels<AccountViewModel>()

    // Adapters
    private lateinit var accountSettingParentAdapter: AccountSettingParentAdapter

    // Variable
    private lateinit var userName:String
    private lateinit var userId:String
    private var roleID : Int=0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        binding.ivQrCode.setOnClickListener {
            val showQrCode = ShowQrCode(requireContext())
            showQrCode.showPopup()
        }
    }

    private fun setUpRecyclerView() {
        accountSettingParentAdapter = AccountSettingParentAdapter(getAccountMenuList(), this)
        binding.rvAccountMenuList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = accountSettingParentAdapter
        }
    }

    private fun getAccountMenuList(): List<ModelParentMenu> {
        val menuList = mutableListOf<ModelParentMenu>()

        val myAccountItemList = mutableListOf<ModelMenuItem>()
        myAccountItemList.add(
            ModelMenuItem(
                NavigationEnum.INCOME,
                "Income",
                "Growth,Commission",
                R.drawable.ic_person
            )
        )
//
//        myAccountItemList.add(
//            ModelMenuItem(
//                NavigationEnum.WALLET,
//                "Wallet",
//                "Wallet,P-Cash,My Coupons",
//                R.drawable.ic_person
//            )
//        )

        myAccountItemList.add(
            ModelMenuItem(
                NavigationEnum.MY_COUPON,
                "My Coupons",
                "Manage & create coupons",
                R.drawable.ic_person
            )
        )


//        myAccountItemList.add(
//            ModelMenuItem(
//                NavigationEnum.PROFILE,
//                "Wallet",
//                "Wallet,P-Cash,My Coupons",
//                R.drawable.ic_person
//            )
//        )
//


        myAccountItemList.add(
            ModelMenuItem(
                NavigationEnum.PROFILE,
                "Profile",
                "change and update your profile data",
                R.drawable.ic_person
            )
        )

        myAccountItemList.add(
            ModelMenuItem(
                NavigationEnum.DOWNLINE,
                "Downline",
                "Track your user network",
                R.drawable.ic_person
            )
        )

        myAccountItemList.add(
            ModelMenuItem(
                NavigationEnum.REPORT,
                "Report",
                "Complaint history",
                R.drawable.ic_person
            )
        )

        val myAccountsMenu = ModelParentMenu("My account", myAccountItemList)


//        val settingMenuItemList = mutableListOf<ModelMenuItem>()
//        settingMenuItemList.add(
//            ModelMenuItem(
//                NavigationEnum.PROFILE,
//                "Profile",
//                "change and update your profile data",
//                R.drawable.ic_person
//            )
//        )
//        settingMenuItemList.add(
//            ModelMenuItem(
//                NavigationEnum.NOTIFICATION,
//                "Notifications",
//                "Change notification settings",
//                R.drawable.ic_round_notifications_24
//            )
//        )
//        settingMenuItemList.add(
//            ModelMenuItem(
//                NavigationEnum.LANGUAGE,
//                "Languages",
//                "Choose app language",
//                R.drawable.ic_round_notifications_24
//            )
//        )
//        settingMenuItemList.add(
//            ModelMenuItem(
//                NavigationEnum.ADDRESS_SETTINGS,
//                "Address",
//                "Manage your address details",
//                R.drawable.ic_round_location_on_24
//            )
//        )
//        settingMenuItemList.add(
//            ModelMenuItem(
//                NavigationEnum.PAYMENT_METHOD,
//                "Payment",
//                "Payment method settings",
//                R.drawable.ic_round_payment_24
//            )
//        )

//        val settingMenu = ModelParentMenu("Settings", settingMenuItemList)

        val helpMenuItemList = mutableListOf<ModelMenuItem>()
//        helpMenuItemList.add(ModelMenuItem(NavigationEnum.FAQ, "FAQ", "", R.drawable.ic_round_help_24))
//        helpMenuItemList.add(
//            ModelMenuItem(
//                NavigationEnum.HOW_IT_WORKS,
//                "How it works?",
//                "",
//                R.drawable.ic_round_help_24
//            )
//        )
        helpMenuItemList.add(
            ModelMenuItem(
                NavigationEnum.HELP_CENTRE,
                "Call Us",
                "",
                R.drawable.ic_phone
            )
        )
        helpMenuItemList.add(
            ModelMenuItem(
                NavigationEnum.ABOUT,
                "About Us",
                "",
                R.drawable.ic_round_info_24
            )
        )
        helpMenuItemList.add(
            ModelMenuItem(
                NavigationEnum.SHARE,
                "Share",
                "",
                R.drawable.ic_baseline_share_24
            )
        )
        helpMenuItemList.add(
            ModelMenuItem(
                NavigationEnum.LOG_OUT,
                "Log out",
                "",
                R.drawable.ic_baseline_logout_24
            )
        )

        val helpMenu = ModelParentMenu("Help", helpMenuItemList)

        menuList.add(myAccountsMenu)
        menuList.add(helpMenu)
        return menuList
    }

    override fun onMenuClick(menu: ModelMenuItem) {
        when (menu.id) {
            NavigationEnum.INCOME->{
                val intent = Intent(context, CustomerGrowthNCommission::class.java)
                intent.putExtra("TYPE",NavigationEnum.COMMISSION)
                startActivity(intent)
            }
            NavigationEnum.MY_COUPON->{
                startActivity(Intent(requireActivity(), MyCoupons::class.java))
            }
            NavigationEnum.PROFILE-> {
                val intent = Intent(requireActivity(), CustomerProfile::class.java)
                startActivity(intent)
            }

            NavigationEnum.REPORT->{
                startActivity(Intent(requireActivity(), ComplaintList::class.java))
            }
            NavigationEnum.ABOUT->{
                val intent = Intent(requireActivity(),AccountActivity::class.java)
                intent.putExtra("SOURCE",NavigationEnum.ABOUT)
                startActivity(intent)
            }
            NavigationEnum.SHARE->{
                val intent = Intent(requireActivity(),AccountActivity::class.java)
                intent.putExtra("SOURCE",NavigationEnum.SHARE)
                startActivity(intent)
            }
            NavigationEnum.LOG_OUT -> {
                val dialogClickListener =
                    DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                viewModel.clearUserInfo()
                                val intent = Intent(requireActivity(), MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                requireActivity().finish()
                                dialog.dismiss()

                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                                dialog.dismiss()
                            }
                        }
                    }
                showAlertDialog("Do you really want to log out?", dialogClickListener)


            }
            NavigationEnum.HELP_CENTRE -> {
                val i = Intent(Intent.ACTION_DIAL)
                val p = "tel:" + "8767404060"
                i.data = Uri.parse(p)
                startActivity(i)
            }
            else -> {
//                val intent = Intent(requireActivity(), AccountActivity::class.java)
//                intent.putExtra("SOURCE", menu.id)
//                startActivity(intent)
            }
        }


    }

    override fun subscribeObservers() {
        viewModel.userName.observe(viewLifecycleOwner, {
            userName = it
            binding.tvUserName.text = userName
        })
        viewModel.userId.observe(viewLifecycleOwner, {
            userId = it
            binding.tvUserId.text = userId
        })
        viewModel.userRoleID.observe(viewLifecycleOwner, {
            roleID = it
            if (userId!="" && roleID!=0){
                viewModel.getWalletBalance(userId,roleID)
                viewModel.getPCashBalance(userId,roleID)
            }

        })
        viewModel.walletBalance.observe(viewLifecycleOwner,{ _result ->
            when(_result.status)
            {
                Status.SUCCESS -> {
                    _result._data?.let {
                       createWalletLayout(it.toString())
                    }

                    displayLoading(false)
                }
                Status.LOADING -> {
                    displayLoading(true)
                }
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })
        viewModel.pCash.observe(viewLifecycleOwner, { _result ->
            when(_result.status)
            {
                Status.SUCCESS -> {
                    _result._data?.let {
                        createPcashLayout(it.toString())
                    }
                    displayLoading(false)
                }
                Status.LOADING -> {
                    displayLoading(true)
                }
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })

    }

    private fun createWalletLayout(amount : String){
        binding.layoutWallet.root.isVisible = true
        binding.layoutWallet.root.setCardBackgroundColor(ContextCompat.getColor(requireContext(),
            R.color.colorPrimaryDark
        ))
        binding.layoutWallet.tvValue.text = amount

        binding.layoutWallet.root.setOnClickListener {
            val intent = Intent(requireActivity(), CustomerWalletActivity::class.java)
            intent.putExtra("FILTER","BUSINESS")
            startActivity(intent)
        }

    }
    private fun createPcashLayout(amount : String){
        binding.layoutPcash.root.isVisible = true
        binding.layoutPcash.root.setCardBackgroundColor(ContextCompat.getColor(requireContext(),
            R.color.Olive
        ))
        binding.layoutPcash.tvValue.text = amount
        binding.layoutPcash.tvSubtitle.text = "P-Cash"
        binding.layoutPcash.root.setOnClickListener {
            val intent = Intent(requireActivity(), CustomerWalletActivity::class.java)
            intent.putExtra("FILTER","INCOME")
            startActivity(intent)
        }
    }


    inner class ShowQrCode(context: Context) : Dialog(context) {

        init {
            dialog = Dialog(context)
        }

        fun showPopup() {

            val dialogview = LayoutInflater.from(context)
                .inflate(R.layout.template_user_qr_popup, null, false)
            //initializing dialog screen
            val imageView = dialogview.findViewById<ImageView>(R.id.iv_qr_code)
            val ivClose = dialogview.findViewById<ImageView>(R.id.img_close)
            ivClose.setOnClickListener {
                dismissPopup()
            }
            val qrgEncoder = QRGEncoder(userId, null, QRGContents.Type.TEXT, 300)
            try {
                imageView.setImageBitmap(qrgEncoder.bitmap)
            } catch (e: WriterException) {
                e.printStackTrace()
            }
            dialog?.setCancelable(true)
            dialog?.setContentView(dialogview)
            dialog?.show()

        }

    }
    companion object{
        var dialog: Dialog? = null
        fun dismissPopup() = dialog?.let { dialog!!.dismiss() }
    }
}