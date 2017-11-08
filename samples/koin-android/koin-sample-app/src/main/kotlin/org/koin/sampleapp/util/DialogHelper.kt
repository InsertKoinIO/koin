package org.koin.sampleapp.util


import android.support.v7.app.AlertDialog.Builder
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout.LayoutParams
import org.koin.sampleapp.R


object DialogHelper {

    val HINT = "i.e: Paris, France"

    fun locationDialog(view: View, callback: (String) -> Unit) {

        val input = EditText(view.context)
        input.hint = HINT

        val builder = Builder(view.context)

        builder.setMessage(R.string.location_title)
                .setPositiveButton(R.string.search) { dialog, id ->
                    dialog.dismiss()
                    callback.invoke(input.text.toString().trim { it <= ' ' }.replace(" ", ""))
                }
                .setNegativeButton(R.string.cancel
                ) { dialog, id ->
                    // User cancelled the dialog
                    dialog.dismiss()
                }

        // Create the AlertDialog object and return it
        val dialog = builder.create()
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        input.layoutParams = lp
        dialog.setView(input)
        dialog.show()
    }
}
