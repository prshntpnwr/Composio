package io.prashantpanwar.composio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.prashantpanwar.composio.blob.BlobEffect
import io.prashantpanwar.composio.blob.BlobShader
import io.prashantpanwar.composio.blob.BlobShape
import io.prashantpanwar.composio.blob.BlobStyle
import io.prashantpanwar.composio.blob.MorphingBlob
import io.prashantpanwar.composio.ui.theme.ComposioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposioTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MorphingBlob(
                        modifier = Modifier
                            .size(260.dp)
                            .padding(innerPadding),
                        morphPoints = 6,
                        blobStyle = BlobStyle(
                            effect = BlobEffect(blurRadius = 1f, alpha = 0.5f),
                            shader = BlobShader.Radial(
                                colors = listOf(
                                    Color(0xFFF97272), Color(
                                        0xFF673AB7
                                    )
                                )
                            ),
                            shape = BlobShape.Fill
                        )
                    )
                }
            }
        }
    }
}
