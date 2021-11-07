package com.sampurna.pocketmoney.mlm.repository

import com.sampurna.pocketmoney.mlm.model.TransactionTypeModel
import com.sampurna.pocketmoney.mlm.model.UniversalFilterItemModel
import com.sampurna.pocketmoney.mlm.model.UniversalFilterModel
import com.sampurna.pocketmoney.mlm.network.MLMApiService
import com.sampurna.pocketmoney.utils.myEnums.PaymentHistoryFilterEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PaymentHistoryFilterRepository @Inject constructor(
        private val mlmApiService: MLMApiService
) {

    private val mFilterList = mutableListOf<UniversalFilterModel>()
/*
    fun updateFilterList(filterId: Int, filterItemId: Int, isSelected: Boolean) {
        for (filter in mFilterList) {
            if (filter.id == filterId) {
                for (filterItem in filter.filterList) {

                    if ((filterItem as UniversalFilterItemModel).ID == filterItemId) {
                        filterItem.isSelected = isSelected
                    }

                }
            }
        }
    }*/

    fun updateFilterList(mList:MutableList<UniversalFilterModel>){
        mFilterList.clear()
        mFilterList.addAll(mList)
    }


    fun populateFilterList(transactionTypeList:MutableList<UniversalFilterItemModel>){
        val timeFilterList = mutableListOf<UniversalFilterItemModel>()
//        timeFilterList.add(UniversalFilterItemModel(1,PaymentHistoryFilterEnum.SINGLE, PaymentHistoryFilterEnum.ALL,  "All"))
        timeFilterList.add(UniversalFilterItemModel(1,PaymentHistoryFilterEnum.SINGLE, PaymentHistoryFilterEnum.LAST_WEEK,  "Last Week"))
        timeFilterList.add(UniversalFilterItemModel(1,PaymentHistoryFilterEnum.SINGLE, PaymentHistoryFilterEnum.LAST_MONTH,  "Last Month",true))
        timeFilterList.add(UniversalFilterItemModel(1,PaymentHistoryFilterEnum.SINGLE, PaymentHistoryFilterEnum.LAST_3_MONTH,  "Last 3 Month"))
        timeFilterList.add(UniversalFilterItemModel(1,PaymentHistoryFilterEnum.SINGLE, PaymentHistoryFilterEnum.LAST_6_MONTH,  "Last 6 Month"))
        timeFilterList.add(UniversalFilterItemModel(1,PaymentHistoryFilterEnum.SINGLE, PaymentHistoryFilterEnum.CUSTOM,  "Custom"))
        val timeFilter = UniversalFilterModel(1, "Time", timeFilterList)
        mFilterList.add(timeFilter)

        val transactionCategoryList = mutableListOf<UniversalFilterItemModel>()
        transactionCategoryList.add(UniversalFilterItemModel(2,PaymentHistoryFilterEnum.SINGLE, PaymentHistoryFilterEnum.ALL,  "All",true))
        transactionCategoryList.add(UniversalFilterItemModel(2,PaymentHistoryFilterEnum.SINGLE, PaymentHistoryFilterEnum.CREDIT,  "Received"))
        transactionCategoryList.add(UniversalFilterItemModel(2,PaymentHistoryFilterEnum.SINGLE, PaymentHistoryFilterEnum.DEBIT,  "Paid"))

        val transactionCategories = UniversalFilterModel(2, "Category", transactionCategoryList)
        mFilterList.add(transactionCategories)

        val transactionTypeFilter = UniversalFilterModel(3, "Transaction Type", transactionTypeList)
        mFilterList.add(transactionTypeFilter)

    }

    suspend fun getTransactionType(): Flow<List<TransactionTypeModel>> {
        return flow {
            val response = mlmApiService.getTransactionTypes()

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchFilterList(): Flow<List<UniversalFilterModel>> {
        return flow {
            emit(mFilterList)
        }.flowOn(Dispatchers.IO)
    }

}