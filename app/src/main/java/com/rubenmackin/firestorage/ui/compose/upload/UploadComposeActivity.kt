package com.rubenmackin.firestorage.ui.compose.upload

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.compose.AsyncImage
import com.rubenmackin.firestorage.R
import com.rubenmackin.firestorage.databinding.ActivityUploadComposeBinding
import com.rubenmackin.firestorage.ui.compose.list.ListComposeActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@AndroidEntryPoint
class UploadComposeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadComposeBinding

    companion object {
        fun create(context: Context) = Intent(context, UploadComposeActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUploadComposeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.composeView.setContent {
            UploadScreen()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @Composable
    fun UploadScreen() {

        val uploadViewModel: UploadComposeViewModel by viewModels()
        var uri: Uri? by remember { mutableStateOf(null) }
        var showImageDialog: Boolean by remember {
            mutableStateOf(false)
        }
        var resultUri: Uri? by remember { mutableStateOf(null) }
        val loading by uploadViewModel.isLoading.collectAsState()
        var userTitle: String by remember {
            mutableStateOf("")
        }

        val focusRequester = remember {
            FocusRequester()
        }
        val focusManager = LocalFocusManager.current

        val intentCameraLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                if (it && uri?.path?.isNotEmpty() == true) {
                    uploadViewModel.uploadAndGetImage(uri!!) { newUri ->
                        userTitle = ""
                        focusManager.clearFocus()
                        resultUri = newUri
                    }
                }
            }

        val intentGalleyLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
                if (it?.path?.isNotEmpty() == true) {
                    uploadViewModel.uploadAndGetImage(it) { newUri ->
                        resultUri = newUri
                    }
                }
            }

        if (showImageDialog) {
            Dialog(onDismissRequest = { showImageDialog = false }) {
                Card(
                    shape = RoundedCornerShape(12),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(Modifier.padding(24.dp)) {
                        OutlinedButton(
                            onClick = {
                                uri = generateUri(userTitle)
                                intentCameraLauncher.launch(uri!!)
                                showImageDialog = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .align(Alignment.CenterHorizontally),
                            border = BorderStroke(2.dp, color = colorResource(id = R.color.green)),
                            shape = RoundedCornerShape(42)
                        ) {
                            Text(text = "Camera", color = colorResource(id = R.color.green))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                uri = generateUri(userTitle)
                                intentGalleyLauncher.launch("image/*")
                                showImageDialog = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .align(Alignment.CenterHorizontally),
                            border = BorderStroke(2.dp, color = colorResource(id = R.color.green)),
                            shape = RoundedCornerShape(42)
                        ) {
                            Text(text = "From gallery", color = colorResource(id = R.color.green))
                        }
                    }
                }
            }
        }

        Column(Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(36.dp))
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                shape = RoundedCornerShape(12),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 36.dp)
            ) {
                if (resultUri != null) {
                    AsyncImage(
                        model = resultUri,
                        contentDescription = "image selected by user",
                        contentScale = ContentScale.Crop
                    )
                }

                if (loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(50.dp), color = colorResource(
                                id = R.color.green
                            )
                        )
                    }
                }

                if (!loading && resultUri == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_upload_image),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = colorResource(id = R.color.green)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            TextField(
                value = userTitle, onValueChange = { userTitle = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .border(
                        2.dp, color = colorResource(id = R.color.green),
                        RoundedCornerShape(22)
                    )
                    .focusRequester(focusRequester),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                FloatingActionButton(
                    onClick = {
                        //uri = generateUri()
                        //intentCameraLauncher.launch(uri!!)
                        showImageDialog = true
                    },
                    contentColor = colorResource(id = R.color.green),
                    containerColor = colorResource(id = R.color.green),
                    shape = FloatingActionButtonDefaults.largeShape
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.weight(2f))

            val context = LocalContext.current
            OutlinedButton(
                onClick = { startActivity(ListComposeActivity.create(context)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(36.dp)
                    .align(Alignment.CenterHorizontally),
                border = BorderStroke(2.dp, color = colorResource(id = R.color.green)),
                shape = RoundedCornerShape(42)
            ) {
                Text(text = "Navigate to list", color = colorResource(id = R.color.green))
            }

        }

    }

    private fun generateUri(userTitle: String): Uri {
        return FileProvider.getUriForFile(
            Objects.requireNonNull(this),
            "com.rubenmackin.firestorage.provider",
            createFile(userTitle)
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun createFile(userTitle: String): File {
        val name: String =
            userTitle.ifEmpty { SimpleDateFormat("yyyyMMdd_hhmmss").format(Date()) + "image" }
        return File.createTempFile(name, ".jpg", externalCacheDir)
    }
}