package dev.cloudants.iulat.lib.components.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.cloudants.iulat.R
import androidx.compose.ui.unit.sp
import dev.cloudants.iulat.lib.components.button.CustomButton


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CustomLoginDialogPreview() {
    LoginDialog(
        title = "LOGIN",
        label = "Successfull Login",
        message = "asd",
        onDismiss = {},
        onConfirm = {},
        isLoginSuccessful = true
    )
}

@Composable
fun LoginDialog(
    title: String? = null,
    label: String? = null,
    message: String? = null,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isLoginSuccessful: Boolean

) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .wrapContentHeight()
    ) {

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 50.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(top = 40.dp))

                if (label != null) {
                    Text(
                        text = label,
                        fontSize = 18.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                if (message != null) {
                    Text(
                        text = message,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                CustomButton(
                    text = "OK",
                    onClick = {
                        if (isLoginSuccessful) {
                            onConfirm()
                        } else {
                            onDismiss()
                        }
                    },
                    width = 200f,
                    height = 50f,
                    backgroundColor = Color(0xFF0049AD),
                    textColor = Color.White
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFF2568ef))
        ) {
            Image(
                painter = painterResource(id = if (isLoginSuccessful) R.drawable.checklist else R.drawable.cross),
                contentDescription = "Login Status Icon",
                modifier = Modifier.size(100.dp)
            )
        }
    }
}
