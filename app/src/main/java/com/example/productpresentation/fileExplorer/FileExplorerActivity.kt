package com.example.productpresentation.fileExplorer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productpresentation.databinding.FileExplorerLayoutBinding
import com.example.productpresentation.tv.TvSettings
import com.example.productpresentation.uriList
import java.io.File

class FileExplorerActivity: AppCompatActivity() {
    private lateinit var binding: FileExplorerLayoutBinding
    private lateinit var dataset: MutableList<FileItem>
    private lateinit var currentFile: File
    private lateinit var previousFile: File
    private lateinit var mediaType: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FileExplorerLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaType = intent.getStringExtra("MEDIA_TYPE").toString()

        val storage = System.getenv("EXTERNAL_STORAGE")


        if(storage!=null){
            currentFile = File(storage)
            dataset = getFiles(currentFile)
            dataset.sortBy{ it.file.name }

            val itemAdapter = DirectoryAdapter(dataset)

            val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.fileRecyclerView.adapter = itemAdapter
            binding.fileRecyclerView.layoutManager = linearLayoutManager
            binding.fileRecyclerView.setHasFixedSize(false)
            binding.fileRecyclerView.requestFocus()

        }
        else{
            onBackPressed()
        }
    }

    private fun getSelected(): Int{
        return dataset.indexOf(dataset.firstOrNull{ it.selected })
    }
    private fun changeSelection(from: Int, position: Int){
        dataset[from].selected = false
        dataset[position].selected = true
    }

    private fun clearSelectionExcept(position: Int){
        dataset.forEach{
            if(dataset.indexOf(it)!=position){
                it.selected = false
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                val position = getSelected()
                if(position-1>=0){
                    changeSelection(position, position - 1)
                }
                binding.fileRecyclerView.adapter?.notifyItemChanged(position-1)
                binding.fileRecyclerView.adapter?.notifyItemChanged(position)
                val firstVisible = (binding.fileRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if(firstVisible >= position - 3){
                    (binding.fileRecyclerView.layoutManager as LinearLayoutManager).scrollToPosition(position - 3)
                }
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                val position = getSelected()
                if(position+1<dataset.size){
                    changeSelection(position, position + 1)
                }
                binding.fileRecyclerView.adapter?.notifyItemChanged(position+1)
                binding.fileRecyclerView.adapter?.notifyItemChanged(position)
                val lastVisible = (binding.fileRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if(lastVisible <= position + 3){
                    (binding.fileRecyclerView.layoutManager as LinearLayoutManager).scrollToPosition(position + 3)
                }
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                Toast.makeText(this, "dpadright", Toast.LENGTH_SHORT).show()
                return true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                Toast.makeText(this, "dpadleft", Toast.LENGTH_SHORT).show()
                return true
            }
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                if(dataset[getSelected()].file.absolutePath!="/.pick"){
                    switchFile(dataset[getSelected()].file.absolutePath)
                }
                else{
                    val uris = getUris(currentFile)
                    uriList.clear()
                    uriList = uris
                    val intent = Intent(this, TvSettings::class.java)
                    intent.putExtra("REQUIRE_PASSWORD", false)
                    finish()
                    startActivity(intent)
                }
                return true
            }
            66->{
                if(dataset[getSelected()].file.absolutePath!="/.pick"){
                    switchFile(dataset[getSelected()].file.absolutePath)
                }
                else{
                    val uris = getUris(currentFile)
                    uriList.clear()
                    uriList = uris
                    val intent = Intent(this, TvSettings::class.java)
                    intent.putExtra("REQUIRE_PASSWORD", false)
                    finish()
                    startActivity(intent)
                }
                return true
            }
            4->{
                val intent = Intent(this, TvSettings::class.java)
                intent.putExtra("REQUIRE_PASSWORD", false)
                finish()
                startActivity(intent)
                return true
            }
            else -> {
                println(keyCode)
                return false
            }
        }
    }

    private fun getUris(file: File): MutableList<Uri> {
        val itemList = mutableListOf<FileItem>()
        if(file.listFiles()!=null){
            file.listFiles()!!.forEach {
                if(mediaType == "PHOTO"){
                    if(!it.isHidden && (isImage(it))){
                        itemList.add(FileItem(it, it.name))
                    }
                }
                else if(mediaType == "VIDEO"){
                    if(!it.isHidden && (isImage(it))){
                        itemList.add(FileItem(it, it.name))
                    }
                }
            }
        }
        itemList.sortBy{ it.file.name }

        val uris = mutableListOf<Uri>()
        itemList.forEach{
           uris.add(Uri.parse(it.file.absolutePath))
        }
        return uris
    }

    private fun getFiles(file: File): MutableList<FileItem>{
        val itemList = mutableListOf<FileItem>()
        if(file.listFiles()!=null){
            file.listFiles()!!.forEach {
                if(mediaType == "PHOTO"){
                    if(!it.isHidden && (it.isDirectory || isImage(it))){
                        itemList.add(FileItem(it, it.name))
                    }
                }
                else if(mediaType == "VIDEO"){
                    if(!it.isHidden && (it.isDirectory || isVideo(it))){
                        itemList.add(FileItem(it, it.name))
                    }
                }
            }
        }
        itemList.sortBy{ it.file.name }
        if(currentFile.absolutePath!=System.getenv("EXTERNAL_STORAGE")){
            itemList.add(0, FileItem(previousFile, "/.."))
        }
        itemList.add(0, FileItem(File(".pick"), "pick folder"))
        itemList[0].selected = true
        return itemList
    }

    private fun getPreviousFromPath(path: String): String {
        for(i in path.length-1 downTo 0){
            val char = path[i]
            if(char=='/' && i!=0){
                return path.subSequence(0, i).toString()
            }
        }
        return System.getenv("EXTERNAL_STORAGE") as String
    }

    private fun switchFile(path: String){
        val candidateFile = File(path)
        if(candidateFile.isDirectory){
            previousFile = File(getPreviousFromPath(path))
            currentFile = candidateFile
            dataset = getFiles(currentFile)
            val itemAdapter = DirectoryAdapter(dataset)
            binding.fileRecyclerView.adapter = itemAdapter
        }
    }

    private fun isImage(file: File): Boolean {
        if(file.absolutePath.endsWith(".jpg")){
            return true
        }
        else if(file.absolutePath.endsWith(".jpeg")){
            return true
        }
        else if(file.absolutePath.endsWith(".png")){
            return true
        }
        else if(file.absolutePath.endsWith(".webp")){
            return true
        }
        return false
    }

    private fun isVideo(file: File): Boolean {
        if(file.absolutePath.endsWith(".mp4")){
            return true
        }
        else if(file.absolutePath.endsWith(".mkv")){
            return true
        }
        else if(file.absolutePath.endsWith(".webm")){
            return true
        }
        return false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, TvSettings::class.java)
        intent.putExtra("REQUIRE_PASSWORD", false)
        finish()
        startActivity(intent)
    }
}
