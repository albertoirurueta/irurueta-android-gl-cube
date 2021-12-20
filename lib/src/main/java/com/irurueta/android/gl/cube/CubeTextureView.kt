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

import android.content.Context
import android.util.AttributeSet
import com.irurueta.android.glutils.GLTextureView
import com.irurueta.geometry.CameraException
import com.irurueta.geometry.PinholeCamera

/**
 * Draws a 3D cube using a [GLTextureView], which allows transparent background
 * and other view effects and animations.
 */
class CubeTextureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GLTextureView(context, attrs, defStyleAttr) {

    /**
     * Renderer in charge of drawing the scene.
     */
    private var cubeRenderer: CubeRenderer = CubeRenderer(context)

    /**
     * Gets or sets listener being notified when GL surface is created and changes its size.
     */
    var onSurfaceChangedListener
        get() = cubeRenderer.onSurfaceChangedListener
        set(value) {
            cubeRenderer.onSurfaceChangedListener = value
        }

    /**
     * Diffuse color to be used for lighting purposes.
     * Setter requests to render a frame.
     * This is only taken into account if normals are used.
     */
    var diffuseColor
        get() = cubeRenderer.diffuseColor
        set(value) {
            cubeRenderer.diffuseColor = value
            requestRender()
        }

    /**
     * Gets or sets orientation to compute a pinhole camera expressed in view coordinates.
     * If orientation is unknown, view camera is not used.
     * Setter requests to render a frame.
     */
    var orientation
        get() = cubeRenderer.orientation
        set(value) {
            cubeRenderer.orientation = value
            requestRender()
        }

    /**
     * Color to clear the scene.
     * If clear color is transparent, views behind this view will be visible.
     * Setter requests to render a frame.
     */
    var clearColor
        get() = cubeRenderer.clearColor
        set(value) {
            cubeRenderer.clearColor = value
            requestRender()
        }

    /**
     * Gets or sets cube size.
     * Setter requests to render a frame.
     */
    var cubeSize
        get() = cubeRenderer.cubeSize
        set(value) {
            cubeRenderer.cubeSize = value
            requestRender()
        }

    /**
     * Cube position.
     * Setter requests to render a frame.
     */
    var cubePosition
        get() = cubeRenderer.cubePosition
        set(value) {
            cubeRenderer.cubePosition = value
            requestRender()
        }

    /**
     * Cube rotation.
     * Setter requests to render a frame.
     */
    var cubeRotation
        get() = cubeRenderer.cubeRotation
        set(value) {
            cubeRenderer.cubeRotation = value
            requestRender()
        }

    /**
     * Gets or sets near plane value.
     * Any vertex nearer to the camera than this value is ignored and not drawn.
     * Setter requests to render a frame.
     */
    var nearPlane: Float
        get() = cubeRenderer.nearPlane ?: CubeRenderer.DEFAULT_NEAR_PLANE
        set(value) {
            cubeRenderer.nearPlane = value
            requestRender()
        }

    /**
     * Gets or sets far plane value.
     * Any vertex further from the camera than this value is ignored and not drawn.
     * Setter requests to render a frame.
     */
    var farPlane: Float
        get() = cubeRenderer.farPlane ?: CubeRenderer.DEFAULT_FAR_PLANE
        set(value) {
            cubeRenderer.farPlane = value
            requestRender()
        }

    /**
     * Sets near and far plane values and requests to render a frame.
     * Any vertex nearer to the camera than the near plane, or any vertex further
     * from the camera than the far plane is ignored and not drawn.
     *
     * @param nearPlane near plane.
     * @param farPlane far plane.
     */
    fun setNearFarPlanes(nearPlane: Float, farPlane: Float) {
        cubeRenderer.setNearFarPlanes(nearPlane, farPlane)
        requestRender()
    }

    /**
     * Gets or sets camera defining point of view to draw the scene.
     * This camera does not take into account Android view coordinates, in Layman
     * terms this means that y coordinates increase upwards.
     * Setter requests to render a frame.
     *
     * @throws CameraException if there are numerical instabilities in provided camera.
     */
    var camera
        get() = cubeRenderer.camera
        @Throws(CameraException::class)
        @Synchronized
        set(value) {
            cubeRenderer.camera = value
            requestRender()
        }

