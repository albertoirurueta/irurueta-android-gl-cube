package com.irurueta.android.gl.cube

import android.content.Context
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import com.irurueta.android.glutils.*
import com.irurueta.geometry.*
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import javax.microedition.khronos.opengles.GL10

@RunWith(RobolectricTestRunner::class)
class CubeTextureViewTest {

    @Test
    fun constructor_setsDefaultValues() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        assertNotNull(view.getPrivateProperty("cubeRenderer"))
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
    fun diffuseColor_setsExpectedValueAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertEquals(Color.rgb(127, 127, 127), view.diffuseColor)

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // set new value
        view.diffuseColor = Color.BLACK

        // check
        assertEquals(Color.BLACK, view.diffuseColor)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun orientation_setsExpectedValueAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertEquals(CameraToDisplayOrientation.ORIENTATION_UNKNOWN, view.orientation)

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // initialize renderer
        val renderer: CubeRenderer? = view.getPrivateProperty("cubeRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        view.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // check
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, view.orientation)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun clearColor_setsExpectedValueAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertEquals(CubeRenderer.DEFAULT_CLEAR_COLOR, view.clearColor)

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // set new value
        view.clearColor = Color.BLACK

        // check
        assertEquals(Color.BLACK, view.clearColor)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun cubeSize_setsExpectedValueAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertEquals(CubeRenderer.DEFAULT_CUBE_SIZE, view.cubeSize)

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // set new value
        val cubeSize = 2.0f * CubeRenderer.DEFAULT_CUBE_SIZE
        view.cubeSize = cubeSize

        // check
        assertEquals(cubeSize, view.cubeSize)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun cubePosition_setsExpectedValueAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertNotNull(view.cubePosition)
        assertEquals(
            view.cubePosition,
            Point3D.create(
                CoordinatesType.INHOMOGENEOUS_COORDINATES,
                doubleArrayOf(0.0, 0.0, CubeRenderer.DEFAULT_CUBE_DISTANCE)
            )
        )

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // set new value
        val cubePosition = Point3D.create()
        view.cubePosition = cubePosition

        // check
        assertSame(cubePosition, view.cubePosition)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun cubeRotation_setsExpectedValueAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertNotNull(view.cubeRotation)
        assertEquals(view.cubeRotation, Quaternion())

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // set new value
        val cubeRotation = Rotation3D.create()
        view.cubeRotation = cubeRotation

