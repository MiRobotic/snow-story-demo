package com.mirobotic.story.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import com.mirobotic.story.R
import com.mirobotic.story.data.AudioModel
import kotlinx.android.synthetic.main.fragment_player.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class MusicPlayerFragment(private var index: Int, private val list: ArrayList<AudioModel>) : BottomSheetDialogFragment() {


    private var oTime = 0
    private var currentTime = 0
    private var durationTime = 0
    private val fTime = 15000
    private val bTime = 15000
    private lateinit var mActivity: Activity
    private var mPlayer: MediaPlayer? = null
    private val handler = Handler()
    private lateinit var audioLoader: AudioLoader

    private val updateSongTime = object : Runnable {
        override fun run() {
            val min = TimeUnit.MILLISECONDS.toMinutes(currentTime.toLong())
            val sec = TimeUnit.MILLISECONDS.toSeconds(currentTime.toLong()) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTime.toLong()))

            val sMin: String
            val sSec: String

            sMin = if (min < 10) {
                "0$min"
            } else {
                "" + min
            }


            sSec = if (sec < 10) {
                "0$sec"
            } else {
                "" + sec
            }

            if (mPlayer != null) {
                currentTime = mPlayer!!.currentPosition
                val time = "$sMin:$sSec"
                tvSeekBarTitle.text = time
                seekBar.progress = currentTime
            }

            if (mPlayer != null && mPlayer!!.isPlaying) {
                handler.postDelayed(this, 100)
            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mActivity = activity!!
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dlg ->
            val d = dlg as BottomSheetDialog

        }

        return dialog
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getView()!!.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.transparent))

        imgClose.setOnClickListener {
            stopSong()
            dismiss()
        }


        progressBar.visibility = View.VISIBLE

        btnNext.setOnClickListener {
            run {
                if (index < list.size - 1) {
                    index += 1
                    stopSong()
                    playSong()
                }
            }
        }
        btnPrev.setOnClickListener {
            run {
                if (index > 0) {
                    index -= 1
                    stopSong()
                    playSong()
                }
            }
        }
        playSong()
    }

    private fun stopSong() {
        if (mPlayer != null && mPlayer!!.isPlaying) {
            clearMediaPlayer()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        stopSong()
    }

    private fun playSong() {
        tvFileName.text = list[index].name
        audioLoader = AudioLoader(list[index])
        audioLoader.execute()

        if (index == 0) {
            btnPrev.visibility = View.INVISIBLE
        } else {
            btnPrev.visibility = View.VISIBLE
        }

        if (index == list.size - 1) {
            btnNext.visibility = View.INVISIBLE
        } else {
            btnNext.visibility = View.VISIBLE
        }
    }

    private fun initPlayer() {


        seekBar.visibility = View.VISIBLE
        tvTimeStart.visibility = View.VISIBLE

        btnForward.isEnabled = false
        btnRewind.isEnabled = false
        seekBar.isEnabled = false
        seekBar.isClickable = false

        //spProgress.setClickable(false);
        durationTime = mPlayer!!.duration
        currentTime = mPlayer!!.currentPosition
        seekBar.max = durationTime

        val min = TimeUnit.MILLISECONDS.toMinutes(durationTime.toLong())
        val sec = TimeUnit.MILLISECONDS.toSeconds(durationTime.toLong()) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationTime.toLong()))

        val sMin: String
        val sSec: String

        sMin = if (min < 10) {
            "0$min"
        } else {
            "" + min
        }


        sSec = if (sec < 10) {
            "0$sec"
        } else {
            "" + sec
        }

        val time = "$sMin:$sSec"

        tvTimeEnd.text = time

        val startTime = String.format("%d:%d ", TimeUnit.MILLISECONDS.toMinutes(currentTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(currentTime.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTime.toLong())))
        val endTime = String.format("%d:%d ", TimeUnit.MILLISECONDS.toMinutes(durationTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(durationTime.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationTime.toLong())))


        Log.e("audio", "start $startTime end $endTime")

        seekBar.progress = 0

        btnPlay.setOnClickListener {
            if (mPlayer != null && mPlayer!!.isPlaying) {

                btnForward.isEnabled = false
                btnRewind.isEnabled = false
                seekBar.isEnabled = false
                seekBar.isClickable = false

                mPlayer!!.pause()
                btnPlay.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_play_button))
                handler.removeCallbacks(updateSongTime)
            } else {

                btnForward.isEnabled = true
                btnRewind.isEnabled = true
                seekBar.isEnabled = true
                seekBar.isClickable = true

                tvSeekBarTitle.visibility = View.VISIBLE
                btnPlay.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_pause_button))


                mPlayer!!.start()
                if (oTime == 0) {
                    seekBar.max = durationTime
                    seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                            val percent = progress / seekBar.max.toDouble()
                            val offset = seekBar.thumbOffset
                            val seekWidth = seekBar.width
                            val `val` = (percent * (seekWidth - 2 * offset)).roundToInt()
                            val labelWidth = tvSeekBarTitle.width
                            tvSeekBarTitle.x = (offset.toFloat() + seekBar.x + `val`.toFloat()
                                    - (percent * offset).roundToLong().toFloat()
                                    - (percent * labelWidth / 2).roundToLong().toFloat())


                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar) {
                            tvSeekBarTitle.visibility = View.VISIBLE
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar) {
                            if (mPlayer != null) {
                                currentTime = seekBar.progress
                                mPlayer!!.seekTo(currentTime)
                                val min1 = TimeUnit.MILLISECONDS.toMinutes(currentTime.toLong())
                                val sec1 = TimeUnit.MILLISECONDS.toSeconds(currentTime.toLong()) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTime.toLong()))

                                val sMin1: String
                                val sSec1: String

                                sMin1 = if (min1 < 10) {
                                    "0$min1"
                                } else {
                                    "" + min1
                                }


                                sSec1 = if (sec1 < 10) {
                                    "0$sec1"
                                } else {
                                    "" + sec1
                                }
                                val time1 = "$sMin1:$sSec1"
                                tvSeekBarTitle.text = time1

                                Log.e("audio", "duration " + mPlayer!!.duration + " seek " + seekBar.progress)
                            }

                        }
                    })
                    oTime = 1
                }

                seekBar.progress = currentTime
                handler.postDelayed(updateSongTime, 100)
            }


        }

        btnRewind.setOnClickListener {

            val rev = currentTime - bTime

            if (rev > 0) {
                if (mPlayer != null && mPlayer!!.isPlaying) {
                    mPlayer!!.seekTo(rev)
                }
            } else {
                if (mPlayer != null && mPlayer!!.isPlaying) {
                    mPlayer!!.seekTo(0)
                }
            }


        }

        btnForward.setOnClickListener {
            val fwdTime = currentTime + fTime

            if (fwdTime < durationTime) {
                if (mPlayer != null && mPlayer!!.isPlaying) {
                    mPlayer!!.seekTo(fwdTime)
                }
            } else {
                Toast.makeText(context, "Cannot jump forward 10 seconds", Toast.LENGTH_SHORT).show()
            }

        }


    }


    private fun clearMediaPlayer() {
        if (mPlayer == null) {
            return
        }
        mPlayer!!.stop()
        mPlayer!!.release()
        mPlayer = null
    }

    @SuppressLint("StaticFieldLeak")
    private inner class AudioLoader(val audioModel: AudioModel) : AsyncTask<Void, Void, Void>() {


        private var isLoaded: Boolean = false


        override fun onPreExecute() {

            isLoaded = false
            super.onPreExecute()

        }

        override fun doInBackground(vararg params: Void?): Void? {
            mPlayer = MediaPlayer()


            try {
                Log.e(tag, "path > " + audioModel.path)

                mPlayer!!.setDataSource(audioModel.path)
                mPlayer!!.prepare()
                mPlayer!!.setVolume(0.5f, 0.5f)
                mPlayer!!.isLooping = false
                isLoaded = true
            } catch (e: IOException) {
                e.printStackTrace()

            }
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            if (mActivity.isDestroyed) {
                return
            }

            if (isLoaded) {
                progressBar.visibility = View.GONE
                initPlayer()
                btnPlay.performClick()
            } else {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Failed to load song!", Toast.LENGTH_LONG).show()
            }

        }
    }


}
