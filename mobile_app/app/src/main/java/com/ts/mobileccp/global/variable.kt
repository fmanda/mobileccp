package com.ts.mobileccp.global

import com.ts.mobileccp.db.entity.LoginInfo

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
    var apiurl = "http://192.168.195.186:8000/public/"
}