package com.nishant.tapnshare

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

// ADD THESE IMPORTS FOR LOTTIE ANIMATION
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

// Define your custom colors here if not already defined in Theme.kt
val DarkNavyBackground = Color(0xFF001e29) // Used for the general background 0xFF000814
val LightBlueAccent = Color(0xFF60a5fa) // Used for blue elements and text
val GreenAccent = Color(0xFF22c55e) // Used for green elements and text
val PurpleAccent = Color(0xFF8b5cf6) // Used for purple elements
val RedAccent = Color(0xFFef4444) // Used for red elements

// 1. UPDATED Data class: Added titleBottomPadding for spacing control
data class OnboardingPageData(
    val title: String,
    val highlightedText: String,
    val description: String,
    val imageContent: @Composable () -> Unit,
    val titleSize: androidx.compose.ui.unit.TextUnit,
    val descriptionSize: androidx.compose.ui.unit.TextUnit,
    val titleBottomPadding: Dp // New field for spacing
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    // 2. UPDATED pages list: Custom sizes and spacing for each screen
    val pages = listOf(
        OnboardingPageData(
            title = "Exchange contacts instantly\n\n",
            highlightedText = "by bringing two phones together\n",
            description = "No typing, no scanning. Just bring your phones close and TapNShare does the rest.",
            imageContent = {
                LottieMobileShareAnimation()
            },
            // Screen 1: Exchange contacts
            titleSize = 24.sp,
            descriptionSize = 16.sp,
            titleBottomPadding = 8.dp // Small gap for tight description
        ),
        OnboardingPageData(
            title = "Your contact is always\n\n",
            highlightedText = "encrypted and secure",
            description = "Advanced encryption ensures your personal information stays private and protected.",
            imageContent = {
                ShieldAnimation()
            },
            // Screen 2: Encrypted and secure
            titleSize = 24.sp,
            descriptionSize = 17.sp,
            titleBottomPadding = 16.dp // Standard gap
        ),
        OnboardingPageData(
            title = "Control who gets saved\n\n",
            highlightedText = "with Accept or Reject",
            description = "You're always in control. Preview contacts before saving and choose who gets added to your phone.",
            imageContent = {
                AcceptRejectAnimation()
            },
            // Screen 3: Accept or Reject
            titleSize = 26.sp,
            descriptionSize = 15.sp,
            titleBottomPadding = 12.dp // Medium gap
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(24.dp)) // Top padding

            // Top bar (TapNShare, Pager Indicators, Arrows)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            if (pagerState.currentPage > 0) {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    },
                    enabled = pagerState.currentPage > 0
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous",
                        tint = if (pagerState.currentPage > 0) Color.White else Color.Gray
                    )
                }

                Text(
                    text = "TapNShare",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            if (pagerState.currentPage < pages.size - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                onGetStarted()
                            }
                        }
                    },
                    enabled = pagerState.currentPage < pages.size
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next",
                        tint = if (pagerState.currentPage < pages.size - 1) Color.White else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(1.dp))

            // Pager Indicators
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp),
                activeColor = LightBlueAccent,
                inactiveColor = GreenAccent.copy(alpha = 0.3f),
                indicatorWidth = 8.dp,
                indicatorHeight = 8.dp,
                spacing = 8.dp,
                indicatorShape = CircleShape
            )

            HorizontalPager(
                count = pages.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                OnboardingSlide(pageData = pages[page])
            }

            // Continue / Get Started Button and Conditional Text
            val isLastPage = pagerState.currentPage == pages.size - 1

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (isLastPage) {
                            onGetStarted()
                        } else {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(LightBlueAccent, GreenAccent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isLastPage) "Get Started" else "Continue",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (isLastPage) {
                Text(
                    text = "Ready to start secure contact exchanges",
                    color = Color.LightGray.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// 3. UPDATED Individual slide layout: Uses dynamic sizes and padding
@Composable
fun OnboardingSlide(pageData: OnboardingPageData) {
    // Define the brush for the highlighted text, matching the button gradient
    val highlightBrush = remember {
        Brush.linearGradient(
            colors = listOf(LightBlueAccent, GreenAccent)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Image/Animation Content
        Box(modifier = Modifier
            .size(200.dp)
            .padding(bottom = 32.dp), contentAlignment = Alignment.Center) {
            pageData.imageContent()
        }

        // Title and Highlighted Text
        Text(
            text = buildAnnotatedString {
                // Apply white color and the specific title size
                withStyle(style = SpanStyle(color = Color.White, fontSize = pageData.titleSize)) {
                    append(pageData.title)
                }
                // Apply gradient brush and the specific title size
                withStyle(style = SpanStyle(brush = highlightBrush, fontSize = pageData.titleSize)) {
                    append(pageData.highlightedText)
                }
            },
            // Set the overall text size
            fontSize = pageData.titleSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            // USE CUSTOM PADDING
            modifier = Modifier.padding(bottom = pageData.titleBottomPadding)
        )

        // Description
        Text(
            text = pageData.description,
            // Use dynamic description size
            fontSize = pageData.descriptionSize,
            color = Color.LightGray.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
    }
}

// --- Specific Onboarding Image/Animation Composables (remain unchanged) ---

@Composable
fun LottieMobileShareAnimation() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.contact)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier
            .size(900.dp)
    )
}

@Composable
fun ShieldAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "shieldTransition")

    val shieldScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "shieldScale"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(pulseScale)
            .clip(RoundedCornerShape(28.dp))
            .background(GreenAccent.copy(alpha = 0.2f))
            .border(2.dp, GreenAccent, RoundedCornerShape(28.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.shield),
            contentDescription = "Shield",
            tint = GreenAccent,
            modifier = Modifier
                .size(80.dp)
                .scale(shieldScale)
        )
    }
}

@Composable
fun AcceptRejectAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "acceptRejectTransition")

    val acceptScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing, delayMillis = 0),
            repeatMode = RepeatMode.Reverse
        ), label = "acceptScale"
    )

    val rejectScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing, delayMillis = 400),
            repeatMode = RepeatMode.Reverse
        ), label = "rejectScale"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(acceptScale)
                        .clip(RoundedCornerShape(20.dp))
                        .background(GreenAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.check),
                        contentDescription = "Accept",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Accept", color = GreenAccent, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(rejectScale)
                        .clip(RoundedCornerShape(20.dp))
                        .background(RedAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.close),
                        contentDescription = "Reject",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Reject", color = RedAccent, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}