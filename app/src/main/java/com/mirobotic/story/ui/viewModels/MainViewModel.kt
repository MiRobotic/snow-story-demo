package com.mirobotic.story.ui.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.mirobotic.story.data.VoiceCommands

class MainViewModel : ViewModel(){

    var activeFragment : String = ""
    var action:MutableLiveData<VoiceCommands> = MutableLiveData()



}