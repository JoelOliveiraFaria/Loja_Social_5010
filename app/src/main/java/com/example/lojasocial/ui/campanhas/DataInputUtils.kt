package com.example.lojasocial.ui.campanhas

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class DateMaskVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.take(8)

        val out = buildString {
            for (i in digits.indices) {
                append(digits[i])
                if (i == 1 || i == 3) append('/')
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                when {
                    offset <= 2 -> offset
                    offset <= 4 -> offset + 1
                    offset <= 8 -> offset + 2
                    else -> 10
                }

            override fun transformedToOriginal(offset: Int): Int =
                when {
                    offset <= 2 -> offset
                    offset <= 5 -> offset - 1
                    offset <= 10 -> offset - 2
                    else -> 8
                }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

fun onlyDateDigits(input: String): String =
    input.filter { it.isDigit() }.take(8)

fun digitsToDashedDatePartial(d: String): String {
    val digits = d.take(8)
    if (digits.isEmpty()) return ""
    val sb = StringBuilder()
    for (i in digits.indices) {
        sb.append(digits[i])
        if (i == 1 || i == 3) sb.append('-')
    }
    return sb.toString()
}

fun dateDigitsToSortableInt(d: String): Int? {
    if (d.length != 8) return null

    val dd = d.substring(0, 2).toIntOrNull() ?: return null
    val mm = d.substring(2, 4).toIntOrNull() ?: return null
    val yyyy = d.substring(4, 8).toIntOrNull() ?: return null

    if (yyyy !in 1900..2100) return null
    if (mm !in 1..12) return null

    val maxDay = when (mm) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(yyyy)) 29 else 28
        else -> return null
    }

    if (dd !in 1..maxDay) return null

    return yyyy * 10000 + mm * 100 + dd
}

fun isLeapYear(year: Int): Boolean =
    (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

