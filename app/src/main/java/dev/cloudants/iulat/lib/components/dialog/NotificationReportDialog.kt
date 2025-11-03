package dev.cloudants.iulat.lib.components.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun NotificationReportDialog(
    onDismiss : () -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        OutlinedCard(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            border = BorderStroke(1.dp, Color(0xFF136204)),
        ){
            Column(
                modifier = Modifier
                    .height(295.dp)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row( modifier = Modifier.fillMaxWidth() ){
                    Text(
                        text = "\uD83D\uDCE3 BARANGAY OFFICIAL",
                        fontSize = 18.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "\t\t\t Magandang araw! Nais po naming ipabatid na " +
                            "natanggap na namin ang inyong ipinasang report at " +
                            "ito ay mag papatuloy na sa validation.",
                    fontSize = 17.sp,
                    textAlign = TextAlign.Justify,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Black,
                )

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    Column(
                        modifier = Modifier, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Maraming Salamat",
                            fontSize = 17.sp,
                            color = Color.Black,
                        )
                        Text(
                            text = "March 10, 2024",
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }
                }
            }
        }
    }
}