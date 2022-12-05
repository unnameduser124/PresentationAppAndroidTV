package com.example.productpresentation.fileExplorer

import java.io.File

data class FileItem(var file: File, var name: String, var selected: Boolean = false) {
}