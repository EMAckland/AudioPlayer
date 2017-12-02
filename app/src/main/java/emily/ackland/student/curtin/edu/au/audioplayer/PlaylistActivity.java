package emily.ackland.student.curtin.edu.au.audioplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Emily on 12/2/2017.
 */

public class PlaylistActivity extends AppCompatActivity {
	FloatingActionButton addNewPl;

	@Override
	protected void onCreate(Bundle savedInst) {
		super.onCreate(savedInst);
		setContentView(R.layout.activity_playlists);
		init();
	}

	private void init() {
		addNewPl = findViewById(R.id.add_playlistFAB);
		addNewPl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				createNewPlaylist();
			}
		});
	}

	private void createNewPlaylist() {
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.prompt_input, null);

		android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						this);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = promptsView
						.findViewById(R.id.editTextDialogUserInput);

		// set dialog message
		alertDialogBuilder
						.setCancelable(true)
						.setPositiveButton("Create Playlist",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int id) {
												Intent intent = new Intent(getBaseContext(), AddTracksToPlaylist.class);
												intent.putExtra("PLAYLIST NAME", userInput.getText());
												startActivity(intent);
											}
										})
						.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int id) {
												dialog.cancel();
											}
										});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}

