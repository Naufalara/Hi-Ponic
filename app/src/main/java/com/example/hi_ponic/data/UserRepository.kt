package com.example.hi_ponic.data

import com.example.hi_ponic.data.Response.LoginResponse
import com.example.hi_ponic.data.Response.SensorResponse
import com.example.hi_ponic.data.Response.PredictConditionResponse
import com.example.hi_ponic.data.pref.UserModel
import com.example.hi_ponic.data.pref.UserPreference
import com.example.hi_ponic.data.response.ErrorResponse
import com.example.hi_ponic.data.response.ListTanamanResponse
import com.example.hi_ponic.data.response.TambahTanamanResponse
import com.example.hi_ponic.data.retrofit.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun getName(): Flow<String> {
        return userPreference.getName()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun login(identifier: String, password: String): LoginResponse {
        return apiService.login(identifier, password)
    }

    suspend fun register(password: String, email: String, username: String): ErrorResponse {
        return apiService.register(username, email, password)
    }

    suspend fun addPlant(token: String, name: RequestBody, date_added: RequestBody, image: MultipartBody.Part) {
        apiService.addPlant(token, name, date_added, image)
    }

    suspend fun getPlant(token: String):ListTanamanResponse{
        return apiService.getPlan(token)
    }

    fun observeSensorValues(): Flow<SensorResponse> = flow {
        while (true) {
            try {
                val sensorData = apiService.getSensorData()
                emit(sensorData)
                delay(120000) // Delay for 60 seconds (1 minute) before fetching data again
            } catch (e: Exception) {
                // Handle error
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun uploadImage(file: MultipartBody.Part): PredictConditionResponse {
        return apiService.uploadImage(file)
    }

    companion object {
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ) = UserRepository(userPreference, apiService)
    }
}
