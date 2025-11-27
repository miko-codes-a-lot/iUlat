package dev.cloudants.iulat.lib.ui.message

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.cloudants.iulat.R
import dev.cloudants.iulat.lib.components.context.MODULE
import dev.cloudants.iulat.lib.ui.message.model.MessageDto
import dev.cloudants.iulat.lib.viewmodels.ChatViewModel
import dev.cloudants.iulat.lib.viewmodels.RobberiesViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.toString

@Composable
fun ChatDirect(
    messages: List<MessageDto>,
    currentUserId: String,
    onSendMessage: suspend (String) -> Unit
) {
    var messageContent by remember { mutableStateOf(TextFieldValue("")) }
    val isSendingMessage = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
//    val listState = rememberLazyListState()
//    LaunchedEffect(messages.size) {
//        if (messages.isNotEmpty()) {
//            listState.animateScrollToItem(messages.size - 1)
//        }
//    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(top = 50.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f),
            reverseLayout = true,
//            state = listState,
        ) {
            items(messages) { message ->
                MessageBubble(
                    message = message,
                    isSentByCurrentUser = message.senderId == currentUserId
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFF0049AD), RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageContent,
                onValueChange = { messageContent = it },
                placeholder = {
                    Text(
                        "Type your message...",
                        color = Color.Black,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 16.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color(0xFF0049AD),
                    unfocusedBorderColor = Color(0xFF0049AD)
                ),
                maxLines = 2
            )
            Icon(
                painter = painterResource(id = R.drawable.icon_send),
                contentDescription = "Send",
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        if (isSendingMessage.value) return@clickable
                        isSendingMessage.value = true
                        if (messageContent.text.isNotBlank()) {
                            scope.launch {
                                onSendMessage(messageContent.text)
                                messageContent = TextFieldValue("")
                                isSendingMessage.value = false
                            }
                        }
                    },
                tint = Color(0xFF0049AD)
            )
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun MessageBubble(message: MessageDto, isSentByCurrentUser: Boolean) {
    val alignment = if (isSentByCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    if (isSentByCurrentUser) {
        PaddingValues(start = 64.dp, end = 8.dp)
    }else {
        PaddingValues(start = 8.dp, end = 64.dp)
    }
    val (containerColor, contentColor) = if (isSentByCurrentUser) {
        Color(0xFF0049AD) to Color.White
    } else {
        Color(0xFFE6E6E6) to Color.Black
    }
    val displayFormat = remember { SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault()) }
    val formattedDate = try {
        val parsedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(message.createdAt.toString())
        parsedDate?.let { displayFormat.format(it) } ?: "Invalid Date"
    } catch (e: Exception) {
        "Invalid Date"
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        contentAlignment = alignment
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.75f),
            shape = RoundedCornerShape(12.dp),
            color = containerColor,
            contentColor = contentColor
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = message.content,
                    maxLines = Int.MAX_VALUE,
                    overflow = TextOverflow.Visible,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = formattedDate,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.38f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
