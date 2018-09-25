package utn.proy2k18.vantrack.mainFunctionality.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import utn.proy2k18.vantrack.R;

public class TripStopsAdapter extends RecyclerView.Adapter<TripStopsAdapter.ModelViewHolder> implements View.OnClickListener {

        private List<TripStop> items;
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

        public TripStopsAdapter(List<TripStop> items) {
            this.items = items;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public ModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_stop, parent, false);
            view.setOnClickListener(this);
            return new ModelViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return items != null ? items.size() : 0;
        }

        @Override
        public void onBindViewHolder(ModelViewHolder holder, int position) {
            holder.bind(items.get(position));
        }

        public void setItems(List<TripStop> items) {
            this.items = items;
        }

        public class ModelViewHolder extends RecyclerView.ViewHolder{

            private TextView stopDescription;
            private TextView stopHour;
            private DateTimeFormatter tf = DateTimeFormat.forPattern("HH:mm");

            public ModelViewHolder(View itemView) {
                super(itemView);
                this.stopDescription = itemView.findViewById(R.id.stopDescription);
                this.stopHour = itemView.findViewById(R.id.stopHour);

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

            public void bind(TripStop tripStop) {
                stopDescription.setText(tripStop.getDescription());
                stopHour.setText(tripStop.getHour().toString(tf));
            }
        }

}
