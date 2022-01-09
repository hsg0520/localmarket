package com.hsg.localmarket.service

import android.app.IntentService
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hsg.localmarket.BuildConfig
import okhttp3.ResponseBody
import java.io.*
import kotlin.math.pow

class DownloadService : IntentService("Download Service") {
    private var totalFileSize = 0.0
    private var apkName = ""

    override fun onHandleIntent(intent: Intent?) {
        apkName = intent?.getStringExtra("APKNAME").toString() + ".apk"
        Log.e("APKNAME", apkName)
        initDownload()
    }

    private fun initDownload() {
        val downloadUrl = "upload/$apkName"
        val request = RetrofitService.API.downloadFile(downloadUrl)
        try {
            downloadFile(request?.execute()?.body(),apkName)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun downloadFile(body: ResponseBody?, filename:String) {
        var count: Int
        val data = ByteArray(1024 * 4)
        val fileSize = body!!.contentLength()
        val bis: InputStream = BufferedInputStream(body.byteStream(), 1024 * 8)

        val savePath = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + "DOWNLOADS")

        if (!savePath.exists()) {
            try {
                savePath.mkdirs()
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
        val outputFile = File(savePath, filename)

        val output: OutputStream = FileOutputStream(outputFile)
        var total: Long = 0
        val startTime = System.currentTimeMillis()
        var timeCount = 1
        while (bis.read(data).also { count = it } != -1) {
            total += count.toLong()
            totalFileSize = (fileSize / 1024.0.pow(2.0))
            val current = total / 1024.0.pow(2.0)
            val progress = total * 100 / fileSize.toDouble()
            val currentTime = System.currentTimeMillis() - startTime
            val download = DownloadProgress()
            download.totalFileSize = totalFileSize
            if (currentTime > 1000 * timeCount) {
                download.currentFileSize = current
                download.progress = progress
                sendNotification(download)
                timeCount++
            }
            output.write(data, 0, count)
        }
        onDownloadComplete()
        output.flush()
        output.close()
        bis.close()

        Thread.sleep(500L)

        installApplication()
    }

    private fun installApplication() {

        val apkPath =
            File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + "DOWNLOADS").toString() + "/$apkName"

        val apkUri =
            FileProvider.getUriForFile(
                applicationContext,
                BuildConfig.APPLICATION_ID + ".fileprovider", File(apkPath)
            )

        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")

        startActivity(intent)

    }

    private fun sendNotification(downloadProgress: DownloadProgress) {
        sendIntent(downloadProgress)
    }

    private fun sendIntent(downloadProgress: DownloadProgress) {
        val intent = Intent("message_progress")
        intent.putExtra("download", downloadProgress)
        LocalBroadcastManager.getInstance(this@DownloadService).sendBroadcast(intent)
    }

    private fun onDownloadComplete() {
        val download = DownloadProgress()
        download.progress = 100.0
        sendIntent(download)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
//        notificationManager.cancel(0);
    }
}