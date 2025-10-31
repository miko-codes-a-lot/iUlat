package dev.cloudants.iulat.lib.ui.message

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.cloudants.iulat.R

@Composable
fun Message(navController: NavController) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val coroutineScope = rememberCoroutineScope()

        val messages = remember {
            mutableStateListOf(
                MessageItem(sender = "User", content = "Hello!"),
                MessageItem(sender = "Admin", content = "Hi, how can I help?"),
                MessageItem(sender = "User", content = "I need assistance with the app.")
            )
        }
        LazyColumn(
            modifier = Modifier
                .background(Color.White)
                .padding(bottom = 50.dp)
                .fillMaxSize(),
            reverseLayout = true,
            verticalArrangement = Arrangement.Bottom
        ) {
            items(messages.size) { index ->
                MessageView(message = messages[index])
            }
        }

        MessageInputField { newMessage ->
            messages.add(0, MessageItem(sender = "User", content = newMessage))
        }
    }
}

@Composable
fun MessageView(message: MessageItem) {
    val (containerColor, contentColor) =
        if (message.sender == "User") {
            Color(0xFF0049AD) to Color(0xFFFFFFFF)
        } else {
            Color(0xFFcccccf) to Color.Black
        }

    val horizontalArrangement =
        if (message.sender == "User") Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .padding(0.dp)
            .fillMaxWidth(),
        horizontalArrangement = horizontalArrangement
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(top = 10.dp, bottom = 10.dp),
            shape = RoundedCornerShape(12.dp),
            color = containerColor,
            contentColor = contentColor
        ) {
            Column(modifier = Modifier
                .padding(12.dp)
            ) {
                Text(
                    text = "",
                    maxLines = Int.MAX_VALUE,
                    overflow = TextOverflow.Visible,
                    fontFamily = FontFamily.SansSerif,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "",
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    textAlign = TextAlign.End,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.38f)
                )
            }
        }
    }
}

@Composable
fun MessageInputField(onSend: (message: String) -> Unit) {
    val messageText = remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF0049AD), RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_upload),
            contentDescription = "Upload Image",
            modifier = Modifier
                .padding(8.dp)
                .size(25.dp)
                .clickable {
                    Log.d("MessageInputField", "Upload icon clicked")
                },
            tint = Color(0xFF0049AD)
        )

        TextField(
            value = messageText.value,
            onValueChange = { messageText.value = it },
            placeholder = { Text("Type your message...", color = Color.Black) },
            modifier = Modifier
                .padding(8.dp)
                .weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color(0xFF0049AD),
                unfocusedBorderColor = Color(0xFF0049AD)
            )
        )

        Icon(
            painter = painterResource(id = R.drawable.icon_send),
            contentDescription = "Send",
            modifier = Modifier
                .padding(8.dp)
                .size(25.dp)
                .clickable {
                    onSend(messageText.value)
                    messageText.value = ""
                },
            tint = Color(0xFF0049AD)
        )
        Spacer(modifier = Modifier.padding(end = 10.dp))
    }
}

data class MessageItem(val sender: String, val content: String)
