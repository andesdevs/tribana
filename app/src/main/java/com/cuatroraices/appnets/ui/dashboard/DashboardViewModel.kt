package com.cuatroraices.appnets.ui.dashboard

import android.app.usage.UsageEvents
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuatroraices.appnets.RetrofitInstance
import com.cuatroraices.appnets.DashboardResponse
import com.cuatroraices.appnets.UserResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardViewModel : ViewModel() {

    private val _dashboardData = MutableLiveData<DashboardResponse?>()
    val dashboardData: LiveData<DashboardResponse?> get() = _dashboardData
    /*private val _dashboardData = MutableLiveData<DashboardResponse?>()
    val dashboardData: LiveData<DashboardResponse?> get() = _dashboardData*/

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _userData = MutableLiveData<UserResponse?>()
    val userData: LiveData<UserResponse?> get() = _userData

    /** 🔹 CORREGIDO: Cargar datos del Dashboard con `postValue()` */
    fun loadDashboardData(rut: String) {
        viewModelScope.launch {
            RetrofitInstance.api.getDashboard(rut).enqueue(object : Callback<DashboardResponse> {
                override fun onResponse(
                    call: Call<DashboardResponse>,
                    response: Response<DashboardResponse>
                ) {
                    if (response.isSuccessful) {
                        _dashboardData.postValue(response.body()) // 🔹 SE CAMBIÓ `value` por `postValue`
                    } else {
                        _errorMessage.postValue("Error al cargar datos")
                    }
                }

                override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                    _errorMessage.postValue("Error: ${t.message}")
                }
            })
        }
    }

    /** 🔹 CORREGIDO: Cargar datos del usuario con `postValue()` y `viewModelScope.launch {}` */
    fun loadUserData(rut: String) {
        viewModelScope.launch {
            RetrofitInstance.api.getUser(rut).enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.isSuccessful) {
                        _userData.postValue(response.body()) // 🔹 SE CAMBIÓ `value` por `postValue`
                    } else {
                        _errorMessage.postValue("Error al cargar datos del usuario")
                    }
                }
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    _errorMessage.postValue("Error: ${t.message}")
                }
            })
        }
    }
}


/*
package com.cuatroraices.appnets.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuatroraices.appnets.RetrofitInstance
import com.cuatroraices.appnets.DashboardResponse
import com.cuatroraices.appnets.UserResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardViewModel : ViewModel() {

    private val _dashboardData = MutableLiveData<DashboardResponse?>()
    val dashboardData: LiveData<DashboardResponse?> get() = _dashboardData

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // NUEVO: LiveData para los datos del usuario (nombre y foto)
    private val _userData = MutableLiveData<UserResponse?>()
    val userData: LiveData<UserResponse?> get() = _userData

    fun loadDashboardData(rut: String) {
        viewModelScope.launch {
            RetrofitInstance.api.getDashboard(rut).enqueue(object : Callback<DashboardResponse> {
                override fun onResponse(
                    call: Call<DashboardResponse>,
                    response: Response<DashboardResponse>
                ) {
                    if (response.isSuccessful) {
                        _dashboardData.value = response.body()
                    } else {
                        _errorMessage.value = "Error al cargar datos"
                    }
                }

                override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                    _errorMessage.value = "Error: ${t.message}"
                }
            })
        }
    }

    // NUEVO: Función para cargar los datos del usuario (nombre y foto)
    fun loadUserData(rut: String) {
        RetrofitInstance.api.getUser(rut).enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                if (response.isSuccessful) {
                    _userData.value = response.body()
                } else {
                    _errorMessage.value = "Error al cargar datos del usuario"
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }
}
*/
