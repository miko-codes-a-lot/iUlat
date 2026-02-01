package dev.cloudants.iulat.lib.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MonthFilterCard(
    selectedMonth: String?,
    onMonthSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance()
    val maxDisplayItems = 6
    val itemHeight = 48.dp
    val maxHeight = itemHeight * maxDisplayItems
    val months = remember {
        mutableListOf<String>().apply {
            repeat(12) {
                add(SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time))
                calendar.add(Calendar.MONTH, -1)
            }
        }
    }


    Box(
        modifier = modifier
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text(
                text = selectedMonth ?: "All Months",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0049AD),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    expanded = true
                }
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF0049AD)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .heightIn(max = maxHeight)
                .background(color = Color.White),
        ) {
            DropdownMenuItem(

                text = {
                    Text(
                        text = "All Months",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.Black
                    )
                },
                onClick = {
                    onMonthSelected(null)
                    expanded = false
                }
            )
            months.forEach { month ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = month,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                    },
                    onClick = {
                        onMonthSelected(month)
                        expanded = false
                    }
                )
            }
        }
    }
}