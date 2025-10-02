package com.nishant.tapnshare

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// NOTE: Assuming color constants (DarkNavyBackground, LightBlueAccent, GreenAccent, PurpleAccent)
// are defined in a separate file (e.g., Color.kt) in this package to avoid conflicts.

@Composable
fun SecureHandshakeScreen(
    onAccept: () -> Unit,
    onReject: () -> Unit,
    otherUserName: String = "Alex Chen" // Default for preview, should be passed in
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground), // Use your consistent background
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1a2b4a)) // Darker blue background for the card
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Top Shield Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PurpleAccent.copy(alpha = 0.2f))
                    .border(2.dp, PurpleAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.shield), // Assuming you have R.drawable.shield
                    contentDescription = "Secure",
                    tint = PurpleAccent,
                    modifier = Modifier.size(48.dp)
                )
            }

            // Title and Description
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Secure Handshake Detected",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$otherUserName wants to share contact securely",
                    color = Color.LightGray.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            // User Avatars and Lock Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Other User Avatar
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(LightBlueAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = otherUserName.take(1).uppercase(), // First letter
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = otherUserName,
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }

                // Middle Lock Icon with Line
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(LightBlueAccent, PurpleAccent, GreenAccent)
                                )
                            )
                    )
                    // Lock Icon on top of the line
                    Box(
                        modifier = Modifier
                            .offset(y = (-10).dp) // Adjust position to sit on the line
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1a2b4a)), // Match card background
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.lock),
                            contentDescription = "Lock",
                            tint = PurpleAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }


                // Your Avatar
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(GreenAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ME",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "You",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }

            // End-to-end Encrypted info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF283c5a)) // Slightly lighter background
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.lock),
                    contentDescription = "Encrypted",
                    tint = PurpleAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "End-to-end encrypted",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Your data remains private",
                        color = Color.LightGray.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                }
            }

            // Action Buttons
            Button(
                onClick = onAccept,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
            ) {
                Text(text = "Accept & Save", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onReject,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF283c5a)) // Match encrypted info background
            ) {
                Text(text = "Reject", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// You'll need to define the color constants if they are not already global:
// val DarkNavyBackground = Color(0xFF001e29)
// val LightBlueAccent = Color(0xFF60a5fa)
// val GreenAccent = Color(0xFF22c55e)
// val PurpleAccent = Color(0xFF8b5cf6)
// val RedAccent = Color(0xFFef4444) // Not used directly in this screen, but good to have