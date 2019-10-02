package com.mirobotic.story.ui.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.mirobotic.story.app.Config
import com.mirobotic.story.data.AudioModel
import java.io.File


class MainViewModel : ViewModel() {

    val mAudioList = MutableLiveData<ArrayList<AudioModel>>()

    init {
        Log.e("MainViewModel", "INITIALIZED")
    }

    fun getAudioFilesFromFolder(context: Context, selectedFolder: String) {

        Thread(Runnable {
            val tempAudioList = ArrayList<AudioModel>()
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            val path = "" + Environment.getExternalStorageDirectory().absolutePath + "/" + Config.MAIN_FOLDER + "/" + selectedFolder

            Log.e("MainViewModel", "path > $path")

            try {
                val projection = arrayOf(MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.TITLE, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST)
                val c = context.contentResolver.query(uri, projection, MediaStore.Audio.Media.DATA + " like '$path'", arrayOf("%utm%"), null)
                if (c != null) {
                    while (c.moveToNext()) {


                        val audioModel = AudioModel(
                                c.getString(0),
                                c.getString(1),
                                c.getString(2),
                                c.getString(3)
                        )
                        Log.d("Name :${audioModel.name}", " Album :${audioModel.album}")
                        Log.e("Path :${audioModel.path}", " Artist :${audioModel.artist}")

                        tempAudioList.add(audioModel)
                    }
                    c.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            mAudioList.postValue(tempAudioList)
        }).start()

    }

    fun getTest(context: Context, selectedFolder: String) {

        Thread(Runnable {
            val tempAudioList = ArrayList<AudioModel>()

            val path = "" + Environment.getExternalStorageDirectory() + "/" + Config.MAIN_FOLDER + "/" + selectedFolder

            Log.e("MainViewModel", "path > $path")

            try {
                val cr = context.contentResolver

                val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
                val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
                val cur = cr.query(uri, null, selection, null, sortOrder)
                val count: Int

                if (cur != null) {
                    count = cur.count

                    if (count > 0) {
                        while (cur.moveToNext()) {
                            val audioModel = AudioModel(
                                    cur.getString(1),
                                    cur.getString(2),
                                    cur.getString(0),
                                    cur.getString(3)
                            )
                            tempAudioList.add(audioModel)
                        }
                    }
                }
                cur!!.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            mAudioList.postValue(tempAudioList)
        }).start()

    }

    fun getFiles(selectedFolder: String) {

        Thread(Runnable {
            val tempAudioList = ArrayList<AudioModel>()

            val path = "" + Environment.getExternalStorageDirectory() + "/" + Config.MAIN_FOLDER + "/" + selectedFolder

            Log.e("MainViewModel", "path > $path")

            try {
                Log.d("Files", "Path: $path")
                val directory = File(path)
                val files = directory.listFiles()
                Log.d("Files", "Size: " + files.size)
                for (i in files.indices) {
                    val audioModel = AudioModel(
                            files[i].absolutePath,
                            files[i].name,
                            "",
                            ""
                    )
                    tempAudioList.add(audioModel)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            mAudioList.postValue(tempAudioList)
        }).start()

    }


}