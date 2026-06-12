package com.example.adoption_and_childcare.ui.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LoadingAnimationScreen(
    onComplete: () -> Unit,
    username: String = "Welcome back!"
) {
    // Animation values
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    // Pulsing circle animation
    val pulseScale = infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Rotating ring animation
    val rotationAngle = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Inner glow animation
    val glowAlpha = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Text fade in animation
    val textAlpha = infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "textAlpha"
    )

    // Trigger completion after 4 seconds
    LaunchedEffect(Unit) {
        delay(4000)
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D3D1F), // Dark forest green
                        Color(0xFF1a5d34)  // Deep green
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main loading animation
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(pulseScale.value),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow blur effect
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50).copy(alpha = glowAlpha.value * 0.6f),
                                    Color(0xFF2E7D32).copy(alpha = glowAlpha.value * 0.2f)
                                ),
                                radius = 100.dp.value
                            ),
                            shape = CircleShape
                        )
                        .blur(20.dp)
                )

                // Rotating outer ring
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(
                            Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFF81C784).copy(alpha = 0.8f),
                                    Color(0xFF4CAF50).copy(alpha = 0.6f),
                                    Color(0xFF2E7D32).copy(alpha = 0.4f),
                                    Color(0xFF81C784).copy(alpha = 0f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .align(Alignment.Center)
                )

                // Inner luminous circle
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF66BB6A).copy(alpha = glowAlpha.value),
                                    Color(0xFF4CAF50).copy(alpha = glowAlpha.value * 0.8f),
                                    Color(0xFF2E7D32).copy(alpha = 0f)
                                ),
                                radius = 50.dp.value
                            ),
                            shape = CircleShape
                        )
                        .align(Alignment.Center)
                )

                // Center checkmark or icon (appears after animation)
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            Color(0xFF2E7D32),
                            shape = CircleShape
                        )
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF81C784)
                    )
                }
            }

            // Loading text
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Preparing your dashboard",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF81C784).copy(alpha = textAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = username,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF66BB6A).copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Animated dots loader
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(3) { index ->
                    val dotScale = infiniteTransition.animateFloat(
                        initialValue = 0.8f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = 600,
                                delayMillis = index * 150
                            ),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot$index"
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .scale(dotScale.value)
                            .background(
                                Color(0xFF81C784),
                                shape = CircleShape
                            )
                            .padding(horizontal = 4.dp)
                    )
                }
            }
        }

        // Bottom accent line animation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF81C784).copy(alpha = glowAlpha.value),
                            Color.Transparent
                        )
                    )
                )
                .align(Alignment.BottomCenter)
        )
    }
}

// Alternative minimal loading screen
@Composable
fun MinimalLoadingScreen(
    onComplete: () -> Unit,
    username: String = "Welcome!"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "minimal_loading")

    val scale = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        delay(2500)
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D3D1F),
                        Color(0xFF1a5d34)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Large luminous dot
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF81C784),
                                Color(0xFF4CAF50),
                                Color(0xFF2E7D32).copy(alpha = 0f)
                            ),
                            radius = 60.dp.value
                        ),
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Loading dashboard...",
                fontSize = 16.sp,
                color = Color(0xFF81C784)
            )

            Text(
                text = username,
                fontSize = 12.sp,
                color = Color(0xFF66BB6A).copy(alpha = 0.7f)
            )
        }
    }
}
