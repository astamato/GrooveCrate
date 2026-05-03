package com.example.myapplication.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

fun Modifier.drawBehindCorner(
    topLeft: Boolean = false,
    topRight: Boolean = false,
    bottomLeft: Boolean = false,
    bottomRight: Boolean = false,
    color: Color,
    stroke: Dp,
) = this.drawBehind {
    val s = stroke.toPx()
    if (topLeft) {
        drawLine(color, Offset(0f, 0f), Offset(size.width, 0f), s)
        drawLine(color, Offset(0f, 0f), Offset(0f, size.height), s)
    }
    if (topRight) {
        drawLine(color, Offset(size.width, 0f), Offset(0f, 0f), s)
        drawLine(color, Offset(size.width, 0f), Offset(size.width, size.height), s)
    }
    if (bottomLeft) {
        drawLine(color, Offset(0f, size.height), Offset(size.width, size.height), s)
        drawLine(color, Offset(0f, size.height), Offset(0f, 0f), s)
    }
    if (bottomRight) {
        drawLine(color, Offset(size.width, size.height), Offset(0f, size.height), s)
        drawLine(color, Offset(size.width, size.height), Offset(size.width, 0f), s)
    }
}
