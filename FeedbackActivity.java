package com.example.medilab.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.medilab.database.DatabaseHelper;
import com.example.medilab.models.FeedbackItem;
import com.example.medilab.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FeedbackActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private RatingBar ratingBar;
    private TextInputEditText etFeedbackComments;
    private MaterialButton btnSubmitFeedback;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        ratingBar = findViewById(R.id.ratingBar);
        etFeedbackComments = findViewById(R.id.etFeedbackComments);
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback);

        btnBack.setOnClickListener(v -> finish());

        btnSubmitFeedback.setOnClickListener(v -> {
            int rating = (int) ratingBar.getRating();
            String comments = etFeedbackComments.getText() != null ? etFeedbackComments.getText().toString().trim() : "";
            if (TextUtils.isEmpty(comments)) {
                comments = "Rating provided: " + rating + " stars";
            }

            int userId = sessionManager.getUserId();
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            FeedbackItem feedback = new FeedbackItem(0, userId, rating, comments, date);
            long id = dbHelper.addFeedback(feedback);

            if (id > 0) {
                Toast.makeText(this, "Thank you! Your feedback has been recorded.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to submit feedback", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
