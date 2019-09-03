package mainFunctionality.search;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Locale;

import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.mainFunctionality.search.TripStop;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ModelViewHolder>
        implements View.OnClickListener {

    private List<Trip> items;
    private OnItemClickListener mlistener;
    private TripsViewModel tripsModel = TripsViewModel.getInstance();


    @Override
    public void onClick(View v) {
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    public TripsAdapter(List<Trip> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public TripsAdapter.ModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_user, parent,
                false);
        view.setOnClickListener(this);
        return new ModelViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public void onBindViewHolder(TripsAdapter.ModelViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    public void setItems(List<Trip> items) {
        this.items = items;
    }

    public class ModelViewHolder extends RecyclerView.ViewHolder{

        private TextView companyName;
        private TextView companyCalification;
        private TextView time;
        private TextView price;
        private TextView availableSeats;
        private DateTimeFormatter tf = DateTimeFormat.forPattern("HH:mm");

        public ModelViewHolder(View itemView) {
            super(itemView);
            this.companyName = itemView.findViewById(R.id.companyName);
            this.companyCalification = itemView.findViewById(R.id.companyCalification);
            this.time = itemView.findViewById(R.id.time);
            this.price = itemView.findViewById(R.id.price);
            this.availableSeats = itemView.findViewById(R.id.available_seats);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mlistener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mlistener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public void bind(Trip trip) {
            companyName.setText(trip.getCompanyName());
            companyCalification.setText(String.format(Locale.ENGLISH, "%.3g%n",
                    trip.getCompanyCalification()));
            TripStop argHopOnStop;
            if (tripsModel.isReturnSearch()) {
                argHopOnStop = trip.getTripStopByDescription(tripsModel.getSearchedDestination());
            } else {
                argHopOnStop = trip.getTripStopByDescription(tripsModel.getSearchedOrigin());
            }
            time.setText(argHopOnStop.getHour().toString(tf));
            price.setText(String.format(Locale.getDefault(), "$%.2f", trip.getPrice()));
            availableSeats.setText(String.valueOf(trip.getSeatsAvailableQty()));
        }
    }
}
