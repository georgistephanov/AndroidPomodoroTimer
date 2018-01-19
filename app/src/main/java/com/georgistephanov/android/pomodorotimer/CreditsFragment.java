package com.georgistephanov.android.pomodorotimer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;

public class CreditsFragment extends DialogFragment {
	Context context;

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (context == null) {
			throw new RuntimeException("No context has been provided to the CreditsFragment");
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder
			.setTitle(context.getResources().getString(R.string.credits_title))
			.setMessage(Html.fromHtml(context.getResources().getString(R.string.credits_body), Html.FROM_HTML_MODE_LEGACY))
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					dialogInterface.dismiss();
				}
			});

		return builder.create();
	}
}
