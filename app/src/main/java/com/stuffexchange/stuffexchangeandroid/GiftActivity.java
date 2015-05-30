package com.stuffexchange.stuffexchangeandroid;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class GiftActivity extends ActionBarActivity {
    private final String LOGTAG = "StuffExchange";
    private Gift gift;
    private String giftId;
    private String token;
    private String userId;
    private DataAccess dataAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift);
        giftId = getIntent().getStringExtra("GiftId");
        Log.d(LOGTAG, "Got giftId: " + giftId);

        StuffExchangeApplication app = (StuffExchangeApplication) getApplication();
        userId = app.getUserId();
        token = app.getToken();
        dataAccess = app.getDataAccess();
        String userId = app.getUserId();
        String token = app.getToken();
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

    private void setButtons(User user) {
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
                // TODO: make wish
            }
        });
        Button unmakeWishButton = (Button) findViewById(R.id.unmakeWishButton);
        Button acceptOfferButton = (Button) findViewById(R.id.acceptOfferButton);
        acceptOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: accept offer
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
                if (this.gift.getOfferedTo().contains(this.userId)) {
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

    private void setGift(Gift gift) {
        this.gift = gift;
        dataAccess.GetUser(new ButtonSetter(), this.userId, this.token);
        TextView titleTextView = (TextView) findViewById(R.id.giftTitle);
        titleTextView.setText(gift.getTitle());
        TextView giftStatusTextView = (TextView) findViewById(R.id.giftStatus);
        giftStatusTextView.setText(gift.getStatus());
        TextView descriptionTextView = (TextView) findViewById(R.id.giftDescription);
        descriptionTextView.setText(gift.getDescription());
        TextView offeredToTextView = (TextView) findViewById(R.id.giftOfferedTo);
        if (gift.isOffered()) {
            offeredToTextView.setText(gift.getOfferedTo());
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

        return super.onOptionsItemSelected(item);
    }
}
