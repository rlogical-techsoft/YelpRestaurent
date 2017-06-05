package com.yelprestaurant;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Category;
import com.yelprestaurant.utils.SquareImageView;
import com.yelprestaurant.utils.TypefaceUtils;

public class RestaurantMoreInfo extends AppCompatActivity {

    private Context context;
    private Business selBusiness;

    private SquareImageView img_restaurant;

    private TextView txt_restaurant_name;
    private TextView txt_restaurant_status;

    private TextView txt_restaurant_categories;

    private TextView txt_restaurant_address_information;
    private TextView txt_restaurant_address;
    private TextView txt_restaurant_distance;

    private TextView item_txt_contact_information;
    private TextView item_txt_radar_phone_1;
    private TextView item_txt_radar_phone_2;

    private TextView item_txt_rating_review_information;
    private TextView item_txt_radar_rating;
    private TextView item_txt_radar_total_reviews;

    private CardView item_card_website;
    private TextView item_txt_website;

    private Typeface exoFonts;
    private Typeface assistantBold;
    private Typeface assistantSemibold;
    private Typeface assistantRegular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_more_detail);

        context = this;

        exoFonts = TypefaceUtils.getExo2RegularFont(this);
        assistantBold = TypefaceUtils.getAssistantBoldFont(this);
        assistantSemibold = TypefaceUtils.getAssistantSemiBoldFont(this);
        assistantRegular = TypefaceUtils.getAssistantRegularFont(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String resMoreInfo = getIntent().getStringExtra("restaurant_detail");
        selBusiness = new Gson().fromJson(resMoreInfo, Business.class);

        getSupportActionBar().setTitle(selBusiness.getName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_restaurant = (SquareImageView) findViewById(R.id.img_restaurant);
        Picasso.with(context).load(selBusiness.getImageUrl()).into(img_restaurant);

        txt_restaurant_name = (TextView) findViewById(R.id.txt_restaurant_name);
        txt_restaurant_name.setText(selBusiness.getName());
        txt_restaurant_name.setTypeface(exoFonts, Typeface.BOLD);

        txt_restaurant_status = (TextView) findViewById(R.id.txt_restaurant_status);
        txt_restaurant_status.setText(selBusiness.getIsClosed() ? "Closed" : "Open");
        txt_restaurant_status.setTextColor(selBusiness.getIsClosed() ? getResources().getColor(R.color.Red) : getResources().getColor(R.color.Green));
        txt_restaurant_status.setTypeface(assistantSemibold);

        txt_restaurant_categories = (TextView) findViewById(R.id.txt_restaurant_categories);
        txt_restaurant_categories.setTypeface(assistantSemibold);

        String categories = "";

        for (Category cat : selBusiness.getCategories()) {

            if (categories.length() > 0)
                categories += ", ";

            categories += cat.getTitle();
        }

        txt_restaurant_categories.setText("Categories: " + categories);

        txt_restaurant_address_information = (TextView) findViewById(R.id.txt_restaurant_address_information);
        txt_restaurant_address_information.setTypeface(assistantBold);

        txt_restaurant_address = (TextView) findViewById(R.id.txt_restaurant_address);
        txt_restaurant_address.setTypeface(assistantRegular);

        String address = "Address: ";

        for (String addr : selBusiness.getLocation().getDisplayAddress())
            address += addr;

        address += " (" + selBusiness.getLocation().getCountry() + " )";

        txt_restaurant_address.setText(address);

        txt_restaurant_distance = (TextView) findViewById(R.id.txt_restaurant_distance);
        txt_restaurant_distance.setText("Distance: " + selBusiness.getDistance());
        txt_restaurant_distance.setTypeface(assistantRegular);

        item_txt_contact_information = (TextView) findViewById(R.id.item_txt_contact_information);
        item_txt_contact_information.setTypeface(assistantBold);

        item_txt_radar_phone_1 = (TextView) findViewById(R.id.item_txt_radar_phone_1);
        item_txt_radar_phone_1.setText("Phone 1: " + selBusiness.getPhone());
        item_txt_radar_phone_1.setTypeface(assistantRegular);

        item_txt_radar_phone_2 = (TextView) findViewById(R.id.item_txt_radar_phone_2);
        item_txt_radar_phone_2.setText("Phone 2: " + selBusiness.getDisplayPhone());
        item_txt_radar_phone_2.setTypeface(assistantRegular);

        item_txt_rating_review_information = (TextView) findViewById(R.id.item_txt_rating_review_information);
        item_txt_rating_review_information.setTypeface(assistantBold);

        item_txt_radar_rating = (TextView) findViewById(R.id.item_txt_radar_rating);
        item_txt_radar_rating.setText("Rating: " + selBusiness.getRating());
        item_txt_radar_rating.setTypeface(assistantRegular);

        item_txt_radar_total_reviews = (TextView) findViewById(R.id.item_txt_radar_total_reviews);
        item_txt_radar_total_reviews.setText("Total Reviews: " + selBusiness.getReviewCount());
        item_txt_radar_total_reviews.setTypeface(assistantRegular);

        item_card_website = (CardView) findViewById(R.id.item_card_website);
        item_card_website.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(selBusiness.getUrl()));
                startActivity(i);
            }
        });

        item_txt_website = (TextView) findViewById(R.id.item_txt_website);
        item_txt_website.setTypeface(assistantSemibold);
    }
}