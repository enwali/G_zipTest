package com.szhua.gip_test;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class TestActivity extends AppCompatActivity {

    @InjectView(R.id.zip_string_input)
    EditText zipStringInput;
    @InjectView(R.id.encrypt)
    Button encrypt;
    @InjectView(R.id.decrypt)
    Button decrypt;
    @InjectView(R.id.textView)
    TextView textView;
    @InjectView(R.id.textView2)
    TextView textView2;
    /*
         是否压缩了；
         */
    private boolean isEncrypt;

  private String result ;
    private ProgressDialog progressDialog;



    String testString ="//FastJson生成json数据\n" +
            "        String jsonData = JsonUtils.objectToJsonForFastJson(personList);\n" +
            "        Log.e(\"MainActivity\", \"压缩前json数据 ---->\" + jsonData);\n" +
            "        Log.e(\"MainActivity\", \"压缩前json数据长度 ---->\" + jsonData.length());\n" +
            "\n" +
            "        //Gzip压缩\n" +
            "        long start = System.currentTimeMillis();\n" +
            "        String gzipStr = ZipUtils.compressForGzip(jsonData);\n" +
            "        long end = System.currentTimeMillis();\n" +
            "        Log.e(\"MainActivity\", \"Gzip压缩耗时 cost time---->\" + (end - start));\n" +
            "        Log.e(\"MainActivity\", \"Gzip压缩后json数据 ---->\" + gzipStr);\n" +
            "        Log.e(\"MainActivity\", \"Gzip压缩后json数据长度 ---->\" + gzipStr.length());\n" +
            "\n" +
            "        //Gzip解压\n" +
            "        start = System.currentTimeMillis();\n" +
            "        String unGzipStr = ZipUtils.decompressForGzip(gzipStr);\n" +
            "        end = System.currentTimeMillis();\n" +
            "        Log.e(\"MainActivity\", \"Gzip解压耗时 cost time---->\" + (end - start));\n" +
            "        Log.e(\"MainActivity\", \"Gzip解压后json数据 ---->\" + unGzipStr);\n" +
            "        Log.e(\"MainActivity\", \"Gzip解压后json数据长度 ---->\" + unGzipStr.length());" ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.inject(this);

        Log.i("leilei",Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.i("leilei","originalLength"+testString.length()) ;
    }


    public void showProgressWithDefinedText(String text) {
        if (null == progressDialog) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(text);
        progressDialog.show();
    }

    public void cancleProgressDialog() {
        if (null != progressDialog) {
            progressDialog.cancel();
        }
    }


    @OnClick({R.id.encrypt, R.id.decrypt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.encrypt:
              if(!TextUtils.isEmpty(zipStringInput.getText().toString())){
                  MyEncryptTask myEncryptTask =new MyEncryptTask() ;
                  myEncryptTask.execute(testString) ;
              }else{
                  Toast.makeText(this,"输入的内容为空",Toast.LENGTH_SHORT).show();
              }
                break;
            case R.id.decrypt:
                if(!TextUtils.isEmpty(TestActivity.this.result)){
                    MyEncryptTask myEncryptTask =new MyEncryptTask() ;
                    myEncryptTask.setType(MyEncryptTask.DECRYPT_TYPE);
                    myEncryptTask.execute(TestActivity.this.result) ;
                }else{
                    Toast.makeText(this,"未有解压的内容",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 解压缩任务
     */
    class MyEncryptTask extends AsyncTask<String, Integer, String> {

        public static final int ENCRYPT_TYPE = 0;
        public static final int DECRYPT_TYPE = 1;
        public long executeTask_StartTime;

        /**
         * 解压任务还是压缩任务 ；
         */
        int type;

        public void setType(int type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            executeTask_StartTime =System.currentTimeMillis() ;
            switch (type) {
                case ENCRYPT_TYPE:
                    showProgressWithDefinedText("正在压缩~~~");
                case DECRYPT_TYPE:
                    showProgressWithDefinedText("正在解压~~~");
                    break;
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            switch (type) {
                case ENCRYPT_TYPE:
                    result = EncryptUtil.encryptGZIP(strings[0]);
                    break;
                case DECRYPT_TYPE:
                    result =  EncryptUtil.decryptGZIP(strings[0]) ;
                    break;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            cancleProgressDialog();
            switch (type) {
                case ENCRYPT_TYPE:
                    TestActivity.this.result =result ;
                    textView.setText("压缩后的文字：" + result.length());
                    EncryptUtil.createFileInSdcard(result.getBytes(),"szhuazip.txt");
                    Log.i("leilei",result);
                    textView2.setText("time:"+(System.currentTimeMillis()-executeTask_StartTime));
                    break;
                case DECRYPT_TYPE:
                    textView.setText("解压后的文字："+result.length());
                    EncryptUtil.createFileInSdcard(result.getBytes(),"szhua.txt");
                    textView2.setText("time:"+(System.currentTimeMillis()-executeTask_StartTime));
                    break;
            }
        }
    }


}
