package com.ruthal.live.cricket.app.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ruthal.live.cricket.app.constants.ApplicationConstants.myUserCheck1
import com.ruthal.live.cricket.app.constants.ApplicationConstants.passValue
import com.ruthal.live.cricket.app.constants.ApplicationConstants.passwordVal
import com.ruthal.live.cricket.app.constants.UnchangedConstants.userBaseExtraDel1
import com.ruthal.live.cricket.app.constants.UnchangedConstants.userBaseExtraDel2
import com.ruthal.live.cricket.app.models.DataModel
import org.json.JSONObject

class StreamingResponse {

    fun parseResponse(data: String?): DataModel {
        val stringValue = data?.let { it1 -> StreamingUtils.saveResponse(it1, passValue) }
        var jobj: JSONObject? = JSONObject()
        jobj = stringValue?.let { JSONObject(it) }
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()
        val date = gson.fromJson(jobj.toString(), DataModel::class.java)
        return date
    }

    fun getExtraValuesFromResponse(date: DataModel?){
        if (date?.extra_1.toString().isNotEmpty()) {
            date?.extra_1 = StreamingUtils.decryptBase64(date?.extra_1)
            val encrypt = date?.extra_1.toString().trim()
            val yourArray: List<String> =
                encrypt.split(userBaseExtraDel2)
            myUserCheck1 = yourArray[0].trim()
            val myRVal: List<String> =
                yourArray[1].split(userBaseExtraDel1)
            passwordVal = myRVal[0].trim()
        }
    }
}