package mainFunctionality.reservations;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Locale;

import utn.proy2k18.vantrack.R;

import utn.proy2k18.vantrack.mainFunctionality.search.Trip;
import utn.proy2k18.vantrack.models.Reservation;


public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ModelViewHolder>
        implements View.OnClickListener {

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation, parent,
                false);
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

        private TextView origin;
        private TextView destination;
        private TextView date;
        private TextView time;
        private TextView company;
        private TextView price;
        private TextView status;
        private DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
        private DateTimeFormatter tf = DateTimeFormat.forPattern("HH:mm");

        public ModelViewHolder(View itemView) {
            super(itemView);
            this.origin = itemView.findViewById(R.id.res_origin);
            this.destination = itemView.findViewById(R.id.res_destination);
            this.date = itemView.findViewById(R.id.res_date);
            this.time = itemView.findViewById(R.id.res_time);
            this.company = itemView.findViewById(R.id.res_company);
            this.price = itemView.findViewById(R.id.res_price);
            this.status = itemView.findViewById(R.id.res_status);

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

        private String removeAfterComma(String string) {
            return string.split(",")[0];
        }

        public void bind(Reservation reservation) {
            Trip bookedTrip = reservation.getBookedTrip();
            origin.setText(removeAfterComma(reservation.getHopOnStop().getDescription()));
            destination.setText(removeAfterComma(bookedTrip.getDestination()));
            date.setText(bookedTrip.getDate().toString(dtf));
            time.setText(bookedTrip.getTime().toString(tf));
            company.setText(bookedTrip.getCompanyName());
            price.setText(String.format(Locale.getDefault(), "$%.2f",
                    reservation.getReservationPrice()));

            if(reservation.isPendingReservation()) {
                status.setBackgroundColor(Color.RED);
            } else {
                status.setBackgroundColor(Color.GREEN);
            }
        }
    }
}
