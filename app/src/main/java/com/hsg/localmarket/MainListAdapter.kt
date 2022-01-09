package com.hsg.localmarket

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.hsg.localmarket.data.Apk
import com.hsg.localmarket.service.DownloadService
import com.hsg.localmarket.service.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainListAdapter (val context: Context, val apkList: Array<Apk>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View = LayoutInflater.from(context).inflate(R.layout.main_item, null)

        val icon = view.findViewById<ImageView>(R.id.icon)
        val applicationName = view.findViewById<TextView>(R.id.txt_name)
        val applicationVersion = view.findViewById<TextView>(R.id.txt_version)
        val installButton = view.findViewById<Button>(R.id.btn_install)

        val apk = apkList[position]

        val iconUrl = "${RetrofitService.BASE_URL}/upload/icon/${apk.applicationIcon}"

        CoroutineScope(Dispatchers.Main).launch {
            val bitmap = withContext(Dispatchers.IO){
                loadIcon(iconUrl)
            }
            icon.setImageBitmap(bitmap)
        }

        applicationName.text = apk.applicationName
        applicationVersion.text = apk.applicationVersion

        val button = installButton.findViewById<Button>(R.id.btn_install)
        button.setOnClickListener {
            val intent = Intent(context, DownloadService::class.java)
            intent.putExtra("APKNAME", apk.applicationName)
            context.startService(intent)
        }

        return view
    }

    fun loadIcon(iconUrl:String): Bitmap? {
        val bmp: Bitmap? = null
        try {
            val url = URL(iconUrl)
            val stream = url.openStream()
            return BitmapFactory.decodeStream(stream)
        } catch (e: Exception) {}
        return bmp;
    }

    override fun getItem(position: Int): Any {
        return apkList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return apkList.size
    }
}