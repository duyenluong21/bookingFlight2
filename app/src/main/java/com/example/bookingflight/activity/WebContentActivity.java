package com.example.bookingflight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookingflight.R;

public class WebContentActivity extends AppCompatActivity {
    private WebView webView;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webcontent);

        webView = findViewById(R.id.webView);
        backButton = findViewById(R.id.backButton);

        // Giấu WebView trước khi tải
        webView.setVisibility(View.GONE);

        // Sự kiện click cho nút back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WebContentActivity.this, LocationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Cấu hình WebView
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Chạy đoạn JavaScript sau khi trang đã tải xong
                injectJavaScript();
            }
        });

        // Lấy URL từ intent
        String url = getIntent().getStringExtra("url");
        if (url != null && !url.isEmpty()) {
            // Tải trang web
            webView.loadUrl(url);
        } else {
            Toast.makeText(this, "URL không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm inject JavaScript để chỉ hiển thị phần nội dung mong muốn
    private void injectJavaScript() {
        String script = "javascript:(function() { " +
                // Lấy phần tử "Cảnh đẹp" và "Ẩm thực"
                "var landscapeDiv = document.querySelector('.about-destinatons .col-md-6.custom-padding');" + // Cảnh đẹp
                "var foodDiv = document.querySelectorAll('.about-destinatons .col-md-6.custom-padding')[1];" + // Ẩm thực
                "var contentDiv = document.querySelectorAll('.destination-content');" + // Nội dung
                "if (landscapeDiv || foodDiv || contentDiv) {" +
                "   document.body.innerHTML = '';" +  // Xóa nội dung cũ
                "   if (landscapeDiv) {" +
                "       document.body.appendChild(landscapeDiv);" + // Thêm phần Cảnh đẹp
                "   }" +
                "   if (foodDiv) {" +
                "       document.body.appendChild(foodDiv);" + // Thêm phần Ẩm thực
                "   }" +
                "   if (contentDiv) {" +
                "       document.body.appendChild(contentDiv[1]);" + // Thêm nội dung của Ẩm thực
                "   }" +
                "   var imgs = document.querySelectorAll('.box-image-destinaton img');" +
                "   imgs.forEach(function(img) {" +
                "       img.style.width = '100%';" +  // Đặt chiều rộng của ảnh thành 100%
                "       img.style.height = 'auto';" +  // Đặt chiều cao tự động để giữ tỷ lệ
                "   });" +
                "   document.body.style.display = 'block';" + // Hiện nội dung
                "} else {" +
                "   document.body.innerHTML = '<h2>Nội dung không tìm thấy!</h2>';" +
                "}" +
                "})()";

        // Hiện WebView và chạy JavaScript
        webView.setVisibility(View.VISIBLE);
        webView.evaluateJavascript(script, null);
    }



}
