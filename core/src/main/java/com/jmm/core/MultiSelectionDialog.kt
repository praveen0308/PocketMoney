package com.jmm.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jmm.core.adapters.MultipleChoiceSelectionAdapter
import com.jmm.core.databinding.FragmentMultiSelectionDialogBinding


class MultiSelectionDialog(private val multiSelectionDialogCallback: MultiSelectionDialogCallback) : DialogFragment(),
    MultipleChoiceSelectionAdapter.MultipleChoiceSelectionInterface {

    private var _binding: FragmentMultiSelectionDialogBinding? = null
    private val binding get() = _binding!!

    var dialogTitle = ""
    var positiveText = ""
    var negativeText = ""
    private var listItems = mutableListOf<Any>()
    private var checkedItems = mutableListOf<Boolean>()

    private lateinit var multipleChoiceSelectionAdapter: MultipleChoiceSelectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMultiSelectionDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        listItems.clear()
        checkedItems.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDialog()

        binding.apply {
            btnPositive.setOnClickListener {
                checkedItems = multipleChoiceSelectionAdapter.getCheckedItems().toMutableList()
                multiSelectionDialogCallback.onPositiveClicked(checkedItems)
                dismiss()
            }
            btnNegative.setOnClickListener {
                multiSelectionDialogCallback.onNegativeClicked()
                dismiss()
            }
        }
    }
    private fun setupRvItems(){
        multipleChoiceSelectionAdapter = MultipleChoiceSelectionAdapter(this)
        binding.rvItems.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = multipleChoiceSelectionAdapter
        }

        multipleChoiceSelectionAdapter.setItemList(listItems,checkedItems)
    }

    fun populateItems(items:List<Any>,checkedList: List<Boolean>){
        this.listItems = items.toMutableList()
        this.checkedItems = checkedList.toMutableList()
    }
    private fun initDialog(){
        binding.apply {
            tvDialogTitle.text = dialogTitle
            if (positiveText.isNotEmpty()) btnPositive.text = positiveText
            if (negativeText.isNotEmpty()) btnNegative.text = negativeText

            setupRvItems()
        }
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    interface MultiSelectionDialogCallback{
        fun onPositiveClicked(checkedItems:List<Boolean>)
        fun onNegativeClicked()
    }
}