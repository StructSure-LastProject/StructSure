package fr.uge.structsure.components

import android.graphics.Bitmap
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import fr.uge.structsure.scanPage.presentation.components.SensorState
import fr.uge.structsure.structuresPage.data.SensorDB
import kotlin.math.sqrt

/**
 * Composable that displays a plan image and allows to add points on it.
 * @param image the image to display
 * @param points the list of points to display on the image
 */
@Composable
fun Plan(
    image: Bitmap,
    points: () -> List<SensorDB>,
    temporaryPoint: SensorDB?,
    addPoint: (Double, Double) -> Unit,
    selectPoint: (SensorDB) -> Unit
) {
    val painter = remember(image) { BitmapPainter(image.asImageBitmap()) }
    val factor = remember(1) { Factor() }
    factor.init(painter.intrinsicSize)

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
                    detectTransformGestures { centroid, pan, zoom, _ -> factor.updatePanZoom(size, centroid, pan, zoom) }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { pos -> onTap(factor, size.toSize(), pos, points(), addPoint, selectPoint) },
                    )
                }
                .graphicsLayer(
                    scaleX = factor.zoom, scaleY = factor.zoom,
                    translationX = factor.pan.value.x, translationY = factor.pan.value.y
                )
        )
        Canvas(Modifier.fillMaxSize()) {
            val positions = if (temporaryPoint == null) points() else points() + temporaryPoint
            positions.forEach {
                val panned = imgToCanvas(factor, size, it.x, it.y)
                point(size, panned.x, panned.y, SensorState.from(it.state))
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
 * @param pos the position of the click in the canvas
 * @param points all the points contained in the plan
 */
private fun onTap(
    factor: Factor,
    size: Size,
    pos: Offset,
    points: List<SensorDB>,
    addPoint: (Double, Double) -> Unit,
    selectPoint: (SensorDB) -> Unit
) {
    val range = (40 / factor.transform / factor.zoom).toInt()
    val imgPos = canvasToImg(factor, size, pos.x, pos.y)
    for (i in points.indices) {
        if (points[i].inRange(imgPos.x.toDouble(), imgPos.y.toDouble(), range)) {
            selectPoint(points[i])
            return
        }
    }
    addPoint(imgPos.x.toDouble(), imgPos.y.toDouble())
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
 * @param factor factor holding conversion values
 * @param size the size of the canvas to draw on
 * @param x the initial x coordinate in the image
 * @param y the initial y coordinate in the image
 */
private fun imgToCanvas(factor: Factor, size: Size, x: Double, y: Double): Offset {
    val transformPoint = factor.transformPoint(size)
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val posX = x * transformPoint + size.width * factor.offset.x
    val posY = y * transformPoint + size.height * factor.offset.y
    return Offset(
        ((posX - centerX) * factor.zoom + centerX + factor.pan.value.x).toFloat(),
        ((posY - centerY) * factor.zoom + centerY + factor.pan.value.y).toFloat()
    )
}

/**
 * Transforms the coordinates of the given point to remove pan and zoom
 * from it and calculates it relative position in the image.
 * @param factor factor holding conversion values
 * @param size the size of the canvas to draw on
 * @param x the initial x coordinate in the canvas
 * @param y the initial y coordinate in the canvas
 */
private fun canvasToImg(factor: Factor, size: Size, x: Float, y: Float): Offset {
    val transformPoint = factor.transformPoint(size)
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val posX = (x - centerX - factor.pan.value.x) / factor.zoom + centerX
    val posY = (y - centerY - factor.pan.value.y) / factor.zoom + centerY
    return Offset(
        ((posX - size.width * factor.offset.x) / transformPoint).toInt().toFloat(),
        ((posY - size.height * factor.offset.y) / transformPoint).toInt().toFloat()
    )
}

/**
 * Checks if the given point is in range of this point or too far
 * @param x the x coordinate of the point to try to reach
 * @param y the y coordinate of the point to try to reach
 * @param range the maximum allowed distance between points
 * @return true if in range, false otherwise
 */
private fun SensorDB.inRange(x: Double, y: Double, range: Int): Boolean {
    val dx = this.x - x
    val dy = this.y - y

    // Square range check (much faster)
    if (dx * dx + dy * dy > range * range) return false

    // Pythagore for exact values
    return sqrt(dx * dx + dy * dy) <= range
}

/**
 * Object containing all the values that enable to switch from original
 * image coordinates to rescaled to panned/zoomed coordinates.
 */
class Factor {

    /** The currently hold image */
    var image = Size(0f, 0f)

    /** Ratio of the canvas calculated on the image original ratio */
    var canvasRatio = 1f

    /** Factor to go from the original image to the re-scaled image */
    var transform = 1f

    /** Margin top or left to center the image in the canvas */
    var offset = Offset(0f, 0f)

    /** The level of zoom of the image */
    var zoom = transform

    /** The horizontal and vertical displacement of the image */
    var pan = mutableStateOf(Offset(0f, 0f))

    /**
     * Initializes this factor for the given image, resetting and
     * calculating all factor to enable zooming, panning and changing
     * from screen's reference to image's reference
     * @param image the size of the loaded image
     */
    fun init(image: Size) {
        if (image == this.image) return
        this.image = image
        canvasRatio = (image.width / image.height).coerceIn(1.5f, 1.75f)
        transform = ((image.width / canvasRatio) / image.height).coerceAtLeast(1f)
        zoom = transform

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

    /**
     * Updates the pan and zoom values of this factor with the given
     * inputs from the user
     * @param size the height and width of the image
     * @param centroid center of the interaction
     * @param pan x and y displacements of the image
     * @param zoom change of the scale of the image
     */
    fun updatePanZoom(size: IntSize, centroid: Offset, pan: Offset, zoom: Float) {
        val adjustment = centroid * (1 - zoom)
        val offset = (offset + pan) * zoom + adjustment
        this.zoom = (this.zoom * zoom).coerceIn(transform, 3 * transform)

        val width = ((size.width * this.zoom - size.width)/2 - (size.width * offset.x * this.zoom)).coerceAtLeast(0f)
        val height = ((size.height * this.zoom - size.height)/2f - (size.height * offset.y * this.zoom)).coerceAtLeast(0f)
        this.pan.value = Offset(
            offset.x.coerceIn(-width, width),
            offset.y.coerceIn(-height, height)
        )
    }
}