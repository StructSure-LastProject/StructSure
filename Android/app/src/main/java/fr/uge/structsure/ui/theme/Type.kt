package fr.uge.structsure.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fr.uge.structsure.R

val fonts = FontFamily(
    Font(R.font.poppins_black, weight=FontWeight.Black),
    Font(R.font.poppins_extrabold, weight=FontWeight.ExtraBold),
    Font(R.font.poppins_bold, weight=FontWeight.Bold),
    Font(R.font.poppins_semibold, weight=FontWeight.SemiBold),
    Font(R.font.poppins_medium, weight=FontWeight.Medium),
    Font(R.font.poppins_regulat, weight=FontWeight.Normal),
    Font(R.font.poppins_light, weight=FontWeight.Light),
    Font(R.font.poppins_extralight, weight=FontWeight.ExtraLight),
    Font(R.font.poppins_thin, weight=FontWeight.Thin)
)

// Set of Material typography styles to start with
val Typography = Typography(
    titleLarge = TextStyle( // Title
        fontFamily = fonts,
        fontWeight = FontWeight.SemiBold,
        fontSize = 25.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle( // Subtitle
        fontFamily = fonts,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle( // Accent
        fontFamily = fonts,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle( // Normal
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.sp
    )
)