import android.animation.FloatArrayEvaluator
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import com.irurueta.geometry.Quaternion

private val rotation = Quaternion()

private val rotationAnimator = ValueAnimator.ofObject(
    FloatArrayEvaluator(),
    floatArrayOf(0.0f, 0.0f, 0.0f),
    floatArrayOf(6.0f * Math.PI.toFloat(), 4.0f * Math.PI.toFloat(), 2.0f * Math.PI.toFloat())
).apply {
    duration = 30000L
    repeatMode = ValueAnimator.RESTART
    repeatCount = ValueAnimator.INFINITE
    interpolator = LinearInterpolator()
    addUpdateListener { animator ->
        val angles = animator.animatedValue as FloatArray
        rotation.setFromEulerAngles(
            angles[0].toDouble(), angles[1].toDouble(), angles[2].toDouble()
        )
        // Each assignment requests a new frame
        cubeView.cubeRotation = rotation
    }
}

override fun onResume() {
    super.onResume()
    cubeView.onResume()
    rotationAnimator.start()
}

override fun onPause() {
    super.onPause()
    cubeView.onPause()
    rotationAnimator.pause()
}
