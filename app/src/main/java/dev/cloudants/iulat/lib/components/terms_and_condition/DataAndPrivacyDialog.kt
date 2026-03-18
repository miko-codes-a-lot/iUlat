package dev.cloudants.iulat.lib.components.terms_and_condition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun DataAndPrivacyDialog(
    onAgree: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFF2F2F2)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.5f))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Data & Privacy",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = buildAnnotatedString {
                        append("We collect data and personal information that include, but are not limited to, ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("names, locations, contact details, report details, and uploaded media (photos, videos, or documents)")
                        }
                        append(" to facilitate community reporting and improve safety initiatives. This data is used to verify reports, enhance response efficiency, and support public awareness. Your data will only be used within the community reporting system to ensure accurate documentation and appropriate response. We are committed to maintaining confidentiality and protecting your information in compliance with data privacy regulations.")
                    },
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "To proceed, you must agree to our policies",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = buildAnnotatedString {
                        append("By continuing, you agree to the ")
                        withStyle(style = SpanStyle(color = Color(0xFF0049AD), fontWeight = FontWeight.Bold)) {
                            append("Terms & Conditions")
                        }
                        append(" of our community reporting system. You also acknowledge and understand our ")
                        withStyle(style = SpanStyle(color = Color(0xFF0049AD), fontWeight = FontWeight.Bold)) {
                            append("Privacy Policy")
                        }
                        append(", including how your data is used to improve safety and reporting efficiency.")
                    },
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onAgree,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0049AD)),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Agree and Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = Color(0xFF0049AD), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}