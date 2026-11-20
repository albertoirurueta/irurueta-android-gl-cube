import com.irurueta.geometry.InhomogeneousPoint3D
import com.irurueta.geometry.PinholeCameraIntrinsicParameters
import com.irurueta.geometry.Quaternion

// Intrinsic parameters: focal lengths, principal point and skewness (in pixels)
cubeView.cameraIntrinsicParameters = PinholeCameraIntrinsicParameters(
    1500.0, 1500.0, // horizontal and vertical focal length
    0.0, 0.0,       // horizontal and vertical principal point
    0.0             // skewness
)

// Move the camera 0.5 units to the left of the origin
cubeView.cameraCenter = InhomogeneousPoint3D(-0.5, 0.0, 0.0)

// Rotate the camera 10 degrees around its vertical axis
cubeView.cameraRotation = Quaternion(0.0, Math.toRadians(10.0), 0.0)
