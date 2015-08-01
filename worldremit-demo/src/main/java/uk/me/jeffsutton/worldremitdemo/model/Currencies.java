package uk.me.jeffsutton.worldremitdemo.model;

import android.content.Context;
import android.util.Log;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.List;

import uk.me.jeffsutton.worldremitdemo.R;


/**
 * List of available currencies as returned by API
 *
 * @author Jeff Sutton
 */
public class Currencies {

    private static final String LOG_TAG = Currencies.class.getSimpleName();
    public List<String> currencies;

    public static Future<Response<byte[]>> getCurrencies(Context context, FutureCallback<Response<byte[]>> callback) {
        return Ion.with(context).load(context.getString(R.string.API_currencies))
                .userAgent("Demo")
                .setLogging(LOG_TAG, Log.VERBOSE)
                .asByteArray().withResponse().setCallback(callback);
    }
}
