package com.github.henriquechsf.syscredentialapp.util

import android.app.AlertDialog
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.google.android.material.snackbar.Snackbar

fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(
        requireContext(),
        message,
        duration
    ).show()
}

fun View.snackBar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration)
        .setAction("OK") {}
        .show()
}

fun Fragment.alertRemove(onConfirm: () -> Unit) {
    AlertDialog.Builder(context)
        .setTitle("Warning")
        .setMessage("Should remove register?")
        .setIcon(R.drawable.ic_warning)
        .setNegativeButton("No") { _, _ -> }
        .setPositiveButton("Yes") { _, _ -> onConfirm() }
        .create().show()
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun loadImage(
    imageView: ImageView,
    path: String,
    extension: String
) {
    Glide.with(imageView.context)
        .load("$path.$extension")
        .into(imageView)
}
