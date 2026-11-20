import com.irurueta.android.glutils.CameraToDisplayOrientation

// Display orientation (for instance, obtained from the device camera and display rotation).
// Once orientation is known, view camera properties become available.
cubeView.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

// Camera expressed in Android view coordinates (y axis pointing downwards)
val viewCamera = cubeView.viewCamera
