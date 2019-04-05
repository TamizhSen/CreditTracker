package com.credittrackr.application.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.credittrackr.application.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String htmlString =
                "<p>Welcome to Credit Tracker’s Privacy Statement. Your right to online security and privacy is important. This Privacy Statement describes CreditTracker Inc.’s collection, protection, disclosure, and use of your information. This Privacy Statement applies to to all CreditTracker’s websites, mobile applications, and/or online services, that link to or reference this Privacy Statement. It covers your personal information that is provided or collected through our services and how we use that information in connection with our services.</p>\n" +
                        "<p>By using CreditTracker, you agree to this Privacy Statement. If you do not agree with any term in this Privacy Statement, please do not use our services.</p>\n" +
                        "<p>Expenses you add in CreditTracker are not public. Only you can edit, delete, and undelete the expenses that have been shared with them.</p>\n" +
                        "<p>In order to use CreditTracker, we ask that you create an account. When you sign up we'll ask for some registration information such as name, email address and phone number. We need this information to create an account so that we can contact you about your account.</p>\n" +
                        "<p>In addition to sharing your registration information to set up your account, we collect some other information about you, such as type of expenses you add, Card Details, Type of card and their expenses details. We use this information to help improve and secure our service and to show your visual expenses graph based on your usage of our services.</p>\n" +
                        "<p>You have control over the type of information you provide and how we use it. We don’t share your registration or personal information for marketing purposes. We will never share your information with other parties for use other than what’s laid out in this policy without your consent. You can view your Privacy and Notification Settings from your Account Settings page. If you have any questions, please contact us at credittrackr@gmail.com.</p>\n" +
                        "<p>CreditTracker or its partners or service providers may maintain operations in the United States or any other country, and therefore your personal information may be processed outside the country where you are located. By using and providing information to us, you agree and consent to the transfer, storage, and processing of your information in other countries, including for example outside the European Economic Area. Countries that we transfer your information to may not have data protection laws like those of where you are located.</p>\n" +
                        "<p>CreditTracker is not intended for use by children under 13 years of age or minors. We do not knowingly collect personally identifiable information from children under 13. If a parent or guardian becomes aware that their child has provided us with Personal Information without their consent, they should contact us at credittrackr@gmail.com. If we become aware that a child under 13 has provided us with Personal Information, we will delete such information from our files immediately.</p>\n";
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
