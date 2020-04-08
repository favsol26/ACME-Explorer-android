package us.master.acme_explorer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Util;
import us.master.acme_explorer.entity.Trip;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    private int column;
    private List<Trip> trips;
    private String aClass;
    private Context context;

    public TripAdapter(List<Trip> trips, String aClass, int column) {
        this.trips = trips;
        this.aClass = aClass;
        this.column = column;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tripView = inflater.inflate(R.layout.trip_item, parent, false);

        return new TripViewHolder(tripView);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = trips.get(position);

        Glide.with(context)
                .load(trip.getUrlImage())
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .error(android.R.drawable.ic_menu_myplaces)
                .into(holder.mFlagImv);

        if (aClass.equals("SelectedTripsFragment"))
            holder.mBuyTripImv.setVisibility(View.VISIBLE);
        else holder.mSelectedImv.setVisibility(View.VISIBLE);

        holder.mSelectedImv.setImageResource(
                (trip.isSelected())
                        ? android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off
        );

        holder.mBuyTripImv.setOnClickListener(
                V -> Toast.makeText(
                        context, String.format("%s %s %s",
                                context.getString(R.string.trip_tag),
                                trip.getId(),
                                context.getString(R.string.bought_tag)), Toast.LENGTH_SHORT).show()
        );

        textSize(holder, (column == 2) ? 19 : 27);

        holder.mPriceTxv.setText(String.valueOf(trip.getPrice()).concat(" $"));
        holder.mArrivalPlaceTxv.setText(trip.getArrivePlace());

        holder.mDepartureDateTxv.setText(String.format("%s%s%s",
                context.getString(R.string.departure),
                ((column == 2) ? "\n" : "  "),
                Util.dateFormatter(trip.getDepartureDate())));

        holder.mArrivalDateTxv.setText(String.format("%s%s%s",
                context.getString(R.string.arrival),
                ((column == 2) ? "\n" : "  "),
                Util.dateFormatter(trip.getArrivalDate())));

        holder.mCardView.setOnClickListener(
                V -> Navigation.findNavController(V).navigate(R.id.nav_active_trip_fragment)
        );
    }

    private void textSize(TripViewHolder holder, int i) {
        holder.mArrivalPlaceTxv.setTextSize(i);
//        holder.mPriceTxv.setTextSize(i );
//        holder.mArrivalDateTxv.setTextSize(i );
//        holder.mDepartureDateTxv.setTextSize(i);
    }

    @Override
    public int getItemCount() {
        return trips.size();
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
