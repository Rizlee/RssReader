package evgen.by.test_task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by evgen on 03.05.2016.
 */
public class ArrayAdapter extends android.widget.ArrayAdapter<String> {
    private final Context context;
    private final String[] dates;
    private final String[] headers;
    private final String[] images;

    public ArrayAdapter(Context context, String[] headers, String[] dates, String[] images) {
        super(context,R.layout.elem_of_list_news,headers);
        this.context = context;
        this.dates = dates;
        this.headers = headers;
        this.images = images;
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


        if (images[position].equals("")){
            imageView.setImageResource(R.drawable.no_image_icon);
        }else {
            Picasso.with(context).load(images[position]).into(imageView);
        }

        return rowView;
    }
}
