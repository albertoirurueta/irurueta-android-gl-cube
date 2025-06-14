/*
 * Copyright (C) 2021 Alberto Irurueta Carro (alberto@irurueta.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

/**
 * Main activity.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Cube texture view.
     */
    private var view: CubeTextureView? = null

    /**
     * Rotation of the cube.
     */
    private var rotation = Quaternion()

    /**
     * Initial rotation for the cube.
     */
    private val initR = floatArrayOf(0.0f, 0.0f, 0.0f)

    /**
     * End rotation for the cube.
     */
    private val endR = floatArrayOf(3.0f * FULL_ROTATION, 2.0f * FULL_ROTATION, FULL_ROTATION)

    /**
     * Animator set to animate cube rotation and skew.
     */
    private val animator = AnimatorSet()

    /**
     * Rotation animator.
     */
    private val rotationAnimator =
        ValueAnimator.ofObject(FloatArrayEvaluator(), initR, endR)

    /**
     * Skew animator.
     */
    private val skewAnimator = ValueAnimator.ofFloat(-1000f, 1000f)

    /**
     * Camera used to render the cube.
     */
    private var camera: PinholeCamera? = null

    /**
     * Called when the activity is created.
     *
     * @param savedInstanceState saved instance state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(com.irurueta.android.gl.cube.app.R.layout.activity_main)
        view = findViewById(com.irurueta.android.gl.cube.app.R.id.cube)

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

    /**
     * Called when the activity is resumed.
     */
    override fun onResume() {
        super.onResume()
        view?.onResume()
        animator.start()
    }

    /**
     * Called when the activity is paused.
     */
    override fun onPause() {
        super.onPause()
        view?.onPause()
        animator.pause()
    }

    private companion object {
        /**
         * Full rotation in radians.
         */
        const val FULL_ROTATION = 2.0f * Math.PI.toFloat()

        /**
         * Duration of the rotation in milliseconds.
         */
        const val ROTATION_DURATION_MILLIS = 30000L

        /**
         * Duration of the skew in milliseconds.
         */
        const val SKEW_DURATION_MILLIS = 1000L

        /**
         * Creates a projection matrix for a Pixel 2 device.
         * @return projection matrix.
         */
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

        /**
         * Creates a camera for the cube.
         *
         * @param view cube texture view.
         * @return camera.
         */
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