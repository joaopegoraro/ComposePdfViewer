# ComposePdfViewer
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

A simple Jetpack Compose PDF viewer


# Demo

<img src="gif/demo.gif" width="270" height="500"/>

# Usage

```kotlin

PdfViewer(
    pdfResId = R.raw.demo,
    modifier = Modifier.fillMaxSize(),
    backgroundColor = Color(0xFF909090),
    pageColor = Color.White,
    listDirection = PdfListDirection.VERTICAL,
    arrangement = Arrangement.spacedBy(16.dp),
    loadingListener = { isLoading, currentPage, maxPage ->
         // Observe loading changes
    }
)

// Or if you already have an InputStream
PdfViewer(
    pdfStream = inputStream,
    modifier = Modifier.fillMaxSize(),
    backgroundColor = Color(0xFF909090),
    pageColor = Color.White,
    listDirection = PdfListDirection.VERTICAL,
    arrangement = Arrangement.spacedBy(16.dp),
    loadingListener = { isLoading, currentPage, maxPage ->
         // Observe loading changes
    }
)

// Or if you want to implement your own page layout
PdfViewer(
   pdfStream = inputStream,
   modifier = Modifier.fillMaxSize(),
   backgroundColor = Color(0xFF909090),
   listDirection = PdfListDirection.VERTICAL,
   arrangement = Arrangement.spacedBy(16.dp),
   loadingListener = { isLoading, currentPage, maxPage ->
        // Observe loading changes
   },
   page = { lazyListState, imageBitmap ->
        // Implement your own custom page
   }
 )

```

# Known problems

- Zoom pan is not restricted (Any fix to that should go to [ComposeZoomableImagePlus](https://github.com/joaopegoraro/ComposeZoomableImagePlus)
- Load performance is slow (on average, the PDF Viewer loads about 4 pages a second on a Pixel 2 emulator)


# Future changes (other than fixing the known problems)

- Add ram chaching for the pdf pages in case the PDF is not as heavy and doesn't need disk caching
- Publish to [Jitpack](https://jitpack.io/)
