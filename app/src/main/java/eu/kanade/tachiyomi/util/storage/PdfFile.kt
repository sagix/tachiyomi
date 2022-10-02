package eu.kanade.tachiyomi.util.storage

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
import android.os.ParcelFileDescriptor
import android.os.ParcelFileDescriptor.MODE_READ_ONLY
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Read pdf as a list of png.
 */
class PdfFile(file: File) : Closeable {

    /**
     * The pdf renderer.
     */
    private val renderer = PdfRenderer(ParcelFileDescriptor.open(file, MODE_READ_ONLY))

    /**
     * Lock to read one page at a time.
     */
    private val lock = ReentrantLock()

    /**
     * Closes the underlying pdf file.
     */
    override fun close() {
        renderer.close()
    }

    /**
     * Returns an input stream for reading the contents of the specified pdf page.
     */
    fun getInputStream(index: Int): InputStream {
        // only one page can be read at a time
        return lock.withLock {
            renderer.openPage(index).use { openPage ->
                val bitmap =
                    Bitmap.createBitmap(
                        openPage.width,
                        openPage.height,
                        Bitmap.Config.ARGB_8888,
                    )

                openPage.render(bitmap, null, null, RENDER_MODE_FOR_DISPLAY)

                ByteArrayOutputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                    bitmap.recycle()
                    ByteArrayInputStream(it.toByteArray())
                }
            }
        }
    }

    /**
     * Returns the path of all the images found in the pdf file.
     */
    fun getImagesFromPages(): List<Int> {
        return (0 until renderer.pageCount).toList()
    }
}
