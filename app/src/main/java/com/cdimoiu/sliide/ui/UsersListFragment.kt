package com.cdimoiu.sliide.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cdimoiu.sliide.R
import com.cdimoiu.sliide.databinding.AddUserLayoutBinding
import com.cdimoiu.sliide.databinding.FragmentUsersListBinding
import com.cdimoiu.sliide.models.ResultStatus
import com.cdimoiu.sliide.models.User
import com.cdimoiu.sliide.models.UserGender
import com.cdimoiu.sliide.models.UserStatus
import com.cdimoiu.sliide.viewmodels.UsersListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UsersListFragment : Fragment() {

    private lateinit var binding: FragmentUsersListBinding
    private lateinit var adapter: UsersAdapter

    private val viewModel: UsersListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUsersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.usersRecyclerView.addItemDecoration(
            DividerItemDecoration(
                binding.usersRecyclerView.context,
                (binding.usersRecyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )

        adapter = UsersAdapter(arrayListOf()) { userId -> setupRemoveUserAlertDialog(userId) }
        binding.usersRecyclerView.adapter = adapter
        binding.swipeContainer.setOnRefreshListener {
            viewModel.getUsers()
            binding.swipeContainer.isRefreshing = false
        }

        binding.addUserButton.setOnClickListener { setupAddUserAlertDialog() }
    }

    private fun setupAddUserAlertDialog() {
        val addUserBinding = AddUserLayoutBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(requireActivity())
            .setView(addUserBinding.root)
            .setTitle(getString(R.string.add_user))
            .setPositiveButton(getString(R.string.add)) { _, _ ->
                viewModel.addUser(
                    User(
                        addUserBinding.userIdEditText.text.toString(),
                        addUserBinding.userNameEditText.text.toString(),
                        addUserBinding.userEmailEditText.text.toString(),
                        if (addUserBinding.maleRadioButton.isChecked) UserGender.MALE.type else UserGender.FEMALE.type,
                        if (addUserBinding.activeSwitch.isChecked) UserStatus.ACTIVE.type else UserStatus.INACTIVE.type

                    )
                )
            }
            .setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ -> dialogInterface.dismiss() }
            .create()
            .show()
    }

    private fun setupRemoveUserAlertDialog(userId: String) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.user_removal_confirmation))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> viewModel.removeUser(userId) }
            .setNegativeButton(getString(R.string.no)) { dialogInterface, _ -> dialogInterface.dismiss() }
            .create()
            .show()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.usersList.collect { resource ->
                    setUiElementsByStatus(resource.status)
                    resource.data?.let { users -> adapter.addUsers(users) }
                    showMessage(resource.message)
                }
            }
        }
    }

    private fun setUiElementsByStatus(status: ResultStatus) {
        when (status) {
            ResultStatus.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.usersRecyclerView.visibility = View.GONE
            }
            ResultStatus.SUCCESS, ResultStatus.ERROR -> {
                binding.usersRecyclerView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showMessage(message: String?) {
        message?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }
}