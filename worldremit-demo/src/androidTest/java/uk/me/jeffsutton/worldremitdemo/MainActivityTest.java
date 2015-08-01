package uk.me.jeffsutton.worldremitdemo;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

/**
 * Created by jeff on 14/06/2015.
 */
public class MainActivityTest  extends ActivityInstrumentationTestCase2<MainActivity>{

    private MainActivity mActivity;
    private EditText contactName;
    private EditText sendAmount;
    private Button calculateButton;
    private Button sendButton;
    private Spinner sendCurrency;
    private Spinner recCurrency;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public MainActivityTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        mActivity = getActivity();

        contactName = (EditText) mActivity.findViewById(R.id.contactName);
        sendAmount = (EditText) mActivity.findViewById(R.id.amountSend);
        calculateButton = (Button) mActivity.findViewById(R.id.btnCalculate);
        sendButton = (Button) mActivity.findViewById(R.id.btnSend);
        sendCurrency = (Spinner) mActivity.findViewById(R.id.currencySpinner);
        recCurrency = (Spinner) mActivity.findViewById(R.id.currencyRecSpinner);
    }

    public void testPreconditions() {
        assertNotNull("MainActivity is null", mActivity);
        assertNotNull("Contact Name is null", contactName);
        assertNotNull("Calculate Button is null", calculateButton);
        assertNotNull("Send Button is null", sendButton);
        assertNotNull("Send Currency is null", sendCurrency);
        assertNotNull("Receive Currency is null", recCurrency);
        assertNotNull("Send Amount is null", sendAmount);
    }

    public void testInitialValues() throws Exception {
        assertEquals("Contact Name is not empty", "", contactName.getText().toString());
        assertFalse("Calculate Button is not disabled", calculateButton.isEnabled());
        assertFalse("Send Button is not disabled", sendButton.isEnabled());
        assertNull("Send Currency Adapter is not null", sendCurrency.getAdapter());
        assertNull("Receive Currency Adapter is not null", recCurrency.getAdapter());
    }

    public void testLoadCurrencies() throws Exception {
        // Wait five seconds - is this too low?
        Thread.sleep(5000);
        assertNotNull("Send Currency Adapter is still null", sendCurrency.getAdapter());
        assertNotNull("Receive Currency Adapter is still null", recCurrency.getAdapter());
    }

    public void testSelectSendCurrency() throws Exception {
        Thread.sleep(3000);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recCurrency.requestFocus();
                recCurrency.setSelection(0);
            }
        });
        getInstrumentation().waitForIdleSync();
        this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
        this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
        this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
        getInstrumentation().waitForIdleSync();
        assertEquals("Send Amount selected position = " + recCurrency.getSelectedItemPosition(), 1, recCurrency.getSelectedItemPosition());
        assertFalse("Send Amount is " + sendAmount.getText(), sendAmount.getText().toString().equalsIgnoreCase("0.00"));
    }

    /**
     * Ensure https request to non-trusted site is not allowed
     * @throws Exception
     */
    public void testPinningDissalowsOtherHTTPS() throws Exception {
        Future<Response<String>> f = Ion.getDefault(mActivity).build(mActivity).load("https://google.com/").asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                assertNotNull(e);
            }
        });
        Response<String> response = f.tryGet();
    }

    /**
     * Ensure https request to trusted site is allowed
     * @throws Exception
     */
    public void testPinningAllowsValidHTTPS() throws Exception {
        Future<Response<String>> f = Ion.getDefault(mActivity).build(mActivity).load("https://wr-interview.herokuapp.com/api/currencies").asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                assertNull(e);
            }
        });
        Response<String> response = f.tryGet();
    }

}
