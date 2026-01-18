
package com.lebr2.browser;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;
import android.app.AlertDialog;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    EditText urlBar;
    TextView httpsIndicator;
    boolean incognito=false, darkMode=false, jsEnabled=true;
    SharedPreferences prefs;
    Set<String> bookmarks;

    @Override
    protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("LeBr2", MODE_PRIVATE);
        bookmarks = new HashSet<>(prefs.getStringSet("bookmarks", new HashSet<>()));

        webView=findViewById(R.id.webview);
        urlBar=findViewById(R.id.urlBar);
        httpsIndicator=findViewById(R.id.httpsIndicator);

        Button go=findViewById(R.id.goBtn);
        Button back=findViewById(R.id.backBtn);
        Button forward=findViewById(R.id.forwardBtn);
        Button refresh=findViewById(R.id.refreshBtn);
        Button bookmark=findViewById(R.id.bookmarkBtn);
        Button editBookmarks=findViewById(R.id.editBookmarksBtn);
        Button incog=findViewById(R.id.incognitoBtn);
        Button dark=findViewById(R.id.darkBtn);
        Button js=findViewById(R.id.jsBtn);
        Button settings=findViewById(R.id.settingsBtn);
        LinearLayout panel=findViewById(R.id.settingsPanel);
        Button clearHistory=findViewById(R.id.clearHistoryBtn);
        Button clearCookies=findViewById(R.id.clearCookiesBtn);
        Button clearCache=findViewById(R.id.clearCacheBtn);
        Button clearAll=findViewById(R.id.clearAllBtn);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        load("https://www.google.com");

        go.setOnClickListener(v->load(urlBar.getText().toString()));
        back.setOnClickListener(v->{ if(webView.canGoBack()) webView.goBack(); });
        forward.setOnClickListener(v->{ if(webView.canGoForward()) webView.goForward(); });
        refresh.setOnClickListener(v->webView.reload());
        bookmark.setOnClickListener(v->{ bookmarks.add(webView.getUrl()); prefs.edit().putStringSet("bookmarks", bookmarks).apply(); });
        editBookmarks.setOnClickListener(v->{
            String[] items=bookmarks.toArray(new String[0]);
            new AlertDialog.Builder(this).setTitle("Edit Bookmarks")
                .setItems(items,(d,i)->{ bookmarks.remove(items[i]); prefs.edit().putStringSet("bookmarks", bookmarks).apply(); })
                .setNegativeButton("Close",null).show();
        });
        incog.setOnClickListener(v->{ toggleIncognito(); });
        dark.setOnClickListener(v->{ toggleDark(); });
        js.setOnClickListener(v->{ toggleJS(); });
        settings.setOnClickListener(v->{ panel.setVisibility(panel.getVisibility()==View.GONE?View.VISIBLE:View.GONE); });
        clearHistory.setOnClickListener(v->{ webView.clearHistory(); });
        clearCookies.setOnClickListener(v->{ CookieManager.getInstance().removeAllCookies(null); CookieManager.getInstance().flush(); });
        clearCache.setOnClickListener(v->{ webView.clearCache(true); });
        clearAll.setOnClickListener(v->{ webView.clearHistory(); webView.clearCache(true); CookieManager.getInstance().removeAllCookies(null); CookieManager.getInstance().flush(); bookmarks.clear(); prefs.edit().clear().apply(); });
    }

    void load(String url){ if(!url.startsWith("http")) url="https://"+url; webView.loadUrl(url); }
    void toggleIncognito(){ incognito=!incognito; webView.getSettings().setSaveFormData(!incognito); webView.getSettings().setCacheMode(incognito?3:0); }
    void toggleDark(){ darkMode=!darkMode; webView.evaluateJavascript(darkMode?"document.body.style.background='#121212';document.body.style.color='white';":"document.body.style.background='white';document.body.style.color='black';",null);}
    void toggleJS(){ jsEnabled=!jsEnabled; webView.getSettings().setJavaScriptEnabled(jsEnabled); webView.reload();}
}
