package com.incanatoapps.retofut.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.widget.Toast
import androidx.core.content.FileProvider
import com.incanatoapps.retofut.data.network.model.PlayerCardResponse
import java.io.File
import java.io.FileOutputStream

object FifaCardShareHelper {

    fun createFifaCardBitmap(context: Context, card: PlayerCardResponse): Bitmap {
        val width = 720
        val height = 1040
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val isGold = card.rating >= 80
        val isIcon = card.rating >= 90

        // 1. Fondo transparente inicial
        canvas.drawColor(android.graphics.Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        // 2. Forma del Escudo FIFA
        val cardPath = Path().apply {
            moveTo(40f, height * 0.08f)
            lineTo(width * 0.16f, 20f)
            lineTo(width * 0.84f, 20f)
            lineTo(width - 40f, height * 0.08f)
            lineTo(width - 40f, height * 0.82f)
            lineTo(width * 0.5f, height - 30f)
            lineTo(40f, height * 0.82f)
            close()
        }

        // 3. Gradiente de la carta
        val gradientColors = when {
            isIcon -> intArrayOf(
                android.graphics.Color.parseColor("#FFFFFF"),
                android.graphics.Color.parseColor("#E2E8F0"),
                android.graphics.Color.parseColor("#94A3B8")
            )
            isGold -> intArrayOf(
                android.graphics.Color.parseColor("#FDE68A"),
                android.graphics.Color.parseColor("#F59E0B"),
                android.graphics.Color.parseColor("#B45309")
            )
            else -> intArrayOf(
                android.graphics.Color.parseColor("#E5E7EB"),
                android.graphics.Color.parseColor("#9CA3AF"),
                android.graphics.Color.parseColor("#4B5563")
            )
        }

        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                gradientColors,
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
            style = Paint.Style.FILL
        }
        canvas.drawPath(cardPath, cardPaint)

        // 4. Borde del Escudo
        val borderColor = if (isGold) android.graphics.Color.parseColor("#78350F")
                          else android.graphics.Color.parseColor("#1F2937")
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }
        canvas.drawPath(cardPath, borderPaint)

