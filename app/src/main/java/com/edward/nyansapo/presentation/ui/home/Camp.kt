package com.nyansapo.presentation.ui.home

data class Camp(override val number:String):Organisation{

    constructor():this("")
    override var name:String?=null
}

