import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.uge.structsure.ui.components.HeaderView
import fr.uge.structsure.ui.components.StructureSummaryView
import fr.uge.structsure.ui.theme.StructSureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StructSureTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        HeaderView()
                        StructureSummaryView()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderViewPreview() {
    StructSureTheme {
        HeaderView()
    }
}

@Preview(showBackground = true)
@Composable
fun StructureSummaryViewPreview() {
    StructSureTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            HeaderView()
            StructureSummaryView()
        }
    }
}