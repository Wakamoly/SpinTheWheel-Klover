package com.lucidsoftworksllc.spinthewheel.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.lucidsoftworksllc.spinthewheel.base.BaseViewModel
import java.util.*

object Extensions {

    fun Fragment.setTitle(title: String) {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.title = title
        }
    }

    fun Fragment.setDisplayHomeAsUpEnabled(bool: Boolean) {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
                bool
            )
        }
    }

    //animate changing the view visibility
    fun View.fadeIn() {
        this.visibility = View.VISIBLE
        this.alpha = 0f
        this.animate().alpha(1f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@fadeIn.alpha = 1f
            }
        })
    }

    //animate changing the view visibility
    fun View.fadeOut() {
        this.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@fadeOut.alpha = 1f
                this@fadeOut.visibility = View.GONE
            }
        })
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun Context.toastShort(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    fun Context.toastLong(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    fun View.visible(isVisible: Boolean){
        visibility = if(isVisible) View.VISIBLE else View.GONE
    }

    fun View.snackbar(message: String, actionText: String = "", action: (() -> Unit)? = null){
        if (Build.VERSION.SDK_INT >= 23) {
            val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
            action?.let {
                snackbar.setAction(actionText) {
                    it()
                }
            }
            snackbar.setActionTextColor(Color.parseColor("#45B431"))
            snackbar.setTextColor(Color.WHITE)
            val snackbarView = snackbar.view
            snackbarView.setBackgroundColor(Color.parseColor("#111111"))
            snackbar.show()
        }else{
            context?.toastLong(message)
        }
    }

    fun View.snackbarShort(message: String, actionText: String, action: (() -> Unit)? = null){
        if (Build.VERSION.SDK_INT >= 23) {
            val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
            action?.let {
                snackbar.setAction(actionText) {
                    it()
                }
            }
            snackbar.setActionTextColor(Color.parseColor("#45B431"))
            snackbar.setTextColor(Color.WHITE)
            val snackbarView = snackbar.view
            snackbarView.setBackgroundColor(Color.parseColor("#111111"))
            snackbar.show()
        }else{
            context?.toastShort(message)
        }
    }

    fun Fragment.startBaseObservables(viewModel: BaseViewModel){
        viewModel.showToast.observe(this, {
            this.activity?.toastShort(it)
        })
    }

}

