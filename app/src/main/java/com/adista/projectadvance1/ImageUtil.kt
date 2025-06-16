package com.adista.projectadvance1

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop

object ImageUtil {
    private const val BASE_URL = "http://192.168.1.6:8000/storage/"

    fun getFullImageUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        return if (path.startsWith("http")) path else BASE_URL + path
    }


    @BindingAdapter("imageUrl")
    fun loadImage(view: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            Glide.with(view.context)
                .load(url)
                .transform(CircleCrop())
                .into(view)
        }
    }
}
