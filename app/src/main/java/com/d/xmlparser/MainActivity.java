package com.d.xmlparser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.d.lib.xmlparser.XmlParser;
import com.d.xmlparser.model.Resp;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String mXmlResp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><resp><code>35</code><desc>xml parser test</desc></resp>";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_parse:
                parse();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
    }

    private void bindView() {
        findViewById(R.id.btn_parse).setOnClickListener(this);
    }

    private void parse() {
        Resp resp = XmlParser.parserInvoke(Resp.class, mXmlResp);
        Log.d("dsiner", "parse result: " + resp.toString());
    }
}
