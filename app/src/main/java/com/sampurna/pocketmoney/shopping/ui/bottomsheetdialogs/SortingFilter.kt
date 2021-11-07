package com.sampurna.pocketmoney.shopping.ui.bottomsheetdialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampurna.pocketmoney.databinding.FragmentSortingFilterBinding
import com.sampurna.pocketmoney.mlm.model.UniversalFilterItemModel
import com.sampurna.pocketmoney.shopping.adapters.SortingFilterAdapter
import com.sampurna.pocketmoney.shopping.viewmodel.FilterViewModel
import com.sampurna.pocketmoney.utils.myEnums.FilterEnum
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SortingFilter : BottomSheetDialogFragment(), SortingFilterAdapter.SortingFilterItemListener {

    //UI
    private var _binding: FragmentSortingFilterBinding? = null
    private val binding get() = _binding!!

    private val filterViewModel: FilterViewModel by activityViewModels()

    //Adapter
    private lateinit var sortingFilterAdapter: SortingFilterAdapter
    private var filterList = mutableListOf<UniversalFilterItemModel>()
    private lateinit var mListener:SortingFilterInterface
    private val args by navArgs<SortingFilterArgs>()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSortingFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = parentFragment as SortingFilterInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement BottomSheetListener")
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        args.filterType

    }

    private fun setupRecyclerView(){
        sortingFilterAdapter = SortingFilterAdapter(this)
        binding.rvListItem.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = sortingFilterAdapter
        }
    }

    override fun onItemClick(item: UniversalFilterItemModel) {
//        filterViewModel.updateSortingFilterList(item.ID)
        mListener.OnItemFilterItemClick(item.type as FilterEnum)
        dismissAllowingStateLoss()
    }


    interface SortingFilterInterface{
        fun OnItemFilterItemClick(filter: FilterEnum)
    }
}