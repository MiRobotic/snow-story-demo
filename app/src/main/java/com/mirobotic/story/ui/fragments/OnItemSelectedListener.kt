package com.mirobotic.story.ui.fragments

import com.mirobotic.story.data.AudioModel

interface OnItemSelectedListener {
    fun onAudioSelected(index: Int, audio: AudioModel)
}