    /**
     * Gets or sets camera that defines point of view to draw the scene expressed in
     * Android view coordinates.
     * This is only available or can only be set if surface is initialized and orientation is
     * defined (not unknown).
     * Setter requests to render a frame.
     *
     * @throws IllegalArgumentException if value to be set is not null when orientation is unknown,
     * or if value to be set is null when orientation is known.
     * @throws CameraException if there are numerical instabilities in provided camera.
     */
    var viewCamera
        get() = cubeRenderer.viewCamera
        @Throws(IllegalArgumentException::class, CameraException::class)
        @Synchronized
        set(value) {
            cubeRenderer.viewCamera = value
            requestRender()
        }

    /**
     * Gets or sets camera intrinsic parameters.
     *
     * @throws IllegalArgumentException if provided value is null.
     */
    var cameraIntrinsicParameters
        get() = cubeRenderer.cameraIntrinsicParameters
        @Throws(IllegalArgumentException::class)
        @Synchronized
        set(value) {
            cubeRenderer.cameraIntrinsicParameters = value
            requestRender()
        }

    /**
     * Gets or sets camera center position.
     *
     * @throws IllegalArgumentException if provided value is null.
     */
    var cameraCenter
        get() = cubeRenderer.cameraCenter
        @Throws(IllegalArgumentException::class)
        @Synchronized
        set(value) {
            cubeRenderer.cameraCenter = value
            requestRender()
        }

    /**
     * Gets or sets camera rotation.
     *
     * @throws IllegalArgumentException if provided value is null.
     */
    var cameraRotation
        get() = cubeRenderer.cameraRotation
        @Throws(IllegalArgumentException::class)
        @Synchronized
        set(value) {
            cubeRenderer.cameraRotation = value
            requestRender()
        }

    /**
     * Gets or sets view camera intrinsic parameters.
     *
     * @throws IllegalStateException if orientation is unknown.
     * @throws IllegalArgumentException if provided value is null.
     */
    var viewCameraIntrinsicParameters
        get() = cubeRenderer.viewCameraIntrinsicParameters
        @Throws(IllegalStateException::class, IllegalArgumentException::class)
        @Synchronized
        set(value) {
            cubeRenderer.viewCameraIntrinsicParameters = value
            requestRender()
        }

    /**
     * Gets or sets view camera center position.
     *
     * @throws IllegalStateException if orientation is unknown.
     * @throws IllegalArgumentException if provided value is null.
     */
    var viewCameraCenter
        get() = cubeRenderer.viewCameraCenter
        @Throws(IllegalStateException::class, IllegalArgumentException::class)
        @Synchronized
        set(value) {
            cubeRenderer.viewCameraCenter = value
            requestRender()
        }

    /**
     * Gets or sets view camera rotation.
     *
     * @throws IllegalStateException if orientation is unknown.
     * @throws IllegalArgumentException if provided value is null.
     */
    var viewCameraRotation
        get() = cubeRenderer.viewCameraRotation
        @Throws(IllegalStateException::class, IllegalArgumentException::class)
        @Synchronized
        set(value) {
            cubeRenderer.viewCameraRotation = value
            requestRender()
        }

    /**
     * Sets all required values to compute the scene camera matrices.
     *
     * @param nearPlane near plane. Any vertex nearer to the camera than this value will be ignored
     * and not drawn.
     * @param farPlane far plane. Any vertex further from the camera than this value will be ignored
     * and not drawn.
     * @param camera camera to be set.
     *
     * @throws CameraException if there are numerical instabilities in provided camera.
     */
    @Throws(CameraException::class)
    @Synchronized
    fun setValues(nearPlane: Float, farPlane: Float, camera: PinholeCamera) {
        cubeRenderer.setValues(nearPlane, farPlane, camera)
        requestRender()
    }

    /**
     * Called when window is detached.
     * Destroys OpenGL program.
     */
    override fun onDetachedFromWindow() {
        cubeRenderer.destroy()
        super.onDetachedFromWindow()
    }

    init {
        // create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)
        // enforce transparent background
        isOpaque = false
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)

        // render only when new camera position is set.
        setRenderer(cubeRenderer)

        renderMode = RENDERMODE_WHEN_DIRTY
    }

    /**
     * Listener to notify when surface size changes.
     * This can be used to initialize this view once the underlying OpenGL surface is initialized.
     */
    interface OnSurfaceChangedListener {
        /**
         * Called when surface size changes.
         *
         * @param width width of surface expressed in pixels.
         * @param height height of surface expressed in pixels.
         */
        fun onSurfaceChanged(width: Int, height: Int)
    }
}