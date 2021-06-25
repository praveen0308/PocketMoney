package com.example.pocketmoney.shopping.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pocketmoney.mlm.model.UniversalFilterItemModel
import com.example.pocketmoney.shopping.repository.FilterRepository
import com.example.pocketmoney.utils.myEnums.FilterEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class FilterViewModel @Inject constructor(
        private val filterRepository: FilterRepository
) : ViewModel() {

//    private val _sortingFilterList = MutableLiveData<MutableList<ModelBottomSheetItem>>()
//    val sortingFilterList: LiveData<MutableList<ModelBottomSheetItem>> = _sortingFilterList

    val sortingFilterList = MutableLiveData<MutableList<UniversalFilterItemModel>>()


    fun getSortingFilterList(){
            populateSortingFilterList()
    }

    fun updateSortingFilterList(id: Any) {
        sortingFilterList.value!!.find { it.ID == id }!!.isSelected = true
//        _sortingFilterList.value!!.find { it.ID== id}!!.isSelected=true
    }

    fun updateSortingFilterList(list: MutableList<UniversalFilterItemModel>) {
        sortingFilterList.value=list
//        _sortingFilterList.value!!.find { it.ID== id}!!.isSelected=true
    }

    private fun populateSortingFilterList() {
        val itemList = mutableListOf<UniversalFilterItemModel>()
//        itemList.add(UniversalFilterItemModel(FilterEnum.POPULAR, 1, "Popular"))
//        itemList.add(UniversalFilterItemModel(FilterEnum.NEWEST, 2, "Newest"))
//        itemList.add(
//                UniversalFilterItemModel(
//                        FilterEnum.PRICE_DESCENDING,
//                        3,
//                        "Price:lowest to High",
//                        true
//                )
//        )
//        itemList.add(
//                UniversalFilterItemModel(
//                        FilterEnum.PRICE_DESCENDING,
//                        4,
//                        "Price:Highest to Low"
//                )
//        )

        sortingFilterList.value = itemList
    }
}