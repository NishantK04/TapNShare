package com.nishant.tapnshare

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onComplete: () -> Unit) {
    // Navigate after 5 seconds
    LaunchedEffect(Unit) {
        delay(5000)
        onComplete()
    }

    val infiniteTransition = rememberInfiniteTransition()

    // Entry animation for phones + text
    var startAnim by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { startAnim = true }

    val phoneOffset by animateDpAsState(
        targetValue = if (startAnim) 0.dp else 40.dp,
        animationSpec = tween(800, easing = FastOutSlowInEasing), label = "phoneOffset"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing), label = "textAlpha"
    )
    val textOffset by animateDpAsState(
        targetValue = if (startAnim) 0.dp else 20.dp,
        animationSpec = tween(800, easing = FastOutSlowInEasing), label = "textOffset"
    )

    // Lock animation state
    val lockScale = remember { Animatable(0f) }
    val lockRotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { lockScale.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing)) }
        launch { lockRotation.animateTo(360f, animationSpec = tween(800, easing = FastOutSlowInEasing)) }
    }

    // Main Box with the dark gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            // *** FINAL ATTEMPT AT SMOOTH BACKGROUND GRADIENT ***
            // Using four close colors for a very subtle, atmospheric fade to black.
            .background(Color(0xFF001e29)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Phones + lock row
            Row(verticalAlignment = Alignment.CenterVertically) {

                // Left phone
                Icon(
                    painter = painterResource(id = R.drawable.mobile),
                    contentDescription = "Left Mobile Phone",
                    tint = Color(0xFF60a5fa), // Light Blue
                    modifier = Modifier
                        .size(48.dp)
                        .offset(x = -phoneOffset)
                        .scale(if (startAnim) 1f else 0f)
                )

                Spacer(modifier = Modifier.width(20.dp))

                // Lock with pulsing hollow circle
                Box(contentAlignment = Alignment.Center) {
                    val circleScale by infiniteTransition.animateFloat(
                        initialValue = 1.3f,
                        targetValue = 1.7f,
                        animationSpec = infiniteRepeatable(
                            tween(800, easing = FastOutSlowInEasing),
                            RepeatMode.Reverse
                        ), label = "circleScale"
                    )

                    // Hollow circle
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .scale(circleScale)
                            // Green border for the security indicator
                            .border(2.dp, Color(0xFF22c55e).copy(alpha = 0.5f), CircleShape)
                    )

                    // Lock icon with proper rotate + scale animation
                    Icon(
                        painter = painterResource(id = R.drawable.lock),
                        contentDescription = "Lock Icon",
                        tint = Color(0xFF22c55e), // Bright Green
                        modifier = Modifier
                            .size(32.dp)
                            .scale(lockScale.value)
                            .rotate(lockRotation.value)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Right phone
                Icon(
                    painter = painterResource(id = R.drawable.mobile),
                    contentDescription = "Right Mobile Phone",
                    tint = Color(0xFF60a5fa), // Light Blue
                    modifier = Modifier
                        .size(48.dp)
                        .offset(x = phoneOffset)
                        .scale(if (startAnim) 1f else 0f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App name - TapNShare
            Text(
                text = "TapNShare",
                fontSize = 45.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(textAlpha)
                    .offset(y = textOffset),
                style = androidx.compose.ui.text.TextStyle(
                    // Gradient text color (Blue -> Green) for branding
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF60A5FA), Color(0xFF22C55E))
                    )
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtext - Secure Contact Sharing
            Text(
                text = "Secure Contact Sharing",
                fontSize = 17.sp,
                color = Color.LightGray,
                modifier = Modifier
                    .alpha(textAlpha)
                    .offset(y = textOffset)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Loading dots animation
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { i ->
                    val dotScale by infiniteTransition.animateFloat(
                        initialValue = 0.7f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            tween(600, delayMillis = i * 200),
                            RepeatMode.Reverse
                        ), label = "dotScale$i"
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .scale(dotScale)
                            // Blue color for the dots
                            .background(Color(0xFF60a5fa), CircleShape)
                    )
                }
            }
        }
    }
}