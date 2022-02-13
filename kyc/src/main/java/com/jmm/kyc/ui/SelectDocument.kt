package com.jmm.kyc.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.jmm.kyc.DocumentType
import com.jmm.kyc.DocumentTypeAdapter
import com.jmm.kyc.KycViewModel
import com.jmm.kyc.databinding.FragmentSelectDocumentBinding
import com.jmm.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectDocument : BaseFragment<FragmentSelectDocumentBinding>(FragmentSelectDocumentBinding::inflate),
    DocumentTypeAdapter.DocumentTypeInterface {

    private val viewModel by activityViewModels<KycViewModel>()
    private lateinit var documentTypeAdapter: DocumentTypeAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvDocumentTypes()
        binding.btnNext.setOnClickListener {
            findNavController().navigate(SelectDocumentDirections.actionSelectDocumentToUploadDocument())
        }
    }

    private fun setupRvDocumentTypes(){
        documentTypeAdapter = DocumentTypeAdapter(this)
        binding.rvDocumentTypes.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context,2)
            adapter = documentTypeAdapter
        }
        val documentTypes = mutableListOf<DocumentType>()
        documentTypes.add(DocumentType(1,"Aadhaar Card",true))
        documentTypes.add(DocumentType(2,"Driving Licence"))
        documentTypes.add(DocumentType(3,"Passport"))
        documentTypes.add(DocumentType(4,"Electricity Bill"))

        documentTypeAdapter.setDocumentTypeList(documentTypes)
        viewModel.selectedDocumentType =documentTypes[0]
    }
    override fun subscribeObservers() {

    }

    override fun onItemSelected(item: DocumentType) {
        viewModel.selectedDocumentType =item
    }


}