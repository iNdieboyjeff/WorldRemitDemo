package uk.me.jeffsutton.worldremitdemo.model;

import android.test.AndroidTestCase;

import com.google.gson.Gson;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.ion.Response;

import java.util.Currency;

/**
 * Created by jeffsutton on 15/06/15.
 */
public class ReceiveCalculationTest extends AndroidTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Ensure we can call the API endpoint and generate a request
     * @throws Exception
     */
    public void testLoadReceiveAmount() throws Exception {
        Currency send = Currency.getInstance("GBP");
        Currency rec = Currency.getInstance("EUR");
        assertNotNull(ReceiveCalculation.getReceiveAmount(getContext(), 1.00, send, rec, null));

    }

    /**
     * Ensure we can call the API with valid data and get an HTTP 200 response
     * @throws Exception
     */
    public void testLoadReceiveAmount2() throws Exception {
        Currency send = Currency.getInstance("GBP");
        Currency rec = Currency.getInstance("EUR");
        Future<Response<byte[]>> f = ReceiveCalculation.getReceiveAmount(getContext(), 1.00, send, rec, null);
        Response<byte[]> r = f.get();
        assertNotNull(r.getHeaders());
        assertEquals(200, r.getHeaders().code());
    }

    /**
     * Ensure we can call the API with invalid data and NOT get an HTTP 200 response
     * @throws Exception
     */
    public void testLoadReceiveAmount3() throws Exception {
        Currency send = Currency.getInstance("GBP");
        Currency rec = Currency.getInstance("TRY");
        Future<Response<byte[]>> f = ReceiveCalculation.getReceiveAmount(getContext(), 1.00, send, rec, null);
        Response<byte[]> r = f.get();
        assertNotNull(r.getHeaders());
        assertNotSame(200, r.getHeaders().code());
    }

    /**
     * Ensure we can call the API with valid data and build a ReceiveCalculation object from the
     * data, with expected values.
     * @throws Exception
     */
    public void testLoadReceiveAmount4() throws Exception {
        Currency send = Currency.getInstance("GBP");
        Currency rec = Currency.getInstance("EUR");
        Future<Response<byte[]>> f = ReceiveCalculation.getReceiveAmount(getContext(), 1.00, send, rec, null);
        Response<byte[]> r = f.get();
        String s = new String(r.getResult(), "UTF-8");
        Gson gson = new Gson();
        ReceiveCalculation calc = gson.fromJson(s, ReceiveCalculation.class);
        assertNotNull(calc);
        assertEquals("GBP", calc.sendcurrency);
        assertEquals("EUR", calc.receivecurrency);
        assertEquals(1.00, calc.sendamount);
        assertNotSame(calc.sendamount, calc.receiveamount);
    }
}