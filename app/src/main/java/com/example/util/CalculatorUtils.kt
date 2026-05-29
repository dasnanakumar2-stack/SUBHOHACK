package com.example.util

import androidx.compose.ui.graphics.Color

data class ResistorColorBand(
    val name: String,
    val color: Color,
    val textOnDark: Boolean = true,
    val digitValue: Int? = null,
    val multiplierValue: Double? = null,
    val toleranceValue: Float? = null, // in percent
    val tempCoeffValue: Int? = null // in ppm/K
)

object ResistorCalculators {

    // Common standard resistor band colors
    val digitBands = listOf(
        ResistorColorBand("Black", Color(0xFF1E1E1E), digitValue = 0, multiplierValue = 1.0),
        ResistorColorBand("Brown", Color(0xFF8B5A2B), digitValue = 1, multiplierValue = 10.0, toleranceValue = 1f, tempCoeffValue = 100),
        ResistorColorBand("Red", Color(0xFFE53935), digitValue = 2, multiplierValue = 100.0, toleranceValue = 2f, tempCoeffValue = 50),
        ResistorColorBand("Orange", Color(0xFFFFB300), textOnDark = false, digitValue = 3, multiplierValue = 1000.0, tempCoeffValue = 15),
        ResistorColorBand("Yellow", Color(0xFFFDD835), textOnDark = false, digitValue = 4, multiplierValue = 10000.0, tempCoeffValue = 25),
        ResistorColorBand("Green", Color(0xFF43A047), digitValue = 5, multiplierValue = 100000.0, toleranceValue = 0.5f, tempCoeffValue = 20),
        ResistorColorBand("Blue", Color(0xFF1E88E5), digitValue = 6, multiplierValue = 1000000.0, toleranceValue = 0.25f, tempCoeffValue = 10),
        ResistorColorBand("Violet", Color(0xFF8E24AA), digitValue = 7, multiplierValue = 10000000.0, toleranceValue = 0.1f, tempCoeffValue = 5),
        ResistorColorBand("Grey", Color(0xFF757575), digitValue = 8, multiplierValue = 100000000.0, toleranceValue = 0.05f, tempCoeffValue = 1),
        ResistorColorBand("White", Color(0xFFF5F5F5), textOnDark = false, digitValue = 9, multiplierValue = 1000000000.0)
    )

    val multiplierOnlyBands = listOf(
        ResistorColorBand("Gold", Color(0xFFFFD700), textOnDark = false, multiplierValue = 0.1, toleranceValue = 5f),
        ResistorColorBand("Silver", Color(0xFFC0C0C0), textOnDark = false, multiplierValue = 0.01, toleranceValue = 10f)
    )

    fun getAllBandsForDigit(): List<ResistorColorBand> = digitBands

    fun getAllBandsForMultiplier(): List<ResistorColorBand> = digitBands + multiplierOnlyBands

    fun getAllBandsForTolerance(): List<ResistorColorBand> = listOf(
        digitBands[1], // Brown
        digitBands[2], // Red
        digitBands[5], // Green
        digitBands[6], // Blue
        digitBands[7], // Violet
        digitBands[8], // Grey
        multiplierOnlyBands[0], // Gold
        multiplierOnlyBands[1]  // Silver
    )

    fun getAllBandsForTempCoeff(): List<ResistorColorBand> = digitBands.filter { it.tempCoeffValue != null }

