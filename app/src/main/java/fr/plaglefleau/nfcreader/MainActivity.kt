package fr.plaglefleau.nfcreader

import android.R.attr.tag
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.plaglefleau.nfcreader.ui.theme.NfcReaderTheme


class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var tag:String = ""
    private val intentFiltersArray: Array<IntentFilter>? = null
    private val techListsArray: Array<Array<String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NfcReaderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // Check if NFC is available on the device
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not supported on this device", Toast.LENGTH_SHORT).show()
            return
        }

        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE
        )

    }

    override fun onResume() {
        super.onResume()
        //enabling the nfc when the app resume
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
    }

    override fun onPause() {
        super.onPause()
        //disabling the nfc when the app is in pause and if the phone has the nfc
        if(nfcAdapter != null) {
            nfcAdapter?.disableForegroundDispatch(this)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        if (intent != null) {
            tag = getTag(intent)
            if(tag != "") {
                Toast.makeText(this, tag, Toast.LENGTH_LONG).show()
                Log.d("nfcReaderTag", tag)
            }
        }
        super.onNewIntent(intent)
    }

    private fun getTag(intent: Intent): String {
        val tag:Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        if(tag == null) {
            Toast.makeText(this, "couldn't read the nfc", Toast.LENGTH_SHORT).show()
            return ""
        }
        return String(tag.id)
    }

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
}