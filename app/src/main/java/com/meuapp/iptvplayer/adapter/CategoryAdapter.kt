package com.meuapp.iptvplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.meuapp.iptvplayer.databinding.ItemCategoryBinding
import com.meuapp.iptvplayer.models.CategoryModel

class CategoryAdapter(
    private val onClick: (CategoryModel) -> Unit
) : ListAdapter<CategoryModel, CategoryAdapter.VH>(DIFF) {

    private var selectedPos = 0

    inner class VH(val b: ItemCategoryBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(h: VH, position: Int) {
        val cat = getItem(position)
        h.b.tvCategoryName.text = cat.categoryName
        h.b.root.isSelected = position == selectedPos
        h.b.root.setOnClickListener {
            val prev = selectedPos
            selectedPos = h.adapterPosition
            notifyItemChanged(prev)
            notifyItemChanged(selectedPos)
            onClick(cat)
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<CategoryModel>() {
            override fun areItemsTheSame(a: CategoryModel, b: CategoryModel) = a.categoryId == b.categoryId
            override fun areContentsTheSame(a: CategoryModel, b: CategoryModel) = a == b
        }
    }
}
