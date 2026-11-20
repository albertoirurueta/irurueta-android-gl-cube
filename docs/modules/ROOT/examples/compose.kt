import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.irurueta.android.gl.cube.CubeTextureView

@Composable
fun CubeScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Regular composable drawn behind the transparent cube
        Text("Lorem ipsum")

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                CubeTextureView(context).apply {
                    onSurfaceChangedListener =
                        object : CubeTextureView.OnSurfaceChangedListener {
                            override fun onSurfaceChanged(width: Int, height: Int) {
                                camera = createCamera(width, height)
                            }
                        }
                }
            }
        )
    }
}
