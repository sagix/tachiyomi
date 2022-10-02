package eu.kanade.tachiyomi.ui.reader.loader

import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.ui.reader.model.ReaderPage
import eu.kanade.tachiyomi.util.storage.PdfFile
import rx.Observable
import java.io.File

/**
 * Loader used to load a chapter from a .pdf file.
 */
class PdfPageLoader(file: File) : PageLoader() {

    /**
     * The pdf file.
     */
    private val pdf = PdfFile(file)

    /**
     * Recycles this loader and the pdf.
     */
    override fun recycle() {
        super.recycle()
        pdf.close()
    }

    /**
     * Returns an observable containing the pages found on this pdf.
     */
    override fun getPages(): Observable<List<ReaderPage>> {
        return pdf.getImagesFromPages()
            .map { index ->
                val streamFn = { pdf.getInputStream(index) }
                ReaderPage(index = index).apply {
                    stream = streamFn
                    status = Page.READY
                }
            }
            .let { Observable.just(it) }
    }

    /**
     * Returns an observable that emits a ready state unless the loader was recycled.
     */
    override fun getPage(page: ReaderPage): Observable<Int> {
        return Observable.just(
            if (isRecycled) {
                Page.ERROR
            } else {
                Page.READY
            },
        )
    }
}
