package dev.cloudants.iulat.lib.ui.report

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import dev.cloudants.iulat.lib.components.context.base64ToBitmap
import dev.cloudants.iulat.lib.components.upload_image.UploadImageUI
import dev.cloudants.iulat.lib.models.entities.UserDto
import dev.cloudants.iulat.lib.viewmodels.GarbageDisposalViewModel
import dev.cloudants.iulat.lib.viewmodels.ReportViewModel

@Composable
fun EditReport(
    navController: NavController,
    reportTitle: String,
    viewModel: ReportViewModel,
    currentUser: UserDto,
    garbageDisposalViewModel : GarbageDisposalViewModel,
    reportId: String
) {
    val state by viewModel.state.collectAsState()
    var textValue by remember { mutableStateOf("") }
    val garbageState by garbageDisposalViewModel.state.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(reportId) {
        garbageDisposalViewModel.fetchReportById(reportId)
    }

    LaunchedEffect(garbageState.selectedReport) {
        textValue = garbageState.selectedReport?.reportDetails ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.weight(1f))

        Text(
            text = "Edit Report ($reportTitle)",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Start,
            color = Color.Black
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = { Text("Type feedback here", color = Color(0xFF0049AD)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 200.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "Upload a softcopy evidence",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Start,
            color = Color.Black
        )
        UploadImageUI(
            title = "Tap to Upload Evidence",
            existingBase64 = garbageState.selectedReport?.reportImage,
            onImageSelected = { uri -> imageUri = uri }
        )


//        if (state.isDialogVisible) {
//            LoginDialog(
//                title = "Report Submitted",
//                label = "Success",
//                message = "Your report has been successfully submitted.",
//                isLoginSuccessful = true,
//                onConfirm = {
//                    viewModel.onIntent(ReportIntent.DismissDialog)
//                    navController.popBackStack()
//                },
//                onDismiss = {
//                    viewModel.onIntent(ReportIntent.DismissDialog)
//                }
//            )
//        }

        Spacer(Modifier.weight(1f))
//        CustomButton(
//            text = "Update",
//            onClick = {
//                if(reportTitle == MODULE.GARBAGE_DISPOSAL || reportTitle.equals("Garbage Disposal")) {
//                     val base64Image = imageUri?.let { uriToBase64(context, it) }
//                        ?: garbageState.selectedReport?.reportImage
//
//                    val garbage = GarbageDisposalDto(
//                        userId = currentUser.id!!,
//                        reportDetails = textValue,
//                        reportImage = base64Image,
//                        createdById = currentUser.id
//                    )
//                    garbageDisposalViewModel.createGarbageReport(garbage)
//
//                    viewModel.onIntent(ReportIntent.SubmitReport(reportContent = textValue))
//
//                }
//            }
//        )
//        Spacer(Modifier.weight(1f))
    }
}