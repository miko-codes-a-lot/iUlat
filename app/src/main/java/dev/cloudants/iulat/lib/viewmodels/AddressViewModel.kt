package dev.cloudants.iulat.lib.viewmodels

import androidx.compose.runtime.mutableStateListOf
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
