package com.example.pocketmoney.shopping.repository

import com.example.pocketmoney.mlm.model.UniversalFilterItemModel
import com.example.pocketmoney.shopping.network.ShoppingApiService
import com.example.pocketmoney.utils.myEnums.FilterEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject


class FilterRepository @Inject constructor(private val apiService: ShoppingApiService) {
    private val sortingFilterList = mutableListOf<UniversalFilterItemModel>()
    suspend fun getSortingFilters(): Flow<UniversalFilterItemModel> {
        populateSortingFilterList()
        return sortingFilterList.asFlow()
    }

    private fun populateSortingFilterList() {
//        sortingFilterList.add(UniversalFilterItemModel(FilterEnum.POPULAR, 1, "Popular"))
//        sortingFilterList.add(UniversalFilterItemModel(FilterEnum.NEWEST, 2, "Newest"))
//        sortingFilterList.add(
//            UniversalFilterItemModel(
//                FilterEnum.PRICE_DESCENDING,
//                3,
//                "Price:lowest to High"
//            )
//        )
//        sortingFilterList.add(
//            UniversalFilterItemModel(
//                FilterEnum.PRICE_DESCENDING,
//                4,
//                "Price:Highest to Low"
//            )
//        )

    }
}