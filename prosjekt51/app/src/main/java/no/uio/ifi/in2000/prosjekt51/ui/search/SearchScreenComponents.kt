package no.uio.ifi.in2000.prosjekt51.ui.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.prosjekt51.MAX_HEIGHT


@Composable
fun CoordinateSymbol(coordText: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .height(52.dp)
            .aspectRatio(1f),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = coordText)
    }
}


@Composable
fun CoordinateInput(coord: String, onValueChange: (String) -> Unit, validateFunc: (String) -> Boolean) {
    OutlinedTextField(
        value = coord,
        onValueChange = onValueChange,
        label = { Text("Degrees") },
        singleLine = true,
        modifier = Modifier,
        isError = !validateFunc(coord) && coord.isNotEmpty()

    )
}

@Composable
fun HeightInput(height: Int, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = if (height == MAX_HEIGHT) "" else if (height.toString().length > 7) height.toString().take(7) else "$height",
        onValueChange = onValueChange,
        label = { Text(text = "[meter]") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 2.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
    )
}