package com.jmm.mlm.ui

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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.google.zxing.WriterException
import com.jmm.core.MainActivity
import com.jmm.core.adapters.SocialItemAdapter
import com.jmm.mlm.MainDashboard
import com.jmm.mlm.R
import com.jmm.mlm.adapters.AccountSettingChildAdapter
import com.jmm.mlm.adapters.AccountSettingParentAdapter
import com.jmm.mlm.adapters.DashboardItemAdapter
import com.jmm.mlm.databinding.FragmentAccountBinding
import com.jmm.mlm.viewmodel.AccountViewModel
import com.jmm.model.ModelMenuItem
import com.jmm.model.ModelParentMenu
import com.jmm.model.ModelTitleValue
import com.jmm.model.SocialLinkModel
import com.jmm.model.myEnums.NavigationEnum
import com.jmm.navigation.NavRoute.ChangePassword
import com.jmm.navigation.NavRoute.ComplaintList
import com.jmm.navigation.NavRoute.CustomerGrowthNCommission
import com.jmm.navigation.NavRoute.ManageCoupon
import com.jmm.navigation.NavRoute.NewCustomerProfile
import com.jmm.util.BaseFragment
import com.jmm.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Account : BaseFragment<FragmentAccountBinding>(FragmentAccountBinding::inflate),
    AccountSettingChildAdapter.AccountSettingChildInterface,
    DashboardItemAdapter.DashboardItemInterface, SocialItemAdapter.SocialItemInterface {

    //ViewModels
    private val viewModel by viewModels<AccountViewModel>()

    // Adapters
    private lateinit var accountSettingParentAdapter: AccountSettingParentAdapter
    private lateinit var dashboardItemAdapter: DashboardItemAdapter
    private lateinit var socialItemAdapter: SocialItemAdapter
    // Variable
    private lateinit var userName:String
    private var userId:String = ""
    private var roleID : Int=0

    override fun onResume() {
        super.onResume()
        (activity as MainDashboard).toggleBottomNav(true)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvDashboardItems()
        setupRvSocialItems()
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

    private fun setupRvSocialItems(){
        socialItemAdapter = SocialItemAdapter(this)
        binding.rvSocialLinks.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter = socialItemAdapter
        }
        val socialLinks = mutableListOf<SocialLinkModel>()
        socialLinks.add(SocialLinkModel("Facebook",R.drawable.ic_facebook,"https://www.facebook.com/Sampurnarecharge"))
        socialLinks.add(SocialLinkModel("Instagram",R.drawable.ic_instagram,"https://www.instagram.com/sampurna.multirecharge/"))
        socialLinks.add(SocialLinkModel("Youtube",R.drawable.ic_youtube,"https://www.youtube.com/channel/UCzaDF5ER5bpzvYbPb6B4mTg"))
        socialLinks.add(SocialLinkModel("LinkedIn",R.drawable.ic_linkedin,"https://www.linkedin.com/in/samp-urna-78bb60227/"))
        socialItemAdapter.setSocialLinkModelList(socialLinks)
    }

    private fun setupRvDashboardItems(){
        dashboardItemAdapter = DashboardItemAdapter(this)
        binding.rvDashboardItems.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context,2)
            adapter = dashboardItemAdapter
        }
    }

    private fun getAccountMenuList(): List<ModelParentMenu> {
        val menuList = mutableListOf<ModelParentMenu>()

        val myAccountItemList = mutableListOf<ModelMenuItem>()
        myAccountItemList.add(
            ModelMenuItem(
                NavigationEnum.INCOME,
                "Commission",
                "Track your income",
                R.drawable.ic_baseline_data_saver_on_24
            )
        )
/*
1
        myAccountItemList.add(
            ModelMenuItem(
                NavigationEnum.WALLET,
                "Wallet",
                "Wallet,P-Cash,My Coupons",
                R.drawable.ic_person
            )
        )
*/

        myAccountItemList.add(
            ModelMenuItem(
                NavigationEnum.MY_COUPON,
                "My Coupons",
                "Manage & create coupons",
                R.drawable.ic_baseline_local_offer_24
            )
        )


/*
        myAccountItemList.add(
            ModelMenuItem(
                NavigationEnum.PROFILE,
                "Wallet",
                "Wallet,P-Cash,My Coupons",
                R.drawable.ic_person
            )
        )
*/


        myAccountItemList.add(
            ModelMenuItem(
                NavigationEnum.PROFILE,
                "Profile",
                "Manage your profile data",
                R.drawable.ic_person
            )
        )

/*
        myAccountItemList.add(
            ModelMenuItem(
                NavigationEnum.DOWNLINE,
                "Downline",
                "Track your user network",
                R.drawable.ic_baseline_device_hub_24
            )
        )
*/

        myAccountItemList.add(
            ModelMenuItem(
                NavigationEnum.REPORT,
                "Report",
                "Complaint history",
                R.drawable.ic_baseline_filter_list_24
            )
        )

        val myAccountsMenu = ModelParentMenu("My account", myAccountItemList)


/*
        val settingMenuItemList = mutableListOf<ModelMenuItem>()
        settingMenuItemList.add(
            ModelMenuItem(
                NavigationEnum.PROFILE,
                "Profile",
                "change and update your profile data",
                R.drawable.ic_person
            )
        )
        settingMenuItemList.add(
            ModelMenuItem(
                NavigationEnum.NOTIFICATION,
                "Notifications",
                "Change notification settings",
                R.drawable.ic_round_notifications_24
            )
        )
        settingMenuItemList.add(
            ModelMenuItem(
                NavigationEnum.LANGUAGE,
                "Languages",
                "Choose app language",
                R.drawable.ic_round_notifications_24
            )
        )
        settingMenuItemList.add(
            ModelMenuItem(
                NavigationEnum.ADDRESS_SETTINGS,
                "Address",
                "Manage your address details",
                R.drawable.ic_round_location_on_24
            )
        )
        settingMenuItemList.add(
            ModelMenuItem(
                NavigationEnum.PAYMENT_METHOD,
                "Payment",
                "Payment method settings",
                R.drawable.ic_round_payment_24
            )
        )

        val settingMenu = ModelParentMenu("Settings", settingMenuItemList)
*/

        val helpMenuItemList = mutableListOf<ModelMenuItem>()
        /*        helpMenuItemList.add(ModelMenuItem(NavigationEnum.FAQ, "FAQ", "", R.drawable.ic_round_help_24))
        helpMenuItemList.add(
            ModelMenuItem(
                NavigationEnum.HOW_IT_WORKS,
                "How it works?",
                "",
                R.drawable.ic_round_help_24
            )
        )*/
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
        if (userId.isNotEmpty()) helpMenuItemList.add(
            ModelMenuItem(
                NavigationEnum.SHARE,
                "Invite",
                "",
                R.drawable.ic_baseline_share_24
            )
        )


        if(userId.isNotEmpty()) {
            helpMenuItemList.add(
                ModelMenuItem(
                    NavigationEnum.CHANGE_PASSWORD,
                    "Change Password",
                    "",
                    R.drawable.ic_lock
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

        }

        val helpMenu = ModelParentMenu("Help", helpMenuItemList)
        if (userId.isNotEmpty()) menuList.add(myAccountsMenu)
        menuList.add(helpMenu)
        return menuList
    }

    override fun onMenuClick(menu: ModelMenuItem) {
        when (menu.id) {
            NavigationEnum.INCOME->{
                val intent = Intent(context,Class.forName(CustomerGrowthNCommission))
                intent.putExtra("TYPE",NavigationEnum.COMMISSION)
                startActivity(intent)
            }
            NavigationEnum.MY_COUPON->{
                startActivity(Intent(requireActivity(), Class.forName(ManageCoupon)))
            }
            NavigationEnum.PROFILE-> {
//                val intent = Intent(requireActivity(), Class.forName(CustomerProfile))
                val intent = Intent(requireActivity(), Class.forName(NewCustomerProfile))
                startActivity(intent)
            }

            NavigationEnum.REPORT->{
                startActivity(Intent(requireActivity(), Class.forName(ComplaintList)))
            }
            NavigationEnum.ABOUT -> {
                val intent = Intent(requireActivity(), AccountActivity::class.java)
                intent.putExtra("SOURCE", NavigationEnum.ABOUT)
                startActivity(intent)
//                findNavController().navigate(AccountDirections.actionAccountToAboutUs())
            }
            NavigationEnum.SHARE -> {
                val intent = Intent(requireActivity(), AccountActivity::class.java)
                intent.putExtra("SOURCE", NavigationEnum.SHARE)
                startActivity(intent)
//                findNavController().navigate(AccountDirections.actionAccountToShareUs())
            }
            NavigationEnum.CHANGE_PASSWORD -> {
                val intent = Intent(requireActivity(), Class.forName(ChangePassword))
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
            setUpRecyclerView()
        })
        viewModel.userRoleID.observe(viewLifecycleOwner, {
            roleID = it
            if (userId!="" && roleID!=0){
                viewModel.getDashboardData(userId,roleID)
            }

        })

        viewModel.dashboardData.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        populateDashboardItems(it)
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

    private fun populateDashboardItems(it: JsonObject) {
        val dashboardItems = mutableListOf<ModelTitleValue>()
        dashboardItems.add(ModelTitleValue("Wallet",it.get("BusinessWallet").toString(),NavigationEnum.WALLET))
        dashboardItems.add(ModelTitleValue("PCash",it.get("IncomeWallet").toString(),NavigationEnum.P_CASH))
        dashboardItems.add(ModelTitleValue("Downline",it.get("DownlineTeamCount").toString(),NavigationEnum.DOWNLINE))
        dashboardItems.add(ModelTitleValue("Direct Team",it.get("DirectTeamCount").toString(),NavigationEnum.DOWNLINE))

        dashboardItemAdapter.setModelTitleValueList(dashboardItems)


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
//            val ivLogo = dialogview.findViewById<ImageView>(R.id.iv_logo)
            ivClose.setOnClickListener {
                dismissPopup()
            }
            val qrgEncoder = QRGEncoder(userId, null, QRGContents.Type.TEXT, 300)
            try {
                imageView.setImageBitmap(qrgEncoder.bitmap)
//                ivLogo.isVisible = true
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

    override fun onDestroyView() {
        super.onDestroyView()
        progressBarHandler.hide()
    }

    override fun onItemClick(item: ModelTitleValue) {
        when(item.mType){
            NavigationEnum.WALLET->{
                val intent = Intent(requireActivity(), CustomerWalletActivity::class.java)
                intent.putExtra("FILTER","BUSINESS")
                startActivity(intent)
            }
            NavigationEnum.P_CASH->{
                val intent = Intent(requireActivity(), CustomerWalletActivity::class.java)
                intent.putExtra("FILTER","INCOME")
                startActivity(intent)
            }
            NavigationEnum.DOWNLINE->{

            }
        }
    }

    override fun onItemClick(item: SocialLinkModel) {
        val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
        defaultBrowser.data = Uri.parse(item.url)
        startActivity(defaultBrowser)
    }
}