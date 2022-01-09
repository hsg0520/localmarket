package com.hsg.localmarket

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hsg.localmarket.data.Apk
import com.hsg.localmarket.databinding.ActivityMainBinding
import com.hsg.localmarket.service.DownloadProgress
import com.hsg.localmarket.service.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        registerReceiver()

    }

    override fun onResume() {
        super.onResume()
        setApkList()
        viewBinding.txtProgress.text="설치할 앱을 선택하세요."
        viewBinding.progressBar.progress = 0
    }

    private fun setApkList(){
        CoroutineScope(Dispatchers.Main).launch {
            val apkList = getApkList()
            val apkAdapter = MainListAdapter(applicationContext, apkList)
            viewBinding.listView.adapter = apkAdapter
        }
    }

    private suspend fun getApkList(): Array<Apk> {
        var apkList : Array<Apk>
        return withContext(Dispatchers.IO) {
            apkList = try {
                RetrofitService.API.getVersion().execute().body()!!.itemList
            } catch (e: Exception) {
                emptyArray()
            }
            apkList
        }
    }

    private fun registerReceiver() {
        val bManager = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction("message_progress")
        bManager.registerReceiver(broadcastReceiver, intentFilter)
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "message_progress") {

                val downloadProgress: DownloadProgress? = intent.getParcelableExtra("download")

                val df = DecimalFormat("#0.00")
                val progress = df.format(downloadProgress?.progress?.toInt() ?: 0)
                if (downloadProgress != null) {
                    viewBinding.txtProgress.text="다운로드 중입니다.(${progress}%)"
                    viewBinding.progressBar.progress = downloadProgress.progress.toInt()

                    if(progress.toString()=="100.00"){
                        viewBinding.txtProgress.text="다운로드가 완료 되었습니다."

                    }
                }
            }
        }
    }
}