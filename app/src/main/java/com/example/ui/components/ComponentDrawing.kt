package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResistorOnCanvas(
    bandColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val centerY = h / 2f
        
        // Leads (leads are drawn as thin grey lines stretching edge to edge)
        drawLine(
            color = Color(0xFFB0BEC5),
            start = Offset(0f, centerY),
            end = Offset(w, centerY),
            strokeWidth = 6f
        )

        // Resistor body
        val bodyWidth = w * 0.7f
        val bodyHeight = h * 0.5f
        val bodyLeft = (w - bodyWidth) / 2f
        val bodyTop = (h - bodyHeight) / 2f

        // Draw body of the resistor (light tan/sand color typical of axial components)
        drawRoundRect(
            color = Color(0xFFF1E4C3),
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyWidth, bodyHeight),
            cornerRadius = CornerRadius(16f, 16f)
        )

        // Draw structural contours at ends
        drawRoundRect(
            color = Color(0xFFE3D3A3),
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyWidth * 0.1f, bodyHeight),
            cornerRadius = CornerRadius(16f, 16f)
        )
        drawRoundRect(
            color = Color(0xFFE3D3A3),
            topLeft = Offset(bodyLeft + bodyWidth * 0.9f, bodyTop),
            size = Size(bodyWidth * 0.1f, bodyHeight),
            cornerRadius = CornerRadius(16f, 16f)
        )

        // Draw actual bands based on data
        val bandCount = bandColors.size
        val startPct = 0.15f
        val endPct = 0.85f
        val range = endPct - startPct
        
        for (i in 0 until bandCount) {
            val bandSize = bodyWidth * 0.06f
            val spacing = range / (bandCount - 1).coerceAtLeast(1)
            val bandLeft = bodyLeft + bodyWidth * (startPct + (i * spacing)) - (bandSize / 2f)
            
            drawRect(
                color = bandColors[i],
                topLeft = Offset(bandLeft, bodyTop),
                size = Size(bandSize, bodyHeight)
            )
        }
    }
}

@Composable
fun SmdResistorOnCanvas(
    code: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF212121), RoundedCornerShape(4.dp))
            .border(2.dp, Color(0xFF757575), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Solder end 1
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.18f)
                    .background(Color(0xFFCFD8DC))
            )
            // Chip body
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.64f)
                    .background(Color(0xFF151515))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = code.uppercase(),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            }
            // Solder end 2
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.18f)
                    .background(Color(0xFFCFD8DC))
            )
        }
    }
}

