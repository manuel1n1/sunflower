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

package com.google.samples.apps.sunflower

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.samples.apps.sunflower.adapters.GardenPlantingAdapter
import com.google.samples.apps.sunflower.adapters.PLANT_LIST_PAGE_INDEX
import com.google.samples.apps.sunflower.databinding.FragmentGardenBinding
import com.google.samples.apps.sunflower.utilities.KeyBoard
import com.google.samples.apps.sunflower.viewmodels.GardenPlantingListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GardenFragment : Fragment() {

    private lateinit var binding: FragmentGardenBinding

    private val viewModel: GardenPlantingListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGardenBinding.inflate(inflater, container, false)
        val adapter = GardenPlantingAdapter()
        binding.gardenList.adapter = adapter

        binding.addPlant.setOnClickListener {
            navigateToPlantListPage()
        }

        subscribeUi(adapter, binding)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        with(viewModel){
            clearPlantName()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_garden, menu)
        val menuItem = menu.findItem(R.id.garden_search_bar)
        if(menuItem != null) {
            val keyboard = KeyBoard(context)
            val searchView = menuItem.actionView as SearchView
            searchView.setOnQueryTextFocusChangeListener { view, focus ->
                if(focus)
                    keyboard.showKeyboard()
                else
                    keyboard.hideKeyboard(view)
            }
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
                }
                override fun onQueryTextChange(text: String?): Boolean {
                    if (text != null)
                        searchByName(text)
                    return true
                }
            })
            menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    searchView.isIconified = false
                    searchView.requestFocusFromTouch()
                    return true
                }
                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    searchView.setQuery("", true)
                    return true
                }
            })
        }
    }

    private fun subscribeUi(adapter: GardenPlantingAdapter, binding: FragmentGardenBinding) {
        viewModel.plantAndGardenPlantings.observe(viewLifecycleOwner) { result ->
            binding.hasPlantings = result.isNotEmpty()
            adapter.submitList(result) {
                // At this point, the content should be drawn
                activity?.reportFullyDrawn()
            }
        }
    }

    // TODO: convert to data binding if applicable
    private fun navigateToPlantListPage() {
        requireActivity().findViewById<ViewPager2>(R.id.view_pager).currentItem =
            PLANT_LIST_PAGE_INDEX
    }

    private fun searchByName(plantName: String) {
        with(viewModel) {
            if(plantName.isBlank()) {
                clearPlantName()
            } else {
                setPlantName(plantName)
            }
        }
    }
}
