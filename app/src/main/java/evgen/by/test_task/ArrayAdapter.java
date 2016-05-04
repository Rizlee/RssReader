package evgen.by.test_task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by evgen on 03.05.2016.
 */
public class ArrayAdapter extends android.widget.ArrayAdapter<String> {
    private final Context context;
    private final String[] dates;
    private final String[] headers;

    public ArrayAdapter(Context context, String[] headers, String[] dates) {
        super(context,R.layout.elem_of_list_news,headers);
        this.context = context;
        this.dates = dates;
        this.headers = headers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.elem_of_list_news, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textViewHeader);
        TextView textViewDate = (TextView) rowView.findViewById(R.id.textViewDate);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(headers[position]);
        textViewDate.setText(dates[position]);
        imageView.setImageResource(R.drawable.no_image_icon);

        return rowView;
    }
}