@Composable
fun ComponentIllustration(
    symbolType: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        when (symbolType) {
            "RESISTOR" -> {
                // Draw a standard axial resistor
                drawLine(Color(0xFF90A4AE), Offset(0f, cy), Offset(cx - 50f, cy), strokeWidth = 5f)
                drawLine(Color(0xFF90A4AE), Offset(cx + 50f, cy), Offset(w, cy), strokeWidth = 5f)
                
                // Zig-zag path for resistor body or schematic symbol
                val path = Path().apply {
                    moveTo(cx - 50f, cy)
                    lineTo(cx - 35f, cy - 20f)
                    lineTo(cx - 20f, cy + 20f)
                    lineTo(cx - 5f, cy - 20f)
                    lineTo(cx + 10f, cy + 20f)
                    lineTo(cx + 25f, cy - 20f)
                    lineTo(cx + 40f, cy + 20f)
                    lineTo(cx + 50f, cy)
                }
                drawPath(path, Color(0xFFE53935), style = Stroke(width = 6f))
            }
            "SMD_RESISTOR" -> {
                // Draw standard Black SMD Chip
                drawRoundRect(
                    color = Color(0xFF455A64),
                    topLeft = Offset(cx - 50f, cy - 25f),
                    size = Size(100f, 50f),
                    cornerRadius = CornerRadius(5f, 5f)
                )
                // Solder pads
                drawRect(Color(0xFFCFD8DC), Offset(cx - 50f, cy - 25f), Size(15f, 50f))
                drawRect(Color(0xFFCFD8DC), Offset(cx + 35f, cy - 25f), Size(15f, 50f))
                // Text representation
                drawRoundRect(
                    color = Color(0xFF1E1E1E),
                    topLeft = Offset(cx - 30f, cy - 20f),
                    size = Size(60f, 40f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
            }
            "CAP_POLAR" -> {
                // Electrolytic canister cylindrical look
                drawRect(Color(0xFFCFD8DC), Offset(cx - 4f, cy), Size(8f, h / 2)) // Leads
                drawRect(Color(0xFFCFD8DC), Offset(cx + 15f, cy), Size(8f, h / 2)) // Leads

                drawRoundRect(
                    color = Color(0xFF039BE5),
                    topLeft = Offset(cx - 30f, cy - 60f),
                    size = Size(60f, 100f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                // Negative strip stripe
                drawRect(
                    color = Color(0xFFCFD8DC),
                    topLeft = Offset(cx + 10f, cy - 60f),
                    size = Size(15f, 100f)
                )
                // Drawn minus signs inside negative stripe
                drawLine(Color(0xFF37474F), Offset(cx + 17f, cy - 40f), Offset(cx + 23f, cy - 40f), strokeWidth = 4f)
                drawLine(Color(0xFF37474F), Offset(cx + 17f, cy - 10f), Offset(cx + 23f, cy - 10f), strokeWidth = 4f)
                drawLine(Color(0xFF37474F), Offset(cx + 17f, cy + 20f), Offset(cx + 23f, cy + 20f), strokeWidth = 4f)
            }
            "CAPACITOR" -> {
                // Ceramic disc (warm orange-tan circle on 2 leads)
                drawLine(Color(0xFFB0BEC5), Offset(cx - 15f, cy), Offset(cx - 15f, h), strokeWidth = 5f)
                drawLine(Color(0xFFB0BEC5), Offset(cx + 15f, cy), Offset(cx + 15f, h), strokeWidth = 5f)
                
                drawCircle(
                    color = Color(0xFFEE8F3F),
                    radius = 45f,
                    center = Offset(cx, cy - 15f)
                )
                drawCircle(
                    color = Color(0xFFDF7E2C),
                    radius = 45f,
                    center = Offset(cx, cy - 15f),
                    style = Stroke(width = 4f)
                )
            }
            "SMD_CAP" -> {
                // Brown MLCC chip
                drawRoundRect(
                    color = Color(0xFFBCAAA4), // Tan/brown typical of MLCC
                    topLeft = Offset(cx - 50f, cy - 25f),
                    size = Size(100f, 50f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                // Silver metal terminals
                drawRoundRect(Color(0xFFECEFF1), Offset(cx - 50f, cy - 25f), Size(20f, 50f), CornerRadius(6f, 6f))
                drawRoundRect(Color(0xFFECEFF1), Offset(cx + 30f, cy - 25f), Size(20f, 50f), CornerRadius(6f, 6f))
            }
            "SMD_CAP_POLAR" -> {
                // SMD Tantalum (yellowish/orange or black prism with stripe)
                drawRoundRect(
                    color = Color(0xFFFFB300),
                    topLeft = Offset(cx - 45f, cy - 25f),
                    size = Size(90f, 50f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                // Positive Stripe
                drawRect(
                    color = Color(0xFFE65100),
                    topLeft = Offset(cx - 45f, cy - 25f),
                    size = Size(15f, 50f)
                )
                // SMT Terminals popping out
                drawRect(Color(0xFFB0BEC5), Offset(cx - 52f, cy - 5f), Size(7f, 10f))
                drawRect(Color(0xFFB0BEC5), Offset(cx + 45f, cy - 5f), Size(7f, 10f))
            }
            "TRANSISTOR_BJT" -> {
                // TO-92 flat side package
                // 3 leads cascading down
                drawLine(Color(0xFF90A4AE), Offset(cx - 25f, cy), Offset(cx - 25f, h), strokeWidth = 4f)
                drawLine(Color(0xFF90A4AE), Offset(cx, cy), Offset(cx, h), strokeWidth = 4f)
                drawLine(Color(0xFF90A4AE), Offset(cx + 25f, cy), Offset(cx + 25f, h), strokeWidth = 4f)

                // Transistor plastic body
                val path = Path().apply {
                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(cx - 40f, cy - 50f, cx + 40f, cy + 10f),
                        startAngleDegrees = 180f,
                        sweepAngleDegrees = 180f,
                        forceMoveTo = true
                    )
                    lineTo(cx + 40f, cy + 10f)
                    lineTo(cx - 40f, cy + 10f)
                    close()
                }
                drawPath(path, Color(0xFF263238))
            }
            "MOSFET_N" -> {
                // TO-220 package with big heat sink tab at top
                drawLine(Color(0xFFB0BEC5), Offset(cx - 30f, cy), Offset(cx - 30f, h - 5f), strokeWidth = 5f)
                drawLine(Color(0xFFB0BEC5), Offset(cx, cy), Offset(cx, h - 5f), strokeWidth = 5f)
                drawLine(Color(0xFFB0BEC5), Offset(cx + 30f, cy), Offset(cx + 30f, h - 5f), strokeWidth = 5f)

                // Metal tab
                drawRoundRect(
                    color = Color(0xFFCFD8DC),
                    topLeft = Offset(cx - 40f, cy - 80f),
                    size = Size(80f, 50f),
                    cornerRadius = CornerRadius(5f, 5f)
                )
                // Metal tab screw hole
                drawCircle(Color(0xFF1A1A1A), radius = 10f, center = Offset(cx, cy - 60f))

                // Ceramic switch casing
                drawRect(
                    color = Color(0xFF1C1C1C),
                    topLeft = Offset(cx - 45f, cy - 35f),
                    size = Size(90f, 65f)
                )
            }
            "DIODE" -> {
                // Axial diode with cathode ring
                drawLine(Color(0xFF90A4AE), Offset(0f, cy), Offset(w, cy), strokeWidth = 5f)
                
                drawRoundRect(
                    color = Color(0xFF212121),
                    topLeft = Offset(cx - 50f, cy - 20f),
                    size = Size(100f, 40f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                
                // Silver cathode stripe
                drawRect(
                    color = Color(0xFFCFD8DC),
                    topLeft = Offset(cx + 25f, cy - 20f),
                    size = Size(15f, 40f)
                )
            }
            "LED" -> {
                // LED representation
                drawLine(Color(0xFFCFD8DC), Offset(cx - 15f, cy), Offset(cx - 15f, h), strokeWidth = 5f)
                drawLine(Color(0xFFCFD8DC), Offset(cx + 15f, cy), Offset(cx + 15f, h), strokeWidth = 4f)

                // Led bulb
                drawRoundRect(
                    color = Color(0xFFFF1744),
                    topLeft = Offset(cx - 35f, cy - 65f),
                    size = Size(70f, 75f),
                    cornerRadius = CornerRadius(30f, 30f)
                )
                // Bottom plastic rim ring
                drawRect(
                    color = Color(0xFFD50000),
                    topLeft = Offset(cx - 38f, cy + 2f),
                    size = Size(76f, 10f)
                )
            }
            "IC_8" -> {
                // DIP-8 standard integrated chip package
                drawRoundRect(
                    color = Color(0xFF263238),
                    topLeft = Offset(cx - 40f, cy - 60f),
                    size = Size(80f, 120f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                // Identifier notch at top edge
                drawCircle(Color(0xFF1E1E1E), radius = 12f, center = Offset(cx, cy - 60f))

                // Left side Pins
                for (i in 0..3) {
                    val y = cy - 45f + (i * 30f)
                    drawRect(Color(0xFFECEFF1), Offset(cx - 60f, y), Size(20f, 10f))
                }
                // Right side Pins
                for (i in 0..3) {
                    val y = cy - 45f + (i * 30f)
                    drawRect(Color(0xFFECEFF1), Offset(cx + 40f, y), Size(20f, 10f))
                }
            }
            "SOIC_8" -> {
                // SOIC-8 SMD representation
                drawRoundRect(
                    color = Color(0xFF37474F),
                    topLeft = Offset(cx - 35f, cy - 50f),
                    size = Size(70f, 100f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                // Corner dot pin 1
                drawCircle(Color(0xFF90A4AE), radius = 5f, center = Offset(cx - 23f, cy - 35f))

                // SMD Lead legs
                for (i in 0..3) {
                    val y = cy - 40f + (i * 26f)
                    // Left leg bend
                    val leftLeg = Path().apply {
                        moveTo(cx - 35f, y + 4f)
                        lineTo(cx - 50f, y + 4f)
                        lineTo(cx - 55f, y + 10f)
                    }
                    drawPath(leftLeg, Color(0xFFB0BEC5), style = Stroke(width = 4f))

                    // Right leg bend
                    val rightLeg = Path().apply {
                        moveTo(cx + 35f, y + 4f)
                        lineTo(cx + 50f, y + 4f)
                        lineTo(cx + 55f, y + 10f)
                    }
                    drawPath(rightLeg, Color(0xFFB0BEC5), style = Stroke(width = 4f))
                }
            }
            "IC_28" -> {
                // DIP-28 wide standard controller chip
                drawRoundRect(
                    color = Color(0xFF1C2C35),
                    topLeft = Offset(cx - 35f, cy - 90f),
                    size = Size(70f, 180f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                // Half moon notch indicator
                drawCircle(Color(0xFF11171A), radius = 10f, center = Offset(cx, cy - 90f))

                // Left Pins
                for (i in 0..10) {
                    val y = cy - 75f + (i * 15f)
                    drawRect(Color(0xFFECEFF1), Offset(cx - 50f, y), Size(15f, 6f))
                }
                // Right Pins
                for (i in 0..10) {
                    val y = cy - 75f + (i * 15f)
                    drawRect(Color(0xFFECEFF1), Offset(cx + 35f, y), Size(15f, 6f))
                }
            }
            "INDUCTOR" -> {
                // Toroidal copper choke
                drawCircle(Color(0xFF4E342E), radius = 45f, center = Offset(cx, cy))
                drawCircle(Color(0xFF1A1A1A), radius = 22f, center = Offset(cx, cy))

                // Copper wire coils wraps
                for (i in 0..6) {
                    val angle = (i * (360f / 7f)) * (Math.PI / 180f)
                    val r1 = 20f
                    val r2 = 48f
                    val sX = cx + (r1 * Math.cos(angle)).toFloat()
                    val sY = cy + (r1 * Math.sin(angle)).toFloat()
                    val eX = cx + (r2 * Math.cos(angle)).toFloat()
                    val eY = cy + (r2 * Math.sin(angle)).toFloat()
                    drawLine(Color(0xFFFFB74D), Offset(sX, sY), Offset(eX, eY), strokeWidth = 8f)
                }
            }
            "SMD_IND" -> {
                // SMD Shielded Inductor (grey square package with solder pads and wire loop outline)
                drawRoundRect(
                    color = Color(0xFF546E7A),
                    topLeft = Offset(cx - 40f, cy - 40f),
                    size = Size(80f, 80f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                // Solder pads
                drawRect(Color(0xFFCFD8DC), Offset(cx - 43f, cy - 20f), Size(6f, 40f))
                drawRect(Color(0xFFCFD8DC), Offset(cx + 37f, cy - 20f), Size(6f, 40f))
                
                // Ring coil indicator
                drawCircle(
                    color = Color(0xFF37474F),
                    radius = 24f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 6f)
                )
            }
            "SWITCH" -> {
                // Switched panel button
                drawRoundRect(
                    color = Color(0xFF0D47A1),
                    topLeft = Offset(cx - 40f, cy - 40f),
                    size = Size(80f, 80f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                // Metal collar ring
                drawCircle(Color(0xFFB0BEC5), radius = 20f, center = Offset(cx, cy))
                // Toggle steel handle pointing up-left
                drawLine(
                    color = Color(0xFFECEFF1),
                    start = Offset(cx, cy),
                    end = Offset(cx - 25f, cy - 28f),
                    strokeWidth = 12f
                )
                drawCircle(Color(0xFFCFD8DC), radius = 8f, center = Offset(cx - 25f, cy - 28f))
            }
            "RELAY" -> {
                // Relays blue box THT
                drawRoundRect(
                    color = Color(0xFF1565C0),
                    topLeft = Offset(cx - 45f, cy - 45f),
                    size = Size(90f, 90f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                // Pin lines underneath
                drawRect(Color(0xFFB0BEC5), Offset(cx - 35f, cy + 45f), Size(8f, 15f))
                drawRect(Color(0xFFB0BEC5), Offset(cx - 15f, cy + 45f), Size(8f, 15f))
                drawRect(Color(0xFFB0BEC5), Offset(cx + 25f, cy + 45f), Size(8f, 15f))
                
                // Coil schematic path doodle on relay face
                val coilPath = Path().apply {
                    moveTo(cx - 25f, cy - 10f)
                    quadraticTo(cx - 15f, cy - 20f, cx - 15f, cy - 10f)
                    quadraticTo(cx - 5f, cy - 20f, cx - 5f, cy - 10f)
                    quadraticTo(cx + 5f, cy - 20f, cx + 5f, cy - 10f)
                    quadraticTo(cx + 15f, cy - 20f, cx + 15f, cy - 10f)
                }
                drawPath(coilPath, Color.White, style = Stroke(width = 3f))
                drawLine(Color.White, Offset(cx - 30f, cy - 10f), Offset(cx - 25f, cy - 10f), strokeWidth = 3f)
                drawLine(Color.White, Offset(cx + 15f, cy - 10f), Offset(cx + 30f, cy - 10f), strokeWidth = 3f)
            }
            else -> {
                // General chip default
                drawRoundRect(
                    color = Color(0xFF455A64),
                    topLeft = Offset(cx - 40f, cy - 40f),
                    size = Size(80f, 80f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }
        }
    }
}

@Composable
fun PinoutDiagramWidget(
    symbolType: String,
    pinList: List<com.example.model.PinInfo>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
            .border(2.dp, Color(0xFF37474F), RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SCHEMATIC PIN ALLOCATION DIAGRAM",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E5FF),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Drawing of the package on left
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ComponentIllustration(symbolType = symbolType, modifier = Modifier.fillMaxSize())
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Pin allocation readouts on right
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    pinList.forEach { pin ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF263238), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color(0xFF00E5FF), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${pin.number}",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = pin.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = pin.description,
                                    fontSize = 9.sp,
                                    color = Color(0xFFB0BEC5),
                                    lineHeight = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
