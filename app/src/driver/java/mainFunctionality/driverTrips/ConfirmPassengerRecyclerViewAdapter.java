package mainFunctionality.driverTrips;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mainFunctionality.driverTrips.ConfirmPassengersFragment.OnListFragmentInteractionListener;

import java.util.List;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.models.Passenger;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Passenger} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class ConfirmPassengerRecyclerViewAdapter extends RecyclerView.Adapter<ConfirmPassengerRecyclerViewAdapter.ViewHolder> {

    private final List<Passenger> mValues;
    private final OnListFragmentInteractionListener mListener;

    public ConfirmPassengerRecyclerViewAdapter(List<Passenger> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_confirm_passenger, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getNameAndSurname());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView mContentView;
        public Passenger mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.nameAndSurname);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
