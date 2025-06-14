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
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.irurueta.algebra.Matrix
import com.irurueta.android.glutils.CameraConverter
import com.irurueta.android.glutils.CameraToDisplayOrientation
import com.irurueta.geometry.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Renderer class in charge of rendering a cube.
 *
 * @property context context.
 * @property onSurfaceChangedListener listener to notify when surface has changed.
 */
class CubeRenderer(
    val context: Context,
    var onSurfaceChangedListener: CubeTextureView.OnSurfaceChangedListener? = null
) : GLSurfaceView.Renderer {

    /**
     * Converts cameras to matrices used by OpenGL.
     */
    private var converter: CameraConverter? = null

    /**
     * 4x4 matrix to draw the scene.
     * Includes both [cameraModelViewProjectionMatrix] and [modelViewMatrix].
     */
    private var modelViewProjectionMatrix = FloatArray(HOM_MATRIX_LENGTH)

    /**
     * 4x4 matrix to transform cube scale, position and rotation
     */
    private var modelViewMatrix = FloatArray(HOM_MATRIX_LENGTH)

    /**
     * Contains camera model view projection matrix defining the frustum to draw into
     * and the camera position and orientation.
     */
    private val cameraModelViewProjectionMatrix
        get() = converter?.modelViewProjectionMatrix

    /**
     * Matrix for normals.
     */
    private var normalMatrix = FloatArray(9)

    /**
     * Coordinates of cube vertices.
     */
    private var cubeVerticesCoordinatesData = floatArrayOf(
        // top points
        1.0f, 1.0f, -1.0f,
        -1.0f, 1.0f, -1.0f,
        -1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        // bottom points
        1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, 1.0f
    )

    /**
     * Indices of cube vertices to be drawn as triangles.
     */
    private var cubeIndices = intArrayOf(
        // top
        0, 1, 2,
        0, 2, 3,
        // bottom
        4, 5, 6,
        4, 6, 7,
        // left
        1, 2, 6,
        6, 5, 1,
        // right
        0, 3, 7,
        7, 4, 0,
        // front
        2, 3, 7,
        7, 6, 2,
        // back
        1, 0, 4,
        4, 5, 1
    )

    /**
     * Array containing id's of uniform variables used by shaders.
     */
    private val uniforms = IntArray(NUM_UNIFORMS)

    /**
     * ID of program where shaders have been linked.
     */
    private var program = 0

    /**
     * ID of attribute where buffer containing vertices positions is bound.
     */
    private var vertexPositionAttribute = 0

    /**
     * ID of attribute where buffer containing vertices normals are bound.
     */
    private var vertexNormalAttribute = 0

    /**
     * ID of attribute where buffer containing RGB vertices colors are bound.
     */
    private var vertexColor3Attribute = 0

    /**
     * ID of attribute where buffer containing RGBA vertices colors are bound.
     */
    private var vertexColor4Attribute = 0

    /**
     * ID of vertices positions buffer.
     */
    private var vertexPositionsBufferId = 0

    /**
     * ID of vertices normals buffer.
     */
    private var vertexNormalsBufferId = 0

    /**
     * ID of vertices colors buffer.
     */
    private var vertexColorsBufferId = 0

    /**
     * ID of buffer containing vertices ordered indices to draw the scene.
     */
    private var vertexIndicesBufferId = 0

    /**
     * Indicates whether this renderer takes into account normals.
     */
    private var hasNormals = DEFAULT_HAS_NORMALS

    /**
     * Indicates whether this renderer takes into account colors.
     */
    private var hasColors = DEFAULT_HAS_COLORS

    /**
     * Indicates whether this renderer has already been destroyed or not.
     */
    private var rendererDestroyed = false

    /**
     * Matrix containing rotation to apply to cube.
     * This is re-used for efficiency purposes.
     */
    private var cubeRotationMatrix =
        Matrix.identity(Rotation3D.INHOM_COORDS, Rotation3D.INHOM_COORDS)

    /**
     * Colors of cube vertices.
     */
    private var cubeColors =
        intArrayOf(COLOR_1, COLOR_2, COLOR_3, COLOR_4, COLOR_5, COLOR_6, COLOR_7, COLOR_8)

    /**
     * Diffuse color to be used for lighting purposes.
     * This is only taken into account if normals are used.
     */
    var diffuseColor = DEFAULT_DIFFUSE_COLOR

    /**
     * Gets or sets orientation to compute a pinhole camera expressed in view coordinates.
     * If orientation is unknown, view camera is not used.
     */
    var orientation
        get() = converter?.orientation ?: CameraToDisplayOrientation.ORIENTATION_UNKNOWN
        set(value) {
            val converter = converter
            checkNotNull(converter)

            converter.orientation = value
        }

    /**
     * Width of the scene expressed in pixels.
     * Notice that OpenGL uses normalized coordinates between -1.0f and 1.0f, but this field
     * contains the actual view width expressed in pixels.
     * This value is only available once the surface to draw has been created.
     */
    val width
        get() = converter?.width

    /**
     * Height of the scene expressed in pixels.
     * Notice that OpenGL uses normalized coordinates between -1.0f and 1.0f, but this field
     * contains the actual view height expressed in pixels.
     * This value is only available once the surface to draw has been created.
     */
    val height
        get() = converter?.height

    /**
     * Color to clear the scene.
     */
    var clearColor = DEFAULT_CLEAR_COLOR

    /**
     * Cube size.
     */
    var cubeSize = DEFAULT_CUBE_SIZE
        set(value) {
            require(value >= 0.0f)
            field = value
        }

    /**
     * Cube position.
     */
    var cubePosition: Point3D = InhomogeneousPoint3D(0.0, 0.0, DEFAULT_CUBE_DISTANCE)

    /**
     * Cube rotation.
     */
    var cubeRotation: Rotation3D = Rotation3D.create()

    /**
     * Near plane value.
     * Any vertex nearer to the camera than this value is ignored and not drawn.
     *
     * @throws IllegalArgumentException if provided value is null
     * @throws IllegalStateException if surface to draw has not been initialized.
     */
    var nearPlane
        get() = converter?.nearPlane
        @Throws(IllegalArgumentException::class, IllegalStateException::class)
        set(value) {
            requireNotNull(value)

            val converter = converter
            checkNotNull(converter)

            converter.nearPlane = value
        }

    /**
     * Far plane value.
     * Any vertex further from the camera than this value is ignored and not drawn.
     *
     * @throws IllegalArgumentException if provided value is null
     * @throws IllegalStateException if surface to draw has not been initialized.
     */
    var farPlane
        get() = converter?.farPlane
        @Throws(IllegalArgumentException::class, IllegalStateException::class)
        set(value) {
            requireNotNull(value)

            val converter = converter
            checkNotNull(converter)

            converter.farPlane = value
        }

    /**
     * Sets near and far plane values.
     * Any vertex nearer to the camera than the near plane, or any vertex further
     * from the camera than the far plane is ignored and not drawn.
     *
     * @param nearPlane near plane.
     * @param farPlane far plane.
     * @throws IllegalStateException if surface to draw has not been initialized.
     */
    @Throws(IllegalStateException::class)
    fun setNearFarPlanes(nearPlane: Float, farPlane: Float) {
        val converter = converter
        checkNotNull(converter)

        converter.setNearFarPlanes(nearPlane, farPlane)
    }

    /**
     * Gets or sets camera defining point of view to draw the scene.
     * This camera does not take into account Android view coordinates, in Layman
     * terms this means that y coordinates increase upwards.
     *
     * @throws IllegalStateException if surface to draw has not been initialized.
     * @throws CameraException if there are numerical instabilities in provided camera.
     */
    var camera
        get() = converter?.camera
        @Throws(IllegalStateException::class, CameraException::class)
        set(value) {
            val converter = converter
            checkNotNull(converter)

            converter.camera = value
        }

    /**
     * Gets or sets camera that defines point of view to draw the scene expressed in
     * Android view coordinates.
     * This is only available or can only be set if surface is initialized and orientation is
     * defined (not unknown).
     *
     * @throws IllegalStateException if surface to draw has not been initialized.
     * @throws IllegalArgumentException if value to be set is not null when orientation is unknown,
     * or if value to be set is null when orientation is known.
     * @throws CameraException if there are numerical instabilities in provided camera.
     */
    var viewCamera
        get() = converter?.viewCamera
        @Throws(
            IllegalStateException::class,
            IllegalArgumentException::class,
            CameraException::class
        )
        set(value) {
            val converter = converter
            checkNotNull(converter)

            converter.viewCamera = value
        }

    /**
     * Gets or sets camera intrinsic parameters.
     *
     * @throws IllegalStateException if surface to draw has not been initialized.
     * @throws IllegalArgumentException if provided value is null.
     */
    var cameraIntrinsicParameters
        get() = converter?.cameraIntrinsicParameters
        @Throws(IllegalStateException::class, IllegalArgumentException::class)
        set(value) {
            val converter = converter
            checkNotNull(converter)

            converter.cameraIntrinsicParameters = value
        }

    /**
     * Gets or sets camera center position.
     *
     * @throws IllegalStateException if surface to draw has not been initialized.
     * @throws IllegalArgumentException if provided value is null.
     */
    var cameraCenter
        get() = converter?.cameraCenter
        @Throws(IllegalStateException::class, IllegalArgumentException::class)
        set(value) {
            val converter = converter
            checkNotNull(converter)

            converter.cameraCenter = value
        }

    /**
     * Gets or sets camera rotation.
     *
     * @throws IllegalStateException if surface to draw has not been initialized.
     * @throws IllegalArgumentException if provided value is null.
     */
    var cameraRotation
        get() = converter?.cameraRotation
        @Throws(IllegalStateException::class, IllegalArgumentException::class)
        set(value) {
            val converter = converter
            checkNotNull(converter)

            converter.cameraRotation = value
        }

    /**
     * Gets or sets view camera intrinsic parameters.
     *
     * @throws IllegalStateException if surface to draw has not been initialized or orientation is
     * unknown.
     * @throws IllegalArgumentException if provided value is null.
     */
    var viewCameraIntrinsicParameters
        get() = converter?.viewCameraIntrinsicParameters
        @Throws(IllegalStateException::class, IllegalArgumentException::class)
        set(value) {
            val converter = converter
            checkNotNull(converter)

            converter.viewCameraIntrinsicParameters = value
        }

    /**
     * Gets or sets view camera center position.
     *
     * @throws IllegalStateException if surface to draw has not been initialized or orientation is
     * unknown.
     * @throws IllegalArgumentException if provided value is null.
     */
    var viewCameraCenter
        get() = converter?.viewCameraCenter
        @Throws(IllegalStateException::class, IllegalArgumentException::class)
        set(value) {
            val converter = converter
            checkNotNull(converter)

            converter.viewCameraCenter = value
        }

    /**
     * Gets or sets view camera rotation.
     *
     * @throws IllegalStateException if surface to draw has not been initialized or orientation is
     * unknown.
     * @throws IllegalArgumentException if provided value is null.
     */
    var viewCameraRotation
        get() = converter?.viewCameraRotation
        @Throws(IllegalStateException::class, IllegalArgumentException::class)
        set(value) {
            val converter = converter
            checkNotNull(converter)

            converter.viewCameraRotation = value
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
     * @throws IllegalStateException if surface to draw has not been initialized.
     * @throws CameraException if there are numerical instabilities in provided camera.
     */
    @Throws(IllegalStateException::class, CameraException::class)
    fun setValues(nearPlane: Float, farPlane: Float, camera: PinholeCamera) {
        val converter = converter
        checkNotNull(converter)

        val width = converter.width
        val height = converter.height

        converter.setValues(nearPlane, farPlane, width, height, camera)
    }

    /**
     * Destroys OpenGL context only if it has not already been destroyed.
     */
    fun destroy() {
        if (!rendererDestroyed) {
            tearDownGL()
        }
        rendererDestroyed = true
    }

    /**
     * Called when surface where drawing occurs has been created in order to initialize OpenGL.
     *
     * @param gl OpenGL context.
     * @param config OpenGL configuration.
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        if (gl == null) {
            return
        }

        updateClearColor()
        setupGL()
    }

    /**
     * Called when size of surface where drawing occurs changes.
     *
     * @param gl OpenGL context.
     * @param width new surface width expressed in pixels.
     * @param height new surface height expressed in pixels.
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val converter = CameraConverter(width, height)
        converter.isModelViewProjectionMatrixComputationEnabled = true
        this.converter = converter
        setupCube()

        onSurfaceChangedListener?.onSurfaceChanged(width, height)
    }

    /**
     * Called when a frame needs to be drawn into the surface.
     * Sets camera and draws the cube.
     *
     * @param gl OpenGL context.
     */
    override fun onDrawFrame(gl: GL10?) {
        //compute model view matrix
        android.opengl.Matrix.setIdentityM(modelViewMatrix, 0)
        android.opengl.Matrix.translateM(
            modelViewMatrix,
            0,
            -cubePosition.inhomX.toFloat(),
            -cubePosition.inhomY.toFloat(),
            -cubePosition.inhomZ.toFloat()
        )

        //set rotation on model view matrix
        cubeRotation.asInhomogeneousMatrix(cubeRotationMatrix)

        val rotationBuffer = cubeRotationMatrix.buffer
        modelViewMatrix[0] = rotationBuffer[0].toFloat()
        modelViewMatrix[1] = rotationBuffer[1].toFloat()
        modelViewMatrix[2] = rotationBuffer[2].toFloat()

        modelViewMatrix[4] = rotationBuffer[3].toFloat()
        modelViewMatrix[5] = rotationBuffer[4].toFloat()
        modelViewMatrix[6] = rotationBuffer[5].toFloat()

        modelViewMatrix[8] = rotationBuffer[6].toFloat()
        modelViewMatrix[9] = rotationBuffer[7].toFloat()
        modelViewMatrix[10] = rotationBuffer[8].toFloat()

        android.opengl.Matrix.scaleM(modelViewMatrix, 0, cubeSize, cubeSize, cubeSize)

        // compute model view projection matrix
        val cameraModelViewProjectionMatrix = this.cameraModelViewProjectionMatrix ?: return

        android.opengl.Matrix.multiplyMM(
            modelViewProjectionMatrix, 0,
            cameraModelViewProjectionMatrix, 0, modelViewMatrix, 0
        )

        // sets identity for normal matrix
        normalMatrix[0] = 1.0f
        normalMatrix[4] = 1.0f
        normalMatrix[8] = 1.0f
        normalMatrix[1] = 0.0f
        normalMatrix[2] = 0.0f
        normalMatrix[3] = 0.0f
        normalMatrix[5] = 0.0f
        normalMatrix[6] = 0.0f
        normalMatrix[7] = 0.0f

        // set clear color
        updateClearColor()

        // clear
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Render the object with ES2

        // set uniforms for camera and colors
        GLES20.glUseProgram(program)
        GLES20.glUniformMatrix4fv(
            uniforms[UNIFORM_MODEL_VIEW_PROJECTION_MATRIX],
            1,
            false,
            modelViewProjectionMatrix,
            0
        )
        GLES20.glUniformMatrix3fv(uniforms[UNIFORM_NORMAL_MATRIX], 1, false, normalMatrix, 0)
        GLES20.glUniform1i(uniforms[UNIFORM_HAS_NORMALS], hasNormals)
        GLES20.glUniform1i(uniforms[UNIFORM_HAS_COLORS], hasColors)

        // diffuse color is only taken into account if normals are being used
        val diffuseRed = colorComponentAsFloat(diffuseColor.red)
        val diffuseGreen = colorComponentAsFloat(diffuseColor.green)
        val diffuseBlue = colorComponentAsFloat(diffuseColor.blue)
        GLES20.glUniform3f(
            uniforms[UNIFORM_DIFFUSE_COLOR],
            diffuseRed,
            diffuseGreen,
            diffuseBlue
        )
        GLES20.glUniform1f(uniforms[UNIFORM_OVERALL_ALPHA], OVERALL_ALPHA)


        // enable vertex attribute arrays depending on available data
        GLES20.glEnableVertexAttribArray(vertexPositionAttribute)

        if (hasNormals != 0) {
            GLES20.glEnableVertexAttribArray(vertexNormalAttribute)
        }

        if (hasColors == 3) {
            GLES20.glEnableVertexAttribArray(vertexColor3Attribute)
        }

        if (hasColors == 4) {
            GLES20.glEnableVertexAttribArray(vertexColor4Attribute)
        }

        //draw cube
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexPositionsBufferId)
        GLES20.glVertexAttribPointer(vertexPositionAttribute, 3, GLES20.GL_FLOAT, false, 0, 0)

        if (hasNormals != 0) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexNormalsBufferId)
            GLES20.glVertexAttribPointer(vertexNormalAttribute, 3, GLES20.GL_FLOAT, false, 0, 0)
        }

        if (hasColors == 3) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexColorsBufferId)
            GLES20.glVertexAttribPointer(
                vertexColor3Attribute,
                4,
                GLES20.GL_UNSIGNED_BYTE,
                true,
                0,
                0
            )
        }

        if (hasColors == 4) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexColorsBufferId)
            GLES20.glVertexAttribPointer(
                vertexColor4Attribute,
                4,
                GLES20.GL_UNSIGNED_BYTE,
                true,
                0,
                0
            )
        }

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vertexIndicesBufferId)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, cubeIndices.size, GLES20.GL_UNSIGNED_SHORT, 0)
    }

    /**
     * Updates clear color.
     */
    private fun updateClearColor() {
        val clearRed = colorComponentAsFloat(clearColor.red)
        val clearGreen = colorComponentAsFloat(clearColor.green)
        val clearBlue = colorComponentAsFloat(clearColor.blue)
        val clearAlpha = colorComponentAsFloat(clearColor.alpha)
        GLES20.glClearColor(clearRed, clearGreen, clearBlue, clearAlpha)
    }

    /**
     * Prepares OpenGL context.
     * @return true if OpenGL was correctly setup, false otherwise.
     */
    private fun setupGL(): Boolean {
        // load and compile shaders and program
        try {
            if (!loadShaders()) {
                return false
            }

            GLES20.glEnable(GLES20.GL_DEPTH_TEST)
            GLES20.glUseProgram(program)

            // get id's for attributes
            vertexPositionAttribute = GLES20.glGetAttribLocation(program, "aPosition")
            vertexNormalAttribute = GLES20.glGetAttribLocation(program, "aNormal")
            vertexColor3Attribute = GLES20.glGetAttribLocation(program, "aColor3")
            vertexColor4Attribute = GLES20.glGetAttribLocation(program, "aColor4")
            return true
        } catch (_: Throwable) {
            return false
        }
    }

    /**
     * Loads shaders to draw using OpenGL ES 2.0.
     * @return true if shaders where loaded, false otherwise.
     */
    private fun loadShaders(): Boolean {
        val vertShader = compileShader(GLES20.GL_VERTEX_SHADER, R.raw.vertex_shader)
        val fragShader = compileShader(GLES20.GL_FRAGMENT_SHADER, R.raw.fragment_shader)

        // create empty OpenGL program
        program = GLES20.glCreateProgram()
        // add the vertex shader to program
        GLES20.glAttachShader(program, vertShader)
        // add the fragment shader to program
        GLES20.glAttachShader(program, fragShader)

        // Bind attribute locations
        // This needs to be done prior to linking
        GLES20.glBindAttribLocation(program, ATTRIB_VERTEX, "aPosition")
        GLES20.glBindAttribLocation(program, ATTRIB_NORMAL, "aNormal")
        GLES20.glBindAttribLocation(program, ATTRIB_COLOR3, "aColor3")
        GLES20.glBindAttribLocation(program, ATTRIB_COLOR4, "aColor4")

        // Link program
        if (!linkProgram()) {
            // program linking failed, so we delete shaders and program
            if (vertShader != 0) {
                GLES20.glDeleteShader(vertShader)
            }
            if (fragShader != 0) {
                GLES20.glDeleteShader(fragShader)
            }
            if (program != 0) {
                GLES20.glDeleteProgram(program)
                program = 0
            }

            return false
        }

        // Get uniform locations
        uniforms[UNIFORM_MODEL_VIEW_PROJECTION_MATRIX] =
            GLES20.glGetUniformLocation(program, "uModelViewProjectionMatrix")
        uniforms[UNIFORM_NORMAL_MATRIX] = GLES20.glGetUniformLocation(program, "uNormalMatrix")
        uniforms[UNIFORM_HAS_NORMALS] = GLES20.glGetUniformLocation(program, "uHasNormals")
        uniforms[UNIFORM_HAS_COLORS] = GLES20.glGetUniformLocation(program, "uHasColors")
        uniforms[UNIFORM_DIFFUSE_COLOR] = GLES20.glGetUniformLocation(program, "uDiffuseColor")
        uniforms[UNIFORM_OVERALL_ALPHA] = GLES20.glGetUniformLocation(program, "uOverallAlpha")

        return true
    }

    /**
     * Compiles shader contained into provided resource.
     *
     * @param type [GLES20.GL_VERTEX_SHADER] or [GLES20.GL_FRAGMENT_SHADER].
     * @param resource id of a resource containing the shader code.
     * @return id of compiled shader.
     */
    private fun compileShader(type: Int, resource: Int): Int {
        val stream = context.resources.openRawResource(resource)
        val builder = StringBuilder()
        stream.use {
            val reader = BufferedReader(InputStreamReader(stream))
            while (true) {
                val line = reader.readLine() ?: break
                builder.append(line)
                builder.append(System.lineSeparator())
            }
        }

        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, builder.toString())
        GLES20.glCompileShader(shader)
        val status = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, IntBuffer.wrap(status))
        if (status[0] == 0) {
            // shader compilation failed, so we remove the instantiated shader
            GLES20.glDeleteShader(shader)
            throw Exception()
        }

        return shader
    }

    /**
     * Links compiled shaders into a program.
     *
     * @return true if program linked successfully, false otherwise.
     */
    private fun linkProgram(): Boolean {
        val status = IntArray(1)
        GLES20.glLinkProgram(program)

        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, IntBuffer.wrap(status))
        return status[0] != 0
    }

    /**
     * Prepares buffers of data to draw a cube.
     */
    private fun setupCube() {
        // Vertex positions

        // generate and bind buffer
        val vertexPositionsBuffer = IntArray(1)
        GLES20.glGenBuffers(1, vertexPositionsBuffer, 0)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexPositionsBuffer[0])

        // copy id
        vertexPositionsBufferId = vertexPositionsBuffer[0]

        // copy data to GPU buffer
        val verticesCoordinatesDataSizeInBytes =
            cubeVerticesCoordinatesData.size * Float.SIZE_BYTES
        var buffer = ByteBuffer.allocateDirect(verticesCoordinatesDataSizeInBytes)
        buffer.order(ByteOrder.nativeOrder())
        var floatBuffer = buffer.asFloatBuffer()
        floatBuffer.put(cubeVerticesCoordinatesData)
        GLES20.glBufferData(
            GLES20.GL_ARRAY_BUFFER,
            verticesCoordinatesDataSizeInBytes,
            buffer,
            GLES20.GL_STATIC_DRAW
        )

        // normals
        if (hasNormals != 0) {
            // generate and bind buffer
            val vertexNormalsBuffer = IntArray(1)
            GLES20.glGenBuffers(1, vertexNormalsBuffer, 0)
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexNormalsBuffer[0])

            // copy id
            vertexNormalsBufferId = vertexNormalsBuffer[0]

            // copy data to GPU buffer
            val normalsData = FloatArray(3) // TODO: here normals should be available!
            val normalsDataSizeInBytes = normalsData.size * Float.SIZE_BYTES
            buffer = ByteBuffer.allocateDirect(normalsDataSizeInBytes)
            buffer.order(ByteOrder.nativeOrder())
            floatBuffer = buffer.asFloatBuffer()
            floatBuffer.put(normalsData)
            GLES20.glBufferData(
                GLES20.GL_ARRAY_BUFFER,
                normalsDataSizeInBytes,
                buffer,
                GLES20.GL_STATIC_DRAW
            )
        }

        // colors
        if (hasColors != 0) {
            // generate and bind buffer
            val vertexColorsBuffer = IntArray(1)
            GLES20.glGenBuffers(1, vertexColorsBuffer, 0)
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexColorsBuffer[0])

            // copy id
            vertexColorsBufferId = vertexColorsBuffer[0]

            // copy data to GPU buffer

            // color data is stored as unsigned bytes (0 to 255) but java bytes are signed (-126 to
            // 127).
            val colorDataSizeInBytes = cubeColors.size * 4
            val colorDataBytes = ByteArray(colorDataSizeInBytes)
            var j = 0
            for (element in cubeColors) {
                colorDataBytes[j++] = colorComponentAsByte(element.red)
                colorDataBytes[j++] = colorComponentAsByte(element.green)
                colorDataBytes[j++] = colorComponentAsByte(element.blue)
                colorDataBytes[j++] = colorComponentAsByte(element.alpha)
            }

            buffer = ByteBuffer.allocateDirect(colorDataSizeInBytes)
            buffer.order(ByteOrder.nativeOrder())
            buffer.put(colorDataBytes)
            buffer.rewind() // go back to start of buffer
            GLES20.glBufferData(
                GLES20.GL_ARRAY_BUFFER,
                colorDataSizeInBytes,
                buffer,
                GLES20.GL_STATIC_DRAW
            )

            // vertex indices

            // generate and bind vertex indices
            val vertexIndicesBuffer = IntArray(1)
            GLES20.glGenBuffers(1, vertexIndicesBuffer, 0)
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vertexIndicesBuffer[0])

            // copy id
            vertexIndicesBufferId = vertexIndicesBuffer[0]

            // copy data to GPU buffer

            // indices data is stored as unsigned shorts (0 to 65535) but java shorts are signed
            // we convert indices from int to unsigned shorts and store them in ByteBuffer
            val indicesDataSizeInBytes = cubeIndices.size * Short.SIZE_BYTES
            buffer = ByteBuffer.allocateDirect(indicesDataSizeInBytes)
            buffer.order(ByteOrder.nativeOrder())
            val shortBuffer = buffer.asShortBuffer()
            for (cubeIndex in cubeIndices) {
                val s = (cubeIndex and 0x0000FFFF).toShort()
                shortBuffer.put(s)
            }
            GLES20.glBufferData(
                GLES20.GL_ELEMENT_ARRAY_BUFFER,
                indicesDataSizeInBytes,
                buffer,
                GLES20.GL_STATIC_DRAW
            )
        }
    }

    /**
     * Destroys OpenGL context.
     */
    private fun tearDownGL() {
        if (program != 0) {
            GLES20.glDeleteProgram(program)
            program = 0
        }
    }

    companion object {
        /**
         * Cube size.
         */
        const val DEFAULT_CUBE_SIZE = 1.0f

        /**
         * Default cube distance.
         */
        const val DEFAULT_CUBE_DISTANCE = 2.0

        /**
         * Default near plane value defining visible frustum.
         */
        const val DEFAULT_NEAR_PLANE = 0.1f

        /**
         * Default far plane value defining visible frustum.
         */
        const val DEFAULT_FAR_PLANE = 1000.0f

        /**
         * Default clear color.
         */
        const val DEFAULT_CLEAR_COLOR = Color.TRANSPARENT

        /**
         * Indicates that this renderer does not take into account normals.
         */
        private const val DEFAULT_HAS_NORMALS = 0

        /**
         * Indicates that this renderer draws colors in RGB format.
         */
        private const val DEFAULT_HAS_COLORS = 3

        /**
         * Contains overall scene alpha used by this renderer.
         */
        private const val OVERALL_ALPHA = 1.0f

        /**
         * Length of arrays containing 4x4 homogeneous matrices to be used by OpenGL.
         */
        private const val HOM_MATRIX_LENGTH = 16

        /**
         * Diffuse color required by shaders.
         */
        private val DEFAULT_DIFFUSE_COLOR = Color.rgb(127, 127, 127)

        /**
         * Color for 1st vertex of cube.
         */
        private val COLOR_1 = Color.rgb(0, 255, 0)

        /**
         * Color for 2nd vertex of cube.
         */
        private val COLOR_2 = Color.rgb(255, 127, 0)

        /**
         * Color for 3rd vertex of cube.
         */
        private val COLOR_3 = Color.rgb(255, 0, 0)

        /**
         * Color for 4th vertex of cube.
         */
        private val COLOR_4 = Color.rgb(255, 127, 0)

        /**
         * Color for 5th vertex of cube.
         */
        private val COLOR_5 = Color.rgb(255, 255, 0)

        /**
         * Color for 6th vertex of cube.
         */
        private val COLOR_6 = Color.rgb(0, 0, 255)

        /**
         * Color for 7th vertex of cube.
         */
        private val COLOR_7 = Color.rgb(0, 127, 255)

        /**
         * Color for 8th vertex of cube.
         */
        private val COLOR_8 = Color.rgb(255, 0, 255)

        /**
         * Position where model view projection matrix id is stored in the array of uniform
         * variables used by shaders.
         */
        private const val UNIFORM_MODEL_VIEW_PROJECTION_MATRIX = 0

        /**
         * Position where normal matrix id is stored in the array of uniform variables used by
         * shaders. Normal matrix is only used when drawn object has normals to ensure proper
         * lighting.
         */
        private const val UNIFORM_NORMAL_MATRIX = 1

        /**
         * Position where id of flag indicating whether object has normals or not is stored in the
         * array of uniform variables used by shaders.
         */
        private const val UNIFORM_HAS_NORMALS = 2

        /**
         * Position where id of flag indicating whether object has colors or not is stored in the
         * array of uniform variables used by shaders.
         */
        private const val UNIFORM_HAS_COLORS = 3

        /**
         * Position where id of variable containing uniform color is stored in the array of uniform
         * variables used by shaders.
         */
        private const val UNIFORM_DIFFUSE_COLOR = 4

        /**
         * Position where id of variable containing overall scene alpha is stored in the array of
         * uniform variables used by shaders.
         */
        private const val UNIFORM_OVERALL_ALPHA = 5

        /**
         * Number of uniform variables used by shaders.
         */
        private const val NUM_UNIFORMS = 6

        /**
         * ID of attribute used by shader containing vertices coordinates.
         */
        private const val ATTRIB_VERTEX = 0

        /**
         * ID of attribute used by shader containing normals.
         */
        private const val ATTRIB_NORMAL = 1

        /**
         * ID of attribute used by shader containing colors in RGB format.
         */
        private const val ATTRIB_COLOR3 = 2

        /**
         * ID of attribute used by shader containing colors in RGBA format.
         */
        private const val ATTRIB_COLOR4 = 3

        /**
         * Converts an integer color component value ranging from 0 to 255 to a value
         * ranging from 0.0f to 1.0f
         */
        private fun colorComponentAsFloat(colorComponent: Int): Float {
            return colorComponent / 255.0f
        }

        /**
         * Converts an integer color component value ranging from 0 to 255 to a java byte
         * value ranging from -126 to 127
         */
        private fun colorComponentAsByte(colorComponent: Int): Byte {
            return (colorComponent and 0x000000FF).toByte()
        }
    }
}