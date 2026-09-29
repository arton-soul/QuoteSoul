package com.soulquote.app.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soulquote.app.BuildConfig
import com.soulquote.app.R
import com.soulquote.app.core.localization.AppStrings
import com.soulquote.app.core.theme.SageGreen80
import com.soulquote.app.core.theme.WarmSand80
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    strings: AppStrings,
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val entranceAlpha = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "mindfulBreathe")
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.50f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    LaunchedEffect(Unit) {
        entranceAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
        )
        // Keep splash display for a serene, mindful pause then transition
        delay(1900)
        onTimeout()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1413),
                        Color(0xFF161E1A),
                        Color(0xFF0F1312)
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Allow instant tap-to-continue if user wants to skip
                onTimeout()
            }
            .alpha(entranceAlpha.value)
    ) {
        // Centered Glowing Emblem & Typography
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(140.dp)
            ) {
                // Gentle breathing halo glow
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .graphicsLayer {
                            scaleX = breatheScale * 1.15f
                            scaleY = breatheScale * 1.15f
                            alpha = glowAlpha
                        }
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0x66E8A87C),
                                    Color(0x22E8A87C),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Central brand icon
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "SoulQuote Logo",
                    modifier = Modifier
                        .size(110.dp)
                        .graphicsLayer {
                            scaleX = breatheScale
                            scaleY = breatheScale
                        }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Brand Name
            Text(
                text = "SoulQuote",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.5.sp,
                color = Color(0xFFFBF8F2)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Mindful Tagline
            Text(
                text = strings.splashTagline,
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 0.8.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = SageGreen80,
                textAlign = TextAlign.Center
            )
        }

        // Bottom Mindful Presence Thought & Version
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "“${strings.splashMindfulBreath}”",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontStyle = FontStyle.Italic,
                    lineHeight = 18.sp
                ),
                color = Color(0xFFB5C9C3).copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "v${BuildConfig.VERSION_NAME}",
                fontSize = 11.sp,
                color = Color.Gray.copy(alpha = 0.5f)
            )
        }
    }
}