    /**
     * Calculates resistance, tolerance and temp coefficient.
     * Returns: Triple(Resistance_Value_String, Tolerance_String, TempCoeff_String)
     */
    fun calculateResistor(
        bandsCount: Int,
        b1: ResistorColorBand,
        b2: ResistorColorBand,
        b3: ResistorColorBand, // Used as digit for 5 & 6 band, or multiplier for 4-band!
        b4: ResistorColorBand, // Used as multiplier for 5 & 6 band, or tolerance for 4-band!
        b5: ResistorColorBand, // Used as tolerance for 5 & 6 band, ignored for 4-band
        b6: ResistorColorBand? // Temp coeff for 6-band
    ): ResistorCalculationResult {
        val rawValue: Double
        val toleranceText: String
        val tempCoeffText: String

        if (bandsCount == 4) {
            val d1 = b1.digitValue ?: 0
            val d2 = b2.digitValue ?: 0
            val mult = b3.multiplierValue ?: 1.0
            rawValue = (d1 * 10 + d2) * mult
            val tol = b4.toleranceValue ?: 5f
            toleranceText = "±$tol%"
            tempCoeffText = ""
        } else {
            // 5 or 6 bands
            val d1 = b1.digitValue ?: 0
            val d2 = b2.digitValue ?: 0
            val d3 = b3.digitValue ?: 0
            val mult = b4.multiplierValue ?: 1.0
            rawValue = (d1 * 100 + d2 * 10 + d3) * mult
            val tol = b5.toleranceValue ?: 1f
            toleranceText = "±$tol%"
            
            if (bandsCount == 6 && b6 != null) {
                val tempCo = b6.tempCoeffValue ?: 100
                tempCoeffText = "${tempCo} ppm/K"
            } else {
                tempCoeffText = ""
            }
        }

        return ResistorCalculationResult(
            valueInOhms = rawValue,
            formattedValue = formatOhmValue(rawValue),
            tolerance = toleranceText,
            tempCoeff = tempCoeffText
        )
    }

    fun formatOhmValue(ohms: Double): String {
        return when {
            ohms >= 1_000_000_000 -> String.format("%.2f GΩ", ohms / 1_000_000_000.0).replace(".00", "")
            ohms >= 1_000_000 -> String.format("%.2f MΩ", ohms / 1_000_000.0).replace(".00", "")
            ohms >= 1000 -> String.format("%.2f KΩ", ohms / 1000.0).replace(".00", "")
            else -> String.format("%.1f Ω", ohms).replace(".00", "").replace(".0", "")
        }
    }

    // --- SMD RESISTOR PARSER ---
    private val eia96Lookup = mapOf(
        "01" to 100, "02" to 102, "03" to 105, "04" to 107, "05" to 110, "06" to 113, "07" to 115, "08" to 118, "09" to 121,
        "10" to 124, "11" to 127, "12" to 130, "13" to 133, "14" to 137, "15" to 140, "16" to 143, "17" to 147, "18" to 150,
        "19" to 154, "20" to 158, "21" to 162, "22" to 165, "23" to 169, "24" to 174, "25" to 178, "26" to 182, "27" to 187,
        "28" to 191, "29" to 196, "30" to 200, "31" to 205, "32" to 210, "33" to 215, "34" to 221, "35" to 226, "36" to 232,
        "37" to 237, "38" to 243, "39" to 249, "40" to 255, "41" to 261, "42" to 267, "43" to 274, "44" to 280, "45" to 287,
        "46" to 294, "47" to 301, "48" to 309, "49" to 316, "50" to 324, "51" to 332, "52" to 340, "53" to 348, "54" to 357,
        "55" to 365, "56" to 374, "57" to 383, "58" to 392, "59" to 402, "60" to 412, "61" to 422, "62" to 432, "63" to 442,
        "64" to 453, "65" to 464, "66" to 475, "67" to 487, "68" to 499, "69" to 511, "70" to 523, "71" to 536, "72" to 549,
        "73" to 562, "74" to 576, "75" to 590, "76" to 604, "77" to 619, "78" to 634, "79" to 649, "80" to 665, "81" to 681,
        "82" to 698, "83" to 715, "84" to 732, "85" to 750, "86" to 768, "87" to 787, "88" to 806, "89" to 825, "90" to 845,
        "91" to 866, "92" to 887, "93" to 909, "94" to 931, "95" to 953, "96" to 976
    )

