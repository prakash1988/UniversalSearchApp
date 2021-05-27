package com.example.universalsearchapp.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.universalsearchapp.data.User
import com.example.universalsearchapp.databinding.UserItemBinding
import javax.inject.Inject

class UserAdapter @Inject constructor() :  RecyclerView.Adapter<UserAdapter.UserViewHolder>(),Filterable  {

     var contactListFiltered: ArrayList<User> = ArrayList()
    var userList : ArrayList<User> = ArrayList()

    fun updateList(userList : ArrayList<User>){
        contactListFiltered = userList
        this.userList = userList
    }

    class UserViewHolder(private val itemBinding: UserItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun binds(user : User){
            itemBinding.txtUserFname.text = user.name
            itemBinding.txtUserDesc.text = user.desc;
            itemBinding.imgUserAvatar.scaleType= ImageView.ScaleType.FIT_XY
            Glide.with(itemBinding.root.context).load(user.image).into(itemBinding.imgUserAvatar)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding: UserItemBinding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        holder.binds(userList?.get(position)!!)
    }

    override fun getItemCount(): Int = if (contactListFiltered.size >0) contactListFiltered.size else 0

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) contactListFiltered = userList else {
                    val filteredList = ArrayList<User>()
                    userList
                        .filter { it.name!!.toLowerCase().contains(charString.toLowerCase()) or it.name!!.contains(constraint!!) }
                        .forEach { filteredList.add(it) }
                    contactListFiltered = filteredList
                }
                return FilterResults().apply { values = contactListFiltered }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                contactListFiltered = results?.values as ArrayList<User>
                notifyDataSetChanged()
            }
        }
    }
}