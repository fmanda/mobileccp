package com.ts.mobileccp.global

import com.ts.mobileccp.db.entity.LoginInfo
import com.ts.mobileccp.db.entity.Setting

object AppVariable {

    var loginInfo: LoginInfo = LoginInfo(
        0,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    )

    var setting: Setting = Setting(
        0,
        "http://192.168.9.62:8000/public/"  //default
    )

//    var apiurl = "http://192.168.195.186:8000/public/"
}