package us.master.acme_explorer.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.Util;
import us.master.acme_explorer.entity.Trip;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    private static final String TAG = TripAdapter.class.getSimpleName();
    private int column;

    private List<String> indexes;
    private Hashtable<String, Trip> dicTrips;
    private String aClass;
    private Context context;
    private String formatText = " ";

    public TripAdapter(String aClass, int column, Context context) {
        this.aClass = aClass;
        this.column = column;
        this.context = context;

        this.dicTrips = new Hashtable<>();
        this.indexes = new ArrayList<>();
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View tripView = inflater.inflate(R.layout.trip_item, parent, false);

        return new TripViewHolder(tripView);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        String tripId = indexes.get(holder.getAdapterPosition());
        Trip trip = dicTrips.get(tripId);

        assert trip != null;
        Picasso.get()
                .load(trip.getUrlImage())
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .error(android.R.drawable.ic_menu_myplaces)
                .fit()
                .into(holder.mFlagImv);

        if (aClass.equals("SelectedTripsFragment"))
            holder.mBuyTripImv.setVisibility(View.VISIBLE);
        else holder.mSelectedImv.setVisibility(View.VISIBLE);

        Util.setState(trip, holder.mSelectedImv);

        holder.mBuyTripImv.setOnClickListener(
                V -> Toast.makeText(context, String.format("%s %s %s",
                        context.getString(R.string.trip_tag),
                        this.indexes.indexOf(trip.getId()) + 1, context.getString(R.string.bought_tag)),
                        Toast.LENGTH_SHORT).show()
        );

        setTextSize(holder, (column % 2 == 0) ? 17 : 20);

        holder.mPriceTxv.setText(String.valueOf(trip.getPrice()).concat(" $"));
        holder.mArrivalPlaceTxv.setText(trip.getArrivalPlace());

        holder.mDepartureDateTxv.setText(String.format("%s%s%s", context.getString(R.string.departure), formatText,
                Util.dateFormatter(trip.getDepartureDate())));

        holder.mArrivalDateTxv.setText(String.format("%s%s%s", context.getString(R.string.arrival), formatText,
                Util.dateFormatter(trip.getArrivalDate())));

        holder.mCardView.setOnClickListener(V -> {
                    Bundle args = new Bundle();
                    args.putString(Constants.IntentTrip, trip.getId());
                    args.putString(Constants.from, this.aClass);
                    Util.navigateTo(V, R.id.nav_active_trip_fragment, args);
                }
        );
    }

    @Override
    public int getItemCount() {
        return indexes.size();
    }

    public void addItem(Trip trip) {
        if (this.indexes.contains(trip.getId())) {
            updateItem(trip);
        } else {
            this.indexes.add(trip.getId());
            this.dicTrips.put(trip.getId(), trip);
            this.notifyDataSetChanged();
        }
    }

    public void updateItem(Trip trip) {
        Log.d(TAG, "updateItem: " + indexes);

        int position = indexes.indexOf(trip.getId());
        Log.d(TAG, " updateItem: " + position);
        if (position > -1) {
            this.indexes.remove(position);
            this.dicTrips.remove(trip.getId());
            this.dicTrips.put(trip.getId(), trip);
            this.indexes.add(position, trip.getId());
            this.notifyItemChanged(position);
        }
    }

    public void removeItem(Trip trip) {
        int position = this.indexes.indexOf(trip.getId());
        if (position > -1) {
            this.indexes.remove(position);
            this.dicTrips.remove(trip.getId());
            this.notifyItemRemoved(position);
        }
    }

    public void clearLists() {
        this.indexes.clear();
        this.dicTrips.clear();
    }

    public void setColumn(int column) {
        this.column = column;
    }

    private void setTextSize(TripViewHolder holder, int i) {
        holder.mArrivalPlaceTxv.setTextSize(i + 2);
        holder.mPriceTxv.setTextSize(i - 2);
        holder.mArrivalDateTxv.setTextSize(i - 2);
        holder.mDepartureDateTxv.setTextSize(i - 2);
    }

    public void setFormatText(String formatText) {
        this.formatText = formatText;
    }

    static class TripViewHolder extends RecyclerView.ViewHolder {
        ImageView mFlagImv, mSelectedImv, mBuyTripImv;
        TextView mPriceTxv, mDepartureDateTxv, mArrivalDateTxv, mArrivalPlaceTxv;
        CardView mCardView;

        TripViewHolder(@NonNull View itemView) {
            super(itemView);

            mCardView = itemView.findViewById(R.id.my_content_trip_card_view);

            mFlagImv = itemView.findViewById(R.id.my_trip_image_view);
            mArrivalPlaceTxv = itemView.findViewById(R.id.my_trip_arrival_place_text_view);

            mSelectedImv = itemView.findViewById(R.id.my_trip_star_image_view);
            mBuyTripImv = itemView.findViewById(R.id.my_trip_pay_image_view);

            mPriceTxv = itemView.findViewById(R.id.my_trip_price_text_view);
            mDepartureDateTxv = itemView.findViewById(R.id.my_trip_departure_date_text_view);
            mArrivalDateTxv = itemView.findViewById(R.id.my_trip_arrival_date_text_view);
        }
    }
}
