package no.uio.ifi.in2000.prosjekt51.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.prosjekt51.ui.LabeledDivider

@ExperimentalMaterial3Api
@Preview
@Composable
fun SearchScreen() {

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(text = "Search") }) }
    ) {innerPadding ->
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {

                LabeledDivider(label = "Coordinates")


                Row(
                    modifier = Modifier
                        .sizeIn(maxHeight = 60.dp)
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 2.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { /*TODO*/ },
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .aspectRatio(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "N")
                    }
                    OutlinedTextField(
                        value = "48",
                        onValueChange = {},
                        label = { Text("Grader") },
                        singleLine = true,
                        modifier = Modifier
                    )
                }

                Row(
                    modifier = Modifier
                        .sizeIn(maxHeight = 60.dp)
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 2.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { /*TODO*/ },
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .aspectRatio(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "E")
                    }
                    OutlinedTextField(
                        value = "48",
                        onValueChange = {},
                        label = { Text("Grader") },
                        singleLine = true
                    )
                }

                LabeledDivider(label = "Time")

                ExposedDropdownMenuBox(expanded = false, onExpandedChange = {}, modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 2.dp)) {
                    TextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true,
                        value = "13:00",
                        onValueChange = {},
                        label = { Text(text = "Launch time") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )

                    ExposedDropdownMenu(
                        modifier = Modifier.fillMaxWidth(),
                        expanded = false,
                        onDismissRequest = { },
                    ) {

                        DropdownMenuItem(
                            text = { Text("13:00") },
                            onClick = { },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )

                    }
                }

                LabeledDivider(label = "Date")

                ExposedDropdownMenuBox(expanded = false, onExpandedChange = {}, modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 2.dp)) {
                    TextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true,
                        value = "25.04.2024",
                        onValueChange = {},
                        label = { Text(text = "Launch time") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )

                    ExposedDropdownMenu(
                        modifier = Modifier.fillMaxWidth(),
                        expanded = false,
                        onDismissRequest = { },
                    ) {

                        DropdownMenuItem(
                            text = { Text("26.04.2024") },
                            onClick = { },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )

                    }
                }

                LabeledDivider(label = "Expected height")

                OutlinedTextField(
                    value = "3000",
                    onValueChange = {},
                    label = { Text(text = "[meter]") },
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 2.dp)
                )
            }
            Column(
                modifier = Modifier.padding(4.dp)
            ) {
                OutlinedButton(
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .height(52.dp)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(text = "More options")
                }
                Button(onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .height(52.dp)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp),) {
                    Text(text = "Search")
                }

            }

        }
    }


}


