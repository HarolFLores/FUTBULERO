package com.incanatoapps.retofut.ui.theme

import androidx.compose.ui.graphics.Color

// --- Modern "Soft UI" Football App Palette ---

// Primary & Accents
val BrandGreen = Color(0xFFA3E635)    // Main accent (buttons, active states)
val BrandGreenSoft = Color(0xFFD9F99D) // Soft background for highlights
val BrandGreenDeep = Color(0xFF8BBD2B) // For contrast text on green

// Background & Surfaces
val AppBackground = Color(0xFFF8FAF5)  // Warm off-white
val CardWhite = Color(0xFFFFFFFF)      // Pure white for elevation
val BorderGray = Color(0xFFE5E7EB)     // Subtle borders

// Neutral Text
val TextPrimary = Color(0xFF1F2937)    // Dark gray for readability
val TextSecondary = Color(0xFF6B7280)  // Muted gray for labels

// Complementary Pastels (for chips/categories)
val PastelYellow = Color(0xFFFEF9C3)
val PastelLilac = Color(0xFFE9D5FF)
val PastelBlue = Color(0xFFDBEAFE)

// Functional
val ErrorRed = Color(0xFFEF4444)
val SuccessGreen = Color(0xFF22C55E)

// Legacy compatibility (mapping to new system)
val White = CardWhite
val PitchGreen = BrandGreen
val PitchGreenLight = BrandGreenSoft
val SoftGray = BorderGray
val TextDark = TextPrimary
val TextGray = TextSecondary
val BackgroundWhite = AppBackground
val SurfaceWhite = CardWhite
val PrimaryPurple = BrandGreen // Temporarily mapping to avoid breaks
val SoftPurple = BrandGreenSoft
val PrimaryGreen = BrandGreen
