package mainFunctionality.reservations;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import utn.proy2k18.vantrack.R;

import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.models.Reservation;


public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ModelViewHolder> implements View.OnClickListener {

    private List<Reservation> items;
    private OnItemClickListener mlistener;

    @Override
    public void onClick(View v) {

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    public ReservationsAdapter(List<Reservation> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public ReservationsAdapter.ModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation, parent, false);
        view.setOnClickListener(this);
        return new ModelViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public void onBindViewHolder(ReservationsAdapter.ModelViewHolder holder, final int position) {
        holder.bind(items.get(position));
    }

    public class ModelViewHolder extends RecyclerView.ViewHolder {

        private TextView company;
        private TextView bookedTripDate;
        private TextView bookedTripHour;
        private TextView origin;
        private TextView destination;
        private DateTimeFormatter tf = DateTimeFormat.forPattern("HH:mm");

        public ModelViewHolder(View itemView) {
            super(itemView);
            this.company = itemView.findViewById(R.id.company);
            this.bookedTripDate = itemView.findViewById(R.id.date);
            this.bookedTripHour=itemView.findViewById(R.id.hour);
            this.origin = itemView.findViewById(R.id.origin);
            this.destination = itemView.findViewById(R.id.destination);

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

        public void bind(Reservation reservation) {
            Trip bookedTrip = reservation.getBookedTrip();
            company.setText(bookedTrip.getCompanyName());
            bookedTripDate.setText(bookedTrip.getDate().toString());
            bookedTripHour.setText(bookedTrip.getTime().toString(tf));
            origin.setText(bookedTrip.getOrigin());
            destination.setText(bookedTrip.getDestination());
        }
    }
}
