package xyz.joaophp.pdfviewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.annotation.RawRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

enum class PdfListDirection {
    HORIZONTAL, VERTICAL
}

@ExperimentalFoundationApi
@Composable
fun PdfViewer(
    @RawRes pdfResId: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF909090),
    pageColor: Color = Color.White,
    listDirection: PdfListDirection = PdfListDirection.VERTICAL,
    arrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(16.dp),
) {
    val context = LocalContext.current
    PdfViewer(
        pdfStream = context.resources.openRawResource(pdfResId),
        modifier = modifier,
        pageColor = pageColor,
        backgroundColor = backgroundColor,
        listDirection = listDirection,
        arrangement = arrangement
    )
}

@ExperimentalFoundationApi
@Composable
fun PdfViewer(
    pdfStream: InputStream,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF909090),
    pageColor: Color = Color.White,
    listDirection: PdfListDirection = PdfListDirection.VERTICAL,
    arrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(16.dp),
) {
    PdfViewer(
        pdfStream = pdfStream,
        modifier = modifier,
        backgroundColor = backgroundColor,
        listDirection = listDirection,
        arrangement = arrangement
    ) { lazyState, imagem ->
        PaginaPDF(
            imagem = imagem,
            lazyState = lazyState,
            backgroundColor = pageColor
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun PdfViewer(
    @RawRes pdfResId: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF909090),
    listDirection: PdfListDirection = PdfListDirection.VERTICAL,
    arrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(16.dp),
    page: @Composable (LazyListState, ImageBitmap) -> Unit
) {
    val context = LocalContext.current
    PdfViewer(
        pdfStream = context.resources.openRawResource(pdfResId),
        modifier = modifier,
        backgroundColor = backgroundColor,
        listDirection = listDirection,
        arrangement = arrangement,
        page = page
    )
}

@ExperimentalFoundationApi
@Composable
fun PdfViewer(
    pdfStream: InputStream,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF909090),
    listDirection: PdfListDirection = PdfListDirection.VERTICAL,
    arrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(16.dp),
    page: @Composable (LazyListState, ImageBitmap) -> Unit
) {
    val context = LocalContext.current
    val pageList = remember {
        mutableStateListOf<ImageBitmap?>()
    }
    LaunchedEffect(true) {
        pageList.clear()
        pageList.addAll(context.loadPdf(pdfStream))
    }
    val lazyState = rememberLazyListState()
    when (listDirection) {
        PdfListDirection.HORIZONTAL ->
            LazyRow(
                modifier = modifier.background(backgroundColor),
                state = lazyState,
                horizontalArrangement = arrangement
            ) {
                items(pageList) { imagem ->
                    if (imagem != null) page(lazyState, imagem)
                }
            }
        PdfListDirection.VERTICAL ->
            LazyColumn(
                modifier = modifier.background(backgroundColor),
                state = lazyState,
                verticalArrangement = arrangement
            ) {
                items(pageList) { imagem ->
                    if (imagem != null) page(lazyState, imagem)
                }
            }
    }
}

@ExperimentalFoundationApi
@Composable
private fun PaginaPDF(
    imagem: ImageBitmap,
    lazyState: LazyListState,
    backgroundColor: Color = Color.White
) {
    Card(
        modifier = Modifier.background(backgroundColor),
        elevation = 5.dp
    ) {
        ZoomableImage(painter = BitmapPainter(imagem), scrollState = lazyState)
    }
}

suspend fun Context.loadPdf(inputStream: InputStream): List<ImageBitmap?> {
    val outputDir = cacheDir
    val tempFile = withContext(Dispatchers.IO) {
        File.createTempFile("temp", "pdf", outputDir)
    }
    tempFile.mkdirs()
    tempFile.deleteOnExit()
    val outputStream = withContext(Dispatchers.IO) {
        FileOutputStream(tempFile)
    }
    copy(inputStream, outputStream)
    val input = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
    val renderer = PdfRenderer(input)

    return (0 until renderer.pageCount).map {
        val page = renderer.openPage(it)
        val bitmap = Bitmap.createBitmap(1240, 1754, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        bitmap.asImageBitmap()
    }.also {
        renderer.close()
    }
}

@Throws(IOException::class)
private fun copy(source: InputStream, target: OutputStream) {
    val buf = ByteArray(8192)
    var length: Int
    while (source.read(buf).also { length = it } > 0) {
        target.write(buf, 0, length)
    }
}
