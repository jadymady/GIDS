package trainedge.jamal;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Button btnSend = (Button) findViewById(R.id.btnSend);
        final EditText etFeedback = (EditText) findViewById(R.id.etFeedback);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = etFeedback.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:hasanjamalinfo@gmail.com")); // only email apps should handle this
                String addresses = "hasanjamalinfo@gmail.com";
                intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                intent.putExtra(Intent.EXTRA_SUBJECT, "GIDS feedback");
                intent.putExtra(Intent.EXTRA_TEXT, msg);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    etFeedback.setText("");
                    startActivity(intent);
                }
            }
        });
    }
}
