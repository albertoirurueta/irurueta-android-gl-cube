# irurueta-android-gl-cube

A view to display a 3D cube with transparent background.

[![Develop](https://github.com/albertoirurueta/irurueta-android-gl-cube/actions/workflows/develop.yml/badge.svg)](https://github.com/albertoirurueta/irurueta-android-gl-cube/actions/workflows/develop.yml)
[![Release](https://github.com/albertoirurueta/irurueta-android-gl-cube/actions/workflows/main.yml/badge.svg)](https://github.com/albertoirurueta/irurueta-android-gl-cube/actions/workflows/main.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.irurueta/irurueta-android-gl-cube)](https://central.sonatype.com/artifact/com.irurueta/irurueta-android-gl-cube)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-gl-cube&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-gl-cube)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-gl-cube&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-gl-cube)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-gl-cube&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-gl-cube)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-gl-cube&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-gl-cube)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-gl-cube&metric=bugs)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-gl-cube)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-gl-cube&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-gl-cube)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-gl-cube&metric=coverage)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-gl-cube)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-gl-cube&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-gl-cube)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-gl-cube&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-gl-cube)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-gl-cube&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-gl-cube)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-android-gl-cube&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-gl-cube)

![Demo](docs/video.gif)

## Overview

This library contains `CubeTextureView`, an example of OpenGL ES 2.0 with support for background transparency that
uses [irurueta-android-glutils](https://github.com/albertoirurueta/irurueta-android-glutils) to compute camera
matrices. Because `CubeTextureView` is a `TextureView` based OpenGL view, it behaves like any other view in the
hierarchy: it can be placed on top of or below other views, be translucent, and be moved, scaled, rotated or
animated. In the demo above, the text is a plain `TextView` placed behind the cube.

## Project Status

| | |
| --- | --- |
| Language | Kotlin (Java 17 bytecode) |
| Build tool | Gradle (Kotlin DSL) |
| Current development version | 1.2.0-SNAPSHOT |
| Latest release | 1.1.8 |
| Platform | Android |
| Min / compile SDK | minSdk 26, compileSdk 36 |
| Kotlin / AGP | Kotlin 2.3.10, AGP 9.0.1 |
| License | Apache License 2.0 |
| CI | GitHub Actions: `develop` builds (Develop) and `main` releases (Release, publishing to Maven Central); both deploy docs to GitHub Pages |
| Quality | SonarCloud, JaCoCo coverage, Android Lint, Dokka |

## Documentation

- [Documentation site](https://albertoirurueta.github.io/irurueta-android-gl-cube/)
- [API documentation (Dokka)](https://albertoirurueta.github.io/irurueta-android-gl-cube/api/index.html)
- [SonarCloud dashboard](https://sonarcloud.io/summary/new_code?id=albertoirurueta_irurueta-android-gl-cube)
- [Changelog](CHANGELOG.md)

## Installation

The library is published on Maven Central. Add the dependency to your module's build file.

Gradle (Groovy DSL):

```groovy
implementation 'com.irurueta:irurueta-android-gl-cube:1.1.8'
```

Gradle (Kotlin DSL):

```kotlin
implementation("com.irurueta:irurueta-android-gl-cube:1.1.8")
```

## Usage

Add a `CubeTextureView` to your layout. Its background is transparent, so any view behind it stays visible:

```xml
<com.irurueta.android.gl.cube.CubeTextureView
    android:id="@+id/cube"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

Camera related properties can only be set once the OpenGL surface exists, so set them from
`onSurfaceChangedListener`. The camera is a `PinholeCamera` whose intrinsic parameters are computed from an OpenGL
projection matrix, and the view must be notified of the activity lifecycle:

```kotlin
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.irurueta.android.gl.cube.CubeTextureView
import com.irurueta.android.glutils.OpenGlToCameraHelper
import com.irurueta.geometry.PinholeCamera
import com.irurueta.geometry.Point3D
import com.irurueta.geometry.Rotation3D

class MainActivity : AppCompatActivity() {

    private lateinit var cubeView: CubeTextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cubeView = findViewById(R.id.cube)
        cubeView.onSurfaceChangedListener = object : CubeTextureView.OnSurfaceChangedListener {
            override fun onSurfaceChanged(width: Int, height: Int) {
                cubeView.camera = createCamera(width, height)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cubeView.onResume()
    }

    override fun onPause() {
        super.onPause()
        cubeView.onPause()
    }

    private fun createCamera(width: Int, height: Int): PinholeCamera {
        // OpenGL projection matrix, defined column-wise as in android.opengl.Matrix
        val projectionMatrix = floatArrayOf(
            2.8693345f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.5806589f, 0.0f, 0.0f,
            -0.004545755f, 0.009158132f, -1.002002f, -1.0f,
            0.0f, 0.0f, -0.2002002f, 0.0f
        )
        val intrinsics = OpenGlToCameraHelper.computePinholeCameraIntrinsicsAndReturnNew(
            projectionMatrix, width, height
        )
        // Camera at the origin with no rotation, looking towards the cube
        return PinholeCamera(intrinsics, Rotation3D.create(), Point3D.create())
    }
}
```

See the [documentation site](https://albertoirurueta.github.io/irurueta-android-gl-cube/) for cube size, position,
rotation, colors, near/far planes, animation and Jetpack Compose usage, and the `app` module for a complete sample.

## Branching

This repository follows gitflow:

- `develop` is the integration branch: feature branches are merged into it, and every push runs the Develop
  workflow (build, tests, lint, SonarCloud and docs deployed to GitHub Pages).
- `main` holds releases: release branches are merged into it, and the Release workflow publishes the library to
  Maven Central and the documentation to GitHub Pages.

## License

Licensed under the [Apache License 2.0](LICENSE).
