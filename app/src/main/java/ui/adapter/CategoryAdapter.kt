package ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hlv.cute.todo.databinding.ItemCategoryBinding
import model.Category

class CategoryAdapter(
    private val context: Context,
    private val onClickMenuListener: (category: Category, anchor: View, wholeItem: View, coordinatePoint: Point?) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

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
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(
                context
            ), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position], onClickMenuListener)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun bind(
            category: Category,
            onClickMenuListener: (category: Category, anchor: View, wholeItem: View, coordinatePoint: Point?) -> Unit
        ) {
            binding.aImgMenu.setOnClickListener {
                onClickMenuListener(category, it, binding.root, null)
            }

            var rawClickPoint: Point? = null
            binding.root.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN)
                    rawClickPoint = Point(event.rawX.toInt(), event.rawY.toInt())

                return@setOnTouchListener false
            }

            binding.root.setOnClickListener {
                onClickMenuListener(category, it, binding.root, rawClickPoint)
            }

            binding.txtTitle.text = category.name
        }
    }

}