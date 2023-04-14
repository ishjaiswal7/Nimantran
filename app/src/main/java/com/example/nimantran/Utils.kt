package com.example.nimantran

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun getTempFileUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat.getDateTimeInstance().format(Date())
    val tempFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", tempFile)
}

fun shareImage(
    ctx: Context,
    packageName: String,
    appName: String,
    uri: String,
    isDefaultShare: Boolean = false
) {
    var intent = Intent(Intent.ACTION_SEND)
    intent.setPackage(packageName);
    intent.setType("image/jpeg");
    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri))
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

    try {
        if (isDefaultShare) {
            startActivity(ctx, Intent.createChooser(intent, "Share image"), null)
        } else {
            startActivity(ctx, intent, null)
        }
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(
            ctx,
            "$appName have not been installed.",
            Toast.LENGTH_SHORT
        ).show()
    }
}