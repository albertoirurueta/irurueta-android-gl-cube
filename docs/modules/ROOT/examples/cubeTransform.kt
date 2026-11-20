import com.irurueta.geometry.InhomogeneousPoint3D
import com.irurueta.geometry.Quaternion

// Size of the cube edges (default is CubeRenderer.DEFAULT_CUBE_SIZE = 1.0)
cubeView.cubeSize = 0.5f

// Cube center. By default it is placed 2 units in front of the camera
// (0.0, 0.0, CubeRenderer.DEFAULT_CUBE_DISTANCE)
cubeView.cubePosition = InhomogeneousPoint3D(0.0, 0.0, 3.0)

// Cube rotation: any Rotation3D (Quaternion, MatrixRotation3D, AxisRotation3D...)
val rotation = Quaternion()
rotation.setFromEulerAngles(Math.toRadians(30.0), Math.toRadians(45.0), 0.0)
cubeView.cubeRotation = rotation
