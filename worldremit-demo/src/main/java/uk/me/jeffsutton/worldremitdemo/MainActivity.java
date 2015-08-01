package uk.me.jeffsutton.worldremitdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.koushikdutta.ion.Ion;

import javax.net.ssl.TrustManager;

import io.fabric.sdk.android.Fabric;
import uk.me.jeffsutton.worldremitdemo.ssl.PinningTrustManager;
import uk.me.jeffsutton.worldremitdemo.ssl.SystemKeyStore;


/**
 * Main entry point for the app.
 *
 * @author Jeff Sutton
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        initPinning();
        setContentView(R.layout.activity_main);
    }


    /**
     * Enable SSL pinning.  Prevent snooping or MITM attacks.
     * SSL Pins here are hardcoded.  In practice they could be loaded from a configuration file
     * to enable remote updating as required without the need for an app update.
     *
     * Pins can be generated from a certificate using openssl as follows:
     *
     * openssl x509 -in [certificate_file] -inform DER -outform DER -noout -pubkey | openssl rsa -pubin -outform DER | openssl sha1 -hex
     */
    private void initPinning() {
        SystemKeyStore sks = SystemKeyStore.getInstance(this);
        PinningTrustManager trustManager = new PinningTrustManager(sks,
               this.getResources().getStringArray(R.array.pins), 0);
        Ion.getDefault(this).getHttpClient().getSSLSocketMiddleware()
                .setTrustManagers(new TrustManager[]{trustManager});
    }
}
