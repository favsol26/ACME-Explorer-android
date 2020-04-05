package us.master.acme_explorer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

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

        CardView mCardView = convertView.findViewById(R.id.my_link_card_view);
        TextView mTextView = convertView.findViewById(R.id.my_link_text_view);
        ImageView mImageView = convertView.findViewById(R.id.my_link_image_view);

        mTextView.setText(link.getDescription());
        mImageView.setImageResource(link.getResourceImageView());
        mCardView.setOnClickListener(v -> {

            if (context instanceof FragmentActivity) {
                Toast.makeText(context, link.getNextFragment().toString(), Toast.LENGTH_SHORT).show();

                FragmentTransaction fragmentTransaction =
                        ((FragmentActivity) context).getSupportFragmentManager()
                                .beginTransaction();

//                fragmentTransaction.remove(convertView.get);
                fragmentTransaction.replace(R.id.nav_host_fragment, link.getNextFragment());

                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //            context.startActivity(new Intent(context, link.getNextClass()));

            }


        });
        return convertView;
    }
}
