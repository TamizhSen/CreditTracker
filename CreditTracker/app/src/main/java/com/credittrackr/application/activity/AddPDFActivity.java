package com.credittrackr.application.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.credittrackr.application.R;
import com.credittrackr.application.app.AppConfig;
import com.credittrackr.application.helper.FilePath;
import com.credittrackr.application.helper.SQLiteHandler;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.UUID;

public class AddPDFActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout selectPDF, UploadPDF, viewPDFCard, viewPDFExpenses, viewPDFGraph;

    public static final String UPLOAD_URL = AppConfig.URL_PDF_UPLOAD;

    //Pdf request code
    private int PICK_PDF_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;


    //Uri to store the image uri
    private Uri filePath;
    private SQLiteHandler db;
    String email = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pdf);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Requesting storage permission
        requestStoragePermission();

        //Initializing views
        selectPDF = (LinearLayout) findViewById(R.id.selectPDF);
        UploadPDF = (LinearLayout) findViewById(R.id.UploadPDF);
        viewPDFCard = (LinearLayout) findViewById(R.id.viewPDFCard);
        viewPDFExpenses = (LinearLayout) findViewById(R.id.viewPDFExpenses);
        // viewPDFGraph = (LinearLayout) findViewById(R.id.viewPDFGraph);

        //Setting clicklistener
        selectPDF.setOnClickListener(this);
        UploadPDF.setOnClickListener(this);
        viewPDFCard.setOnClickListener(this);
        viewPDFExpenses.setOnClickListener(this);
        db = new SQLiteHandler(getApplicationContext());
        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        email = user.get("email");
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

    /*
     * This is the method responsible for pdf upload
     * We need the full pdf path and the name for the pdf in this method
     * */

    public void uploadMultipart() {
        //getting name for the image

        if (filePath == null) {
            Toast.makeText(this, "Please select a pdf file to upload", Toast.LENGTH_LONG).show();
            return;
        }

        //getting the actual path of the image
        String path = FilePath.getPath(this, filePath);

        if (path == null) {

            Toast.makeText(this, "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            //Uploading code
            try {

                // Toast.makeText(this, "Please move your .pdf file to internal storage and retry"+ path, Toast.LENGTH_LONG).show();
                String uploadId = UUID.randomUUID().toString();

                //Creating a multi part request
                new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                        .addFileToUpload(path, "pdf") //Adding file
                        .addParameter("username", email) //Adding text parameter to the request
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload

            } catch (Exception exc) {
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
        }
    }


    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void viewStatement() {
        // Launching the login activity
        Intent intent = new Intent(AddPDFActivity.this, PDFStatementActivity.class);
        startActivity(intent);
        // finish();
    }

    public void viewCard() {
        // Launching the login activity
        Intent intent = new Intent(AddPDFActivity.this, PDFCardActivity.class);
        startActivity(intent);
        // finish();
    }



    @Override
    public void onClick(View v) {
        if (v == selectPDF) {
            showFileChooser();
        }
        if (v == UploadPDF) {
            uploadMultipart();
        }
        if (v == viewPDFExpenses) {
            viewStatement();
        }
        if (v == viewPDFCard) {
            viewCard();
        }
    }
}
