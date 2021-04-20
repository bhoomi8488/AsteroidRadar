package com.udacity.asteroidradar.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AstoridListBinding
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, MainViewModel.Factory(activity.application)).get(MainViewModel::class.java)
    }
    var viewModelAdapter: AsteroidAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAsteroids()
    }

    private fun observeAsteroids() {
        viewModel.asteroidList.observe(viewLifecycleOwner, Observer<List<Asteroid>> { asteroids ->
            asteroids?.apply {
                Log.i("size====","=="+asteroids.size)
                viewModelAdapter?.asteroids = asteroids
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        /**
         * RecyclerView Adapter for converting a list of Video to cards.
         */

        viewModelAdapter = AsteroidAdapter(VideoClick {
            this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
            // Tell the ViewModel we've made the navigate call to prevent multiple navigation
            viewModel.displayPropertyDetailsComplete()
        })

        binding.root.findViewById<RecyclerView>(R.id.asteroid_recycler).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //Fill the adapter according to menu option click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.show_today_menu -> {
                    viewModel.AsteroidToday()
                    observeAsteroids()
                    return true
                }
                R.id.show_week_menu -> {
                    viewModel.AsteroidWeek()
                    observeAsteroids()
                    return true
                }
                else -> {
                    viewModel.AsteroidAll()
                    observeAsteroids()
                    return true
                }
            }
    }
}

class VideoClick(val block: (Asteroid) -> Unit) {
    /**
     * Called when a video is clicked
     *
     * @param video the video that was clicked
     */
    fun onClick(asteroid: Asteroid) = block(asteroid)
}

/**
 * RecyclerView Adapter for setting up data binding on the items in the list.
 */
class AsteroidAdapter(val callback: VideoClick) : RecyclerView.Adapter<DevByteViewHolder>() {

    /**
     * The Asteroid that our Adapter will show
     */
    var asteroids: List<Asteroid> = emptyList()
        set(value) {
            field = value
            // For an extra challenge, update this to use the paging library.

            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevByteViewHolder {
        val withDataBinding: AstoridListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            DevByteViewHolder.LAYOUT,
            parent,
            false)
        return DevByteViewHolder(withDataBinding)
    }

    override fun getItemCount() = asteroids.size

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     */
    override fun onBindViewHolder(holder: DevByteViewHolder, position: Int) {
        holder.viewDataBinding.also {
            Log.i("position==","=="+position)
            it.asteroid = asteroids[position]
            it.videoCallback = callback
        }
    }

}

/**
 * ViewHolder for DevByte items. All work is done by data binding.
 */
class DevByteViewHolder(val viewDataBinding: AstoridListBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.astorid_list
    }
}
