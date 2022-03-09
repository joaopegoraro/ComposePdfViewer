package xyz.joaophp.composepdfviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import xyz.joaophp.composepdfviewer.ui.theme.ComposePdfViewerTheme
import xyz.joaophp.pdfviewer.PdfViewer

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePdfViewerTheme {
                // A surface container using the 'background' color from the theme
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
    PdfViewer(
        modifier = Modifier.fillMaxSize(),
        pdfResId = R.raw.demo
    )
}
