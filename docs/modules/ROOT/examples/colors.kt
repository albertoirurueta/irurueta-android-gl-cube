import android.graphics.Color

// Default is Color.TRANSPARENT: views behind the cube remain visible
cubeView.clearColor = Color.argb(128, 0, 0, 0) // translucent black background

// Diffuse lighting color (only used when normals are available)
cubeView.diffuseColor = Color.rgb(200, 200, 200)