        // check
        assertSame(cubeRotation, view.cubeRotation)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun nearPlane_setsExpectedValuesAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertEquals(CubeRenderer.DEFAULT_NEAR_PLANE, view.nearPlane)

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // initialize renderer
        val renderer: CubeRenderer? = view.getPrivateProperty("cubeRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        val nearPlane = 2.0f * NEAR_PLANE
        view.nearPlane = nearPlane

        // check
        assertEquals(nearPlane, view.nearPlane)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun farPlane_setsExpectedValuesAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertEquals(CubeRenderer.DEFAULT_FAR_PLANE, view.farPlane)

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // initialize renderer
        val renderer: CubeRenderer? = view.getPrivateProperty("cubeRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        val farPlane = 2.0f * FAR_PLANE
        view.farPlane = farPlane

        // check
        assertEquals(farPlane, view.farPlane)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun setNearFarPlanes_setsExpectedValuesAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertEquals(CubeRenderer.DEFAULT_NEAR_PLANE, view.nearPlane)
        assertEquals(CubeRenderer.DEFAULT_FAR_PLANE, view.farPlane)

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // initialize renderer
        val renderer: CubeRenderer? = view.getPrivateProperty("cubeRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new values
        val nearPlane = 2.0f * NEAR_PLANE
        val farPlane = 2.0f * FAR_PLANE
        view.setNearFarPlanes(nearPlane, farPlane)

        // check
        assertEquals(nearPlane, view.nearPlane)
        assertEquals(farPlane, view.farPlane)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun camera_setsExpectedValueAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertNull(view.camera)

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // initialize renderer
        val renderer: CubeRenderer? = view.getPrivateProperty("cubeRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        val camera = PinholeCamera()
        view.camera = camera

        // check
        assertSame(camera, view.camera)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun viewCamera_setsExpectedValueAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertNull(view.viewCamera)
        assertNull(view.camera)

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // initialize renderer
        val renderer: CubeRenderer? = view.getPrivateProperty("cubeRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set known orientation
        view.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // set new value
        val viewCamera = PinholeCamera()
        view.viewCamera = viewCamera

        // check
        assertSame(viewCamera, view.viewCamera)
        assertNotNull(view.camera)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, view.orientation)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun cameraIntrinsicParameters_setsExpectedValueAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertNull(view.cameraIntrinsicParameters)
        assertNull(view.camera)

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // initialize renderer
        val renderer: CubeRenderer? = view.getPrivateProperty("cubeRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        val intrinsics = PinholeCameraIntrinsicParameters()
        view.cameraIntrinsicParameters = intrinsics

        // check
        assertSame(intrinsics, view.cameraIntrinsicParameters)
        assertNotNull(view.camera)
        assertSame(intrinsics, view.camera?.intrinsicParameters)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun cameraCenter_setsExpectedValueAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertNull(view.cameraCenter)
        assertNull(view.camera)

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // initialize renderer
        val renderer: CubeRenderer? = view.getPrivateProperty("cubeRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        val center = Point3D.create()
        view.cameraCenter = center

        // check
        assertSame(center, view.cameraCenter)
        assertNotNull(view.camera)
        assertSame(center, view.camera?.cameraCenter)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun cameraRotation_setsExpectedValueAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertNull(view.cameraRotation)
        assertNull(view.camera)

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // initialize renderer
        val renderer: CubeRenderer? = view.getPrivateProperty("cubeRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        val rotation = Rotation3D.create()
        view.cameraRotation = rotation

        // check
        assertSame(rotation, view.cameraRotation)
        assertNotNull(view.camera)
        assertSame(rotation, view.camera?.cameraRotation)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun viewCameraIntrinsicParameters_setsExpectedValueAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertNull(view.viewCameraIntrinsicParameters)
        assertNull(view.viewCamera)
        assertNull(view.camera)

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // initialize renderer
        val renderer: CubeRenderer? = view.getPrivateProperty("cubeRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set known orientation
        view.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // set new value
        val intrinsics = PinholeCameraIntrinsicParameters()
        view.viewCameraIntrinsicParameters = intrinsics

        // check
        assertSame(intrinsics, view.viewCameraIntrinsicParameters)
        assertNotNull(view.viewCamera)
        assertSame(intrinsics, view.viewCamera?.intrinsicParameters)
        assertNotNull(view.camera)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, view.orientation)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun viewCameraCenter_setsExpectedValueAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertNull(view.viewCameraCenter)
        assertNull(view.viewCamera)
        assertNull(view.camera)

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // initialize renderer
        val renderer: CubeRenderer? = view.getPrivateProperty("cubeRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set known orientation
        view.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // set new value
        val center = Point3D.create()
        view.viewCameraCenter = center

        // check
        assertSame(center, view.viewCameraCenter)
        assertNotNull(view.viewCamera)
        assertSame(center, view.viewCamera?.cameraCenter)
        assertNotNull(view.camera)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, view.orientation)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun viewCameraRotation_setsExpectedValueAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertNull(view.viewCameraRotation)
        assertNull(view.viewCamera)
        assertNull(view.camera)

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // initialize renderer
        val renderer: CubeRenderer? = view.getPrivateProperty("cubeRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set known orientation
        view.orientation = CameraToDisplayOrientation.ORIENTATION_0_DEGREES

        // set new value
        val rotation = Rotation3D.create()
        view.viewCameraRotation = rotation

        // check
        assertSame(rotation, view.viewCameraRotation)
        assertNotNull(view.viewCamera)
        assertSame(rotation, view.viewCamera?.cameraRotation)
        assertNotNull(view.camera)
        assertEquals(CameraToDisplayOrientation.ORIENTATION_0_DEGREES, view.orientation)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun setValues_setsExpectedValuesAndRequestsRender() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // check default value
        assertEquals(CubeRenderer.DEFAULT_NEAR_PLANE, view.nearPlane)
        assertEquals(CubeRenderer.DEFAULT_FAR_PLANE, view.farPlane)
        assertNull(view.camera)

        // ensure flag to request render is false
        val glThreadField = GLTextureView::class.java.getDeclaredField("glThread")
        glThreadField.isAccessible = true
        val glThread = glThreadField.get(view) as Thread?
        requireNotNull(glThread)

        val classes = GLTextureView::class.java.declaredClasses
        val glThreadClass: Class<*>? = classes.firstOrNull { it.name.endsWith("GLThread") }
        requireNotNull(glThreadClass)
        val field = glThreadClass.getDeclaredField("requestRender")
        field.isAccessible = true
        val requestRender1 = field.getBoolean(glThread)
        assertTrue(requestRender1)

        field.set(glThread, false)

        val requestRender2: Boolean = field.getBoolean(glThread)
        assertFalse(requestRender2)

        // initialize renderer
        val renderer: CubeRenderer? = view.getPrivateProperty("cubeRenderer")
        requireNotNull(renderer)

        val gl = mockk<GL10>()
        renderer.onSurfaceChanged(gl, WIDTH, HEIGHT)

        // set new value
        val nearPlane = 2.0f * NEAR_PLANE
        val farPlane = 2.0f * FAR_PLANE
        val camera = PinholeCamera()
        view.setValues(nearPlane, farPlane, camera)

        // check
        assertEquals(nearPlane, view.nearPlane)
        assertEquals(farPlane, view.farPlane)
        assertSame(camera, view.camera)

        val requestRender3 = field.getBoolean(glThread)
        assertTrue(requestRender3)
    }

    @Test
    fun onDetachedFromWindow() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val view = CubeTextureView(context)

        // set renderer spy
        val renderer: CubeRenderer? = view.getPrivateProperty("cubeRenderer")
        requireNotNull(renderer)
        val rendererSpy = spyk(renderer)

        view.setPrivateProperty("cubeRenderer", rendererSpy)

        // execute
        view.callPrivateFunc("onDetachedFromWindow")

        // check renderer is called
        verify(exactly = 1) { rendererSpy.destroy() }

        // check super is called
        val detachedField = GLTextureView::class.java.getDeclaredField("detached")
        detachedField.isAccessible = true
        val detached1: Boolean = detachedField.getBoolean(view)
        assertTrue(detached1)
    }

    private companion object {
        const val WIDTH = 480
        const val HEIGHT = 640

        const val NEAR_PLANE = 0.1f
        const val FAR_PLANE = 100.0f
    }
}