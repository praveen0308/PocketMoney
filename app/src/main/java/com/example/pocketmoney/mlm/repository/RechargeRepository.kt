package com.example.pocketmoney.mlm.repository

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.example.pocketmoney.R
import com.example.pocketmoney.mlm.model.ModelContact
import com.example.pocketmoney.mlm.model.ModelOperator
import com.example.pocketmoney.mlm.model.RechargeEnum
import com.example.pocketmoney.mlm.model.serviceModels.IdNameModel
import com.example.pocketmoney.mlm.model.serviceModels.MobileCircleOperator
import com.example.pocketmoney.mlm.model.serviceModels.MobileOperatorPlan
import com.example.pocketmoney.mlm.model.serviceModels.SimplePlanResponse
import com.example.pocketmoney.mlm.network.MLMApiService
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.extractMobileNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class RechargeRepository @Inject constructor(
        val context: Context,
        val mlmApiService: MLMApiService
) {

    fun getContactList(): Flow<DataState<List<ModelContact>>> = flow {

        emit(DataState.Loading)

        val mContactList: MutableList<ModelContact> = ArrayList()

        try {
            val cursor: Cursor? = context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
            if ((cursor?.count ?: 0) > 0) {
                while (cursor!!.moveToNext()) {
                    val name: String = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val phoneNo: String = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
//                    val photoUri: String = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
//                    Log.e("contact", "getAllContacts: $name $phoneNo $photoUri")


                    mContactList.add(ModelContact(name, extractMobileNumber(phoneNo)))
                    emit(DataState.Success(mContactList))
                }
            }


            cursor?.close()
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    fun getOperatorList(operatorOf: String): Flow<DataState<List<ModelOperator>>> = flow {

        emit(DataState.Loading)

        try {
//            lateinit var mOperatorList = mlmApiService.getOperatorList(operatorOf)
            lateinit var mOperatorList : List<ModelOperator>
            when(operatorOf){
                "PREPAID"-> mOperatorList=getMobileOperators()
                "POSTPAID"-> mOperatorList=getMobileOperators()
                "DTH"-> mOperatorList=getDTHOperators()
                "ELECTRICITY"-> mOperatorList=getElectricityBoards()
            }


            emit(DataState.Success(mOperatorList))

        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    fun getOperators(operatorOf: RechargeEnum):List<ModelOperator>{
        return when(operatorOf){
                RechargeEnum.PREPAID,RechargeEnum.POSTPAID->getMobileOperators()
                RechargeEnum.DTH->getDTHOperators()
                else->getDTHOperators()
            }

    }

    private fun getElectricityBoards(): List<ModelOperator> {
        val boardsList:MutableList<ModelOperator> = ArrayList()

        boardsList.add(ModelOperator("Adani Electricity Mumbai Limited"))
        boardsList.add(ModelOperator("Ajmer Vidyut Vitran Nigam Ltd"))
        boardsList.add(ModelOperator("Andhra Pradesh State Electricity Board (APSEB), Andhra Pradesh"))
        boardsList.add(ModelOperator("Southern Power Distribution Company of Andhra Pradesh Limited"))
        boardsList.add(ModelOperator("Assam Power Distribution Company Limited (APDCL), Assam"))
        boardsList.add(ModelOperator("Bangalore Electricity Supply Company"))
        boardsList.add(ModelOperator("Brihanmumbai Electric Supply and Transport"))
        boardsList.add(ModelOperator("BSES Rajdhani Power Ltd. Delhi"))
        boardsList.add(ModelOperator("BSES Yamuna Power Ltd. Delhi"))
        boardsList.add(ModelOperator("Calcutta Electric Supply Corporation"))
        boardsList.add(ModelOperator("Chamundeshwari Electricity Supply Corporation Limited"))
        boardsList.add(ModelOperator("Dakshin Gujarat Vij Company Ltd. (DGVCL) Surat"))
        boardsList.add(ModelOperator("Dakshin Haryana Bijli Vitran Nigam"))
        boardsList.add(ModelOperator("Damodar Valley Corporation"))
        boardsList.add(ModelOperator("Essel Vidhyut Vitran Ujjain Pvt. Ltd."))
        boardsList.add(ModelOperator("Goa Electricity Board"))
        boardsList.add(ModelOperator("Gulbarga Electicity Supply Company Limited"))
        boardsList.add(ModelOperator("Hubli Electricity Supply Company Limited"))
        boardsList.add(ModelOperator("India Power Corporation Limited"))
        boardsList.add(ModelOperator("Jaipur Vidyut Vitran Nigam Limited"))
        boardsList.add(ModelOperator("Jodhpur Vidyut Vitran Nigam Ltd"))
        boardsList.add(ModelOperator("Karnataka Power Corporation Limited"))
        boardsList.add(ModelOperator("Kerala State Electricity Board"))
        boardsList.add(ModelOperator("Madhya Pradesh Paschim Kshetra Vidyut Vitaran Company Ltd."))
        boardsList.add(ModelOperator("Madhya Pradesh Poorv Kshetra Vidyut Vitaran Company Ltd."))
        boardsList.add(ModelOperator("Madhya Pradesh Madhya Kshetra Vidyut Vitaran Company Ltd."))
        boardsList.add(ModelOperator("Madhya Gujarat Vij Company Ltd. (MGVCL) Vadodara"))
        boardsList.add(ModelOperator("Maharashtra State Electricity Distribution Company Limited"))
        boardsList.add(ModelOperator("Mangalore Electricity Supply Company Limited"))
        boardsList.add(ModelOperator("Manipur State Power Distribution Company Limited"))
        boardsList.add(ModelOperator("National Thermal Power Corporation"))
        boardsList.add(ModelOperator("Neyveli Lignite Corporation"))
        boardsList.add(ModelOperator("North Eastern Supply Company of Odisha Ltd"))
        boardsList.add(ModelOperator("Noida Power Company Limited"))
        boardsList.add(ModelOperator("North Bihar Power Distribution Company Limited"))
        boardsList.add(ModelOperator("Paschim Gujarat Vij Company Ltd (PGVCL) Rajkot"))
        boardsList.add(ModelOperator("Power Development Department"))
        boardsList.add(ModelOperator("PowerGrid Corporation of India"))
        boardsList.add(ModelOperator("Punjab State Power Corporation Limited"))
        boardsList.add(ModelOperator("Reliance Infrastructure"))
        boardsList.add(ModelOperator("South Bihar Power Distribution Company Limited"))
        boardsList.add(ModelOperator("Southern Electricity Supply Company of Orissa"))
        boardsList.add(ModelOperator("Tamil Nadu Electricity Board"))
        boardsList.add(ModelOperator("Tata Power"))
        boardsList.add(ModelOperator("Tata Power Delhi Distribution Limited (NDPL), Delhi"))
        boardsList.add(ModelOperator("Torrent Power Ltd"))
        boardsList.add(ModelOperator("Torrent Power Ltd, Agra"))
        boardsList.add(ModelOperator("Torrent Power Ltd, Ahmedabad"))
        boardsList.add(ModelOperator("Torrent Power Ltd, Surat"))
        boardsList.add(ModelOperator("Tripura State Electricity Corporation Limited (TSECL)"))
        boardsList.add(ModelOperator("Uttar Gujarat Vij Company Ltd (UGVCL) Mehsana"))
        boardsList.add(ModelOperator("Uttar Haryana Bijli Vitran Nigam Limited"))
        boardsList.add(ModelOperator("Uttar Pradesh Power Corporation Limited"))
        boardsList.add(ModelOperator("West Bengal State Electricity Board (WBSEDCL)"))
        return boardsList
    }

    private fun getDTHOperators(): List<ModelOperator> {
        val dthOperatorList:MutableList<ModelOperator> = ArrayList()

        dthOperatorList.add(ModelOperator("Tata Sky",R.drawable.ic_tata_sky))
        dthOperatorList.add(ModelOperator("Airtel DTH",R.drawable.ic_airtel))
        dthOperatorList.add(ModelOperator("Big TV",R.drawable.ic_big_tv))
        dthOperatorList.add(ModelOperator("Dish TV",R.drawable.ic_dish_tv))
        dthOperatorList.add(ModelOperator("Sun Direct",R.drawable.ic_sun_direct))
        dthOperatorList.add(ModelOperator("Videocon D2H",R.drawable.ic_videocon_d2h))

        return dthOperatorList
    }

    private fun getMobileOperators(): List<ModelOperator> {
        val mobileOperatorList:MutableList<ModelOperator> = ArrayList()

        mobileOperatorList.add(ModelOperator("Jio", R.drawable.ic_jio))
        mobileOperatorList.add(ModelOperator("Airtel",R.drawable.ic_airtel))
        mobileOperatorList.add(ModelOperator("VI",R.drawable.ic_vi_vodafone_idea))
//        mobileOperatorList.add(ModelOperator("Tata Docomo",R.drawable.ict))
        mobileOperatorList.add(ModelOperator("BSNL",R.drawable.ic_bsnl))
        mobileOperatorList.add(ModelOperator("MTNL",R.drawable.ic_mtnl))

        return mobileOperatorList
    }

    suspend fun getOperatorNCircleOfMobileNo(
            mobileNumber: String
    ): Flow<MobileCircleOperator> {
        return flow {
            val response = mlmApiService.fetchOperatorNCircleOfMobile(mobileNumber)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getMobileSimplePlans(
            circle: String,
            mobileOperator:String
    ): Flow<SimplePlanResponse> {
        return flow {
            val response = mlmApiService.fetchMobileSimplePlan(circle, mobileOperator)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getMobileSpecialPlans(
            mobileNumber: String,
            mobileOperator:String
    ): Flow<List<MobileOperatorPlan>> {
        return flow {
            val response = mlmApiService.fetchMobileSpecialPlan(mobileNumber, mobileOperator)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getMobileServiceCircle(
            providerId:Int
    ): Flow<List<IdNameModel>> {
        return flow {
            val response = mlmApiService.getMobileServiceCircle(providerId)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getMobileServiceOperators(
            serviceTypeId: Int,
            serviceProviderId: Int,
            circleCode: String?
    ): Flow<List<IdNameModel>> {
        return flow {
            val response = mlmApiService.getMobileServiceOperator(serviceTypeId,serviceProviderId,circleCode)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }




}
