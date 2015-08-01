package uk.me.jeffsutton.worldremitdemo.model;

import android.test.AndroidTestCase;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.ion.Response;


/**
 * Created by jeff on 14/06/2015.
 */
public class CurrenciesTest extends AndroidTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testLoadCurrencies() throws Exception {
        assertNotNull(Currencies.getCurrencies(getContext(), null));
    }

    public void testLoadCurrencies2() throws Exception {
        Future<Response<byte[]>> f = Currencies.getCurrencies(getContext(), null);
        Response<byte[]> r = f.get();
        assertNotNull(r.getHeaders());
        assertEquals(200, r.getHeaders().code());
    }

    public void testLoadCurrencies3() throws Exception {
        Future<Response<byte[]>> f = Currencies.getCurrencies(getContext(), null);
        Response<byte[]> r = f.get();
        String s = new String(r.getResult(), "UTF-8");
        String expected = "[\"GBP\",\"USD\",\"PHP\",\"EUR\"]";
        assertEquals(s, expected);
    }
}