package zlayground

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun Dashboard() {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(10.dp)
    ) {
        Text(output, fontFamily = FontFamily.Monospace)

        Button(
            onClick = {
                o(UUID.randomUUID().toString())
            }
        ) {
            Text("Generate UUID")
        }

        Row {
            Button(
                onClick = ::checkFolderExistence
            ) {
                Text("Check folder existence")
            }

            Button(
                onClick = ::createFolder
            ) {
                Text("Create folder")
            }

            Button(
                onClick = ::deleteFolder
            ) {
                Text("Delete folder")
            }
        }

        Row {
            Button(
                onClick = ::checkJsonExistence
            ) {
                Text("Check JSON")
            }

            Button(
                onClick = ::createJson
            ) {
                Text("Create JSON")
            }

            Button(
                onClick = ::deleteJson
            ) {
                Text("Delete JSON")
            }
        }

        /*Row {
            Button(
                onClick = ::checkDirectJsonExistence
            ) {
                Text("Check direct JSON")
            }

            Button(
                onClick = ::createDirectJson
            ) {
                Text("Create direct JSON")
            }

            Button(
                onClick = ::deleteDirectJson
            ) {
                Text("Delete direct JSON")
            }
        }*/

        Row {
            Button(
                onClick = ::saveJsonData
            ) {
                Text("Save to JSON")
            }

            Button(
                onClick = ::readJsonData
            ) {
                Text("Read JSON")
            }

            Button(
                onClick = ::readChangeWriteJson
            ) {
                Text("Read, change, write")
            }
        }

        Row {
            Button(
                onClick = ::readNewJsonData
            ) {
                Text("Read new JSON")
            }
        }

        Row {
            Button(
                onClick = ::testAdam
            ) {
                Text("Test Adam")
            }
        }

        Row {
            Button(
                onClick = ::initDdmlib
            ) {
                Text("Init Ddmlib")
            }

            Button(
                onClick = ::createAdb
            ) {
                Text("Create ADB")
            }

            Button(
                onClick = ::checkInitialDevicesList
            ) {
                Text("Has Initial Devices")
            }

            Button(
                onClick = ::showDevicesList
            ) {
                Text("Show Devices")
            }
        }
    }
}