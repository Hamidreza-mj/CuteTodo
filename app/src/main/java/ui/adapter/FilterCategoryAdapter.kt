package ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hlv.cute.todo.databinding.ItemCategoryFilterBinding
import model.Category

class FilterCategoryAdapter(private val context: Context) :
    RecyclerView.Adapter<FilterCategoryAdapter.ViewHolder>() {

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
        val binding = ItemCategoryFilterBinding.inflate(
            LayoutInflater.from(
                context
            ), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (differ.currentList[position] != null) holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    val selectedItems: List<Category?>
        get() {
            val selected: MutableList<Category> = ArrayList()
            for (i in differ.currentList.indices) {
                val temp = differ.currentList[i]

                if (temp?.isSelectedForFilter == true)
                    selected.add(temp)
            }

            return selected
        }

    inner class ViewHolder(private val binding: ItemCategoryFilterBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {

        fun bind(category: Category?) {
            if (category == null)
                return

            binding.root.requestLayout()

            binding.chip.requestLayout()

            binding.chip.isChecked = category.isSelectedForFilter

            with(category) {
                if (id == 0 && name == null)
                    binding.chip.text = "-بدون دسته‌بندی-"
                else
                    binding.chip.text = name
            }


            binding.chip.setOnCheckedChangeListener(null)

            binding.chip.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
                if (buttonView.isPressed)
                    category.isSelectedForFilter = isChecked
            }
        }
    }
}