        // 5. Encabezado "FUTBULERO • CHICLAYO"
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            letterSpacing = 0.25f
        }
        canvas.drawText("FUTBULERO • ULTIMATE CARD", width / 2f, 75f, headerPaint)

        // 6. Rating OVR (Grande)
        val ratingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            textSize = 108f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("${card.rating}", 160f, 210f, ratingPaint)

        // 7. Posición
        val posPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            textSize = 42f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(card.posicion ?: "DC", 160f, 265f, posPaint)

        // Bandera Perú (Rojo - Blanco - Rojo)
        val flagLeft = 135f
        val flagTop = 295f
        val flagWidth = 50f
        val flagHeight = 32f
        val redPaint = Paint().apply { color = android.graphics.Color.parseColor("#DC2626") }
        val whitePaint = Paint().apply { color = android.graphics.Color.WHITE }
        canvas.drawRect(flagLeft, flagTop, flagLeft + flagWidth / 3f, flagTop + flagHeight, redPaint)
        canvas.drawRect(flagLeft + flagWidth / 3f, flagTop, flagLeft + (flagWidth * 2f / 3f), flagTop + flagHeight, whitePaint)
        canvas.drawRect(flagLeft + (flagWidth * 2f / 3f), flagTop, flagLeft + flagWidth, flagTop + flagHeight, redPaint)

        // 8. Avatar / Foto del jugador
        val avatarCenterX = 440f
        val avatarCenterY = 240f
        val avatarRadius = 130f
        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawCircle(avatarCenterX, avatarCenterY, avatarRadius, circlePaint)

        val circleBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        canvas.drawCircle(avatarCenterX, avatarCenterY, avatarRadius, circleBorderPaint)

        // Iniciales del jugador dentro del avatar
        val initialsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            textSize = 72f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val initials = card.nombre.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
        canvas.drawText(if (initials.isNotEmpty()) initials else "PF", avatarCenterX, avatarCenterY + 26f, initialsPaint)

        // 9. Nombre del Jugador
        val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            textSize = 52f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            letterSpacing = 0.15f
        }
        canvas.drawText(card.nombre.uppercase(), width / 2f, 440f, namePaint)

        // Línea divisoria
        val linePaint = Paint().apply {
            color = borderColor
            alpha = 100
            strokeWidth = 3f
        }
        canvas.drawLine(140f, 465f, width - 140f, 465f, linePaint)

        // 10. Atributos FIFA (Columna Izquierda y Derecha)
        val statValuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            textSize = 44f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }
        val statLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            textSize = 34f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
            alpha = 220
        }

        // Columna Izquierda: PAC, SHO, PAS
        val leftXVal = 240f
        val leftXLabel = 260f
        val statStartY = 540f
        val statSpacing = 68f

        canvas.drawText("${card.stats.PAC}", leftXVal, statStartY, statValuePaint)
        canvas.drawText("PAC", leftXLabel, statStartY, statLabelPaint)

        canvas.drawText("${card.stats.SHO}", leftXVal, statStartY + statSpacing, statValuePaint)
        canvas.drawText("SHO", leftXLabel, statStartY + statSpacing, statLabelPaint)

        canvas.drawText("${card.stats.PAS}", leftXVal, statStartY + statSpacing * 2, statValuePaint)
        canvas.drawText("PAS", leftXLabel, statStartY + statSpacing * 2, statLabelPaint)

        // Línea vertical central
        canvas.drawLine(width / 2f, statStartY - 40f, width / 2f, statStartY + statSpacing * 2 + 20f, linePaint)

        // Columna Derecha: DRI, DEF, PHY
        val rightXVal = 500f
        val rightXLabel = 520f

        canvas.drawText("${card.stats.DRI}", rightXVal, statStartY, statValuePaint)
        canvas.drawText("DRI", rightXLabel, statStartY, statLabelPaint)

        canvas.drawText("${card.stats.DEF}", rightXVal, statStartY + statSpacing, statValuePaint)
        canvas.drawText("DEF", rightXLabel, statStartY + statSpacing, statLabelPaint)

        canvas.drawText("${card.stats.PHY}", rightXVal, statStartY + statSpacing * 2, statValuePaint)
        canvas.drawText("PHY", rightXLabel, statStartY + statSpacing * 2, statLabelPaint)

        // 11. Footer: Partidos, Goles, Asistencias
        val footerBgPaint = Paint().apply {
            color = borderColor
            alpha = 40
            style = Paint.Style.FILL
        }
        canvas.drawRect(70f, 780f, width - 70f, 890f, footerBgPaint)

        val footerValPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            textSize = 38f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val footerLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            alpha = 200
        }

        // 3 columnas de rendimiento
        val col1X = 180f
        val col2X = width / 2f
        val col3X = width - 180f

        canvas.drawText("${card.performance.partidosJugados}", col1X, 835f, footerValPaint)
        canvas.drawText("PARTIDOS", col1X, 870f, footerLabelPaint)

        canvas.drawText("${card.performance.goles} ⚽", col2X, 835f, footerValPaint)
        canvas.drawText("GOLES", col2X, 870f, footerLabelPaint)

        canvas.drawText("${card.performance.asistencias}", col3X, 835f, footerValPaint)
        canvas.drawText("ASISTENCIAS", col3X, 870f, footerLabelPaint)

        // 12. Marca de Agua
        val watermarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            textSize = 19f
            textAlign = Paint.Align.CENTER
            alpha = 160
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("⭐ CARTA OFICIAL • FUTBULERO APP ⭐", width / 2f, 960f, watermarkPaint)

        return bitmap
    }

    fun shareFifaCard(
        context: Context,
        card: PlayerCardResponse,
        customMessage: String = "¡Mira mi Carta Oficial de FUTBULERO! Rating ${card.rating} OVR • ${card.nombre} • ${card.posicion} ⚽🔥"
    ) {
        try {
            val bitmap = createFifaCardBitmap(context, card)
            val imagesFolder = File(context.cacheDir, "images")
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs()
            }
            val file = File(imagesFolder, "fifa_card_${card.id}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_TEXT, customMessage)
                putExtra(Intent.EXTRA_SUBJECT, "Ficha FIFA FUTBULERO - ${card.nombre}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(
                Intent.createChooser(shareIntent, "Compartir Carta FIFA por WhatsApp")
            )
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Error al generar o compartir la carta: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
