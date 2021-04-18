package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.database.PictureDatabase
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.detail.DetailFragmentArgs

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        val dataSource = PictureDatabase.getInstance(activity.application).sleepDatabaseDao

        ViewModelProvider(this, MainViewModel.Factory(dataSource,activity.application)).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this


        binding.viewModel = viewModel

        binding.asteroidRecycler.adapter = AsteroidAdapter(AsteroidAdapter.OnClickListener {
            viewModel.displayPropertyDetails(it)
        })

        setHasOptionsMenu(true)

        viewModel.navigateToSelectedProperty.observe(this.viewLifecycleOwner, Observer {
            if ( null != it ) {
                // Must find the NavController from the Fragment
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                viewModel.displayPropertyDetailsComplete()
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}
