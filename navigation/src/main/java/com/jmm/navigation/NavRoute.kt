package com.jmm.navigation

object NavRoute {
    private const val developer = "jmm"
    private const val comp1 = "sampurna"

    private const val addMoneyModule = "com.$developer.add_money"
    private const val authenticationModule = "com.$developer.authentication"
    private const val commissionModule = "com.$developer.commission"
    private const val complaintReportModule = "com.$developer.complaint_report"
    private const val coreModule = "com.$developer.core"
    private const val couponModule = "com.$developer.coupon"
    private const val forgotPasswordModule = "com.$developer.forgot_password"
    private const val kycModule = "com.$developer.kyc"
    private const val lockScreenModule = "com.$developer.lock_screen"
    private const val memberShipModule = "com.$developer.membership"
    private const val mlmModule = "com.$developer.mlm"
    private const val onBoardingModule = "com.$developer.onboarding"
    private const val paymentGatewayModule = "com.$developer.payment_gateway"
    private const val payoutModule = "com.$developer.payout"
    private const val profileModule = "com.$developer.profile"
    private const val dthModule = "com.$developer.dth"
    private const val electricityModule = "com.$developer.electricity"
    private const val mobileRechargeModule = "com.$developer.mobile_recharge"
    private const val playRechargeModule = "com.$developer.play_recharge"
    private const val shoppingModule = "com.$developer.shopping"
    private const val transactionsModule = "com.$developer.transactions"
    private const val transferMoneyModule = "com.$developer.transfer_money"
    private const val utilModule = "com.$developer.util"

    // Activities
    const val ForgotPassword = "$forgotPasswordModule.ForgotPassword"
    const val ChangePassword = "$forgotPasswordModule.ChangePassword"


    const val MainDashboard = "$mlmModule.MainDashboard"

    const val SignIn = "$authenticationModule.SignIn"
    const val SignUp = "$authenticationModule.SignUp"


    const val CustomerProfile = "$profileModule.CustomerProfile"
    const val NewCustomerProfile = "$profileModule.NewCustomerProfile"

    const val CustomerGrowthNCommission = "$commissionModule.CustomerGrowthNCommission"

    const val ManageCoupon = "$couponModule.ManageCoupon"

    const val ComplaintList = "$complaintReportModule.ComplaintList"
    const val ChatActivity = "$complaintReportModule.ChatActivity"

    const val B2BTransfer = "$transferMoneyModule.B2BTransfer"

    const val NewPayout = "$payoutModule.NewPayout"

    const val KycActivity = "$kycModule.KycActivity"

    const val AddMoneyToWallet = "$addMoneyModule.AddMoneyToWallet"

    /*** Services ****/
    const val NewRechargeActivity = "$mobileRechargeModule.NewRechargeActivity"
    const val DthActivity = "$dthModule.DthActivity"
    const val GooglePlayRecharge = "$playRechargeModule.GooglePlayRecharge"

}