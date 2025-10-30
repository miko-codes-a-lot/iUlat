package dev.cloudants.iulat.lib.components.button

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CustomButtonPreview() {
    CustomButton(
        text = "Click Me",
        onClick = {},
        width = 300f,
        height = 70f,
        textSize = 18f,
        backgroundColor = Color(0xFF0049AD),
        textColor = Color.White
    )
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    width: Float = 400f,
    height: Float = 60f,
    textColor: Color = Color.White,
    backgroundColor: Color = Color(0xFF0049AD),
    textSize: Float = 16f,
    cornerRadius: Float = 8f
) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .width(width.dp)
            .height(height.dp),
        shape = RoundedCornerShape(cornerRadius.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp
        )
    ) {
        Text(
            text = text,
            fontSize = textSize.sp,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
        )
    }
}
