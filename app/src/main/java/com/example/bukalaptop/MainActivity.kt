package com.example.bukalaptop

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bukalaptop.pegawai.SignInPegawaiActivity
import com.example.bukalaptop.pelanggan.SignInPelangganActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Top bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(65.dp)
                                .background(Color(0xFFEDEDED)), contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.logs),
                                contentDescription = null,
                                modifier = Modifier.height(60.dp)
                            )
                        }

                        // Konten pegawai + pelanggan
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

//                            Pegawai
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        val pegawaiIntent =
                                            Intent(
                                                this@MainActivity,
                                                SignInPegawaiActivity::class.java
                                            )
                                        startActivity(pegawaiIntent)
                                        overridePendingTransition(
                                            R.anim.slide_in_left,
                                            R.anim.slide_out_right
                                        )
                                    }
                                    .padding(horizontal = 16.dp, vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_person_50),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(148.dp)
                                        .clip(CircleShape)
                                        .background(color = Color(0xFFF03328))
                                        .padding(16.dp)
                                )

                                Text(
                                    text = "Pegawai",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    textAlign = TextAlign.Center,
                                    fontSize = 34.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(300.dp)
                                    .background(Color.Black)
                            )

//                            Pelanggan
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        val pelangganIntent =
                                            Intent(
                                                this@MainActivity,
                                                SignInPelangganActivity::class.java
                                            )
                                        startActivity(pelangganIntent)
                                        overridePendingTransition(
                                            R.anim.slide_in_right,
                                            R.anim.slide_out_left
                                        )
                                    }
                                    .padding(horizontal = 16.dp, vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.baseline_person_50),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(148.dp)
                                        .clip(CircleShape)
                                        .background(color = Color(0xFFFFBB46))
                                        .padding(16.dp)
                                )

                                Text(
                                    text = "Pelanggan",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    textAlign = TextAlign.Center,
                                    fontSize = 34.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}