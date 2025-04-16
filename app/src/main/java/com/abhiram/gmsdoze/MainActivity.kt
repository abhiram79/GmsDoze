package com.abhiram.gmsdoze

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.abhiram.gmsdoze.ui.theme.GmsDozeTheme
import java.io.DataOutputStream
import androidx.compose.ui.text.font.FontWeight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GmsDozeTheme {
                var selectedTab by remember { mutableStateOf(0) }

                @OptIn(ExperimentalMaterial3Api::class)
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = { Icon(Icons.Default.List, contentDescription = "Apps") },
                                label = { Text("Packages") }
                            )
                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = { Icon(Icons.Default.Info, contentDescription = "Info") },
                                label = { Text("Info") }
                            )
                        }
                    }
                ) { padding ->
                    Surface(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        if (selectedTab == 0) {
                            GmsDozeUI { packageName, enable ->
                                val command = if (enable) {
                                    "pm enable $packageName"
                                } else {
                                    "pm disable-user --user 0 $packageName"
                                }

                                val success = runRootCommand(command)

                                val message = if (success) {
                                    "$packageName ${if (enable) "enabled" else "disabled"}"
                                } else {
                                    "Failed to ${if (enable) "enable" else "disable"} $packageName"
                                }

                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            InfoScreen()
                        }
                    }
                }
            }
        }
    }

    private fun runRootCommand(command: String): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val outputStream = DataOutputStream(process.outputStream)
            outputStream.writeBytes("$command\n")
            outputStream.writeBytes("exit\n")
            outputStream.flush()
            process.waitFor() == 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun GmsDozeUI(onToggleApp: (String, Boolean) -> Unit) {
    val appList = listOf(
        // Removed GMS core for quick settings tile handling
        "com.google.android.gsf",
        "com.google.android.gsf.login",
        "com.google.android.syncadapters.contacts",
        "com.android.vending"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "GmsDoze",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )
        Spacer(modifier = Modifier.height(24.dp))    
        appList.forEach { packageName ->
            var enabled by remember { mutableStateOf(true) }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = packageName, modifier = Modifier.weight(1f))
                Switch(
                    checked = enabled,
                    onCheckedChange = {
                        enabled = it
                        onToggleApp(packageName, it)
                    }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun InfoScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Text("Description" , style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 16.dp),
    contentAlignment = Alignment.Center
    ) {
    Text(
        text = "GmsDoze Application uses Root Access to Enable/Disable Google Play Services and other related packages. This is an experimental app currently in development. Feel free to contribute to our app.",
        style = MaterialTheme.typography.bodyMedium
         )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Credits", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Developed by abhiram79")
        Text("Version 1.0")
        
        Button(onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/abhiram79/GmsDoze"))
            context.startActivity(intent)
        }) {
            Text("GitHub Repo")
        }
    }
}