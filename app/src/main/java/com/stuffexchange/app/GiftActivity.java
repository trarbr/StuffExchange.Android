package com.stuffexchange.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stuffexchange.dataAccess.DataAccess;
import com.stuffexchange.dataAccess.OnTaskCompleted;
import com.stuffexchange.model.Comment;
import com.stuffexchange.model.Gift;
import com.stuffexchange.model.User;

import java.util.List;


public class GiftActivity extends ActionBarActivity {
    private final String LOGTAG = "StuffExchange";
    private Gift gift;
    private String giftId;
    private String token;
    private String userId;
    private DataAccess dataAccess;
    private int pixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift);
        giftId = getIntent().getStringExtra("GiftId");
        Log.d(LOGTAG, "Got giftId: " + giftId);
        int dp = 100;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
        pixels = Math.round(px);

        StuffExchangeApplication app = (StuffExchangeApplication) getApplication();
        userId = app.getUserId();
        token = app.getToken();
        dataAccess = app.getDataAccess();
        dataAccess.GetGift(new GiftGetter(), giftId);
    }

    private class ButtonSetter implements OnTaskCompleted {
        @Override
        public void onTaskCompleted(Object o) {
            if (o == null) {
                String message = "Could not get user from server";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            }
            else {
                User user = (User) o;
                GiftActivity.this.setButtons(user);
            }
        }
    }

    private class WishMaker implements OnTaskCompleted {
        @Override
        public void onTaskCompleted(Object o) {
            if (o != null && (Boolean) o) {
                String message = "Wish made!";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
                dataAccess.GetGift(new GiftGetter(), giftId);
            } else {
                String message = "Failed to make wish";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            }
        }
    }

    private class WishUnmaker implements OnTaskCompleted {
        @Override
        public void onTaskCompleted(Object o) {
            if (o != null && (Boolean) o) {
                String message = "Wish unmade!";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
                dataAccess.GetGift(new GiftGetter(), giftId);
            } else {
                String message = "Failed to unmake wish";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            }
        }
    }

    private class OfferAccepter implements OnTaskCompleted {
        @Override
        public void onTaskCompleted(Object o) {
            if (o != null && (Boolean) o) {
                String message = "Offer accepted!";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
                dataAccess.GetGift(new GiftGetter(), giftId);
            } else {
                String message = "Failed to accept offer";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            }
        }
    }

    private void setButtons(User user) {
        // TODO: add button for add comment
        Button makeOfferButton = (Button) findViewById(R.id.makeOfferButton);
        makeOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MakeOfferActivity.class);
                intent.putExtra("GiftId", giftId);
                startActivity(intent);
            }
        });
        Button makeWishButton = (Button) findViewById(R.id.makeWishButton);
        makeWishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataAccess.MakeWish(new WishMaker(), giftId, token);
            }
        });
        Button unmakeWishButton = (Button) findViewById(R.id.unmakeWishButton);
        unmakeWishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataAccess.UnmakeWish(new WishUnmaker(), giftId, token);
            }
        });
        Button acceptOfferButton = (Button) findViewById(R.id.acceptOfferButton);
        acceptOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataAccess.AcceptOffer(new OfferAccepter(), giftId, token);
            }
        });
        Button declineOfferButton = (Button) findViewById(R.id.declineOfferButton);
        makeOfferButton.setVisibility(View.GONE);
        makeWishButton.setVisibility(View.GONE);
        unmakeWishButton.setVisibility(View.GONE);
        acceptOfferButton.setVisibility(View.GONE);
        declineOfferButton.setVisibility(View.GONE);
        if (this.gift.getUser().getId().equals(this.userId)) {
            Log.d(LOGTAG, "Gift belongs to this user");
            if (this.gift.getGiftState() != Gift.GiftState.GivenAway &&
                    !this.gift.isOffered() && this.gift.hasWishers()) {
                makeOfferButton.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (!user.getWishList().contains(this.giftId)) {
                makeWishButton.setVisibility(View.VISIBLE);
            } else if (this.gift.getGiftState() != Gift.GiftState.GivenAway) {
                if (this.gift.isOffered() && this.gift.getOfferedTo().getId().equals(this.userId)) {
                    acceptOfferButton.setVisibility(View.VISIBLE);
                    declineOfferButton.setVisibility(View.VISIBLE);
                } else {
                    unmakeWishButton.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private class GiftGetter implements OnTaskCompleted {
        @Override
        public void onTaskCompleted(Object o) {
            if (o == null) {
                String message = "Could not get gift from server";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            }
            else {
                Gift gift = (Gift) o;
                GiftActivity.this.setGift(gift);
            }
        }
    }

    private class ImageSetter implements OnTaskCompleted {
        private String imageId;
        public ImageSetter(String imageId) {
            this.imageId = imageId;
        }
        @Override
        public void onTaskCompleted(Object o) {
            if (o != null) {
                Bitmap image = (Bitmap) o;
                addImageToGallery(imageId, image);
            } else {
                Log.d(LOGTAG, "Failed to get image");
            }
        }
    }

    public void addImageToGallery(String imageId, Bitmap image) {
        LinearLayout galleryLayout = (LinearLayout) findViewById(R.id.galleryLayout);
        ImageView view = new ImageView(getApplicationContext());
        view.setImageBitmap(image);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pixels, pixels);
        params.setMargins(0, 0, 10, 0);
        view.setLayoutParams(params);
        view.setTag(imageId);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageId = (String) v.getTag();
                Intent intent = new Intent(getApplicationContext(), ViewImageActivity.class);
                intent.putExtra("ImageId", imageId);
                startActivity(intent);
            }
        });
        galleryLayout.addView(view);
    }

    private void setGift(Gift gift) {
        this.gift = gift;
        dataAccess.GetUser(new ButtonSetter(), this.userId, this.token);
        LinearLayout galleryLayout = (LinearLayout) findViewById(R.id.galleryLayout);
        galleryLayout.removeAllViews();
        if (gift.hasImages()) {
            for (String imageId : gift.getImages()) {
                String thumbId = imageId + "_thumb";
                dataAccess.GetImage(new ImageSetter(imageId), thumbId);
            }
        } else {
            galleryLayout.setVisibility(View.GONE);
        }
        TextView titleTextView = (TextView) findViewById(R.id.giftTitle);
        titleTextView.setText(gift.getTitle());
        TextView giftStatusTextView = (TextView) findViewById(R.id.giftStatus);
        giftStatusTextView.setText(gift.getStatus());
        TextView descriptionTextView = (TextView) findViewById(R.id.giftDescription);
        descriptionTextView.setText(gift.getDescription());
        TextView offeredToTextView = (TextView) findViewById(R.id.giftOfferedTo);
        if (gift.isOffered()) {
            offeredToTextView.setText("Offered to " + gift.getOfferedTo().getUsername());
            offeredToTextView.setVisibility(View.VISIBLE);
        } else {
            offeredToTextView.setVisibility(View.GONE);
        }
        if (gift.hasComments()) {
            final ListView commentsListView = (ListView)findViewById(R.id.commentsListView);
            final CommentArrayAdapter adapter = new CommentArrayAdapter(GiftActivity.this, gift.getComments());
            commentsListView.setAdapter(adapter);
        }
    }

    private class CommentArrayAdapter extends ArrayAdapter<Comment> {

        public CommentArrayAdapter(Context context, List<Comment> comments) {
            super(context, R.layout.comment_layout, comments);
        }

        public class CommentViewHolder {
            TextView usernameTextView;
            TextView timestampTextView;
            TextView contentTextView;

            public CommentViewHolder(View view) {
                usernameTextView = (TextView) view.findViewById(R.id.username);
                timestampTextView = (TextView) view.findViewById(R.id.timestamp);
                contentTextView = (TextView) view.findViewById(R.id.content);
            }

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            CommentViewHolder viewHolder = null;
            if (row == null) {
                row = getLayoutInflater().inflate(R.layout.comment_layout, parent, false);
                viewHolder = new CommentViewHolder(row);
                row.setTag(viewHolder);
            } else {
                viewHolder = (CommentViewHolder) row.getTag();
            }

            Comment comment = getItem(position);
            viewHolder.usernameTextView.setText(comment.getUsername());
            viewHolder.timestampTextView.setText(comment.getTimestamp().toString());
            viewHolder.contentTextView.setText(comment.getContent());

            return row;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gift, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.editGift) {
            Intent intent = new Intent(getApplicationContext(), EditGiftActivity.class);
            intent.putExtra("GiftId", this.giftId);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
