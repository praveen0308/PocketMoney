package com.example.pocketmoney.mlm.ui.recharge

import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.ActivityRechargeBinding
import com.example.pocketmoney.mlm.model.RechargeEnum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_recharge.*


@AndroidEntryPoint
class RechargeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRechargeBinding

    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph
    val args: RechargeActivityArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRechargeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment = nav_host_recharge_activity as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        navGraph = graphInflater.inflate(R.navigation.nav_recharge)
        navController = navHostFragment.navController

//        when (args.rechargetype) {
//            RechargeEnum.PREPAID, RechargeEnum.POSTPAID -> {
//                navGraph.startDestination = R.id.contactList
//                navController.graph = navGraph
//            }
//            RechargeEnum.DTH -> {
//                navGraph.startDestination = R.id.operatorList
//                val bundle = Bundle()
//                bundle.putString("OPERATOR_TYPE", "DTH")
//                navController.setGraph(navGraph,bundle)
//            }
//            RechargeEnum.ELECTRICITY -> {
//                navGraph.startDestination = R.id.operatorList
//                val bundle = Bundle()
//                bundle.putString("OPERATOR_TYPE", "ELECTRICITY")
//                navController.setGraph(navGraph,bundle)
//            }
//        }


    }


}