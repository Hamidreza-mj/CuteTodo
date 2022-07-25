package ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hlv.cute.todo.databinding.ItemDropdownBinding
import model.Category

class DropDownCategoryAdapter(
    private val context: Context,
    private val onClickCategory: (Category) -> Unit
) : RecyclerView.Adapter<DropDownCategoryAdapter.ViewHolder>() {

    val differ: AsyncListDiffer<Category>

    init {
        val diffCallback: DiffUtil.ItemCallback<Category> =
            object : DiffUtil.ItemCallback<Category>() {
                override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                    return oldItem.compareTo(newItem) == 0
                }
            }
        differ = AsyncListDiffer(this, diffCallback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDropdownBinding.inflate(
            LayoutInflater.from(
                context
            ), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position], onClickCategory)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(private val binding: ItemDropdownBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category, onClickCategory: (Category) -> Unit) {
            binding.txt.text =
                if (category.id == 0 && category.name == null) "-- بدون دسته‌بندی --" else category.name

            binding.txt.setOnClickListener { onClickCategory(category) }
        }
    }
}