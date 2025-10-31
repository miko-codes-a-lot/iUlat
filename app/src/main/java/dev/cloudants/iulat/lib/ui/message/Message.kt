package dev.cloudants.iulat.lib.ui.message

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.cloudants.iulat.R
import dev.cloudants.iulat.lib.ui.message.model.MessageModel

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MessagePrev() {
    Message(navController = rememberNavController())
}

@Composable
fun Message(navController: NavController) {
    val messages = remember {
        mutableStateListOf(
            MessageModel(senderId = "User", content = "Hello!"),
            MessageModel(senderId = "Admin", content = "Hi, how can I help?"),
            MessageModel(senderId = "User", content = "I need assistance with the app.")
        )
    }

    fun onSend(newMessage: String) {
        if (newMessage.isNotBlank()) {
            messages.add(MessageModel(senderId = "User", content = newMessage))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        MessageContainer(
            messageList = messages,
            onSend = ::onSend
        )
    }
}

@Composable
fun MessageContainer(
    messageList: List<MessageModel>,
    onSend: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(bottom = 40.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .background(Color.White)
                .weight(1f)
                .fillMaxSize(),
            reverseLayout = true,
            verticalArrangement = Arrangement.Bottom
        ) {
            items(messageList.size) { index ->
                val message = messageList[messageList.size - 1 - index]
                MessageView(message = message)
            }
        }
        MessageInputField(onSend = onSend)
    }
}

@Composable
fun MessageView(message: MessageModel) {
    val (containerColor, contentColor) =
        if (message.senderId == "User") {
            Color(0xFF0049AD) to Color(0xFFFFFFFF)
        } else {
            Color(0xFFcccccf) to Color.Black
        }

    val horizontalArrangement =
        if (message.senderId == "User") Arrangement.End else Arrangement.Start

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
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.content,
                    maxLines = Int.MAX_VALUE,
                    overflow = TextOverflow.Visible,
                    fontFamily = FontFamily.SansSerif,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message.senderId,
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
