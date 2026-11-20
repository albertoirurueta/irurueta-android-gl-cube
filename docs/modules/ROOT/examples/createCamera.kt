import com.irurueta.android.glutils.OpenGlToCameraHelper
import com.irurueta.geometry.PinholeCamera
import com.irurueta.geometry.Point3D
import com.irurueta.geometry.Rotation3D

fun createCamera(width: Int, height: Int): PinholeCamera {
    // OpenGL projection matrix, defined column-wise as in android.opengl.Matrix.
    // These values come from an ARCore session on a Pixel 2 device.
    val projectionMatrix = floatArrayOf(
        2.8693345f, 0.0f, 0.0f, 0.0f,
        0.0f, 1.5806589f, 0.0f, 0.0f,
        -0.004545755f, 0.009158132f, -1.002002f, -1.0f,
        0.0f, 0.0f, -0.2002002f, 0.0f
    )

    // Convert the projection matrix into pinhole camera intrinsic parameters
    val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
        projectionMatrix,
        width,
        height
    )

    // Camera located at the origin with no rotation, looking towards the cube
    return PinholeCamera(intrinsics, Rotation3D.create(), Point3D.create())
}
