package com.hsg.localmarket.data

data class ApkList(var itemList:Array<Apk>)
data class Apk(var applicationName:String, var applicationVersion:String, var applicationIcon:String)
