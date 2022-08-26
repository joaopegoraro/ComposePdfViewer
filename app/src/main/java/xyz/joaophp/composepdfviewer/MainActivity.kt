package xyz.joaophp.composepdfviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.joaophp.composepdfviewer.ui.theme.ComposePdfViewerTheme
import xyz.joaophp.pdfviewer.PdfViewer

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePdfViewerTheme {
                Surface(color = MaterialTheme.colors.background) {
                    DemoLayout()
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun DemoLayout() {
    var isLoading by remember {
        mutableStateOf(false)
    }
    var currentLoadingPage by remember {
        mutableStateOf<Int?>(null)
    }
    var pageCount by remember {
        mutableStateOf<Int?>(null)
    }
    Box {
        PdfViewer(
            modifier = Modifier.fillMaxSize(),
            pdfResId = R.raw.demo,
//            pdfResId = R.raw.corrupted_pdf_file, //try this to test the fallback Widget
            fallbackWidget = {
                Icon(
                    Icons.Rounded.Info,
                    contentDescription = "Error Component"
                )
            },
            loadingListener = { loading, currentPage, maxPage ->
                isLoading = loading
                if (currentPage != null) currentLoadingPage = currentPage
                if (maxPage != null) pageCount = maxPage
            }
        )
        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                    progress = if (currentLoadingPage == null || pageCount == null) 0f
                    else currentLoadingPage!!.toFloat() / pageCount!!.toFloat()
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 5.dp)
                        .padding(horizontal = 30.dp),
                    text = "${currentLoadingPage ?: "-"} pages loaded/${pageCount ?: "-"} total pages"
                )
            }
        }
    }
}
