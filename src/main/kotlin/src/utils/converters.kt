package src.utils

import javafx.util.StringConverter
import javafx.util.converter.NumberStringConverter
import org.joda.time.DateTime
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


fun LocalDate.toDate(defaultValue: DateTime = DateTime(1900, 1, 1, 0, 0, 0)): DateTime {
    return DateTime(this.year, this.monthValue, this.dayOfMonth, 0, 0, 0)
}

fun DateTime.toJavaLocalDate(): LocalDate {
    return LocalDate.of(this.year, this.monthOfYear, this.dayOfMonth)
}

fun String.tryInt(defaultValue: Int = 0): Int {
    val cleanVal = this.replace(Regex("[,a-z]", option = RegexOption.IGNORE_CASE), "")
    return try {
        cleanVal.toInt()
    } catch (e: NumberFormatException) {
        defaultValue
    }
}

object IntegerConverter : StringConverter<Int>() {
    override fun toString(int: Int?): String = DecimalFormat("#,##0").format(int)
    override fun fromString(text: String?): Int {
        val standardizedString = text?.replace(Regex("[,a-z]", option = RegexOption.IGNORE_CASE), "") ?: "0"
        return standardizedString.tryInt(0)
    }
}


class SafeIntegerConverter : NumberStringConverter() {
    override fun fromString(value: String?): Number {
        return value?.tryInt() ?: 0
    }
}

class SafeDateStringConverter : StringConverter<LocalDate>() {
    override fun toString(dateVal: LocalDate): String {
        return try {
            dateVal.format(DateTimeFormatter.ISO_DATE)
        } catch (e: Exception) {
            println(e)
            ""
        }
    }

    override fun fromString(value: String?): LocalDate {
        return try {
            LocalDate.parse(value, DateTimeFormatter.ISO_DATE)
        } catch (e: Exception) {
            LocalDate.of(1900, 1, 1)
        }
    }
}