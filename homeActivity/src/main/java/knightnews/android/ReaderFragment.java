package knightnews.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReaderFragment extends Fragment {
	private static final String TAG = "ReaderFragment";

	public static final String KEY_STORY = "com.sigmobile.ucf_news.KEY_STORY";
	private ImageView mStoryImageTextView;
	private WebView mContentWebView;
	private StoryItem mStory;
	private ShareActionProvider mShareActionProvider;
	private Context mContext;

	public ReaderFragment() {
	}

	public static ReaderFragment newInstance(StoryItem story) {
		ReaderFragment rf = new ReaderFragment();
		Bundle args = new Bundle();
		args.putSerializable(KEY_STORY, story);
		rf.setArguments(args);
		return rf;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setRetainInstance(true);
		mContext = getActivity();

		// get the fragments arguments
		Bundle args = getArguments();
		if (args != null) {
			mStory = (StoryItem) args.getSerializable(KEY_STORY);
			// Log.d(TAG, "***Contents: " + mStory.getUnparsedContent());
		}
		setHasOptionsMenu(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		mContentWebView.onPause();
	}

	@Override
	public void onDestroy() {
		mContext = null;
		super.onDestroy();
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater.inflate(R.menu.feed, menu);
		// Set up ShareActionProvider's default share intent
		MenuItem shareItem = menu.findItem(R.id.action_share);
		mShareActionProvider = (ShareActionProvider) MenuItemCompat
				.getActionProvider(shareItem);

		if (mShareActionProvider != null) {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, mStory.getTitle());
			intent.putExtra(Intent.EXTRA_TEXT, mStory.getDescription() + "\n"
					+ mStory.getUrl() + "\n\n\n" + "Via KnightNews for Android");

			mShareActionProvider.setShareIntent(intent);
		}

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case (R.id.open_in_browser):
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mStory.getUrl()));
				startActivity(browserIntent);
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		MenuItem item = menu.findItem(R.id.open_in_browser);
		item.setVisible(true);

		item = menu.findItem(R.id.action_share);
		item.setVisible(true);


	}


	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater,
	                         final ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_reader_parallax, container, false);

		mStoryImageTextView = (ImageView)v.findViewById(R.id.fragment_reader_story_image);
		Picasso.with(mContext).load(mStory.getPictureUrl()).fit()
		       .error(R.drawable
				       .news_error)
		       .into(mStoryImageTextView);

		mContentWebView = (WebView) v
				.findViewById(R.id.fragment_reader_story_content);

		//very important for playing the videos
		mContentWebView.setWebViewClient(webClient);
		mContentWebView.setWebChromeClient(new WebChromeClient());
		mContentWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
		mContentWebView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
		mContentWebView.getSettings().setJavaScriptEnabled(true);
		//mContentWebView.getSettings().setLoadWithOverviewMode(true);


		String storyContent = String.format("<h1 style='color:#333333'>%s</h1><div style='color:#999999;" +
				"font-style:italic;'><div style='float:left;'>%s</div><div style='float:right'>%s</div>" +
				"</div><br><p style='color:#333333'>%s</p>", mStory.getTitle(), mStory.getAuthor(),
				mStory.getDate(), mStory.getUnparsedContent());

		//veryVeryVery important for playing the videos!
		mContentWebView.loadDataWithBaseURL(mStory.getUrl(), storyContent,
				"text/html", "UTF-8", null);

		return v;
	}

	private WebViewClient webClient = new WebViewClient() {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			Intent i = new Intent(mContext, ReaderWebViewActivity.class);
			i.putExtra(ReaderWebViewActivity.KEY_URL, url);
			i.putExtra(ReaderWebViewActivity.KEY_STORY, mStory);
			startActivity(i);

			return true;
		}

	};
}


