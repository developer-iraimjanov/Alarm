package com.iraimjanov.alarm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iraimjanov.alarm.databinding.ItemMusicBinding
import com.iraimjanov.alarm.models.Music

class RVMusicAdapter(
    private val listMusic: ArrayList<Music>,
    private val rvClickMusic: RVClickMusic,
) :
    RecyclerView.Adapter<RVMusicAdapter.VH>() {

    inner class VH(private var itemRV: ItemMusicBinding) : RecyclerView.ViewHolder(itemRV.root) {
        fun onBind(music: Music) {
            itemRV.tvName.text = music.title
            itemRV.tvArtist.text = music.author

            Glide.with(itemRV.root).load(music.imagePath).centerCrop().into(itemRV.imageMusic)

            itemView.setOnClickListener {
                rvClickMusic.click(music)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(listMusic[position])
    }

    override fun getItemCount(): Int = listMusic.size

    interface RVClickMusic {
        fun click(music: Music)
    }

}