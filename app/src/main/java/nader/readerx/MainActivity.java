package nader.readerx;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
	
	private static final int PICK_FILE_REQUEST_CODE = 100;
	public static TextView t;
	private FloatingActionButton settingsButton;
	
	private int lastStart = -1;
	private int lastEnd = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		t = findViewById(R.id.txtc);
		t.setTextIsSelectable(false);
		t.setMovementMethod(LinkMovementMethod.getInstance());
		
		settingsButton = findViewById(R.id.settings_button);
		settingsButton.setOnClickListener(v -> {
			SettingsFragment settingsFragment = new SettingsFragment();
			settingsFragment.show(getSupportFragmentManager(), settingsFragment.getTag());
		});
		
		openFileChooser();
	}
	
	private void openFileChooser() {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
	}
	
	private String getFileName(Uri uri) {
		String result = null;
		if (uri.getScheme().equals("content")) {
			Cursor cursor = getContentResolver().query(uri, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
				cursor.close();
			}
			} else if (uri.getScheme().equals("file")) {
			result = uri.getPath();
		}
		return result;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == PICK_FILE_REQUEST_CODE) {
			Uri uri = data.getData();
			String fileName = getFileName(uri);
			Toast.makeText(MainActivity.this, "Selected file: " + fileName, Toast.LENGTH_SHORT).show();
			readFileContents(uri);
		}
	}
	
	private void readFileContents(Uri uri) {
		try {
			InputStream inputStream = getContentResolver().openInputStream(uri);
			if (inputStream != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
				reader.close();
				Toast.makeText(MainActivity.this,"Detected Language: "+NaderLanguageDetector.getMajorityLanguage(sb.toString())+" ", Toast.LENGTH_LONG).show();
				setInteractiveText(sb.toString());
			}
			} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(MainActivity.this, "Error reading file.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setInteractiveText(String text) {
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		Pattern pattern = Pattern.compile("\\b\\w+\\b");
		Matcher matcher = pattern.matcher(text);
		
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			String word = text.substring(start, end);
			
			builder.setSpan(new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					SpannableStringBuilder currentText = new SpannableStringBuilder(t.getText());
					
					// Remove previous highlight
					if (lastStart >= 0 && lastEnd > lastStart) {
						BackgroundColorSpan[] bgSpans = currentText.getSpans(lastStart, lastEnd, BackgroundColorSpan.class);
						ForegroundColorSpan[] fgSpans = currentText.getSpans(lastStart, lastEnd, ForegroundColorSpan.class);
						for (BackgroundColorSpan span : bgSpans) currentText.removeSpan(span);
						for (ForegroundColorSpan span : fgSpans) currentText.removeSpan(span);
					}
					
					// Apply new highlight
					currentText.setSpan(new BackgroundColorSpan(Color.parseColor("#87CEFA")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					currentText.setSpan(new ForegroundColorSpan(Color.BLACK), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					
					t.setText(currentText, TextView.BufferType.SPANNABLE);
					lastStart = start;
					lastEnd = end;
					
					Toast.makeText(MainActivity.this, word, Toast.LENGTH_SHORT).show();
				}
				
				@Override
				public void updateDrawState(android.text.TextPaint ds) {
					// Prevent underline or link-like style
					ds.setUnderlineText(false);
					ds.setColor(t.getCurrentTextColor());
				}
			}, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		t.setText(builder);
	}
}