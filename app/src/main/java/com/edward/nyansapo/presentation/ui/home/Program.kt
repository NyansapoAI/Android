package com.nyansapo.presentation.ui.home

data class Program(override val number: String):Organisation {

    constructor() : this("")

    override var name: String? = null
}

