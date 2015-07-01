package com.liferay.mobile.screens.osos.activities;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.liferay.mobile.screens.osos.R;
import com.liferay.mobile.screens.osos.utils.Card;
import com.liferay.mobile.screens.osos.utils.EndAnimationListener;
import com.liferay.mobile.screens.base.list.BaseListListener;
import com.liferay.mobile.screens.base.list.BaseListScreenlet;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.ddl.form.DDLFormListener;
import com.liferay.mobile.screens.ddl.form.DDLFormScreenlet;
import com.liferay.mobile.screens.ddl.list.DDLEntry;
import com.liferay.mobile.screens.ddl.list.DDLListScreenlet;
import com.liferay.mobile.screens.ddl.model.DocumentField;
import com.liferay.mobile.screens.ddl.model.Record;
import com.liferay.mobile.screens.viewsets.defaultviews.LiferayCrouton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Javier Gamarra
 */
public class IssuesActivity extends CardActivity implements View.OnClickListener, DDLFormListener, BaseListListener<DDLEntry>, View.OnTouchListener {


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			_card2Open = true;
			_ddlFormScreenlet.startUploadByPosition(requestCode);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issues);

		_backgroundCard = findViewById(R.id.background);

		_card1ToBackgroundMenu = (ImageView) findViewById(R.id.card1_to_background_menu);
		_card1ToBackgroundMenu.setOnClickListener(this);
		_card1ToBackground = (ImageView) findViewById(R.id.card1_to_background);
		_card1ToBackground.setOnClickListener(this);

		_reportIssueTitle = (TextView) findViewById(R.id.report_issue_title);

		_ddlFormScreenlet = (DDLFormScreenlet) findViewById(R.id.ddlform);
		_ddlFormScreenlet.setListener(this);
		_ddlListScreenlet = (DDLListScreenlet) findViewById(R.id.ddllist);
		_ddlListScreenlet.setListener(this);

		_sendButton = (Button) findViewById(R.id.liferay_form_submit);

		TextView callMenuEntry = (TextView) findViewById(R.id.call_menu_entry);
		callMenuEntry.setText(getCallSpannableString(), TextView.BufferType.SPANNABLE);
		callMenuEntry.setOnTouchListener(this);
		findViewById(R.id.account_settings_menu_entry).setOnTouchListener(this);
		findViewById(R.id.send_message_menu_entry).setOnTouchListener(this);
		findViewById(R.id.web_menu_entry).setOnTouchListener(this);
		findViewById(R.id.sign_out_menu_entry).setOnTouchListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		//we don't want to crash if activity gets restored without session
		if (!SessionContext.hasSession()) {
			finish();
		}
		if(_card2Open){
			toCard2(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.card1_to_front_view:
				goLeftCard1();
				_reportIssueTitle.setText(getString(R.string.report_issue));
				break;
			case R.id.card1_to_background:
			case R.id.card1_to_background_menu:
				if (_cardHistory.peek() == Card.BACKGROUND) {
					toCard1();
					_card1ToBackgroundMenu.setImageDrawable(getResources().getDrawable(R.drawable.icon_options_red));
				}
				else {
					_card1ToBackgroundMenu.setImageDrawable(getResources().getDrawable(R.drawable.icon_options_close));
					toBackground();
				}
				break;
			default:
				super.onClick(v);
		}
	}

	@Override
	public void onListItemSelected(DDLEntry element, View view) {
		if (view.getId() == R.id.liferay_list_view) {
			goRightCard1(element);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			v.setBackgroundColor(getResources().getColor(android.R.color.white));
			return true;
		}
		else if (event.getAction() == MotionEvent.ACTION_UP) {
			launchMenu(v);
		}
		return false;
	}

	@Override
	public void onDDLFormRecordAdded(Record record) {
		reloadListAndShowResult(getString(R.string.entry_submitted));
	}

	@Override
	public void onDDLFormRecordUpdated(Record record) {
		reloadListAndShowResult(getString(R.string.entry_updated));
	}

	@Override
	public void onDDLFormLoadFailed(Exception e) {
	}

	@Override
	public void onDDLFormLoaded(Record record) {
	}

	@Override
	public void onDDLFormRecordLoaded(Record record) {
	}

	@Override
	public void onDDLFormRecordLoadFailed(Exception e) {
	}

	@Override
	public void onDDLFormRecordAddFailed(Exception e) {
	}

	@Override
	public void onDDLFormUpdateRecordFailed(Exception e) {
	}

	@Override
	public void onDDLFormDocumentUploaded(DocumentField documentField, JSONObject jsonObject) {
	}

	@Override
	public void onDDLFormDocumentUploadFailed(DocumentField documentField, Exception e) {
	}

	@Override
	public void onListPageFailed(BaseListScreenlet source, int page, Exception e) {
	}

	@Override
	public void onListPageReceived(BaseListScreenlet source, int page, List<DDLEntry> entries,
								   int rowCount) {
	}

	@Override
	protected void animateScreenAfterLoad() {
		_cardHistory.offer(Card.CARD1);
		_card1ToBackgroundMenu.setImageDrawable(getResources().getDrawable(R.drawable.icon_options_red));

		//TODO extract this animation
		_backgroundCard.setY(_maxHeight);
		_card1.setY(_maxHeight);
		_card2.setY(_maxHeight);

		_card1.animate().y(_card1RestPosition);
		_card2.animate().y(_card2FoldedPosition).setListener(new EndAnimationListener() {
			@Override
			public void onAnimationEnd(Animator animator) {
				_backgroundCard.setY(0);
			}
		});
	}

	@Override
	protected void toBackground() {
		super.toBackground();
		_card1ToBackground.setImageResource(R.drawable.icon_up);
		_card1ToBackground.setVisibility(View.VISIBLE);
	}

	@Override
	protected void toCard1(Animator.AnimatorListener listener) {
		super.toCard1(listener);
		_ddlFormScreenlet.loadForm();

		clearDDLEntrySelected();

		_card1ToBackgroundMenu.setImageDrawable(getResources().getDrawable(R.drawable.icon_options_red));
		_card1ToBackground.setImageResource(R.drawable.icon_down);
	}

	@Override
	protected void toCard2(boolean clean) {
		super.toCard2(clean);
		_card2Open = false;
		if (_entry != null) {
			_ddlFormScreenlet.setRecordId((Integer) _entry.getAttributes("recordId"));
			_ddlFormScreenlet.loadRecord();
			goLeftCard1();
		}
		else {
			if(clean) {
				clearDDLEntrySelected();
			}
		}
	}

	private SpannableStringBuilder getCallSpannableString() {
		int darkGrayColor = getResources().getColor(R.color.westeros_dark_gray);
		int subTitleStart = 4;

		SpannableStringBuilder ssb = new SpannableStringBuilder(getString(R.string.call_menu_entry));
		ssb.setSpan(new StyleSpan(Typeface.NORMAL), subTitleStart, ssb.length(), 0);
		ssb.setSpan(new ForegroundColorSpan(darkGrayColor), subTitleStart, ssb.length(), 0);
		return ssb;
	}


	private void clearDDLEntrySelected() {
		_entry = null;
		_reportIssueTitle.setText(getString(R.string.report_issue));
		_sendButton.setText(getString(R.string.send).toUpperCase());
		_ddlFormScreenlet.setRecordId(0);
	}

	private void reloadListAndShowResult(final String message) {
		_ddlListScreenlet.loadPage(0);
		toCard1(new EndAnimationListener() {
			@Override
			public void onAnimationEnd(Animator animator) {
				LiferayCrouton.info(IssuesActivity.this, message.toUpperCase());
			}
		});
	}

	private void goRightCard1(DDLEntry element) {
		TextView issueTitle = (TextView) findViewById(R.id.issue_title);
		issueTitle.setText(element.getValue(getString(R.string.liferay_recordset_fields)));

		String date = new SimpleDateFormat("dd/MM/yyyy").format(element.getAttributes("createDate"));
		((TextView) findViewById(R.id.createdAt)).setText("Created " + date);

		try {
			JSONObject photo = new JSONObject(element.getValue("Photo"));
			WebView web = (WebView) findViewById(R.id.entry_photo);
			StringBuilder sb = new StringBuilder(getString(com.liferay.mobile.screens.R.string.liferay_server));
			sb.append("/documents/");
			sb.append(photo.get("groupId"));
			sb.append("/0/a/");
				sb.append(photo.get("uuid"));
			sb.append("?=");
			sb.append(System.currentTimeMillis());
			sb.append("&imageThumbnail=1");
			web.loadUrl(sb.toString());
		} catch (JSONException e) {
			Log.e("app",e.getMessage());
		}


		goRightCard1();
	}

	private void launchMenu(View v) {
		int color = android.R.color.transparent;
		switch (v.getId()) {
			case R.id.account_settings_menu_entry:
				startActivity(new Intent(this, AccountSettingsActivity.class));
				overridePendingTransition(0, 0);
				break;
			case R.id.call_menu_entry:
				color = R.color.westeros_light_gray;

				startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(getString(R.string.default_telephone_uri))));
				break;
			case R.id.send_message_menu_entry:
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.default_sms_uri))));
				break;
			case R.id.web_menu_entry:
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(com.liferay.mobile.screens.R.string.liferay_server))));
				break;
			case R.id.sign_out_menu_entry:
				color = R.color.westeros_light_gray;

				SessionContext.clearSession();
				Intent intent = new Intent(this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				break;
		}
		v.setBackgroundColor(getResources().getColor(color));
	}

	private DDLFormScreenlet _ddlFormScreenlet;
	private DDLListScreenlet _ddlListScreenlet;

	private DDLEntry _entry;

	private View _backgroundCard;
	private ImageView _card1ToBackground;
	private ImageView _card1ToBackgroundMenu;
	private TextView _reportIssueTitle;
	private Button _sendButton;
	private boolean _card2Open=false;

}