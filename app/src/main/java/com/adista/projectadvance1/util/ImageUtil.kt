package com.adista.projectadvance1.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.adista.projectadvance1.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop

object ImageUtil {
    private const val BASE_URL = Constants.BASE_IMAGE_URL

    fun getFullImageUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        return if (path.startsWith("http")) path else BASE_URL + path
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(view: ImageView, url: String?) {
        Glide.with(view.context)
            .load(url)
            .transform(CircleCrop())
            .into(view)
    }
}
