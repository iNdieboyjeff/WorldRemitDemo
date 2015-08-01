package uk.me.jeffsutton.worldremitdemo.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;

import uk.me.jeffsutton.worldremitdemo.R;
import uk.me.jeffsutton.worldremitdemo.adapter.CurrencySpinnerAdapter;
import uk.me.jeffsutton.worldremitdemo.model.Currencies;
import uk.me.jeffsutton.worldremitdemo.model.ReceiveCalculation;
import uk.me.jeffsutton.worldremitdemo.model.SendData;

/**
 * Main fragment for the app.
 *
 * @author Jeff Sutton
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static final int CONTACT_PICKER_RESULT = 9000;

    private EditText contactName;
    private EditText sendAmount;
    private Spinner currencySpinner;
    private Spinner currencyRecSpinner;
    private Button calculateButton;
    private Button sendButton;
    private TextView receiveAmount;

    private ProgressDialog pd;

    private boolean calculationDone = false;
    private boolean contactPicked = false;

    private ReceiveCalculation calculation = null;

    // Default empty constructor
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup views
        contactName = (EditText) view.findViewById(R.id.contactName);
        currencySpinner = (Spinner) view.findViewById(R.id.currencySpinner);
        currencyRecSpinner = (Spinner) view.findViewById(R.id.currencyRecSpinner);
        sendAmount = (EditText) view.findViewById(R.id.amountSend);
        sendAmount.setRawInputType(Configuration.KEYBOARD_12KEY);
        calculateButton = (Button) view.findViewById(R.id.btnCalculate);
        sendButton = (Button) view.findViewById(R.id.btnSend);
        receiveAmount = (TextView) view.findViewById(R.id.valueRecAmount);

        // Attach listeners
        contactName.setOnClickListener(chooseContactListener);
        sendAmount.addTextChangedListener(sendAmountWatcher);
        currencySpinner.setOnItemSelectedListener(sendSelectionListener);
        currencyRecSpinner.setOnItemSelectedListener(receiveSelectionListener);
        calculateButton.setOnClickListener(calculateClickListener);
        sendButton.setOnClickListener(sendClickListener);

        // Init data
        pd = ProgressDialog.show(getActivity(), "", getString(R.string.loadingCurrencies), true, false);
        pd.show();
        Currencies.getCurrencies(getActivity(), getCurrenciesCallback);
    }



    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == CONTACT_PICKER_RESULT) {
            // User has picked a contact.  We need to grab the contacts details
            Uri result = data.getData();
            Cursor cursor = getActivity().getContentResolver().query(result, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // column index of the contact name
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                // Display the name on the form
                contactName.setText(cursor.getString(nameIndex));
                contactPicked = true;
                cursor.close();
            }
            enableSendCheck();
        }
    }

    /**
     * Open the system contact picker activity
     */
    private final View.OnClickListener chooseContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
        }
    };

    /**
     * Make a request to calculate the amount that will be received.
     */
    private final View.OnClickListener calculateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Currency sendCurrency = ((CurrencySpinnerAdapter) currencySpinner.getAdapter())
                    .getCurencyAtPosition(currencySpinner.getSelectedItemPosition());
            Currency recCurrency = ((CurrencySpinnerAdapter) currencyRecSpinner.getAdapter())
                    .getCurencyAtPosition(currencyRecSpinner.getSelectedItemPosition());

            String replaceable = String.format(getString(R.string.replaceCurrency), sendCurrency.getSymbol());

            String cleanString = sendAmount.getText().toString().replaceAll(replaceable, "");

            BigDecimal parsed = new BigDecimal(cleanString)
                    .setScale(sendCurrency.getDefaultFractionDigits(), BigDecimal.ROUND_FLOOR)
                    .divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);

            pd = ProgressDialog.show(getActivity(), "", getString(R.string.loadingReceiveAmount),
                    true, false);
            pd.show();

            ReceiveCalculation.getReceiveAmount(getActivity(), parsed.doubleValue(), sendCurrency,
                    recCurrency, getReceiveAmountCallback);
        }
    };

    /**
     * Make a request to send money.
     */
    private final View.OnClickListener sendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Currency sendCurrency = ((CurrencySpinnerAdapter) currencySpinner.getAdapter())
                    .getCurencyAtPosition(currencySpinner.getSelectedItemPosition());
            Currency recCurrency = ((CurrencySpinnerAdapter) currencyRecSpinner.getAdapter())
                    .getCurencyAtPosition(currencyRecSpinner.getSelectedItemPosition());

            pd = ProgressDialog.show(getActivity(), "", getString(R.string.sendingMoney),
                    true, false);
            pd.show();

            SendData.sendMoney(getActivity(), contactName.getText().toString(), calculation.sendamount,
                    calculation.receiveamount, sendCurrency, recCurrency, sendMoneyCallback);
        }
    };

    /**
     * A send currency has been selected.  Force the send amount text to update to the correct
     * currency symbol.
     */
    private final AdapterView.OnItemSelectedListener sendSelectionListener =
            new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            sendAmount.setText(sendAmount.getText().toString());
            calculationDone = false;
            calculation = null;
            enableCalculateCheck();
            enableSendCheck();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            enableCalculateCheck();
            enableSendCheck();
        }
    };

    private final AdapterView.OnItemSelectedListener receiveSelectionListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    calculationDone = false;
                    calculation = null;
                    enableCalculateCheck();
                    enableSendCheck();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    enableCalculateCheck();
                    enableSendCheck();
                }
            };

    /**
     * The send amount text has been changed.  Format this value in the correct manner.
     */
    private final TextWatcher sendAmountWatcher = new TextWatcher() {

        String current = "";
        String usingSymbol = "";

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            sendAmount.removeTextChangedListener(this);

            String replaceable = String.format(getString(R.string.replaceCurrency), usingSymbol);

            String cleanString = s.toString().replaceAll(replaceable, "");

            Currency currency = ((CurrencySpinnerAdapter) currencySpinner.getAdapter())
                    .getCurencyAtPosition(currencySpinner.getSelectedItemPosition());

            BigDecimal parsed = new BigDecimal(cleanString)
                    .setScale(currency.getDefaultFractionDigits(), BigDecimal.ROUND_FLOOR)
                    .divide(new BigDecimal(100),BigDecimal.ROUND_FLOOR);
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            nf.setCurrency(currency);
            String formatTo = nf.format(parsed);

            current = formatTo;
            usingSymbol = currency.getSymbol();
            sendAmount.setText(formatTo);
            sendAmount.setSelection(formatTo.length());
            sendAmount.addTextChangedListener(this);
            calculationDone = false;
            calculation = null;
            enableCalculateCheck();
            enableSendCheck();
        }


    };

    /**
     * The list of available currencies has been loaded.  Initialize the send and receive
     * spinners with this data.
     *
     * For ease of use we wrap the array response inside a JSON object for parsing using Gson.
     */
    public final FutureCallback<Response<byte[]>> getCurrenciesCallback =
            new FutureCallback<Response<byte[]>>() {

        /**
         * onCompleted is called by the Future with the result or exception of the asynchronous operation.
         *
         * @param e      Exception encountered by the operation
         * @param result Result returned from the operation
         */
        @Override
        public void onCompleted(Exception e, Response<byte[]> result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (result != null && result.getHeaders() != null && result.getHeaders().code() == 200) {
                try {
                    String responseString = new String(result.getResult(), "UTF-8");
                    responseString = "{\"currencies\":" + responseString + "}";
                    Currencies currencies = Ion.getDefault(getActivity()).configure()
                            .getGson().fromJson(responseString, Currencies.class);
                    currencySpinner.setAdapter(new CurrencySpinnerAdapter(getActivity(), currencies));
                    currencyRecSpinner.setAdapter(new CurrencySpinnerAdapter(getActivity(), currencies));

                } catch (Exception err) {
                    err.printStackTrace();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.error).setMessage(R.string.loadCurrencyError)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                pd = ProgressDialog.show(getActivity(), "", getString(R.string.loadingCurrencies), true, false);
                                pd.show();
                                Currencies.getCurrencies(getActivity(), getCurrenciesCallback);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                getActivity().finish();
                            }
                        });
                builder.show();
            }

        }
    };

    /**
     * Callback for the send money request.  Let the user know if the request was successful or not.  If it
     * was a success clear the form ready for a new transaction.
     */
    private final FutureCallback<Response<byte[]>> sendMoneyCallback = new FutureCallback<Response<byte[]>>() {
        @Override
        public void onCompleted(Exception e, Response<byte[]> result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (result.getHeaders() != null && result.getHeaders().code() == 201) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.success).setMessage(R.string.moneySent)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                resetForm();
                            }
                        });
                builder.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.error).setMessage(R.string.loadReceiveAmountError)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                sendButton.performClick();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        }
    };

    private final FutureCallback<Response<byte[]>> getReceiveAmountCallback =
            new FutureCallback<Response<byte[]>>() {

        /**
         * onCompleted is called by the Future with the result or exception of the asynchronous operation.
         *
         * @param e      Exception encountered by the operation
         * @param result Result returned from the operation
         */
        @Override
        public void onCompleted(Exception e, Response<byte[]> result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (result != null && result.getHeaders() != null && result.getHeaders().code() == 200) {
                try {
                    String responseString = new String(result.getResult(), "UTF-8");
                    ReceiveCalculation response = Ion.getDefault(getActivity()).configure()
                            .getGson().fromJson(responseString, ReceiveCalculation.class);


                    Currency currency = Currency.getInstance(response.receivecurrency);

                    BigDecimal parsed = new BigDecimal(response.receiveamount)
                            .setScale(currency.getDefaultFractionDigits(), BigDecimal.ROUND_FLOOR);
                    NumberFormat nf = NumberFormat.getCurrencyInstance();
                    nf.setCurrency(currency);
                    String formatTo = nf.format(parsed);
                    receiveAmount.setText(formatTo);
                    calculationDone = true;
                    calculation = response;
                    enableSendCheck();
                } catch (Exception err) {
                    err.printStackTrace();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.error).setMessage(R.string.loadReceiveAmountError)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                               calculateButton.performClick();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }

        }
    };

    /**
     * Determine if enough information has been entered to enable us to check how much
     * will be received.
     */
    private boolean enableCalculateCheck() {
        if (currencySpinner == null || currencySpinner.getAdapter() == null) return false;
        if (currencyRecSpinner == null || currencyRecSpinner.getAdapter() == null) return false;

        boolean sendCurr = false;
        boolean recCurr = false;
        boolean sendAmt = false;

        if (currencySpinner.getSelectedItemPosition() > -1) {
            sendCurr = true;
        }

        if (currencyRecSpinner.getSelectedItemPosition() > -1) {
            recCurr = true;
        }

        Currency currency = ((CurrencySpinnerAdapter) currencySpinner.getAdapter())
                .getCurencyAtPosition(currencySpinner.getSelectedItemPosition());

        String replaceable = String.format(getString(R.string.replaceCurrency), currency.getSymbol());

        String cleanString = sendAmount.getText().toString().replaceAll(replaceable, "");

        BigDecimal parsed = new BigDecimal(cleanString).setScale(currency.getDefaultFractionDigits(),
                BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100),
                BigDecimal.ROUND_FLOOR);

        if (parsed.compareTo(new BigDecimal(0)) > 0) {
            sendAmt = true;
        }

        if (sendAmt && sendCurr && recCurr) {
            calculateButton.setEnabled(true);
            return true;
        } else {
            calculateButton.setEnabled(false);
            return false;
        }
    }

    /**
     * Determine if the send button should be enabled.
     *
     * @return boolean - has the send button been anabled
     */
    private boolean enableSendCheck() {
        if (!enableCalculateCheck() || !calculationDone || !contactPicked) {
            sendButton.setEnabled(false);
            return false;
        } else {
            sendButton.setEnabled(true);
            return true;
        }

    }

    /**
     * Clear the form data ready for a new transaction
     */
    private void resetForm() {
        contactName.setText(null);
        contactPicked = false;
        sendAmount.setText("0.00");
        calculationDone = false;
        calculation = null;
        receiveAmount.setText(null);
        enableCalculateCheck();
        enableSendCheck();
    }

}
