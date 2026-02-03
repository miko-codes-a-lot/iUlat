package dev.cloudants.iulat.lib.components.UploadVideoUI

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.cloudants.iulat.R

@Composable
fun UploadVideoUI(
    title: String,
    onVideoSelected: (Uri?) -> Unit,
    selectedUri: Uri? = null
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> onVideoSelected(uri) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .clickable { launcher.launch("video/*") },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.icon_upload),
                contentDescription = null,
                tint = Color(0xFF0049AD),
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = if (selectedUri != null) "Video Selected ✓" else title,
                fontSize = 14.sp,
                color = if (selectedUri != null) Color(0xFF4CAF50) else Color.Gray
            )
        }
    }
}
