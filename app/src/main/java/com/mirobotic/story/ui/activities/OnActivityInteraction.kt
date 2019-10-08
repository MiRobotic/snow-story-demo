package com.mirobotic.story.ui.activities

interface OnActivityInteraction {
    fun sendData(json: String)
    fun startDance()
    fun stopDance()
}