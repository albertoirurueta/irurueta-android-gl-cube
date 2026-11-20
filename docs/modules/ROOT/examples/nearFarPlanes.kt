// Vertices nearer than 0.1 or further than 100 units from the camera are not drawn
cubeView.setNearFarPlanes(0.1f, 100.0f)

// Or set planes and camera at once, requesting a single frame
cubeView.setValues(0.1f, 100.0f, camera)
