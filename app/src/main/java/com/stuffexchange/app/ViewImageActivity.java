package com.stuffexchange.app;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


public class ViewImageActivity extends ActionBarActivity {
    private final String LOGTAG = "StuffExchange";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        String imageId = getIntent().getStringExtra("ImageId");
        StuffExchangeApplication app = (StuffExchangeApplication) getApplication();
        DataAccess dataAccess = app.getDataAccess();
        Log.d(LOGTAG, "ViewImage got imageId: " + imageId);
        dataAccess.GetImage(new ImageSetter(), imageId);
    }

    private class ImageSetter implements OnTaskCompleted {
        @Override
        public void onTaskCompleted(Object o) {
            if (o != null) {
                Bitmap image = (Bitmap) o;
                setImage(image);
            }
        }
    }

    private void setImage(Bitmap image) {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(image);
        imageView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_image, menu);
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
