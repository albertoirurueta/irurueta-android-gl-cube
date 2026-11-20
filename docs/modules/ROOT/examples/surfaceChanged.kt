val cubeView = findViewById<CubeTextureView>(R.id.cube)

cubeView.onSurfaceChangedListener = object : CubeTextureView.OnSurfaceChangedListener {
    override fun onSurfaceChanged(width: Int, height: Int) {
        // The OpenGL surface exists now: camera related properties can be set
        cubeView.cubeSize = 0.5f * CubeRenderer.DEFAULT_CUBE_SIZE
        cubeView.camera = createCamera(width, height)
    }
}
