package com.example.foodypet.community.adapter

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class CommunityPostImageAdapter(
    private val onImageClick: () -> Unit
) : RecyclerView.Adapter<CommunityPostImageAdapter.ImageViewHolder>() {

    private val images = mutableListOf<Uri>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val container = FrameLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            clipChildren = false
            clipToPadding = false
        }

        val imageView = ImageView(parent.context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

            scaleType = ImageView.ScaleType.CENTER_CROP
            isClickable = true
            isFocusable = true
        }

        container.addView(imageView)

        return ImageViewHolder(container, imageView, onImageClick)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

    fun submitImages(newImages: List<Uri>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }

    class ImageViewHolder(
        container: FrameLayout,
        private val imageView: ImageView,
        private val onImageClick: () -> Unit
    ) : RecyclerView.ViewHolder(container) {

        fun bind(uri: Uri) {
            imageView.setImageURI(uri)

            imageView.setOnClickListener {
                onImageClick()
            }
        }
    }

    companion object {
        private fun dpToPx(parent: ViewGroup, dp: Int): Int {
            return (dp * parent.resources.displayMetrics.density).toInt()
        }
    }
}