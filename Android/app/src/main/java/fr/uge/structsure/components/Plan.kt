package fr.uge.structsure.components

import android.content.res.Resources.getSystem
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.uge.structsure.startScan.presentation.components.SensorState
import kotlin.math.max

private val Int.toPx: Int get() = (this * getSystem().displayMetrics.density).toInt()

data class Point(val x: Int, val y: Int)

@Composable
fun Plan(@DrawableRes image: Int) {
    val points = mutableListOf(Point(0, 0), Point(100, 100))
    val imgSize = painterResource(image).intrinsicSize
    val ratio = (imgSize.width / imgSize.height).coerceIn(1.5f, 1.75f)
    println("${(imgSize.width / imgSize.height)}")
    val transformFactor = (imgSize.width / ratio) / imgSize.height
    val offsetFactor = computeOffsetFactor(imgSize, ratio)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(0.dp))
            .fillMaxWidth()
            .aspectRatio(ratio),
        Alignment.Center
    ) {
        // Define mutable state variables to keep track of the scale and offset.
        var scale by remember { mutableFloatStateOf(max(1f, transformFactor)) }
        var offset by remember { mutableStateOf(Offset(0f, 0f)) }

        Image(
            painterResource(image),
            "Plan",
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) { /* Zoom and pane the image*/
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        val adjustment = centroid * (1 - zoom)
                        offset = (offset + pan) * zoom + adjustment
                        scale = (scale * zoom).coerceIn(transformFactor.coerceAtLeast(1f), 3 * transformFactor.coerceAtLeast(1f))
                        println("Factor $transformFactor")

                        val width = ((size.width * scale - size.width)/2 - (size.width * offsetFactor.x * scale)).coerceAtLeast(0f)
                        val height = ((size.height * scale - size.height)/2f - (size.height * offsetFactor.y * scale)).coerceAtLeast(0f)
                        println("${offset.x} - ${offset.y}")
                        offset = Offset(
                            offset.x.coerceIn(-width, width),
                            offset.y.coerceIn(-height, height)
                        )
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { /* Double click to zoom in or out */
                            if (scale >= 1.1f) {
                                scale = 1f
                                offset = Offset(0f, 0f)
                            } else scale = 3f
                        },
                        onTap = { pos ->
                            // TODO on click
                        }
                    )
                }
                .graphicsLayer(
                    scaleX = scale, scaleY = scale,
                    translationX = offset.x, translationY = offset.y
                )
        )

        Canvas(Modifier.fillMaxSize()) {
            val pointTransform = if (imgSize.width / imgSize.height > 1.57f) size.width/imgSize.width else size.height/imgSize.height
            points.forEach {
                val panned = zoomAndPan(offset, scale, size,
                    it.x * pointTransform + size.width * offsetFactor.x,
                    it.y * pointTransform + size.height * offsetFactor.y
                )
                point(size, panned.x, panned.y, SensorState.NOK)
            }
        }
    }
}

/**
 * Draw a point representing a sensor on a plan.
 * @param x the x coordinate of the center of the point
 * @param y the y coordinate of the center of the point
 * @param state the state of the sensor to show its color
 */
private fun DrawScope.point(size: Size, x: Float, y: Float, state: SensorState) {
    val visibleX = x + 30 >= 0 && x - 30 <= size.width
    val visibleY = y + 30 >= 0 && y - 30 <= size.height
    if (!visibleX || !visibleY) return // out of canvas
    drawCircle(
        color = state.color,
        radius = 20f,
        center = Offset(x + 0f,  y + 0f),
    )
    drawCircle(
        color = state.color.copy(alpha = 0.25F),
        radius = 30f,
        center = Offset(x + 0f, y + 0f),
    )
}

/**
 * Transforms the coordinates of the given point to apply pan and zoom
 * to it and calculates it new position on the screen.
 * @param pan values of the horizontal/vertical displacement to apply
 * @param zoom level of zoom to apply between 1 and 3
 * @param size the size of the canvas to draw on
 * @param x the initial x coordinate in the plan without zoom
 * @param y the initial y coordinate in the plan iwz
 */
private fun zoomAndPan(pan: Offset, zoom: Float, size: Size, x: Float, y: Float): Offset {
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    return Offset((x - centerX) * zoom + centerX + pan.x, (y - centerY) * zoom + centerY + pan.y)
}

private fun computeOffsetFactor(img: Size, ratio: Float): Offset {
    var offsetTop = 0f
    var offsetLeft = 0f
    if (img.width / img.height > 1.57f) {
        offsetTop = ((img.width / ratio) - img.height) / 2f / (img.width / ratio)
        offsetLeft = 0f
    } else {
        offsetTop = 0f
        offsetLeft = ((img.height * ratio) - img.width) / 2f / (img.height * ratio)
    }
    return Offset(offsetLeft, offsetTop)
}