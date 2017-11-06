package com.tommybear.batcitycrossfit;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.vision.text.Text;

import java.util.List;

import static android.R.id.message;
import static com.tommybear.batcitycrossfit.R.id.messageTextView;
import static com.tommybear.batcitycrossfit.R.mipmap.can;

public class StoreItemsAdapter extends ArrayAdapter<StoreItemsMessage> {
    public StoreItemsAdapter(Context context, int resource, List<StoreItemsMessage> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.storelayout, parent, false);
        }

        ImageView storeImage = (ImageView) convertView.findViewById(R.id.storeimage);
        TextView description = (TextView) convertView.findViewById(R.id.storedescription);
        TextView price = (TextView) convertView.findViewById(R.id.storeprice);

        StoreItemsMessage message = getItem(position);

        description.setVisibility(View.VISIBLE);
        description.setText(message.getdescription());
        price.setText(message.getprice());
        if (message.getdescription().equals("Knee Sleeves"))
        {
            storeImage.setImageResource(R.mipmap.kneesleeves);
        }
        if (message.getdescription().equals("Drop In"))
        {
            storeImage.setImageResource(R.mipmap.kb);
        }
        if (message.getdescription().equals("Fit Aid (1 can)"))
        {
            storeImage.setImageResource(R.mipmap.faid);
        }
        if (message.getdescription().equals("Water"))
        {
            storeImage.setImageResource(R.mipmap.fijiwater);
        }
        if (message.getdescription().equals("T-Shirt or Tank"))
        {
            storeImage.setImageResource(R.mipmap.tshirt1);
        }

        return convertView;
    }
}
