package dev.cloudants.iulat.lib.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cloudants.iulat.lib.models.entities.AddressDto
import dev.cloudants.iulat.lib.services.AddressService
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    val addressService: AddressService
): ViewModel() {
    val addressList: SnapshotStateList<AddressDto> = mutableStateListOf()
    val selectedAddress = mutableStateOf<AddressDto?>(null)
    fun fetchAddress(coords: String) {
        val parts = coords.split(",")
        if (parts.size != 2) {
            selectedAddress.value = null
            return
        }

        val lat = parts[0].toDoubleOrNull() ?: 0.0
        val lng = parts[1].toDoubleOrNull() ?: 0.0

        selectedAddress.value = AddressDto(
            latitude = lat,
            longitude = lng,
            zone = "",
            province = "",
            barangay = "",
            municipality = ""
        )
    }

    fun loadAddresses() {
        viewModelScope.launch {
            val addresses = addressService.fetchAll()
            addressList.clear()
            addressList.addAll(addresses)
        }
    }

    fun getAddressById(id: String): AddressDto? {
        return addressService.fetchOne(id)
    }
}
