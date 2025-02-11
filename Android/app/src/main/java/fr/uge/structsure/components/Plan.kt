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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.toSize
import fr.uge.structsure.startScan.presentation.components.SensorState
import kotlin.math.max
import kotlin.math.sqrt

private val Int.toPx: Int get() = (this * getSystem().displayMetrics.density).toInt()

data class Point(val x: Int, val y: Int, val state: SensorState)

@Composable
fun Plan(@DrawableRes image: Int) {
    val points = remember { mutableStateListOf(
        Point(0, 0, SensorState.OK),
        Point(100, 100, SensorState.OK)
    ) }
    val imgSize = painterResource(image).intrinsicSize
    val state = remember { mutableStateOf(SensorState.NOK) }

    val ratio = remember(imgSize) { (imgSize.width / imgSize.height).coerceIn(1.5f, 1.75f) }
    val transformFactor = remember(imgSize, ratio) { (imgSize.width / ratio) / imgSize.height }
    val offsetFactor = computeOffsetFactor(imgSize, ratio)

    var scale by remember { mutableFloatStateOf(max(1f, transformFactor)) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(0.dp))
            .fillMaxWidth()
            .aspectRatio(ratio),
        Alignment.Center
    ) {
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

                        val width = ((size.width * scale - size.width)/2 - (size.width * offsetFactor.x * scale)).coerceAtLeast(0f)
                        val height = ((size.height * scale - size.height)/2f - (size.height * offsetFactor.y * scale)).coerceAtLeast(0f)
                        offset = Offset(
                            offset.x.coerceIn(-width, width),
                            offset.y.coerceIn(-height, height)
                        )
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { pos ->
                            // TODO on click
                            var imgPos = canvasToImg(offset, scale, size.toSize(), imgSize, offsetFactor, pos.x, pos.y)
                            var imgPoint = Point(imgPos.x.toInt(), imgPos.y.toInt(), SensorState.NOK)
                            // TODO range may be wrong because not in px
                            for (i in 0 ..< points.size) {
                                if (imgPoint.inRange(points[i], 30)) {
                                    points[i] = Point(points[i].x, points[i].y, if (points[i].state == SensorState.NOK) SensorState.OK else SensorState.NOK)
                                    println("Point $i clicked")
                                }
                            }
                        }
                    )
                }
                .graphicsLayer(
                    scaleX = scale, scaleY = scale,
                    translationX = offset.x, translationY = offset.y
                )
        )

        Canvas(Modifier.fillMaxSize()) {
            points.forEach {
                val panned = imgToCanvas(offset, scale, size, imgSize, offsetFactor, it.x, it.y)
                point(size, panned.x, panned.y, it.state)
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
 * @param imgSize the original size of the image
 * @param offsetFactor offset to center the image if needed
 * @param x the initial x coordinate in the image
 * @param y the initial y coordinate in the image
 */
private fun imgToCanvas(pan: Offset, zoom: Float, size: Size, imgSize: Size, offsetFactor: Offset, x: Int, y: Int): Offset {
    val pointTransform = if (imgSize.width / imgSize.height > 1.57f) size.width/imgSize.width
    else size.height/imgSize.height

    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val posX = x * pointTransform + size.width * offsetFactor.x
    val posY = y * pointTransform + size.height * offsetFactor.y
    return Offset(
        (posX - centerX) * zoom + centerX + pan.x,
        (posY - centerY) * zoom + centerY + pan.y
    )
}

/**
 * Transforms the coordinates of the given point to remove pan and zoom
 * from it and calculates it relative position in the image.
 * @param pan values of the horizontal/vertical displacement to apply
 * @param zoom level of zoom to apply between 1 and 3
 * @param size the size of the canvas to draw on
 * @param imgSize the original size of the image
 * @param offsetFactor offset to center the image if needed
 * @param x the initial x coordinate in the canvas
 * @param y the initial y coordinate in the canvas
 */
private fun canvasToImg(pan: Offset, zoom: Float, size: Size, imgSize: Size, offsetFactor: Offset, x: Float, y: Float): Offset {
    val pointTransform = if (imgSize.width / imgSize.height > 1.57f) size.width/imgSize.width
    else size.height/imgSize.height

    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val posX = (x - centerX - pan.x) / zoom + centerX
    val posY = (y - centerY - pan.y) / zoom + centerY
    return Offset(
        ((posX - size.width * offsetFactor.x) / pointTransform).toInt().toFloat(),
        ((posY - size.height * offsetFactor.y) / pointTransform).toInt().toFloat()
    )
}

/**
 * Checks if the given point is in range of this point or too far
 * @param point the point to try to reach
 * @param range the maximum allowed distance between points
 * @return true if in range, false otherwise
 */
private fun Point.inRange(point: Point, range: Int): Boolean {
    val dx = point.x - x
    val dy = point.y - y

    // Square range check (much faster)
    if (dx * dx + dy * dy > range * range) return false

    // Pythagore for exact values
    return sqrt((dx * dx + dy * dy).toDouble()) <= range
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
