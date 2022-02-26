package com.jmm.payment_gateway

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jmm.model.myEnums.PaymentEnum
import com.jmm.model.serviceModels.PaytmResponseModel
import com.jmm.model.shopping_models.ModelPaymentMethod
import com.jmm.payment_gateway.databinding.FragmentPaymentMethodsBinding
import com.jmm.util.BaseBottomSheetDialogFragment
import org.json.JSONObject


class PaymentMethods(
    private val paymentMethodsInterface: PaymentMethodsInterface,
    val isCod: Boolean = false,
    val isOnline: Boolean = true
) :
    BaseBottomSheetDialogFragment<FragmentPaymentMethodsBinding>(FragmentPaymentMethodsBinding::inflate),
    PaymentMethodAdapter.PaymentMethodInterface {

    private var selectedPaymentMethod = PaymentEnum.WALLET

    private lateinit var paymentMethodAdapter: PaymentMethodAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRvPaymentMethods()

        binding.btnActivate.setOnClickListener {
            paymentMethodsInterface.onPaymentMethodSelected(selectedPaymentMethod)
            dismiss()
        }

    }

    private fun setupRvPaymentMethods() {
        paymentMethodAdapter =
            PaymentMethodAdapter(getPaymentMethods(), this)
        binding.rvPaymentMethods.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = paymentMethodAdapter
        }

    }

    private fun getPaymentMethods(): MutableList<Any> {

        val paymentMethods = mutableListOf<Any>()
        paymentMethods.add(
            ModelPaymentMethod(
                PaymentEnum.WALLET,
                "Wallet",
                R.drawable.ic_logo,
                true
            )
        )
        paymentMethods.add(ModelPaymentMethod(PaymentEnum.PCASH, "PCash", R.drawable.ic_wallet))
        if (isOnline) paymentMethods.add(
            ModelPaymentMethod(
                PaymentEnum.PAYTM,
                "Payment gateway",
                R.drawable.ic_paytm

            )
        )

        if (isCod) {
            paymentMethods.add(
                ModelPaymentMethod(
                    PaymentEnum.COD,
                    "Cash On Delivery",
                    0

                )
            )

        }



        return paymentMethods
    }


    override fun subscribeObservers() {

    }

    override fun onPaymentModeSelected(item: ModelPaymentMethod) {
        selectedPaymentMethod = item.method

    }

    interface PaymentMethodsInterface {
        fun onPaymentMethodSelected(method: PaymentEnum)
    }


    companion object {
        fun getPaytmResponse(it: Bundle): PaytmResponseModel {
            val paytmResponseModel = PaytmResponseModel()

            if (it.containsKey("STATUS")) paytmResponseModel.STATUS =
                it.getString("STATUS")?.substring(4)
            if (it.containsKey("TXNAMOUNT")) paytmResponseModel.TXNAMOUNT =
                it.getString("TXNAMOUNT")
            if (it.containsKey("TXNDATE")) paytmResponseModel.TXNDATE = it.getString("TXNDATE")
            if (it.containsKey("MID")) paytmResponseModel.MID = it.getString("MID")
            if (it.containsKey("ORDERID")) paytmResponseModel.ORDERID = it.getString("ORDERID")
            if (it.containsKey("TXNID")) paytmResponseModel.TXNID = it.getString("TXNID")
            if (it.containsKey("RESPCODE")) paytmResponseModel.RESPCODE = it.getString("RESPCODE")
            if (it.containsKey("PAYMENTMODE")) paytmResponseModel.PAYMENTMODE =
                it.getString("PAYMENTMODE")
            if (it.containsKey("BANKTXNID")) paytmResponseModel.BANKTXNID =
                it.getString("BANKTXNID")
            if (it.containsKey("CURRENCY")) paytmResponseModel.CURRENCY = it.getString("CURRENCY")
            if (it.containsKey("GATEWAYNAME")) paytmResponseModel.GATEWAYNAME =
                it.getString("GATEWAYNAME")
            if (it.containsKey("RESPMSG")) paytmResponseModel.RESPMSG = it.getString("RESPMSG")
            if (it.containsKey("CHARGEAMOUNT")) paytmResponseModel.CHARGEAMOUNT =
                it.getString("CHARGEAMOUNT")

            return paytmResponseModel
        }

        fun getPaytmResponse(it: JSONObject): PaytmResponseModel {
            val paytmResponseModel = PaytmResponseModel()

            if (it.has("STATUS")) paytmResponseModel.STATUS = it.getString("STATUS").substring(4)
            if (it.has("TXNAMOUNT")) paytmResponseModel.TXNAMOUNT = it.getString("TXNAMOUNT")
            if (it.has("TXNDATE")) paytmResponseModel.TXNDATE = it.getString("TXNDATE")
            if (it.has("MID")) paytmResponseModel.MID = it.getString("MID")
            if (it.has("ORDERID")) paytmResponseModel.ORDERID = it.getString("ORDERID")
            if (it.has("TXNID")) paytmResponseModel.TXNID = it.getString("TXNID")
            if (it.has("RESPCODE")) paytmResponseModel.RESPCODE = it.getString("RESPCODE")
            if (it.has("PAYMENTMODE")) paytmResponseModel.PAYMENTMODE = it.getString("PAYMENTMODE")
            if (it.has("BANKTXNID")) paytmResponseModel.BANKTXNID = it.getString("BANKTXNID")
            if (it.has("CURRENCY")) paytmResponseModel.CURRENCY = it.getString("CURRENCY")
            if (it.has("GATEWAYNAME")) paytmResponseModel.GATEWAYNAME = it.getString("GATEWAYNAME")
            if (it.has("RESPMSG")) paytmResponseModel.RESPMSG = it.getString("RESPMSG")
            if (it.has("CHARGEAMOUNT")) paytmResponseModel.CHARGEAMOUNT =
                it.getString("CHARGEAMOUNT")

            return paytmResponseModel
        }
    }
}