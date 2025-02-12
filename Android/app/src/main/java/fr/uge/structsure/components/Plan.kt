package fr.uge.structsure.components

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
import androidx.compose.ui.unit.toSize
import fr.uge.structsure.startScan.presentation.components.SensorState
import kotlin.math.sqrt

data class Point(val x: Int, val y: Int, val state: SensorState)

@Composable
fun Plan(@DrawableRes image: Int, points: MutableList<Point>) {
    val painter = painterResource(image)
    val factor = remember(image) { Factor(painter.intrinsicSize) }

    var scale by remember { mutableFloatStateOf(factor.transform) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(0.dp))
            .fillMaxWidth()
            .aspectRatio(factor.canvasRatio),
        Alignment.Center
    ) {
        Image(
            painter,
            "Plan",
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) { /* Zoom and pane the image*/
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        val adjustment = centroid * (1 - zoom)
                        offset = (offset + pan) * zoom + adjustment
                        scale = (scale * zoom).coerceIn(factor.transform, 3 * factor.transform)

                        val width = ((size.width * scale - size.width)/2 - (size.width * factor.offset.x * scale)).coerceAtLeast(0f)
                        val height = ((size.height * scale - size.height)/2f - (size.height * factor.offset.y * scale)).coerceAtLeast(0f)
                        offset = Offset(
                            offset.x.coerceIn(-width, width),
                            offset.y.coerceIn(-height, height)
                        )
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { pos -> onTap(factor, size.toSize(), scale, offset, pos, points) },
                        onLongPress = { pos -> onTap(factor, size.toSize(), scale, offset, pos, points, true) }
                    )
                }
                .graphicsLayer(
                    scaleX = scale, scaleY = scale,
                    translationX = offset.x, translationY = offset.y
                )
        )

        Canvas(Modifier.fillMaxSize()) {
            val transformPoint = factor.transformPoint(size)
            points.forEach {
                val panned = imgToCanvas(offset, scale, size, transformPoint, factor.offset, it.x, it.y)
                point(size, panned.x, panned.y, it.state)
            }
        }
    }
}

/**
 * Process a tap on the canvas to determine if a point got touched or
 * not and call the adapted function.
 * @param factor transformation toolkit to calculate the position in
 *     the image
 * @param size the size of the canvas
 * @param scale the level of zoom currently applied to the image
 * @param offset the level of pan currently applied to the image
 * @param pos the position of the click in the canvas
 * @param points all the points contained in the plan
 * @param long true if the press was a long press, false otherwise
 */
private fun onTap(factor: Factor, size: Size, scale: Float, offset: Offset, pos: Offset, points: MutableList<Point>, long: Boolean = false) {
    val range = (30 / factor.transform / scale).toInt()
    val imgPos = canvasToImg(offset, scale, size, factor.transformPoint(size), factor.offset, pos.x, pos.y)
    val imgPoint = Point(imgPos.x.toInt(), imgPos.y.toInt(), SensorState.NOK)
    for (i in 0 ..< points.size) {
        if (imgPoint.inRange(points[i], range)) {
            if (long) onPointLongPress(points, i) else onPointTap(points, i)
            return
        }
    }
    if (!long) onVoidTap(points, imgPos.x.toInt(), imgPos.y.toInt())
}

/**
 * Action to run when one point is pressed.
 * @param points the list of points that contains the pressed point
 * @param x coordinate of the click in the image
 * @param y coordinate of the click in the image
 */
private fun onVoidTap(points: MutableList<Point>, x: Int, y: Int) {
    points.add(Point(x, y, SensorState.DEFECTIVE))
}

/**
 * Action to run when one point is pressed.
 * @param points the list of points that contains the pressed point
 * @param i the index of the point in the list
 */
private fun onPointTap(points: MutableList<Point>, i: Int) {
    points[i] = Point(points[i].x, points[i].y, if (points[i].state == SensorState.NOK) SensorState.OK else SensorState.NOK)
}

/**
 * Action to run when one point is pressed for a long time
 * @param points the list of points that contains the pressed point
 * @param i the index of the point in the list
 */
private fun onPointLongPress(points: MutableList<Point>, i: Int) {
    points.removeAt(i)
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
 * @param transformPoint factor to correct image scale
 * @param offsetFactor offset to center the image if needed
 * @param x the initial x coordinate in the image
 * @param y the initial y coordinate in the image
 */
private fun imgToCanvas(pan: Offset, zoom: Float, size: Size, transformPoint: Float, offsetFactor: Offset, x: Int, y: Int): Offset {
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val posX = x * transformPoint + size.width * offsetFactor.x
    val posY = y * transformPoint + size.height * offsetFactor.y
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
 * @param transformPoint factor to correct image scale
 * @param offsetFactor offset to center the image if needed
 * @param x the initial x coordinate in the canvas
 * @param y the initial y coordinate in the canvas
 */
private fun canvasToImg(pan: Offset, zoom: Float, size: Size, transformPoint: Float, offsetFactor: Offset, x: Float, y: Float): Offset {
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val posX = (x - centerX - pan.x) / zoom + centerX
    val posY = (y - centerY - pan.y) / zoom + centerY
    return Offset(
        ((posX - size.width * offsetFactor.x) / transformPoint).toInt().toFloat(),
        ((posY - size.height * offsetFactor.y) / transformPoint).toInt().toFloat()
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

/**
 * Object containing all the values that enable to switch from original
 * image coordinates to rescaled to panned/zoomed coordinates.
 */
private class Factor(val image: Size) {

    /** Ratio of the canvas calculated on the image original ratio */
    val canvasRatio = (image.width / image.height).coerceIn(1.5f, 1.75f)

    /** Factor to go from the original image to the re-scaled image */
    val transform = ((image.width / canvasRatio) / image.height).coerceAtLeast(1f)

    /** Margin top or left to center the image in the canvas */
    val offset: Offset

    init {
        /* Compute offset */
        var offsetTop = 0f
        var offsetLeft = 0f
        if (image.width / image.height > 1.57f) {
            offsetTop = ((image.width / canvasRatio) - image.height) / 2f / (image.width / canvasRatio)
        } else {
            offsetLeft = ((image.height * canvasRatio) - image.width) / 2f / (image.height * canvasRatio)
        }
        offset = Offset(offsetLeft, offsetTop)
    }

    /**
     * Factor that enable to adapt a point position to the displayed
     * image rescaled.
     * @param size the size of the canvas
     * @return the adapted factor
     */
    fun transformPoint(size: Size): Float {
        return if (image.width / image.height > 1.57f) {
            size.width/image.width
        } else {
            size.height/image.height
        }
    }
}