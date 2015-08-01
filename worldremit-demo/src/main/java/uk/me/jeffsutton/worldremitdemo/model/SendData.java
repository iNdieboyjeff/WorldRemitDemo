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
 * Object representing the payload for the send money transaction.
 *
 * @author Jeff Sutton
 */
public class SendData {

    private static final String LOG_TAG = SendData.class.getSimpleName();

    public double sendamount;
    public String sendcurrency;
    public double receiveamount;
    public String receivecurrency;
    public String recipient;

    public static Future<Response<byte[]>> sendMoney(Context context, String recipient,
                          double sendAmount, double receiveAmount, Currency sendCurrency, Currency receiveCurrency,
                                 FutureCallback<Response<byte[]>> callback) {

        SendData postBody = new SendData();
        postBody.recipient = recipient;
        postBody.sendcurrency = sendCurrency.getCurrencyCode();
        postBody.receivecurrency = receiveCurrency.getCurrencyCode();
        postBody.sendamount = sendAmount;
        postBody.receiveamount = receiveAmount;

        return Ion.with(context)
                .load(context.getString(R.string.API_send))
                .userAgent("Demo")
                .setLogging(LOG_TAG, Log.VERBOSE)
                .setJsonPojoBody(postBody)
                .asByteArray().withResponse().setCallback(callback);
    }
}
