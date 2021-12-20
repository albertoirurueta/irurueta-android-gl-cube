package com.irurueta.android.gl.cube

import android.animation.AnimatorSet
import android.animation.FloatArrayEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.irurueta.android.glutils.OpenGlToCameraHelper
import com.irurueta.geometry.PinholeCamera
import com.irurueta.geometry.Point3D
import com.irurueta.geometry.Quaternion
import com.irurueta.geometry.Rotation3D

class MainActivity : AppCompatActivity() {
    private var view: CubeTextureView? = null

    private var rotation = Quaternion()

    private val initR = floatArrayOf(0.0f, 0.0f, 0.0f)
    private val endR = floatArrayOf(3.0f * FULL_ROTATION, 2.0f * FULL_ROTATION, FULL_ROTATION)

    private val animator = AnimatorSet()

    private val rotationAnimator =
        ValueAnimator.ofObject(FloatArrayEvaluator(), initR, endR)

    private val skewAnimator = ValueAnimator.ofFloat(-1000f, 1000f)

    private var camera: PinholeCamera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        view = findViewById(R.id.cube)

        val view = view ?: return
        view.onSurfaceChangedListener = object : CubeTextureView.OnSurfaceChangedListener {
            override fun onSurfaceChanged(width: Int, height: Int) {
                view.cubeSize = 0.5f * CubeRenderer.DEFAULT_CUBE_SIZE
                view.cubeRotation = rotation
                camera = createCamera(view)
                view.camera = camera
            }
        }

        rotationAnimator.setTarget(view)
        rotationAnimator.addUpdateListener { animator ->
            val r = animator.animatedValue as FloatArray
            rotation.setFromEulerAngles(r[0].toDouble(), r[1].toDouble(), r[2].toDouble())
            view.cubeRotation = rotation
        }
        rotationAnimator.duration = ROTATION_DURATION_MILLIS
        rotationAnimator.repeatMode = ValueAnimator.RESTART
        rotationAnimator.repeatCount = ValueAnimator.INFINITE
        rotationAnimator.interpolator = LinearInterpolator()

        skewAnimator.setTarget(view)
        skewAnimator.addUpdateListener { animator ->
            val skew = animator.animatedValue as Float
            val camera = camera ?: return@addUpdateListener
            val intrinsics = camera.intrinsicParameters
            intrinsics.skewness = skew.toDouble()
            camera.intrinsicParameters = intrinsics
            view.camera = camera
        }
        skewAnimator.duration = SKEW_DURATION_MILLIS
        skewAnimator.repeatMode = ValueAnimator.REVERSE
        skewAnimator.repeatCount = ValueAnimator.INFINITE
        skewAnimator.interpolator = AccelerateDecelerateInterpolator()

        animator.playTogether(rotationAnimator, skewAnimator)
    }

    override fun onResume() {
        super.onResume()
        view?.onResume()
        animator.start()
    }

    override fun onPause() {
        super.onPause()
        view?.onPause()
        animator.pause()
    }

    private companion object {
        const val FULL_ROTATION = 2.0f * Math.PI.toFloat()
        const val ROTATION_DURATION_MILLIS = 30000L
        const val SKEW_DURATION_MILLIS = 1000L

        private fun createProjectionMatrix(): FloatArray {
            // Pixel 2 device has the following projection matrix
            // [2.8693345   0.0         -0.004545755        0.0         ]
            // [0.0	        1.5806589   0.009158132         0.0         ]
            // [0.0	        0.0         -1.002002           -0.2002002  ]
            // [0.0         0.0         -1.0                0.0         ]

            // android.opengl.Matrix defines values column-wise
            return floatArrayOf(
                2.8693345f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.5806589f, 0.0f, 0.0f,
                -0.004545755f, 0.009158132f, -1.002002f, -1.0f,
                0.0f, 0.0f, -0.2002002f, 0.0f
            )
        }

        private fun createCamera(view: CubeTextureView): PinholeCamera {
            val projectionMatrix = createProjectionMatrix()
            val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
                projectionMatrix,
                view.width,
                view.height
            )
            return PinholeCamera(intrinsics, Rotation3D.create(), Point3D.create())
        }
    }
}