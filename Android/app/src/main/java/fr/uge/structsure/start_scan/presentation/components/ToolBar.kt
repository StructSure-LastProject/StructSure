import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.start_scan.presentation.components.Variables

@Composable
fun ToolBar(
    onPlayClick: () -> Unit,
    onSyncClick: () -> Unit,
    onContentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .width(77.dp)
                .height(58.dp)
                .background(color = Variables.White, shape = RoundedCornerShape(size = 50.dp))
                .padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 15.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .width(47.dp)
                    .height(28.dp)
                    .padding(start = 2.dp, top = 2.dp, bottom = 2.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.undo),
                    contentDescription = "image description",
                    contentScale = ContentScale.None,
                    modifier = Modifier
                        .padding(1.dp)
                        .width(28.dp)
                        .height(22.dp)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(72.dp)
                .height(72.dp)
                .background(color = Variables.Black, shape = RoundedCornerShape(size = 100.dp))
                .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.play),
                contentDescription = "image description",
                contentScale = ContentScale.None,
                modifier = Modifier
                    .padding(1.dp)
                    .width(32.dp)
                    .height(32.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(75.dp)
                .height(58.dp)
                .background(color = Variables.White, shape = RoundedCornerShape(size = 50.dp))
                .padding(start = 24.dp, top = 15.dp, end = 23.dp, bottom = 15.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.notepad_text),
                contentDescription = "image description",
                contentScale = ContentScale.None,
                modifier = Modifier
                    .padding(1.dp)
                    .width(28.dp)
                    .height(28.dp)
            )
        }


    }
}
