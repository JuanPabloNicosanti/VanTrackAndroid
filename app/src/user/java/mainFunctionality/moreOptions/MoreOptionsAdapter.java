package mainFunctionality.moreOptions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import utn.proy2k18.vantrack.R;

public class MoreOptionsAdapter extends ArrayAdapter<Option> {

    public MoreOptionsAdapter(Context context, ArrayList<Option> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Option option = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.option, parent, false);
            convertView.setOnClickListener(option.getOnClickListener());
        }
        // Lookup view for data population
        TextView opName = (TextView) convertView.findViewById(R.id.option_title);
        ImageView opImage = (ImageView) convertView.findViewById(R.id.option_image);
        // Populate the data into the template view using the data object
        opName.setText(option.getTitle());
        opImage.setImageResource(option.getImage());
        // Return the completed view to render on screen
        return convertView;
    }

}
