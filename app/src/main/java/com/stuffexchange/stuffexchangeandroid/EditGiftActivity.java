package com.stuffexchange.stuffexchangeandroid;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class EditGiftActivity extends ActionBarActivity {
    private final String LOGTAG = "StuffExchange";
    private Uri photoUri;
    private final int ADD_IMAGE_REQUEST_CODE = 100;
    private String giftId;
    private String token;
    private int pixels;
    private DataAccess dataAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gift);
        int dp = 100;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
        pixels = Math.round(px);
        giftId = getIntent().getStringExtra("GiftId");
        StuffExchangeApplication app = (StuffExchangeApplication) getApplication();
        token = app.getToken();
        dataAccess = app.getDataAccess();
        dataAccess.GetGift(new GiftGetter(), giftId);

        Button addImageButton = (Button) findViewById(R.id.addImageButton);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File photo = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "image.jpeg");
                photoUri = Uri.fromFile(photo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, ADD_IMAGE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == ADD_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getContentResolver().notifyChange(photoUri, null);
                ContentResolver cr = getContentResolver();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(cr, photoUri);
                    dataAccess.AddImage(new ImageUploader(), giftId, photoUri, token);
                } catch (IOException ex) {
                    // TODO handle
                }
            }
        }
    }

    private class ImageUploader implements OnTaskCompleted {
        @Override
        public void onTaskCompleted(Object o) {
           Log.d(LOGTAG, "Image uploaded! Maybe!");
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
                EditGiftActivity.this.setGift(gift);
            }
        }
    }

    private void setGift(Gift gift) {
        EditText titleEditText = (EditText) findViewById(R.id.titleEditText);
        titleEditText.setText(gift.getTitle());
        EditText descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        descriptionEditText.setText(gift.getDescription());
        if (gift.hasImages()) {
            for (String imageId : gift.getImages()) {
                String thumbId = imageId + "_thumb";
                dataAccess.GetImage(new ImageSetter(imageId), thumbId);
            }
        } else {
            HorizontalScrollView gallery = (HorizontalScrollView) findViewById(R.id.gallery);
            gallery.setVisibility(View.GONE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_gift, menu);
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
