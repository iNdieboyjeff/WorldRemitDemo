package uk.me.jeffsutton.worldremitdemo.model;

import android.test.AndroidTestCase;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.ion.Response;

import java.util.Currency;

/**
 * Created by jeffsutton on 15/06/15.
 */
public class SendDataTest extends AndroidTestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    /**
     * Ensure we can call the API endpoint and generate a request
     * @throws Exception
     */
    public void testSendData() throws Exception {
        Currency send = Currency.getInstance("GBP");
        Currency rec = Currency.getInstance("GBP");
        assertNotNull(SendData.sendMoney(getContext(), "Test User-Name", 1.00, 1.00, send, rec, null));
    }

    public void testSendData2() throws Exception {
        Currency send = Currency.getInstance("GBP");
        Currency rec = Currency.getInstance("GBP");
        Future<Response<byte[]>> f = SendData.sendMoney(getContext(), "Test User-Name", 1.00, 1.00, send, rec, null);
        Response<byte[]> r = f.get();
        assertNotNull(r.getHeaders());
        assertEquals(201, r.getHeaders().code());
    }

    public void testSendInvalidReceiveAmount() throws Exception {
        Currency send = Currency.getInstance("GBP");
        Currency rec = Currency.getInstance("GBP");
        Future<Response<byte[]>> f = SendData.sendMoney(getContext(), "Test User-Name", 1.00, 2.00, send, rec, null);
        Response<byte[]> r = f.get();
        assertNotNull(r.getHeaders());
        assertNotSame(201, r.getHeaders().code());
    }

    public void testSendInvalidCurrency() throws Exception {
        Currency send = Currency.getInstance("GBP");
        Currency rec = Currency.getInstance("TRY");
        Future<Response<byte[]>> f = SendData.sendMoney(getContext(), "Test User-Name", 1.00, 1.00, send, rec, null);
        Response<byte[]> r = f.get();
        assertNotNull(r.getHeaders());
        assertNotSame(201, r.getHeaders().code());
    }
}