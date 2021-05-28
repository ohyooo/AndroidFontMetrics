package net.studymongolian.fontmetrics

import android.graphics.fonts.SystemFonts
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import java.io.File


class MainViewModel : ViewModel() {
    val text = ObservableField("My text line")
    val textSize = ObservableField<String>("50")

    private val systemFontPaths by lazy { sequenceOf(File("/system/fonts"), File("/product/fonts")).filter { it.exists() && it.canRead() }.toList() }

    private var systemFontFilesList: List<File>? = null

    private val FILE_NAME_FILTERS = arrayOf(
        "bold",
        "clock",
        "emoji",
        "extra",
        "mono",
        "script",
        "symbol",
        "json", // there may be a config.json in /product/fonts
    )

    private val TRUSTED_FONT = arrayOf(
        "ComingSoon.ttf"
    )

    // some manufacturers like XiaoMi, they didn't add locale info in their own font. Ignore files' size under 50 kb
    private val FILTER_FILE_SIZE = 50 * 1024

    fun getSystemFontFiles(): List<File> = systemFontFilesList ?: if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        systemFontPaths.map { folder ->
            folder.walk()
                .maxDepth(1)
                .filter { file ->
                    FILE_NAME_FILTERS.none { file.name.contains(it, true) }
                }
                .filter {
                    it.length() > FILTER_FILE_SIZE
                }
                .sortedBy { it.name }
                .toList()
        }.flatten()
    } else {
        getSystemFonts()
    }.also {
        systemFontFilesList = it
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getSystemFonts() = SystemFonts.getAvailableFonts()
        .asSequence()
        .filter { font ->
            // set to italic if file or name is null, it will be filted here
            FILE_NAME_FILTERS.none { filter ->
                (font.file?.name ?: "italic").contains(filter, true)
            }
        }
        .filter {
            it.file?.length() ?: 0 > FILTER_FILE_SIZE
        }
        .filterNot {
            it.localeList.toLanguageTags().contains("und-")
        }
        // file won't be null here
        .groupBy { it.file!!.path }
        .map {
            it.value.first().file!!
        }
        .sortedBy { it.name }
        .toList()
}
