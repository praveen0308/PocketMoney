package com.sampurna.pocketmoney.shopping.repository

import com.sampurna.pocketmoney.shopping.model.ModelAddress
import com.sampurna.pocketmoney.shopping.model.ModelCity
import com.sampurna.pocketmoney.shopping.model.ModelState
import com.sampurna.pocketmoney.shopping.network.ShoppingApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AddressRepository @Inject constructor(
    private val shoppingApiService: ShoppingApiService
) {
//
//    suspend fun getCustomerAddressByUserId(userId:String): Flow<DataState<List<ModelAddress>>> = flow{
//        emit(DataState.Loading)
//        try {
//            val result = shoppingApiService.getCustomerAddressByUserID(userId)
//            emit(DataState.Success(result))
//        }catch (e: Exception){
//            (DataState.Error(e))
//        }
//    }

    suspend fun getCustomerAddressByUserId(userId:String): Flow<List<ModelAddress>> {
        return flow {
            val response = shoppingApiService.getCustomerAddressByUserID(userId)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }


//    suspend fun addNewAddress(modelAddress: ModelAddress): Flow<DataState<Boolean>> = flow{
//        emit(DataState.Loading)
//        try {
//            val result = shoppingApiService.addAddress(modelAddress)
//            emit(DataState.Success(result))
//        }catch (e: Exception){
//            (DataState.Error(e))
//        }
//    }

    suspend fun addNewAddress(modelAddress: ModelAddress): Flow<Boolean> {
        return flow {
            val response = shoppingApiService.addAddress(modelAddress)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

//
//    suspend fun updateAddress(modelAddress: ModelAddress): Flow<DataState<Boolean>> = flow{
//        emit(DataState.Loading)
//        try {
//            val result = shoppingApiService.updateAddress(modelAddress)
//            emit(DataState.Success(result))
//        }catch (e: Exception){
//            (DataState.Error(e))
//        }
//    }


    suspend fun updateAddress(modelAddress: ModelAddress): Flow<Boolean> {
        return flow {
            val response = shoppingApiService.updateAddress(modelAddress)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getAddressDetailById(
            addressId:Int,
            userId: String
    ): Flow<ModelAddress> {
        return flow {
            val response = shoppingApiService.getAddressDetailById(addressId.toString(),userId)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getShippingCharge(
            addressId:Int,
            userId: String
    ): Flow<Double> {
        return flow {
            val response = shoppingApiService.getShippingCharge(addressId.toString(),userId)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAllStates(): Flow<List<ModelState>> {
        return flow {
            val response = shoppingApiService.getAllStates()

            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getCitiesByStateCode(stateCode:String): Flow<List<ModelCity>> {
        return flow {
            val response = shoppingApiService.getCitiesByStateCode(stateCode)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAllCities(): Flow<List<ModelCity>> {
        return flow {
            val response = shoppingApiService.getAllCity()

            emit(response)
        }.flowOn(Dispatchers.IO)
    }
}