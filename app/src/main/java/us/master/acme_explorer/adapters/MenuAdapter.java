package us.master.acme_explorer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;

import com.squareup.picasso.Picasso;

import java.util.List;

import us.master.acme_explorer.R;
import us.master.acme_explorer.entity.links;

public class MenuAdapter extends BaseAdapter {
    private List<links> linksList;
    private Context context;

    public MenuAdapter(List<links> linksList, Context context) {
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
        final links link = linksList.get(position);
        if (convertView == null)
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.link_item, parent, false);

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
                v -> Navigation.findNavController(v).navigate(link.getNextView())
        );

        return convertView;
    }
}