    fun parseSmdCode(code: String): SmdParseResult {
        val trimmed = code.trim().uppercase()
        if (trimmed.isEmpty()) {
            return SmdParseResult(false, "No input", "Enter 3-digit, 4-digit, or EIA-96 code (e.g., 103, 4702, 01A)")
        }

        // Catch low resistance value (e.g. 1R5, R100, R010)
        if (trimmed.contains('R')) {
            val parts = trimmed.split('R')
            if (parts.size == 2) {
                val before = parts[0].toDoubleOrNull() ?: 0.0
                val afterStr = parts[1]
                if (afterStr.isEmpty()) {
                    return SmdParseResult(true, formatOhmValue(before), "Low Resistance ($trimmed)")
                }
                val div = Math.pow(10.0, afterStr.length.toDouble())
                val after = (afterStr.toDoubleOrNull() ?: 0.0) / div
                val finalVal = before + after
                return SmdParseResult(true, formatOhmValue(finalVal), "Precision Low Resistance ($trimmed)")
            }
        }

        // Try 3-digit standard code (E24 Series, 5% Tolerance)
        if (trimmed.length == 3 && trimmed.all { it.isDigit() }) {
            val digit1 = trimmed[0] - '0'
            val digit2 = trimmed[1] - '0'
            val multiplier = trimmed[2] - '0'
            
            if (multiplier > 7) {
                return SmdParseResult(false, "Invalid multiplier", "3rd digit multiplier must be 0-7")
            }

            val value = (digit1 * 10 + digit2) * Math.pow(10.0, multiplier.toDouble())
            return SmdParseResult(true, formatOhmValue(value), "Standard 3-Digit (E-24 Series, ±5%)")
        }

        // Try 4-digit standard code (E96 Series, 1% Tolerance)
        if (trimmed.length == 4 && trimmed.all { it.isDigit() }) {
            val digit1 = trimmed[0] - '0'
            val digit2 = trimmed[1] - '0'
            val digit3 = trimmed[2] - '0'
            val multiplier = trimmed[3] - '0'

            if (multiplier > 8) {
                return SmdParseResult(false, "Invalid multiplier", "4th digit multiplier must be 0-8")
            }

            val value = (digit1 * 100 + digit2 * 10 + digit3) * Math.pow(10.0, multiplier.toDouble())
            return SmdParseResult(true, formatOhmValue(value), "Precision 4-Digit (E-96 Series, ±1%)")
        }

        // Try EIA-96 standard code (E96 1% SMD resistors)
        if (trimmed.length == 3) {
            val indexCode = trimmed.substring(0, 2)
            val multChar = trimmed[2]
            
            val baseValue = eia96Lookup[indexCode]
            if (baseValue != null) {
                val multFactor = when (multChar) {
                    'Y', 'R' -> 0.01
                    'X', 'S' -> 0.1
                    'A' -> 1.0
                    'B' -> 10.0
                    'C' -> 100.0
                    'D' -> 1000.0
                    'E' -> 10000.0
                    'F' -> 100000.0
                    else -> null
                }

                if (multFactor != null) {
                    val finalVal = baseValue * multFactor
                    return SmdParseResult(true, formatOhmValue(finalVal), "EIA-96 Ultra-Precision (±1% Component)")
                }
            }
        }

        return SmdParseResult(
            success = false,
            value = "Unsupported Code",
            explanation = "Ensure correct format:\n- 3-Digit: '103' is 10k\n- 4-Digit: '4702' is 47k\n- Low Resistance: '2R2' is 2.2Ω\n- EIA-96: '01A' is 100Ω"
        )
    }
}

data class ResistorCalculationResult(
    val valueInOhms: Double,
    val formattedValue: String,
    val tolerance: String,
    val tempCoeff: String
)

data class SmdParseResult(
    val success: Boolean,
    val value: String,
    val explanation: String
)
