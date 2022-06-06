/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.samples.apps.sunflower.data.GardenPlantingRepository
import com.google.samples.apps.sunflower.data.PlantAndGardenPlantings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class GardenPlantingListViewModel @Inject internal constructor(
    gardenPlantingRepository: GardenPlantingRepository
) : ViewModel() {

    private val plantName: MutableStateFlow<String> = MutableStateFlow("")

    val plantAndGardenPlantings: LiveData<List<PlantAndGardenPlantings>> =
        plantName.flatMapLatest { plantName ->
            if(plantName.isEmpty())
                gardenPlantingRepository.getPlantedGardens()
            else
                gardenPlantingRepository.getPlantedGardensByName(plantName)
        }
    .asLiveData()

    fun setPlantName(name : String) {
        plantName.value = name
    }

    fun clearPlantName() {
        plantName.value = ""
    }
}
