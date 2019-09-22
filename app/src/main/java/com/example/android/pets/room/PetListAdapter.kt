package com.example.android.pets.room

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.pets.R
import com.example.android.pets.room.PetListAdapter.ViewHolder
import kotlinx.android.synthetic.main.list_item.view.*


class PetListAdapter(private val context: Context) : ListAdapter<Pet, ViewHolder>(DIFF_UTIL_CALLBACK), Filterable {

    private var mFilteredPetEntries : MutableList<Pet> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

//    fun addPet(pet: Pet) {
//        if (!pets.contains(pet)) {
//            pets.add(pet)
//            notifyItemInserted(pets.size)
//        }
//    }

    fun getPetAt(position: Int): Pet {
        return this.getItem(position)
    }

    companion object {
        val DIFF_UTIL_CALLBACK = object : DiffUtil.ItemCallback<Pet>() {

            override fun areItemsTheSame(oldItem: Pet, newItem: Pet): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Pet, newItem: Pet): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            val petDao = PetDatabase.getInstance(context).petDao()

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                var filteredList: MutableList<Pet> = ArrayList()
                filteredList = when {
                    charString.isEmpty() -> petDao.getPets().toMutableList()
                    charString.toLowerCase() == "dog" -> petDao.searchType(0).toMutableList()
                    charString.toLowerCase() == "cat" -> petDao.searchType(1).toMutableList()
                    charString.toLowerCase() == "fish" -> petDao.searchType(2).toMutableList()
                    charString.toLowerCase() == "bird" -> petDao.searchType(3).toMutableList()
                    charString.toLowerCase() == "other" -> petDao.searchType(4).toMutableList()
                    else -> {
                        val searchQuery = "%$charString%"
                        petDao.searchPet(searchQuery).toMutableList()
                    }
                }

                val filterResults = Filter.FilterResults()
                mFilteredPetEntries = filteredList
                filterResults.values = mFilteredPetEntries
                Log.e("AdapterTest","filter result count = ${filterResults.count}")
                Log.e("AdapterTest","FilteredList $filteredList")
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                Log.e("AdapterTest","result count = ${results!!.count}")
                mFilteredPetEntries = results.values as MutableList<Pet>
                submitList(mFilteredPetEntries)
                notifyDataSetChanged()
            }

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var id = -1
        val context = itemView.context!!

        fun bind(pet: Pet) {
            id = pet.id
            itemView.name.text = pet.name
            itemView.summary.text = if (!pet.breed.isEmpty()) pet.breed else "Unknown breed"

            itemView.type.setImageResource(
                when (pet.type) {
                    0 -> R.drawable.ic_dog_selected

                    1 -> R.drawable.ic_cat_selected

                    2 -> R.drawable.ic_fish_selected

                    3 -> R.drawable.ic_bird_selected

                    4 -> R.drawable.ic_other_selected

                    else -> R.drawable.ic_other
                }
            )
        }
    }
}