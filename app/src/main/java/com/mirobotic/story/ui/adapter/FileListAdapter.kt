package com.mirobotic.story.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mirobotic.story.R
import com.mirobotic.story.app.Config.Companion.DIR_SONG
import com.mirobotic.story.data.AudioModel
import com.mirobotic.story.ui.fragments.OnItemSelectedListener

class FileListAdapter(private val list: List<AudioModel>, private val selectedListener: OnItemSelectedListener, private val type: String) : RecyclerView.Adapter<FileListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val audio = list[position]

        if (type == DIR_SONG) {
            holder.imgIcon.setImageResource(R.drawable.ic_karaoke)
        } else {
            holder.imgIcon.setImageResource(R.drawable.ic_story)
        }

        holder.tvName.text = audio.name
        holder.itemView.setOnClickListener { selectedListener.onAudioSelected(holder.adapterPosition, audio) }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgIcon = itemView.findViewById<ImageView>(R.id.imgAlbum)!!
        val tvName = itemView.findViewById<TextView>(R.id.tvFileName)!!

    }


}