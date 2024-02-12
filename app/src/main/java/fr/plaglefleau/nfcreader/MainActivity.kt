package fr.plaglefleau.nfcreader

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.plaglefleau.nfcreader.ui.theme.NfcReaderTheme


class MainActivity : ComponentActivity() {

    private var tag:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(NfcAdapter.getDefaultAdapter(this) == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    //new android stuff
    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        NfcReaderTheme {
            Greeting("Android")
        }
    }

    override fun onResume() {
        super.onResume()
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )
        val intentFilters = arrayOf<IntentFilter>(
            IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        )
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null)
    }

    override fun onPause() {
        super.onPause()
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }
            tag?.id?.let {
                val tagValue = it.toHexString()
                Toast.makeText(this, "NFC tag detected: $tagValue", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ByteArray.toHexString(): String {
        val hexChars = "0123456789ABCDEF"
        val result = StringBuilder(size * 2)

        map { byte ->
            val value = byte.toInt()
            val hexChar1 = hexChars[value shr 4 and 0x0F]
            val hexChar2 = hexChars[value and 0x0F]
            result.append(hexChar1)
            result.append(hexChar2)
        }

        return result.toString()
    }
}