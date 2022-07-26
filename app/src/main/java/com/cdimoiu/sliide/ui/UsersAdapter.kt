package com.cdimoiu.sliide.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cdimoiu.sliide.R
import com.cdimoiu.sliide.databinding.UserItemBinding
import com.cdimoiu.sliide.models.User
import com.cdimoiu.sliide.models.UserGender
import com.cdimoiu.sliide.models.UserStatus
import com.cdimoiu.sliide.ui.UsersAdapter.UsersViewHolder

class UsersAdapter(
    private val users: ArrayList<User>,
    private val onLongClick: (String) -> Unit
) : RecyclerView.Adapter<UsersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersViewHolder(parent, binding, onLongClick)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(users[position])
    }

    fun addUsers(users: List<User>) {
        this.users.apply {
            clear()
            addAll(users)
            notifyDataSetChanged()
        }
    }

    class UsersViewHolder(
        private val viewGroup: ViewGroup,
        private val binding: UserItemBinding,
        private val onLongClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.userNameTextView.text = user.name
            binding.userEmailTextView.text = user.email
            binding.userIconImageView.apply {
                setImageResource(getGenderIcon(user.gender))
                setColorFilter(getStatusColor(user.status))
            }

            binding.root.setOnLongClickListener {
                onLongClick(user.id)
                true
            }
        }

        private fun getGenderIcon(gender: String): Int {
            return if (gender == UserGender.MALE.type) {
                R.drawable.ic_male_user
            } else {
                R.drawable.ic_female_user
            }
        }

        private fun getStatusColor(status: String): Int {
            return if (status == UserStatus.ACTIVE.type) {
                viewGroup.context.getColor(R.color.light_blue)
            } else {
                viewGroup.context.getColor(R.color.light_grey)
            }
        }
    }
}