package com.wpos.sdkdemo.emv;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wpos.sdkdemo.R;
import com.wpos.sdkdemo.util.MoneyUtil;

public class InputMoneyActivity extends Activity implements View.OnClickListener {
    private TextView inputMoneyYuanText,inputMoneyFenText;
    private long inputMoney = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_money);

        initView();
    }

    private void initView() {
        //顶部标题栏
        ImageView titleBackImage = (ImageView) findViewById(R.id.titleBackImage);
        titleBackImage.setOnClickListener(this);

        inputMoneyYuanText = (TextView) findViewById(R.id.inputMoneyYuanText);
        inputMoneyFenText = (TextView) findViewById(R.id.inputMoneyFenText);

        Button btn_num00 = (Button) findViewById(R.id.btn_num00);
        btn_num00.setOnClickListener(this);
        Button btn_num0 = (Button) findViewById(R.id.btn_num0);
        btn_num0.setOnClickListener(this);
        Button btn_num1 = (Button) findViewById(R.id.btn_num1);
        btn_num1.setOnClickListener(this);
        Button btn_num2 = (Button) findViewById(R.id.btn_num2);
        btn_num2.setOnClickListener(this);
        Button btn_num3 = (Button) findViewById(R.id.btn_num3);
        btn_num3.setOnClickListener(this);
        Button btn_num4 = (Button) findViewById(R.id.btn_num4);
        btn_num4.setOnClickListener(this);
        Button btn_num5 = (Button) findViewById(R.id.btn_num5);
        btn_num5.setOnClickListener(this);
        Button btn_num6 = (Button) findViewById(R.id.btn_num6);
        btn_num6.setOnClickListener(this);
        Button btn_num7 = (Button) findViewById(R.id.btn_num7);
        btn_num7.setOnClickListener(this);
        Button btn_num8 = (Button) findViewById(R.id.btn_num8);
        btn_num8.setOnClickListener(this);
        Button btn_num9 = (Button) findViewById(R.id.btn_num9);
        btn_num9.setOnClickListener(this);

        ImageView btn_num_clear = (ImageView) findViewById(R.id.btn_num_clear);
        btn_num_clear.setOnClickListener(this);
        ImageView btn_trans = (ImageView) findViewById(R.id.btn_trans);
        btn_trans.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.titleBackImage:
                finish();
                break;
            case R.id.btn_num0:
            case R.id.btn_num1:
            case R.id.btn_num2:
            case R.id.btn_num3:
            case R.id.btn_num4:
            case R.id.btn_num5:
            case R.id.btn_num6:
            case R.id.btn_num7:
            case R.id.btn_num8:
            case R.id.btn_num9:
                if((inputMoney+"").length()<11) {
                    inputMoney = Long.parseLong(inputMoney + ((Button) v).getText().toString());
                    inputMoneySetText();
                }
                break;
            case R.id.btn_num00:
                if((inputMoney+"").length()<10) {
                    inputMoney = Long.parseLong(inputMoney + ((Button) v).getText().toString());
                    inputMoneySetText();
                }
                break;

            case R.id.btn_num_clear:
                if(inputMoney>0) {
                    inputMoney = inputMoney / 10;
                    inputMoneySetText();
                }
                break;
            case R.id.btn_trans:
                if(inputMoney>0){
                    Intent intent = new Intent(this, PayActivity.class);
                    intent.putExtra("orderAmount",inputMoney);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(this, "Please enter ！",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;

        }
    }

    private void inputMoneySetText(){
        String inputMoneyString = MoneyUtil.fen2yuan(inputMoney);
        inputMoneyYuanText.setText(inputMoneyString.substring(0,inputMoneyString.length()-2));
        inputMoneyFenText.setText(inputMoneyString.substring(inputMoneyString.length()-2));
    }
}
