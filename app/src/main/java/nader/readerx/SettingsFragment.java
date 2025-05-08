package nader.readerx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SettingsFragment extends BottomSheetDialogFragment {
	
	private TextView previewText;
	private SeekBar fontSizeSeekBar, paddingSeekBar, spaceSeekBar;
	private Spinner fontSpinner;
	private String originalText = "";
	
	public SettingsFragment() {
		// Required empty public constructor
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		
		//previewText = rootView.findViewById(R.id.preview_text);
		fontSizeSeekBar = rootView.findViewById(R.id.font_size_seekbar);
		paddingSeekBar = rootView.findViewById(R.id.margin_seekbar);
		spaceSeekBar = rootView.findViewById(R.id.space_seekbar);
		fontSpinner = rootView.findViewById(R.id.font_spinner);
		
		if (MainActivity.t != null && MainActivity.t.getText() != null) {
			originalText = MainActivity.t.getText().toString();
		}
		
		// Font size adjustment
		fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				MainActivity.t.setTextSize(progress);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
		
		// Padding adjustment
		paddingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				MainActivity.t.setPadding(progress, progress, progress, progress);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
		
		// Word spacing adjustment (debounced)
		spaceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int progress = seekBar.getProgress();
				applySpacing(progress);
			}
		});
		
		return rootView;
	}
	
	private void applySpacing(int progress) {
		if (originalText == null || originalText.isEmpty()) return;
		
		new Thread(() -> {
			String spacing = new String(new char[progress]).replace('\0', ' ');
			String spacedText = originalText.trim().replaceAll("\\s+", spacing);
			
			if (getActivity() != null) {
				getActivity().runOnUiThread(() -> {
					if (MainActivity.t != null) {
						MainActivity.t.setText(spacedText);
					}
				});
			}
		}).start();
	}
}