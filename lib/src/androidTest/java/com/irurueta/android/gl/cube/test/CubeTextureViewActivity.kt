package com.irurueta.android.gl.cube.test

import android.app.Activity
import android.os.Bundle
import com.irurueta.android.gl.cube.CubeTextureView

class CubeTextureViewActivity : Activity() {

    private var view: CubeTextureView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cube_texture_view_activity)
        view = findViewById(R.id.cube_texture_view_test)
    }

    override fun onPause() {
        super.onPause()
        view?.onPause()
    }

    override fun onResume() {
        super.onResume()
        view?.onResume()
    }
}