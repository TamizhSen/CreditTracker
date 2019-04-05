package com.credittrackr.application.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.credittrackr.application.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String htmlString =
                "<p>CreditTracker is an Android Banking App, It is based on your credit expense information. We organize all your shared expenses in one place, so that you can see how much you owe, how to pay back your credit balance without interest and keep track of it. Whether you are spending a vacation, sharing rent with roommates, CreditTracker makes life easier. We store data “in the cloud,” so that you can access it anywhere: on your Phone.</p>\n" +
                        "<p>Most people want to be perfect with their credit history, but they tend to forget it. This App helps you to keep track of your Credit card history and balances. This helps you pay back all your owing balances perfectly on time and to maintain a good credit history. Here you can add your expenses either manually or by uploading the pdf. You can also keep track of your credit card expenses history graphically, which provides your complete expenses history either monthly or yearly, also we provide you with a mail notification which helps you pay your bills on time.</p>\n";
        TextView textView = (TextView) findViewById(R.id.display_html_string);
        textView.setText(Html.fromHtml(htmlString));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
