package com.github.henriquechsf.syscredentialapp.presenter.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferfalk.simplesearchview.SimpleSearchView
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.databinding.FragmentUserListBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.presenter.base.ResultState
import com.github.henriquechsf.syscredentialapp.util.hide
import com.github.henriquechsf.syscredentialapp.util.initToolbar
import com.github.henriquechsf.syscredentialapp.util.show
import com.github.henriquechsf.syscredentialapp.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserListFragment : BaseFragment<FragmentUserListBinding>() {

    private val userListViewModel: UserListViewModel by viewModels()

    private val userListAdapter by lazy { UserListAdapter() }

    private var userList = listOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentUserListBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        getUserList()
        setupRecyclerView()
        clickItemAdapter()
        configSearchView()
    }

    private fun clickItemAdapter() {
        userListAdapter.setOnClickListener { user ->
            val action = UserListFragmentDirections.actionUserListFragmentToUserFormFragment(user)
            findNavController().navigate(action)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_register, menu)

        val item = menu.findItem(R.id.menu_search)
        binding.searchView.setMenuItem(item)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                //findNavController().navigate(R.id.action_personsListFragment_to_personFormFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() = with(binding) {
        rvListUsers.apply {
            adapter = userListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun getUserList() {
        userListViewModel.getProfileList().observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is ResultState.Loading -> {
                    binding.progressBar.show()
                }
                is ResultState.Success -> {
                    binding.progressBar.hide()

                    stateView.data?.let {
                        binding.rvListUsers.show()

                        userList = it
                        userListAdapter.users = userList
                    }
                }
                is ResultState.Error -> {
                    binding.progressBar.hide()
                    toast(message = stateView.message ?: "")
                }
                is ResultState.Empty -> {
                    binding.progressBar.hide()
                    binding.tvEmptyUsers.show()
                }
            }
        }
    }

    private fun configSearchView() = with(binding) {
        searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return if (newText.isNotEmpty()) {
                    val newList = userList.filter { user ->
                        user.name.contains(newText, true)
                    }
                    userListAdapter.users = newList
                    true
                } else {
                    userListAdapter.users = userList
                    false
                }
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })



        searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                userListAdapter.users = userList
            }

            override fun onSearchViewClosedAnimation() {}
            override fun onSearchViewShown() {}
            override fun onSearchViewShownAnimation() {}
        })
    }
}