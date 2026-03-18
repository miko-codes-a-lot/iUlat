package dev.cloudants.iulat.lib.components.date

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogView(
    showDialog: Boolean,
    initialDateMillis: Long,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    if (!showDialog) return
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    onDateSelected(millis)
                }
                onDismiss()
            }) {
                Text("OK", color = Color(0xFF0049AD))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = Color.White
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                dividerColor = Color(0xFF0049AD),
                titleContentColor = Color(0xFF0049AD),
                headlineContentColor = Color(0xFF0049AD),
                selectedDayContainerColor = Color(0xFF0049AD),
                selectedDayContentColor = Color.White,
                todayDateBorderColor = Color(0xFF0049AD),
                todayContentColor = Color(0xFF0049AD)
            )
        )
    }
}