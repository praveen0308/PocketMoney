package com.example.pocketmoney.utils.myEnums

enum class ShippingStatus(val status:Int)
{
    ShippingNotRequired(1),
    NotYetShipped(2),
    PartiallyShipped(3),
    Shipped(4),
    Delivered(5);

    companion object {
        fun getStatus(status: Int) = values().find{ it.status == status }
    }

}

enum class OtherEnum{
    ADD,EDIT,DELETE

}

enum class PaymentModes(val id:Int){
    Wallet(1),
    Online(2),
    CashOnDelivery(3),
    PCash(4)
}
enum class PaymentStatus(val id: Int)
{
    Pending(1),
    Paid(2),
    PartiallyRefunded(3),
    Refunded(4),
    Voided(5);
}



enum class OrderStatus(private val status: Int)
{
    Pending(1),
    Processing(2),
    Complete(3),
    Cancelled(4),
    CancelRequested(5),
    CancelRejected(6),
    CancelApproved(7),
    ReturnRequested(8),
    ReturnRejected(9),
    ReturnApproved(1),
    Returned(1);

    companion object {
        fun getStatus(status: Int) = values().find{ it.status == status }
    }

}


enum class AddressType(val type: Int)
{
    BaseAddress(1),
    BillingAddress(2),
    ShippingAddress(3);

    companion object {
        fun getType(type: Int) = values().find{ it.type == type }
    }

}

enum class DiscountType(val type: Int)
{
    MemberSpecific(1),
    Generic(2),
    Vacation(3);

    companion object {
        fun getType(type: Int) = values().find{ it.type == type }
    }

}


enum class NavigationType(val type: Int)
{
    NotificationPreferences(1),
    HelpCentre(2),
    PrivacyPolicy(3);

    companion object {
        fun getSource(type: Int) = values().find{ it.type == type }
    }


}

enum class NavigationEnum{
    NONE,INCOME,GROWTH,COMMISSION,WALLET,P_CASH,CREATE_COUPON,MY_COUPON,PROFILE,DOWNLINE,OFFER,REPORT,TICKET_HISTORY,ALL_TRANSACTION,PARENT_MENU,LOG_OUT,


    SYSTEM_GROWTH,UPDATE_COUNT,RENEWAL_COUNT,
    DIRECT_COMMISSION,SHOPPING_COMMISSION,UPDATE_COMMISSION,SERVICE_COMMISSION

    ,HELP_CENTRE,ABOUT,SHARE,

}


enum class PaymentHistoryFilterEnum{

    SINGLE,MULTI,
    ALL,LAST_MONTH,LAST_WEEK,LAST_3_MONTH,LAST_6_MONTH,CUSTOM
    ,CREDIT,DEBIT,

    ALL_TRANSACTION,CATEGORIES,TIME
}

enum class DateTimeEnum{

    LAST_WEEK,LAST_MONTH,LAST_3_MONTH,LAST_6_MONTH,CUSTOM
}