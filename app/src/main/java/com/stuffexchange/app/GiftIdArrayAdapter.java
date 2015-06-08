package com.stuffexchange.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stuffexchange.dataAccess.DataAccess;
import com.stuffexchange.dataAccess.OnTaskCompleted;
import com.stuffexchange.model.Gift;

import java.util.List;

class GiftIdArrayAdapter extends ArrayAdapter<String> {
    private Context context;
    private DataAccess dataAccess;

    public GiftIdArrayAdapter(Context context, DataAccess dataAccess, List<String> giftIds) {
        super(context, R.layout.gift_list_layout, giftIds);
        this.context = context;
        this.dataAccess = dataAccess;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        GiftViewHolder viewHolder = null;
        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.gift_list_layout, parent, false);
            viewHolder = new GiftViewHolder(row);
            row.setTag(viewHolder);
        } else {
            viewHolder = (GiftViewHolder) row.getTag();
        }

        String giftId = getItem(position);
        dataAccess.GetGift(new GiftGetter(context, dataAccess, viewHolder, position), giftId);
        return row;
    }

    public class GiftViewHolder {
        ImageView giftImageView;
        TextView titleTextView;
        TextView descriptionTextView;

        public GiftViewHolder(View v) {
            giftImageView = (ImageView) v.findViewById(R.id.giftImageView);
            titleTextView = (TextView) v.findViewById(R.id.commentUser);
            descriptionTextView = (TextView) v.findViewById(R.id.giftDescription);
        }
    }

    private class GiftGetter implements OnTaskCompleted {
        private GiftIdArrayAdapter.GiftViewHolder giftView;
        private int position;
        private Context context;
        private DataAccess dataAccess;
        public GiftGetter(Context context, DataAccess dataAccess,
                          GiftIdArrayAdapter.GiftViewHolder giftView, int position) {
            this.giftView = giftView;
            this.position = position;
            this.context = context;
            this.dataAccess = dataAccess;
        }
        @Override
        public void onTaskCompleted(Object o) {
            if (o == null) {
                String message = "Could not get gift from server";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            } else {
                Gift gift = (Gift) o;
                ImageView giftImageView = giftView.giftImageView;
                giftImageView.setTag(position);
                if (gift.hasImages()) {
                    String imageId = gift.getCoverImage() + "_thumb";
                    dataAccess.GetImage(new CoverImageGetter(context,
                            giftImageView, position), imageId);
                } else {
                    giftImageView.setImageResource(R.drawable.default_image);
                }
                giftView.titleTextView.setText(gift.getTitle());
                giftView.descriptionTextView.setText(gift.getDescription());
            }
        }
    }

    private class CoverImageGetter implements OnTaskCompleted {
        ImageView mImageView;
        int mPosition;
        Context context;
        public CoverImageGetter(Context context, ImageView imageView, int position) {
            mImageView = imageView;
            mPosition = position;
            this.context = context;
        }
        @Override
        public void onTaskCompleted(Object o) {
            if (o == null) {
                String message = "Could not get image from server";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            }
            else {
                if ((int)mImageView.getTag() == mPosition) {
                    Bitmap image = (Bitmap) o;
                    mImageView.setImageBitmap(image);
                }
            }
        }
    }
}
