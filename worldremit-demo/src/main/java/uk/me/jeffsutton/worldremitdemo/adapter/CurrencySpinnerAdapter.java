package uk.me.jeffsutton.worldremitdemo.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.Currency;

import uk.me.jeffsutton.worldremitdemo.R;
import uk.me.jeffsutton.worldremitdemo.model.Currencies;

/**
 * <p>A basic SpinnerAdapter for displaying currencies in a list.</p>
 *
 * <p>For such a basic implementation the ViewHolder pattern has not been used.  In a more
 * complex list we would do so.</p>
 *
 * @author Jeff Sutton
 */
public class CurrencySpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private final Currencies mData;
    private final Context mContext;
    private final LayoutInflater mInflater;

    public CurrencySpinnerAdapter(Context context, Currencies currencies) {
        this.mContext = context;
        this.mData = currencies;
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        if (mData == null || mData.currencies == null)
            return 0;
        else
            return mData.currencies.size();
    }

    @Override
    public Object getItem(int position) {
        if (mData == null || mData.currencies == null)
            return null;
        else
            return mData.currencies.get(position);
    }

    public Currency getCurencyAtPosition(int position) {
        return Currency.getInstance((String) getItem(position));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.currency_item, parent, false);
        }

        TextView label = (TextView) convertView.findViewById(R.id.currencyDisplayLabel);

        Currency c = getCurencyAtPosition(position);

        if (Build.VERSION.SDK_INT >= 19) {
            label.setText(c.getDisplayName() + " (" + c.getSymbol() + ")");
        } else {
            label.setText(c.getCurrencyCode() + " (" + c.getSymbol() + ")");
        }

        return convertView;
    }
}
