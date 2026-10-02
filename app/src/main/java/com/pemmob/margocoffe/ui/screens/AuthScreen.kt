package com.pemmob.margocoffe.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.margocoffe.R

private val MargoBlue = Color(0xFF084DB5)
private val Ink = Color(0xFF000000)
private val MutedInk = Color(0xFF465264)
private val FieldBorder = Color(0xFF9AA5B5)

@Composable
fun AuthScreen(onAuthenticated: () -> Unit) {
    var showAuthForm by remember { mutableStateOf(false) }
    var isRegistering by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val requiredFieldsFilled = name.isNotBlank() && password.isNotBlank() &&
        (!isRegistering || phoneNumber.isNotBlank())

    if (!showAuthForm) {
        MargoWelcomeScreen(
            onLogin = {
                isRegistering = false
                showAuthForm = true
            },
            onRegister = {
                isRegistering = true
                submitted = false
                showAuthForm = true
            },
            onSkip = onAuthenticated
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MargoBlue)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .height(218.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.logomargo_foreground),
                    contentDescription = "Maskot Margo",
                    modifier = Modifier.size(136.dp)
                )
                Spacer(Modifier.width(2.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "MARGO",
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 1.dp)
                            .width(64.dp)
                            .height(4.dp)
                            .background(Color.White)
                    )
                }
            }
            Text(
                text = "Temukan rasa favoritmu",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 26.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (isRegistering) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                isRegistering = false
                                submitted = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali ke masuk",
                                tint = MargoBlue
                            )
                        }
                        Text(
                            text = "Kembali ke masuk",
                            color = MargoBlue,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                isRegistering = false
                                submitted = false
                            }
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { showAuthForm = false }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali ke halaman awal",
                                tint = MargoBlue
                            )
                        }
                        Text(
                            text = "Kembali",
                            color = MargoBlue,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { showAuthForm = false }
                        )
                    }
                }
                Text(
                    text = if (isRegistering) "Buat akun Margo" else "Selamat datang di Margo!",
                    color = Ink,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = if (isRegistering) "Daftar untuk mulai menikmati Margo."
                    else "Masuk untuk memesan favoritmu.",
                    color = MutedInk,
                    fontSize = 14.sp
                )

                if (isRegistering) {
                    AuthField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = "Nomor telepon",
                        keyboardType = KeyboardType.Phone,
                        isError = submitted && phoneNumber.isBlank()
                    )
                }
                AuthField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nama",
                    isError = submitted && name.isBlank()
                )
                AuthField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Sandi",
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    isError = submitted && password.isBlank()
                )

                if (submitted && !requiredFieldsFilled) {
                    Text(
                        text = "Lengkapi semua data terlebih dahulu.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                Spacer(Modifier.height(2.dp))
                Button(
                    onClick = {
                        submitted = true
                        if (requiredFieldsFilled) onAuthenticated()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MargoBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (isRegistering) "Daftar" else "Masuk",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isRegistering) "Sudah punya akun? " else "Belum punya akun? ",
                        color = MutedInk,
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (isRegistering) "Masuk" else "Daftar",
                        color = MargoBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            isRegistering = !isRegistering
                            submitted = false
                        }
                    )
                }
                Box(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun MargoWelcomeScreen(
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onSkip: () -> Unit
) {
    val productImages = listOf(
        R.drawable.chocomargo,
        R.drawable.chillberry,
        R.drawable.milo
    )
    val pagerState = rememberPagerState(pageCount = { productImages.size })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MargoBlue)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.58f)
                .background(Color.White)
        ) { page ->
            Image(
                painter = painterResource(productImages[page]),
                contentDescription = "Menu minuman Margo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(productImages.size) { page ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(if (pagerState.currentPage == page) 9.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == page) Color.White
                            else Color(0xFF8DB8F2)
                        )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.42f)
                .padding(horizontal = 28.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Semua Favoritmu dalam Satu Aplikasi",
                color = Color.White,
                fontSize = 23.sp,
                lineHeight = 29.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Praktis pesan kopi favoritmu di Margo App!",
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 21.sp
            )
            Spacer(Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.74f)
                    .height(48.dp),
                shape = RoundedCornerShape(50),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier
                            .size(width = 25.dp, height = 17.dp)
                            .clip(RoundedCornerShape(2.dp))
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(Color(0xFFE5253A))
                        )
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(Color.White)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Indonesia",
                        color = Ink,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Ink
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            Button(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MargoBlue
                )
            ) {
                Text("Masuk", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Belum punya akun? ", color = Color.White, fontSize = 14.sp)
                Text(
                    text = "Daftar",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onRegister)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Lewati tahap ini",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onSkip)
            )
        }
    }
}

@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isError: Boolean = false
) {
    val fieldShape = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(fieldShape)
            .background(Color.White)
            .border(
                width = 1.dp,
                color = if (isError) Color(0xFFB3261E) else FieldBorder,
                shape = fieldShape
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = if (isError) Color(0xFFB3261E) else Ink,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(5.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            cursorBrush = SolidColor(MargoBlue),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text = "Masukkan $label",
                            color = MutedInk,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}