package uk.me.jeffsutton.worldremitdemo.model;

import android.content.Context;
import android.util.Log;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.Currency;

import uk.me.jeffsutton.worldremitdemo.R;


/**
 * Object representing the response from the calculate endpoint.
 *
 * @author Jeff Sutton
 */
public class ReceiveCalculation {

    private static final String LOG_TAG = ReceiveCalculation.class.getSimpleName();

    public double sendamount;
    public String sendcurrency;
    public double receiveamount;
    public String receivecurrency;

    public static Future<Response<byte[]>> getReceiveAmount(Context context, double sendAmount,
                          Currency sendCurrency, Currency receiveCurrency,
                                 FutureCallback<Response<byte[]>> callback) {
        return Ion.with(context)
                .load(String.format(context.getString(R.string.API_calculate),
                        sendAmount, sendCurrency.getCurrencyCode(), receiveCurrency.getCurrencyCode()))
                .userAgent("Demo")
                .setLogging(LOG_TAG, Log.VERBOSE)
                .asByteArray().withResponse().setCallback(callback);
    }
}
