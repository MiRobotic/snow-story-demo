package com.mirobotic.story.ui.fragments


import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mirobotic.story.R
import com.mirobotic.story.app.Config
import com.mirobotic.story.app.Config.Companion.LANG_CANTONESE
import com.mirobotic.story.app.Config.Companion.LANG_ENGLISH
import com.mirobotic.story.app.Config.Companion.LANG_HOKKIEN
import com.mirobotic.story.app.Config.Companion.LANG_MANDARIN
import com.mirobotic.story.data.AudioModel
import com.mirobotic.story.ui.adapter.FileListAdapter
import com.mirobotic.story.ui.viewModels.MainViewModel
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import kotlinx.android.synthetic.main.fragment_sing.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import java.util.regex.Pattern


/**
 * A simple [Fragment] subclass.
 */
class FilesFragment : Fragment() {

    private var selectedFolder = ""
    private val mTag = "FilesFragment"
    private var type: String = ""
    private lateinit var model: MainViewModel
    private var list: ArrayList<AudioModel> = arrayListOf()
    private var musicPlayerFragment: MusicPlayerFragment? = null
    private val filePickerRequest = 101

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment


        model = ViewModelProviders.of(this)[MainViewModel::class.java]


        type = arguments!!.getString("type")!!
        val lang = arguments!!.getString("lang")!!

        selectedFolder = when (lang) {
            LANG_CANTONESE -> {
                "$type/$LANG_CANTONESE"
            }
            LANG_MANDARIN -> {
                "$type/$LANG_MANDARIN"
            }
            LANG_HOKKIEN -> {
                "$type/$LANG_HOKKIEN"
            }
            else -> {
                "$type/$LANG_ENGLISH"
            }

        }
        Log.e(mTag, "path $selectedFolder")

        val builder = StrictMode.VmPolicy.Builder()
        builder.detectFileUriExposure()
        StrictMode.setVmPolicy(builder.build())

        return inflater.inflate(R.layout.fragment_sing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabAddSong.setOnClickListener { openFilePicker() }


        loadSongs()

    }

    private fun loadSongs() {
        val songs = ArrayList<AudioModel>()
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

                    songs.add(audioModel)
                }
                c.close()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        val itemSelectedListener = object : OnItemSelectedListener {

            override fun onAudioSelected(index: Int, audio: AudioModel) {
                if (musicPlayerFragment != null) {
                    try {
                        musicPlayerFragment!!.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                musicPlayerFragment = MusicPlayerFragment(index, list)
                musicPlayerFragment!!.show(childFragmentManager, MusicPlayerFragment::class.java.simpleName)

            }

        }


        if (songs.size > 0) {
            val adapter = FileListAdapter(songs, itemSelectedListener, type)
            rvSongs.layoutManager = GridLayoutManager(context!!, 3)
            rvSongs.adapter = adapter
            return
        }
        Toast.makeText(context, "No Audio files found, Please add files to play", Toast.LENGTH_SHORT).show()


    }

    private fun openFilePicker() {

        MaterialFilePicker()
                .withSupportFragment(this)
                .withRequestCode(filePickerRequest)
                .withHiddenFiles(false)
                .withFilter(Pattern.compile(".*\\.mp3$"))
                .withTitle("Select Audio File")
                .start()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == filePickerRequest && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return
            }

            Thread(Runnable {
                val filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)
                val destinationPath = "" + Environment.getExternalStorageDirectory() + "/" + Config.MAIN_FOLDER + "/" + selectedFolder + "/"
                Log.e("file", "selected file >> $filePath")
                copyFileOrDirectory(filePath, destinationPath)
                model.getFiles(selectedFolder)
            }).start()


        }
    }

    private fun copyFileOrDirectory(srcDir: String, dstDir: String) {

        try {
            val src = File(srcDir)
            val dst = File(dstDir, src.name)

            if (src.isDirectory) {

                val files = src.list()
                val filesLength = files.size
                for (i in 0 until filesLength) {
                    val src1 = File(src, files[i]).path
                    val dst1 = dst.path
                    copyFileOrDirectory(src1, dst1)
                }
            } else {
                copyFile(src, dst)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun copyFile(sourceFile: File, destFile: File) {
        if (!destFile.parentFile.exists())
            destFile.parentFile.mkdirs()

        if (!destFile.exists()) {
            destFile.createNewFile()
        }

        var source: FileChannel? = null
        var destination: FileChannel? = null

        try {
            source = FileInputStream(sourceFile).channel
            destination = FileOutputStream(destFile).channel
            destination!!.transferFrom(source, 0, source!!.size())
        } finally {
            source?.close()
            destination?.close()
        }
    }

}
