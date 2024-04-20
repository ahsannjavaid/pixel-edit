package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.canhub.cropper.CropImage.CancelledResult.uriContent
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import java.io.OutputStream

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) {result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
        }
        else {
            val exception = result.error
        }
    }

    if (imageUri != null) {
        if (Build.VERSION.SDK_INT < 28) {
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        }
        else {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri!!)
            bitmap = ImageDecoder.decodeBitmap(source)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight(0.1f)
                .fillMaxWidth()
                .background(Color.Black)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { }) {
                Text("Back")
            }

            Button(onClick = {
                saveBitmapToFile(context, bitmap)
            }) {
                Text("Save")
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.DarkGray),
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap?.asImageBitmap()!!,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxHeight(0.1f)
                .fillMaxWidth()
                .background(Color.Black)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                val cropOption = CropImageContractOptions(uriContent, CropImageOptions())
                imageCropLauncher.launch(cropOption)
            }) {
                Text("Select Image and Crop")
            }

            Button(onClick = { }) {
                Text("Polygon Crop")
            }
        }
    }
}

private fun saveBitmapToFile(context: Context, bitmap: Bitmap?) {
    bitmap?.let {
        val fileName = "cropped_image.jpg"
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
                it.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }
}