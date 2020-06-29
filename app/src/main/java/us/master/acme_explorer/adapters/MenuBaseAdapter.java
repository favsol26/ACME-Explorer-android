package us.master.acme_explorer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.squareup.picasso.Picasso;

import java.util.List;

import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Util;
import us.master.acme_explorer.entity.Links;

public class MenuBaseAdapter extends BaseAdapter {
    private List<Links> linksList;
    private Context context;

    public MenuBaseAdapter(List<Links> linksList, Context context) {
        this.linksList = linksList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return linksList.size();
    }

    @Override
    public Object getItem(int position) {
        return linksList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return linksList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Links link = linksList.get(position);
        if (convertView == null)
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_link, parent, false);

        TextView mTextView = convertView.findViewById(R.id.my_link_text_view);
        mTextView.setText(link.getDescription());

        ImageView mImageView = convertView.findViewById(R.id.my_link_image_view);
        Picasso.get()
                .load(link.getImageURL())
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .error(android.R.drawable.ic_menu_myplaces)
                .fit()
                .into(mImageView);

        CardView mCardView = convertView.findViewById(R.id.my_link_card_view);
        mCardView.setOnClickListener(
                v -> Util.navigateTo(v, link.getAction(), null)
        );

        return convertView;
    }
}
