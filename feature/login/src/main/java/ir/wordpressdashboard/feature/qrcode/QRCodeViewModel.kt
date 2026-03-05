package ir.wordpressdashboard.feature.qrcode

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.wordpressdashboard.usecase.SaveCredentialsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import javax.inject.Inject

data class QRCredentials(
    val baseUrl: String,
    val consumerKey: String,
    val secretKey: String
)

sealed class QRScanState {
    object Idle : QRScanState()
    data class Success(val credentials: QRCredentials) : QRScanState()
    data class Error(val message: String) : QRScanState()
}

@HiltViewModel
class QRCodeViewModel @Inject constructor(
    private val saveCredentialsUseCase: SaveCredentialsUseCase
) : ViewModel() {

    private val _scanState = MutableStateFlow<QRScanState>(QRScanState.Idle)
    val scanState: StateFlow<QRScanState> = _scanState.asStateFlow()

    fun onQRCodeScanned(rawValue: String) {
        if (_scanState.value is QRScanState.Success) return

        Log.d("QRCodeViewModel", "Raw QR value: [$rawValue]")

        val credentials = parseQRCode(rawValue)
        if (credentials != null) {
            Log.d("QRCodeViewModel", "Parsed OK → url=${credentials.baseUrl}")
            saveCredentialsUseCase(
                baseUrl = credentials.baseUrl,
                consumerKey = credentials.consumerKey,
                secretKey = credentials.secretKey
            )
            _scanState.value = QRScanState.Success(credentials)
        } else {
            Log.e("QRCodeViewModel", "Parse failed for: [$rawValue]")
            _scanState.value = QRScanState.Error("فرمت QR Code معتبر نیست\n\nمحتوا:\n$rawValue")
        }
    }

    fun resetState() {
        _scanState.value = QRScanState.Idle
    }

    /**
     * Supported QR formats:
     *  1. JSON:        {"url":"https://...","ck":"ck_xxx","cs":"cs_xxx"}
     *                  {"url":"...","consumer_key":"...","secret_key":"..."}
     *  2. Pipe:        https://example.com/wp-json/wc/v3/|ck_xxx|cs_xxx
     *  3. Semicolon:   https://example.com/wp-json/wc/v3/;ck_xxx;cs_xxx
     *  4. Query-string: url=https://...&ck=ck_xxx&cs=cs_xxx
     *  5. Comma:       https://example.com/wp-json/wc/v3/,ck_xxx,cs_xxx
     */
    private fun parseQRCode(raw: String): QRCredentials? {
        val trimmed = raw.trim()
        Log.d("QRCodeViewModel", "Trying to parse: [$trimmed]")

        // 1. JSON format
        if (trimmed.startsWith("{")) {
            return try {
                val json = JSONObject(trimmed)
                val url = json.optString("url").takeIf { it.isNotEmpty() }
                    ?: json.optString("base_url").takeIf { it.isNotEmpty() }
                    ?: json.optString("site_url").takeIf { it.isNotEmpty() }
                val ck = json.optString("ck").takeIf { it.isNotEmpty() }
                    ?: json.optString("consumer_key").takeIf { it.isNotEmpty() }
                val cs = json.optString("cs").takeIf { it.isNotEmpty() }
                    ?: json.optString("secret_key").takeIf { it.isNotEmpty() }
                if (url != null && ck != null && cs != null)
                    QRCredentials(baseUrl = url, consumerKey = ck, secretKey = cs)
                else null
            } catch (_: Exception) { null }
        }

        // 2. Query-string format: url=...&ck=...&cs=...
        if (trimmed.contains("=") && trimmed.contains("&")) {
            val map = trimmed.split("&").mapNotNull {
                val kv = it.split("=", limit = 2)
                if (kv.size == 2) kv[0].trim() to kv[1].trim() else null
            }.toMap()
            val url = map["url"] ?: map["base_url"] ?: map["site_url"]
            val ck = map["ck"] ?: map["consumer_key"]
            val cs = map["cs"] ?: map["secret_key"]
            if (url != null && ck != null && cs != null)
                return QRCredentials(baseUrl = url, consumerKey = ck, secretKey = cs)
        }

        // 3. Delimiter-separated: pipe | semicolon ; or comma ,
        for (delimiter in listOf("|", ";", ",")) {
            val parts = trimmed.split(delimiter)
            if (parts.size >= 3) {
                val url = parts[0].trim()
                val ck = parts[1].trim()
                val cs = parts[2].trim()
                if (url.isNotEmpty() && ck.isNotEmpty() && cs.isNotEmpty())
                    return QRCredentials(baseUrl = url, consumerKey = ck, secretKey = cs)
            }
            // Only ck|cs (no URL) → save keys only, URL will be entered by user
            if (parts.size == 2) {
                val ck = parts[0].trim()
                val cs = parts[1].trim()
                if (ck.startsWith("ck_") && cs.startsWith("cs_"))
                    return QRCredentials(
                        baseUrl = "",
                        consumerKey = ck,
                        secretKey = cs
                    )
            }
        }

        return null
    }
}
