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

package com.irurueta.android.gl.cube.test

import android.graphics.Color
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.irurueta.android.glutils.CameraToDisplayOrientation
import com.irurueta.android.glutils.OpenGlToCameraHelper
import com.irurueta.android.gl.cube.CubeRenderer
import com.irurueta.android.gl.cube.CubeTextureView
import com.irurueta.geometry.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CubeTextureViewTest {

    @get:Rule
    val rule = activityScenarioRule<CubeTextureViewActivity>()

    private var activity: CubeTextureViewActivity? = null
    private var view: CubeTextureView? = null

    @Before
    fun setUp() {
        rule.scenario.onActivity { activity ->
            this.activity = activity
            view = activity.findViewById(R.id.cube_texture_view_test)
        }
    }

    @After
    fun tearDown() {
        view = null
        activity = null
    }

    @Test
    fun constructor_whenNotAttached_setsDefaultValues() {
        val activity = this.activity ?: return fail()
        UiThreadStatement.runOnUiThread {
            val view = CubeTextureView(activity)

            assertEquals(Color.rgb(127, 127, 127), view.diffuseColor)
            assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, view.orientation)
            assertEquals(CubeRenderer.DEFAULT_CLEAR_COLOR, view.clearColor)
            assertEquals(CubeRenderer.DEFAULT_CUBE_SIZE, view.cubeSize)
            assertNotNull(view.cubePosition)
            assertEquals(
                view.cubePosition,
                Point3D.create(
                    CoordinatesType.INHOMOGENEOUS_COORDINATES,
                    doubleArrayOf(0.0, 0.0, CubeRenderer.DEFAULT_CUBE_DISTANCE)
                )
            )
            assertNotNull(view.cubeRotation)
            assertEquals(view.cubeRotation, Quaternion())
            assertEquals(CubeRenderer.DEFAULT_NEAR_PLANE, view.nearPlane)
            assertEquals(CubeRenderer.DEFAULT_FAR_PLANE, view.farPlane)
            assertNull(view.camera)
            assertNull(view.viewCamera)
            assertNull(view.cameraIntrinsicParameters)
            assertNull(view.cameraCenter)
            assertNull(view.cameraRotation)
            assertNull(view.viewCameraIntrinsicParameters)
            assertNull(view.viewCameraCenter)
            assertNull(view.viewCameraRotation)
        }
    }

    @Test
    fun constructor_whenAttached_setsDefaultValues() {
        val view = this.view ?: return fail()

        assertEquals(Color.rgb(127, 127, 127), view.diffuseColor)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, view.orientation)
        assertEquals(CubeRenderer.DEFAULT_CLEAR_COLOR, view.clearColor)
        assertEquals(CubeRenderer.DEFAULT_CUBE_SIZE, view.cubeSize)
        assertNotNull(view.cubePosition)
        assertEquals(
            view.cubePosition,
            Point3D.create(
                CoordinatesType.INHOMOGENEOUS_COORDINATES,
                doubleArrayOf(0.0, 0.0, CubeRenderer.DEFAULT_CUBE_DISTANCE)
            )
        )
        assertNotNull(view.cubeRotation)
        assertEquals(view.cubeRotation, Quaternion())
        assertEquals(CubeRenderer.DEFAULT_NEAR_PLANE, view.nearPlane)
        assertEquals(CubeRenderer.DEFAULT_FAR_PLANE, view.farPlane)
        assertNull(view.camera)
        assertNull(view.viewCamera)
        assertNull(view.cameraIntrinsicParameters)
        assertNull(view.cameraCenter)
        assertNull(view.cameraRotation)
        assertNull(view.viewCameraIntrinsicParameters)
        assertNull(view.viewCameraCenter)
        assertNull(view.viewCameraRotation)
    }

    @Test
    fun diffuseColor_changesIllumination() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.camera)
        assertEquals(Color.rgb(127, 127, 127), view.diffuseColor)

        // set camera
        initializeCamera(view)
        Thread.sleep(SLEEP)

        // set diffuse color
        val diffuseColor = Color.rgb(50, 50, 50)
        UiThreadStatement.runOnUiThread {
            view.diffuseColor = diffuseColor
        }

        // check
        assertEquals(diffuseColor, view.diffuseColor)
        Thread.sleep(SLEEP)
    }

    @Test
    fun orientation_when0_changesOrientation() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.camera)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, view.orientation)

        // set camera
        initializeCamera(view)
        Thread.sleep(SLEEP)

        // set cube size
        val cubeSize = 0.5f * CubeRenderer.DEFAULT_CUBE_SIZE
        UiThreadStatement.runOnUiThread {
            view.cubeSize = cubeSize
        }

        // check
        assertEquals(cubeSize, view.cubeSize)

        // set orientation
        UiThreadStatement.runOnUiThread {
            view.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES
        }

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, view.orientation)
        Thread.sleep(SLEEP)

        val viewCamera = createCamera(view)
        UiThreadStatement.runOnUiThread {
            view.viewCamera = viewCamera
        }

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, view.orientation)
        assertSame(viewCamera, view.viewCamera)
        assertNotNull(view.camera)
        Thread.sleep(SLEEP)

        // set back to unknown orientation
        UiThreadStatement.runOnUiThread {
            view.orientation = CameraToDisplayOrientation.ORIENTATION_UNKNOWN
        }

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, view.orientation)
        assertNull(view.viewCamera)
        assertNotNull(view.camera)
        Thread.sleep(SLEEP)
    }

    @Test
    fun orientation_when90_changesOrientation() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.camera)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, view.orientation)

        // set camera
        initializeCamera(view)
        Thread.sleep(SLEEP)

        // set cube size
        val cubeSize = 0.5f * CubeRenderer.DEFAULT_CUBE_SIZE
        UiThreadStatement.runOnUiThread {
            view.cubeSize = cubeSize
        }

        // check
        assertEquals(cubeSize, view.cubeSize)

        // set orientation
        UiThreadStatement.runOnUiThread {
            view.orientation = CameraToDisplayOrientation.ORIENTATION_90_DEGREES
        }

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, view.orientation)
        Thread.sleep(SLEEP)

        val viewCamera = createCamera(view)
        UiThreadStatement.runOnUiThread {
            view.viewCamera = viewCamera
        }

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_90_DEGREES, view.orientation)
        assertSame(viewCamera, view.viewCamera)
        assertNotNull(view.camera)
        Thread.sleep(SLEEP)

        // set back to unknown orientation
        UiThreadStatement.runOnUiThread {
            view.orientation = CameraToDisplayOrientation.ORIENTATION_UNKNOWN
        }

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, view.orientation)
        assertNull(view.viewCamera)
        assertNotNull(view.camera)
        Thread.sleep(SLEEP)
    }

    @Test
    fun orientation_when180_changesOrientation() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.camera)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, view.orientation)

        // set camera
        initializeCamera(view)
        Thread.sleep(SLEEP)

        // set cube size
        val cubeSize = 0.5f * CubeRenderer.DEFAULT_CUBE_SIZE
        UiThreadStatement.runOnUiThread {
            view.cubeSize = cubeSize
        }

        // check
        assertEquals(cubeSize, view.cubeSize)

        // set orientation
        UiThreadStatement.runOnUiThread {
            view.orientation = CameraToDisplayOrientation.ORIENTATION_180_DEGREES
        }

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, view.orientation)
        Thread.sleep(SLEEP)

        val viewCamera = createCamera(view)
        UiThreadStatement.runOnUiThread {
            view.viewCamera = viewCamera
        }

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_180_DEGREES, view.orientation)
        assertSame(viewCamera, view.viewCamera)
        assertNotNull(view.camera)
        Thread.sleep(SLEEP)

        // set back to unknown orientation
        UiThreadStatement.runOnUiThread {
            view.orientation = CameraToDisplayOrientation.ORIENTATION_UNKNOWN
        }

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, view.orientation)
        assertNull(view.viewCamera)
        assertNotNull(view.camera)
        Thread.sleep(SLEEP)
    }

    @Test
    fun orientation_when270_changesOrientation() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.camera)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, view.orientation)

        // set camera
        initializeCamera(view)
        Thread.sleep(SLEEP)

        // set cube size
        val cubeSize = 0.5f * CubeRenderer.DEFAULT_CUBE_SIZE
        UiThreadStatement.runOnUiThread {
            view.cubeSize = cubeSize
        }

        // check
        assertEquals(cubeSize, view.cubeSize)

        // set orientation
        UiThreadStatement.runOnUiThread {
            view.orientation = CameraToDisplayOrientation.ORIENTATION_270_DEGREES
        }

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, view.orientation)
        Thread.sleep(SLEEP)

        val viewCamera = createCamera(view)
        UiThreadStatement.runOnUiThread {
            view.viewCamera = viewCamera
        }

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_270_DEGREES, view.orientation)
        assertSame(viewCamera, view.viewCamera)
        assertNotNull(view.camera)
        Thread.sleep(SLEEP)

        // set back to unknown orientation
        UiThreadStatement.runOnUiThread {
            view.orientation = CameraToDisplayOrientation.ORIENTATION_UNKNOWN
        }

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, view.orientation)
        assertNull(view.viewCamera)
        assertNotNull(view.camera)
        Thread.sleep(SLEEP)
    }

    @Test
    fun clearColor_setsNewColor() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.camera)
        assertEquals(CubeRenderer.DEFAULT_CLEAR_COLOR, view.clearColor)

        // set camera
        initializeCamera(view)
        Thread.sleep(SLEEP)

        // set cube size
        val cubeSize = 0.5f * CubeRenderer.DEFAULT_CUBE_SIZE
        UiThreadStatement.runOnUiThread {
            view.cubeSize = cubeSize
        }

        // check
        assertEquals(cubeSize, view.cubeSize)

        // set clear color
        UiThreadStatement.runOnUiThread {
            view.clearColor = Color.WHITE
        }

        // check
        assertEquals(Color.WHITE, view.clearColor)
        Thread.sleep(SLEEP)
    }

    @Test
    fun cubeSize_changesCubeSize() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.camera)
        assertEquals(CubeRenderer.DEFAULT_CUBE_SIZE, view.cubeSize)

        // set camera
        initializeCamera(view)
        Thread.sleep(SLEEP)

        // set cube size
        val cubeSize = 0.5f * CubeRenderer.DEFAULT_CUBE_SIZE
        UiThreadStatement.runOnUiThread {
            view.cubeSize = cubeSize
        }

        // check
        assertEquals(cubeSize, view.cubeSize)
        Thread.sleep(SLEEP)
    }

    @Test
    fun cubePosition_changesCubePosition() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.camera)
        assertNotNull(view.cubePosition)
        assertEquals(
            view.cubePosition,
            Point3D.create(
                CoordinatesType.INHOMOGENEOUS_COORDINATES,
                doubleArrayOf(0.0, 0.0, CubeRenderer.DEFAULT_CUBE_DISTANCE)
            )
        )

        // set camera
        initializeCamera(view)
        Thread.sleep(SLEEP)

        // set cube position
        val cubePosition =
            InhomogeneousPoint3D(-0.5, -0.5, 2.0 * CubeRenderer.DEFAULT_CUBE_DISTANCE)
        UiThreadStatement.runOnUiThread {
            view.cubePosition = cubePosition
        }

        // check
        assertSame(cubePosition, view.cubePosition)
        Thread.sleep(SLEEP)
    }

    @Test
    fun cubeRotation_changesCubeRotation() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.camera)
        assertNotNull(view.cubeRotation)
        assertEquals(view.cubeRotation, Quaternion())

        // set camera
        initializeCamera(view)
        Thread.sleep(SLEEP)

        // set cube size
        val cubeSize = 0.5f * CubeRenderer.DEFAULT_CUBE_SIZE
        UiThreadStatement.runOnUiThread {
            view.cubeSize = cubeSize
        }

        // check
        assertEquals(cubeSize, view.cubeSize)

        // set cube rotation
        val cubeRotation =
            Quaternion(Math.toRadians(20.0), Math.toRadians(20.0), Math.toRadians(20.0))
        UiThreadStatement.runOnUiThread {
            view.cubeRotation = cubeRotation
        }

        // check
        assertSame(cubeRotation, view.cubeRotation)
        Thread.sleep(SLEEP)
    }

    @Test
    fun nearPlane_changesNearPlane() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.camera)
        assertEquals(CubeRenderer.DEFAULT_NEAR_PLANE, view.nearPlane)

        // set camera
        initializeCamera(view)
        Thread.sleep(SLEEP)

        // set near plane
        val nearPlane = 2.0f * CubeRenderer.DEFAULT_NEAR_PLANE
        UiThreadStatement.runOnUiThread {
            view.nearPlane = nearPlane
        }

        // check
        assertEquals(nearPlane, view.nearPlane)
        Thread.sleep(SLEEP)
    }

    @Test
    fun farPlane_changesFarPlane() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.camera)
        assertEquals(CubeRenderer.DEFAULT_FAR_PLANE, view.farPlane)

        // set camera
        initializeCamera(view)
        Thread.sleep(SLEEP)

        // set far plane
        val farPlane = 2.0f * CubeRenderer.DEFAULT_FAR_PLANE
        UiThreadStatement.runOnUiThread {
            view.farPlane = farPlane
        }

        // check
        assertEquals(farPlane, view.farPlane)
        Thread.sleep(SLEEP)
    }

    @Test
    fun setNearFarPlanes_changesNearFarPlanes() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.camera)
        assertEquals(CubeRenderer.DEFAULT_NEAR_PLANE, view.nearPlane)
        assertEquals(CubeRenderer.DEFAULT_FAR_PLANE, view.farPlane)

        // set camera
        initializeCamera(view)
        Thread.sleep(SLEEP)

        // set near far planes
        val nearPlane = 2.0f * CubeRenderer.DEFAULT_NEAR_PLANE
        val farPlane = 2.0f * CubeRenderer.DEFAULT_FAR_PLANE
        UiThreadStatement.runOnUiThread {
            view.setNearFarPlanes(nearPlane, farPlane)
        }

        // check
        assertEquals(nearPlane, view.nearPlane)
        assertEquals(farPlane, view.farPlane)
        Thread.sleep(SLEEP)
    }

    @Test
    fun camera_setsProvidedCamera() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.camera)

        // set camera
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            view.width,
            view.height
        )
        val camera = PinholeCamera(intrinsics, Rotation3D.create(), Point3D.create())
        UiThreadStatement.runOnUiThread {
            view.camera = camera
        }

        assertSame(camera, view.camera)
        Thread.sleep(SLEEP)
    }

    @Test
    fun viewCamera_setsProvidedCamera() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.camera)
        assertNull(view.viewCamera)

        // set camera
        initializeCamera(view)
        assertNull(view.viewCamera)
        assertNotNull(view.camera)
        Thread.sleep(SLEEP)

        // set orientation
        UiThreadStatement.runOnUiThread {
            view.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES
        }

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, view.orientation)
        assertNotNull(view.viewCamera)
        assertNotNull(view.camera)
        Thread.sleep(SLEEP)

        // set view camera
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            view.width,
            view.height
        )
        val camera = PinholeCamera(intrinsics, Rotation3D.create(), Point3D.create())
        UiThreadStatement.runOnUiThread {
            view.viewCamera = camera
        }

        // check
        assertSame(camera, view.viewCamera)
        Thread.sleep(SLEEP)
    }

    @Test
    fun cameraIntrinsicParameters_setsProvidedValue() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.cameraIntrinsicParameters)

        // set intrinsic parameters
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            view.width,
            view.height
        )
        UiThreadStatement.runOnUiThread {
            view.cameraIntrinsicParameters = intrinsics
        }

        assertSame(intrinsics, view.cameraIntrinsicParameters)
        Thread.sleep(SLEEP)
    }

    @Test
    fun cameraCenter_setsProvidedValue() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.cameraCenter)

        // set camera center
        val center = Point3D.create()
        UiThreadStatement.runOnUiThread {
            view.cameraCenter = center
        }

        assertSame(center, view.cameraCenter)
        Thread.sleep(SLEEP)
    }

    @Test
    fun cameraRotation_setsProvidedValue() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.cameraRotation)

        // set camera rotation
        val rotation = Rotation3D.create()
        UiThreadStatement.runOnUiThread {
            view.cameraRotation = rotation
        }

        assertSame(rotation, view.cameraRotation)
        Thread.sleep(SLEEP)
    }

    @Test
    fun viewCameraIntrinsicParameters_setsProvidedValue() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.viewCameraIntrinsicParameters)
        assertNull(view.camera)

        // set camera
        initializeCamera(view)
        assertNull(view.viewCameraIntrinsicParameters)
        assertNotNull(view.camera)
        Thread.sleep(SLEEP)

        // set orientation
        UiThreadStatement.runOnUiThread {
            view.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES
        }

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, view.orientation)
        assertNotNull(view.viewCameraIntrinsicParameters)
        assertNotNull(view.camera)
        Thread.sleep(SLEEP)

        // set view camera intrinsic parameters
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            view.width,
            view.height
        )
        UiThreadStatement.runOnUiThread {
            view.viewCameraIntrinsicParameters = intrinsics
        }

        // check
        assertSame(intrinsics, view.viewCameraIntrinsicParameters)
        Thread.sleep(SLEEP)
    }

    @Test
    fun viewCameraCenter_setsProvidedValue() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.viewCameraCenter)
        assertNull(view.camera)

        // set camera
        initializeCamera(view)
        assertNull(view.viewCameraCenter)
        assertNotNull(view.camera)
        Thread.sleep(SLEEP)

        // set orientation
        UiThreadStatement.runOnUiThread {
            view.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES
        }

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, view.orientation)
        assertNotNull(view.viewCameraCenter)
        assertNotNull(view.camera)
        Thread.sleep(SLEEP)

        // set view camera center
        val center = Point3D.create()
        UiThreadStatement.runOnUiThread {
            view.viewCameraCenter = center
        }

        // check
        assertSame(center, view.viewCameraCenter)
        Thread.sleep(SLEEP)
    }

    @Test
    fun viewCameraRotation_setsProvidedValue() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.viewCameraRotation)
        assertNull(view.camera)

        // set camera
        initializeCamera(view)
        assertNull(view.viewCameraRotation)
        assertNotNull(view.camera)
        Thread.sleep(SLEEP)

        // set orientation
        UiThreadStatement.runOnUiThread {
            view.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES
        }

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, view.orientation)
        assertNotNull(view.viewCameraRotation)
        assertNotNull(view.camera)
        Thread.sleep(SLEEP)

        // set view camera rotation
        val rotation = Rotation3D.create()
        UiThreadStatement.runOnUiThread {
            view.viewCameraRotation = rotation
        }
    }

    @Test
    fun setValues_setsProvidedValues() {
        val view = this.view ?: return fail()

        // check default value
        assertNull(view.camera)

        // set values
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            view.width,
            view.height
        )
        val nearPlane = 2.0f * CubeRenderer.DEFAULT_NEAR_PLANE
        val farPlane = 2.0f * CubeRenderer.DEFAULT_FAR_PLANE
        val camera = PinholeCamera(intrinsics, Rotation3D.create(), Point3D.create())
        UiThreadStatement.runOnUiThread {
            view.setValues(nearPlane, farPlane, camera)
        }

        assertEquals(nearPlane, view.nearPlane)
        assertEquals(farPlane, view.farPlane)
        assertSame(camera, view.camera)
        Thread.sleep(SLEEP)
    }

    private fun initializeCamera(view: CubeTextureView) {
        val camera = createCamera(view)
        UiThreadStatement.runOnUiThread {
            view.camera = camera
        }

        assertSame(camera, view.camera)
    }

    private fun createCamera(view: CubeTextureView) : PinholeCamera {
        val projectionMatrix = createProjectionMatrix()
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix,
            view.width,
            view.height
        )
        return PinholeCamera(intrinsics, Rotation3D.create(), Point3D.create())
    }

    private companion object {
        const val SLEEP = 500L

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
    }
}