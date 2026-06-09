package com.example.foodypet.network

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull

object MultipartUtil {

    fun createImagePart(
        context: Context,
        uri: Uri,
        partName: String = "image"
    ): MultipartBody.Part {
        val contentResolver = context.contentResolver

        val mimeType = contentResolver.getType(uri) ?: "image/*"
        val fileName = getFileName(context, uri)

        val bytes = contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes()
        } ?: throw IllegalArgumentException("이미지 파일을 읽을 수 없습니다.")

        val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            partName,
            fileName,
            requestBody
        )
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var fileName = "community_post_image.jpg"

        val cursor = context.contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            if (nameIndex >= 0 && it.moveToFirst()) {
                fileName = it.getString(nameIndex)
            }
        }

        return fileName
    }
}