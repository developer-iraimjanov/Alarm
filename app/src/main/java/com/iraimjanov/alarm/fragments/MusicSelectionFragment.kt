package com.iraimjanov.alarm.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.iraimjanov.alarm.adapter.RVMusicAdapter
import com.iraimjanov.alarm.databinding.FragmentMusicSelectionBinding
import com.iraimjanov.alarm.fragments.AddFragment
import com.iraimjanov.alarm.models.Music
import com.iraimjanov.alarm.utils.PermissionsService

class MusicSelectionFragment : Fragment() {
    private lateinit var binding: FragmentMusicSelectionBinding
    private var listMusic: ArrayList<Music> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMusicSelectionBinding.inflate(layoutInflater)
        checkForStorageAccess()
        return binding.root
    }

    private fun buildRV() {
        binding.rcMusic.adapter = RVMusicAdapter(listMusic, object : RVMusicAdapter.RVClickMusic {
            override fun click(music: Music) {
                AddFragment.music = music
                EditFragment.music = music
                findNavController().popBackStack()
            }
        })
    }

    private fun checkForStorageAccess() {
        if (PermissionsService().checkPermission(requireActivity())) {
            loadMusic()
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        val mPermissionResult =
            registerForActivityResult(ActivityResultContracts.RequestPermission()
            ) { result ->
                if (result) {
                    loadMusic()
                } else {
                    buildAlertMessageNoPermissions()
                }
            }
        mPermissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun loadMusic() {
        listMusic = loadMusicInDevices()
        buildRV()
    }

    private fun buildAlertMessageNoPermissions() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage("Storage permission to run media files for proper use of the application.\n" +
                "Go to Permission>Storage to open it")
            .setCancelable(false)
            .setPositiveButton("To Open It") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    @SuppressLint("Recycle", "Range")
    private fun loadMusicInDevices(): ArrayList<Music> {
        val list = ArrayList<Music>()

        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"

        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

        val cursor: Cursor? = requireActivity().contentResolver.query(
            uri,
            null,
            selection,
            null,
            sortOrder
        )

        if (cursor != null && cursor.moveToFirst()) {
            val id: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val title: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val authorId: Int = cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)

            do {
                val audioId: Long = cursor.getLong(id)
                val audioTitle: String = cursor.getString(title)
                val albumIdC =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                        .toString()
                val uri = Uri.parse("content://media//external/audio/albumart")
                val imagePath = Uri.withAppendedPath(uri, albumIdC).toString()
                val musicPath: String =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val artist = cursor.getString(authorId)

                list.add(Music(audioId, audioTitle, imagePath, musicPath, artist))
            } while (cursor.moveToNext())
        }
        return list
    }

}