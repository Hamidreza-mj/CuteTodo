package ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hlv.cute.todo.databinding.ItemTodoBinding
import model.Priority
import model.Todo
import utils.TextHelper

class TodoAdapter(
    private val context: Context,
    private val onCheckChangedListener: (todoID: Int) -> Unit,
    private val onClickMenuListener: (todo: Todo, view: View) -> Unit
) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    val differ: AsyncListDiffer<Todo>

    init {
        val diffCallback: DiffUtil.ItemCallback<Todo> = object : DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem.compareTo(newItem) == 0
            }
        }

        differ = AsyncListDiffer(this, diffCallback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTodoBinding.inflate(
            LayoutInflater.from(
                context
            ), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position], onCheckChangedListener, onClickMenuListener)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            todo: Todo,
            onCheckChangedListener: (todoID: Int) -> Unit,
            onClickMenuListener: (todo: Todo, view: View) -> Unit
        ) {
            //must be set null
            //to avoid when recyclerview scroll, default implemented interface body called!
            binding.aChkBoxTitle.setOnCheckedChangeListener(null)

            //root.setTransitionName("t-" + todo.getId());

            //for first run
            binding.aChkBoxTitle.isChecked = todo.isDone

            binding.aChkBoxTitle.text = todo.title

            if (todo.isDone)
                TextHelper.addLineThrough(binding.aChkBoxTitle) else TextHelper.removeLineThrough(
                binding.aChkBoxTitle
            )

            binding.aChkBoxTitle.setOnCheckedChangeListener { compoundButton: CompoundButton, checked: Boolean ->
                //after checked
                if (compoundButton.isPressed) {
                    todo.isDone = checked

                    if (todo.isDone)
                        TextHelper.addLineThrough(binding.aChkBoxTitle)
                    else
                        TextHelper.removeLineThrough(binding.aChkBoxTitle)

                    onCheckChangedListener(todo.id)
                }
            }


            binding.aImgMenu.setOnClickListener { onClickMenuListener(todo, it) }

            if (todo.category != null) {
                binding.txtCategory.visibility = View.VISIBLE
                binding.txtCategory.text = todo.category
            } else {
                binding.txtCategory.visibility = View.GONE
            }

            if (todo.arriveDate != 0L) {
                binding.lytDate.visibility = View.VISIBLE
                binding.txtDate.text = todo.dateTime!!.persianDate
                binding.txtClock.text = todo.clock
            } else {
                binding.lytDate.visibility = View.GONE
            }

            when (todo.priority) {
                Priority.LOW -> {
                    binding.txtLowPriority.visibility = View.VISIBLE
                    binding.txtNormalPriority.visibility = View.GONE
                    binding.txtHighPriority.visibility = View.GONE
                }

                Priority.NORMAL -> {
                    binding.txtNormalPriority.visibility = View.VISIBLE
                    binding.txtLowPriority.visibility = View.GONE
                    binding.txtHighPriority.visibility = View.GONE
                }

                Priority.HIGH -> {
                    binding.txtHighPriority.visibility = View.VISIBLE
                    binding.txtLowPriority.visibility = View.GONE
                    binding.txtNormalPriority.visibility = View.GONE
                }

                else -> {
                    binding.txtLowPriority.visibility = View.VISIBLE
                    binding.txtNormalPriority.visibility = View.GONE
                    binding.txtHighPriority.visibility = View.GONE
                }
            }
        }

    }
}