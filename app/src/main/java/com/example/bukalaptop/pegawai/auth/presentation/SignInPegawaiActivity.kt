package com.example.bukalaptop.pegawai.auth.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.PegawaiActivity
import com.example.bukalaptop.ui.components.BukalaptopTopBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInPegawaiActivity : AppCompatActivity() {

    private val viewModel: SignInPegawaiViewModel by viewModels()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                SignInPegawaiRoute(viewModel)
            }
        }
    }

    public override fun onStart() {
        super.onStart()

        viewModel.checkCurrentUser()
    }

    override fun onResume() {
        super.onResume()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

@Composable
fun SignInPegawaiRoute(viewModel: SignInPegawaiViewModel) {

    val signInState by viewModel.signInState.collectAsStateWithLifecycle()

    val passwordState = rememberTextFieldState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    if (signInState is SignInState.Loading) {
        ProgressDialog(showDialog = true)
    }

    LaunchedEffect(passwordState.text) {
        viewModel.onPasswordChange(passwordState.text.toString())
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.signInState.collect { state ->
                when (state) {
                    SignInState.Success -> {
                        context.startActivity(
                            Intent(
                                context,
                                PegawaiActivity::class.java
                            )
                        )
                        (context as? ComponentActivity)?.finish()
                    }

                    is SignInState.Error -> {
                        Toast.makeText(
                            context,
                            state.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    SignInState.NotPegawai -> {
                        Toast.makeText(
                            context,
                            (R.string.anda_belum_mempunyai_akun_sebagai_pegawai),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    SignInState.Idle -> Unit
                    else -> {}
                }
            }
        }
    }

    SignInPegawaiScreen(
        email = viewModel.email,
        passwordState = passwordState,
        isEmailError = viewModel.email.isNotEmpty() && !viewModel.isEmailValid,
        isPasswordError = viewModel.password.isNotEmpty() && !viewModel.isPasswordValid,
        onEmailChange = viewModel::onEmailChange,
        onSignInClick = {
            viewModel.signIn(viewModel.email, viewModel.password)
        }
    )
}

@Composable
fun SignInPegawaiScreen(
    email: String,
    passwordState: TextFieldState,
    isEmailError: Boolean,
    isPasswordError: Boolean,
    onEmailChange: (String) -> Unit,
    onSignInClick: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            BukalaptopTopBar()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.pegawai),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 34.sp
                )

                // email
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    label = { Text(text = stringResource(R.string.email)) },
                    isError = isEmailError,
                    supportingText = {
                        if (isEmailError) {
                            Text(text = stringResource(R.string.email_tidak_valid))
                        }
                    },
                    singleLine = true
                )

                // password
                OutlinedSecureTextField(
                    state = passwordState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    label = { Text(text = stringResource(R.string.password)) },
                    isError = isPasswordError,
                    supportingText = {
                        if (isPasswordError) {
                            Text(text = stringResource(R.string.password_minimal_harus_6_karakter))
                        }
                    }
                )

                Button(
                    modifier = Modifier.padding(top = 24.dp),
                    onClick = onSignInClick,
                    enabled = !isEmailError && !isPasswordError && email.isNotBlank() && passwordState.text.isNotBlank()
                ) {
                    Text(text = stringResource(R.string.sign_in))
                }
            }
        }
    }
}

@Composable
fun ProgressDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit = {}
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(45.dp),
                )

                Text(
                    text = stringResource(R.string.signing_in),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInPegawaiPreview() {
    MaterialTheme {
        SignInPegawaiScreen(
            email = "",
            passwordState = rememberTextFieldState(),
            isEmailError = false,
            isPasswordError = false,
            onEmailChange = {},
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
fun SignInPegawaiErrorPreview() {
    MaterialTheme {
        SignInPegawaiScreen(
            email = "abc",
            passwordState = rememberTextFieldState(),
            isEmailError = true,
            isPasswordError = true,
            onEmailChange = {},
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressDialogPreview() {
    ProgressDialog(showDialog = true)
}