package com.jmm.repository.shopping_repo

import com.jmm.model.UniversalFilterItemModel
import com.jmm.network.services.ShoppingApiService
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