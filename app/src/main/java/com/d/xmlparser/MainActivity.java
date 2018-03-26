package com.d.xmlparser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.d.xmlparser.model.bean.Resp;
import com.d.xmlparser.model.bean.Resp$$XmlBinder;

public class MainActivity extends AppCompatActivity {
    private final String xmlResp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><resp><code>35</code><desc>xml parser test</desc></resp>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });
    }

    private void test() {
        Resp resp = new Resp$$XmlBinder().parserXml(xmlResp);
        Log.d("dsiner", "---------");
    }
}
