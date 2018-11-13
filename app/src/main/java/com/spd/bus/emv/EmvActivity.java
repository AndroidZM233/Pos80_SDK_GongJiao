package com.spd.bus.emv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spd.bus.R;
import com.spd.bus.util.TLVList;

import sdk4.wangpos.libemvbinder.CAPK;
import sdk4.wangpos.libemvbinder.EmvAppList;
import sdk4.wangpos.libemvbinder.EmvCore;
import wangpos.sdk4.libbasebinder.BankCard;

/**
 * Created by Administrator on 2018/1/19.
 */

public class EmvActivity  extends Activity implements View.OnClickListener{
    private Context context;
    private Button aid_capk,trans,exit;

    private EmvCore emvCore;
    private BankCard bankCard;

    private static String[] CAPK_DATA = {
            "9F0605A0000003339F220180DF05083230333031323330DF060101DF070101DF028180CCDBA686E2EFB84CE2EA01209EEB53BEF21AB6D353274FF8391D7035D76E2156CAEDD07510E07DAFCACABB7CCB0950BA2F0A3CEC313C52EE6CD09EF00401A3D6CC5F68CA5FCD0AC6132141FAFD1CFA36A2692D02DDC27EDA4CD5BEA6FF21913B513CE78BF33E6877AA5B605BC69A534F3777CBED6376BA649C72516A7E16AF85DF0403010001DF0314A5E44BB0E1FA4F96A11709186670D0835057D35E",
            "9F0605A0000003339F220183DF05083230333031323330DF060101DF070101DF028190E46C9D054471D24A3DAEEA13875ECFB92C34D309106092E6AF57BD612C18E4E2BB3FBBC9E14F86D8660A065848B821347D04521578D4B789FD57231185DF92F45C5733C7912C291D7B13E649B094B33B1B75151C0E4E71E45CCDFD5217DC9F3EF39C3D324CA460DDC40C45CC27B2E421A2B409A47FAAEFD65F8A7F58A269B38CFD9C18210856A493A6624141677F5E95DF040103DF03141CC9BA05BC70F3D049F817404051122E35AC9683",
            "9F0605A0000003339F220184DF05083230333031323330DF060101DF070101DF0281B0F9EA5503CFE43038596C720645A94E0154793DE73AE5A935D1FB9D0FE77286B61261E3BB1D3DFEC547449992E2037C01FF4EFB88DA8A82F30FEA3198D5D16754247A1626E9CFFB4CD9E31399990E43FCA77C744A93685A260A20E6A607F3EE3FAE2ABBE99678C9F19DFD2D8EA76789239D13369D7D2D56AF3F2793068950B5BD808C462571662D4364B30A2582959DB238333BADACB442F9516B5C336C8A613FE014B7D773581AE10FDF7BDB2669012DDF040103DF03144D4E6D415F2CF8C394D40C49FB2459110578CF22",
            "9F0605A0000003339F220185DF05083230333031323330DF060101DF070101DF0281F8CD026B3E11A7234EFC24FB5976D9F51F7188A1598861AA8A6CA8D9A55300C6E6C39ED97E128973306E7D15DF603823A2C0C2E4C01C5AC0D4E71127DFEC69F2B17DAB12F2E8A84CD30AFC791AE71CD6D69D1B7E7648B2F0BB2140791C585E9CAC6642230B13C81A66E52E927681594EC08CFB30E10658F4199B8BF48B55F140925DEEEF4341E2C6C91E039944A5C44DD72379C2227F02105F462C0E977A2E79D2841143941EB4B4BC1ADAC274E3B0129DE7FDCC77C75BBC29A2861DCE7F748EBEE1E69339348667B729C2900EC6A6D43881622555FA8F8B85E18BD2B8B6F56EBD47643181FF7039D883CB5D723D9DEBD073A5A0CD7B980F0DDF040103DF031496C22F92B7644934F03B4065F1C37BC9DBEA45B0",
    };

    private static String[] AID_DATA = {
            "9F0607A0000000031010DF0101009F0802008CDF1105D84000A800DF1205D84004F800DF130500100000009F1B0431303030DF150435303030DF160100DF170100DF14029F37DF1801019F7B06000000000100DF1906000000100000DF2006000000100000",
            "9F0607A0000000032010DF0101009F0802008CDF1105D84000A800DF1205D84004F800DF130500100000009F1B0431303030DF150435303030DF160100DF170100DF14029F37DF1801019F7B06000000000100DF1906000000100000DF2006000000100000",
            "9F0607A0000000033010DF0101009F0802008CDF1105D84000A800DF1205D84004F800DF130500100000009F1B0431303030DF150435303030DF160100DF170100DF14029F37DF1801019F7B06000000000100DF1906000000100000DF2006000000100000",
            "9F0607A0000000038010DF0101009F0802008CDF1105D84000A800DF1205D84004F800DF130500100000009F1B0431303030DF150435303030DF160100DF170100DF14029F37DF1801019F7B06000000000100DF1906000000100000DF2006000000100000",
            "9F0608A000000333010101DF0101009F0802008CDF1105D84000A800DF1205D84004F800DF130500100000009F1B0431303030DF150435303030DF160100DF170100DF14029F37DF1801019F7B06000000000100DF1906000000100000DF2006000000100000",
            "9F0608A000000003999910DF0101009F0802008CDF1105D84000A800DF1205D84004F800DF130500100000009F1B0431303030DF150435303030DF160100DF170100DF14029F37DF1801019F7B06000000000100DF1906000000100000DF2006000000100000",
            "9F0607A0000000041010DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000",
            "9F0607A0000000043060DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000",
            "9F0607A0000000046000DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000",
            "9F0607A0000000046010DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000",
            "9F0607A0000000042000DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000",
            "9F0607A0000000042010DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000",
            "9F0607A0000000043000DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000",
            "9F0607A0000003330101DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000DF2106000000100000",
            "9F0607A0000000048010DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000",
            "9F0605A0000000659F2201099F1B0431303030DF05083230303931323331DF060101DF070101DF028180B72A8FEF5B27F2B550398FDCC256F714BAD497FF56094B7408328CB626AA6F0E6A9DF8388EB9887BC930170BCC1213E90FC070D52C8DCD0FF9E10FAD36801FE93FC998A721705091F18BC7C98241CADC15A2B9DA7FB963142C0AB640D5D0135E77EBAE95AF1B4FEFADCF9C012366BDDA0455C1564A68810D7127676D493890BDDF040103DF03144410C6D51C2F83ADFD92528FA6E38A32DF048D0ADF1906000000100000DF2006000000100000",
            "9F0605A0000000659F2201109F1B0431303030DF05083230313231323331DF060101DF070101DF02819099B63464EE0B4957E4FD23BF923D12B61469B8FFF8814346B2ED6A780F8988EA9CF0433BC1E655F05EFA66D0C98098F25B659D7A25B8478A36E489760D071F54CDF7416948ED733D816349DA2AADDA227EE45936203CBF628CD033AABA5E5A6E4AE37FBACB4611B4113ED427529C636F6C3304F8ABDD6D9AD660516AE87F7F2DDF1D2FA44C164727E56BBC9BA23C0285DF040103DF0314C75E5210CBE6E8F0594A0F1911B07418CADB5BABDF1906000000100000DF2006000000100000",
            "9F0605A0000000659F2201129F1B0431303030DF05083230313431323331DF060101DF070101DF0281B0ADF05CD4C5B490B087C3467B0F3043750438848461288BFEFD6198DD576DC3AD7A7CFA07DBA128C247A8EAB30DC3A30B02FCD7F1C8167965463626FEFF8AB1AA61A4B9AEF09EE12B009842A1ABA01ADB4A2B170668781EC92B60F605FD12B2B2A6F1FE734BE510F60DC5D189E401451B62B4E06851EC20EBFF4522AACC2E9CDC89BC5D8CDE5D633CFD77220FF6BBD4A9B441473CC3C6FEFC8D13E57C3DE97E1269FA19F655215B23563ED1D1860D8681DF040103DF0314874B379B7F607DC1CAF87A19E400B6A9E25163E8DF1906000000100000DF2006000000100000"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emv);
        context = this;

        aid_capk = (Button) findViewById(R.id.aid_capk);
        trans = (Button) findViewById(R.id.trans);
        exit = (Button) findViewById(R.id.exit);
        aid_capk.setOnClickListener(this);
        trans.setOnClickListener(this);
        exit.setOnClickListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                emvCore = new EmvCore(getApplicationContext());
                bankCard = new BankCard(getApplicationContext());
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.aid_capk:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        setCAPK();
                        setAID();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showCustomizeDialog("Set CAPK, AID", "Set CAPK, AID successful!");
                            }
                        });
                    }
                }).start();
                break;
            case R.id.trans:
                Intent pay = new Intent(context,InputMoneyActivity.class);
                startActivity(pay);
                break;
            case R.id.exit:
                finish();
                break;
        }
    }

    private void showCustomizeDialog(String title, String content) {
        final AlertDialog.Builder customizeDialog = new AlertDialog.Builder(this);
        customizeDialog.setTitle(title);
        customizeDialog.setMessage(content);
        customizeDialog.setPositiveButton("OK",null);
        customizeDialog.show();
    }

    private void setCAPK() {
        try {
            emvCore.delAllCAPK();//delete capk
        }catch (RemoteException ex){
            ex.printStackTrace();
        }
        for (int i=0; i<CAPK_DATA.length; i++){
            TLVList tlvList = TLVList.fromBinary(CAPK_DATA[i]);
            try {
                CAPK capk = new CAPK(context);

                capk.setRID(tlvList.getTLV("9F06").getValue());// rid
                capk.setKeyID(tlvList.getTLV("9F22").getValue());//认证中心公钥索引(CA Public Key Index)

                if (tlvList.getTLV("DF05") != null) {
                    capk.setExpDate(tlvList.getTLV("DF05").getValue());//认证中心公钥有效期(CA Public Key period of validity)
                    Log.e("acpkData", "ExpDate--->" + capk.getExpDate() + "\ntag-->" + tlvList.getTLV("DF05").getValue());
                }
                if (tlvList.getTLV("DF06") != null) {
                    capk.setHashInd(tlvList.getTLV("DF06").getValue());//认证中心公钥哈什算法标识(CA Public Key Hash algorithm identification)
                    Log.e("acpkData", "HashInd--->" + capk.getHashInd() + "\ntag-->" + tlvList.getTLV("DF06").getValue());
                }
                if (tlvList.getTLV("DF07") != null) {
                    capk.setArithInd(tlvList.getTLV("DF07").getValue());//认证中心公钥算法标识(CA Public Key Algorithm identification)
                    Log.e("acpkData", "ArithInd--->" + capk.getArithInd() + "\ntag-->" + tlvList.getTLV("DF07").getValue());
                }
                if (tlvList.getTLV("DF02") != null) {
                    capk.setModul(tlvList.getTLV("DF02").getValue());//认证中心公钥模(CA Public Key module)
                    Log.e("acpkData", "tModul--->" + capk.getModul() + "\ntag-->" + tlvList.getTLV("DF02").getValue());
                }
                if (tlvList.getTLV("DF04") != null) {
                    capk.setExponent(tlvList.getTLV("DF04").getValue());//认证中心公钥指数(CA Public Key exponent)
                    Log.e("acpkData", "Exponent--->" + capk.getExponent() + "\ntag-->" + tlvList.getTLV("DF04").getValue());
                }
                if (tlvList.getTLV("DF03") != null) {
                    capk.setCheckSum(tlvList.getTLV("DF03").getValue().substring(0, 40));//认证中心公钥校验值(CA Public Key Check value)
                    Log.e("acpkData", tlvList.getTLV("DF03").getLength() + "----" + tlvList.getTLV("DF03").getTLLength() + "CheckSum--->" + capk.getCheckSum() + "\ntag-->" + tlvList.getTLV("DF03").getValue() + "\ndataSize" + capk.toByteArray().length);
                }
                Log.e("addCapk", tlvList.toString() + "\n" + "capkSize-->" + capk.toByteArray().length + "\n" + capk.print());
                byte[] capkByte = capk.toByteArray();
                int result = emvCore.addCAPK(capkByte);
                Log.e("addCapk_Result", result + "");
            } catch (RemoteException ex){
                ex.printStackTrace();
            }
        }
    }

    private void setAID() {
        try {
            emvCore.delAllAID();//delete Aid
        }catch (RemoteException ex){
            ex.printStackTrace();
        }
        for (int i=0; i<AID_DATA.length; i++){
            TLVList tlvList =TLVList.fromBinary(AID_DATA[i]);
            try {
                EmvAppList emvAppList  =new EmvAppList(context);
                emvAppList.setAID(tlvList.getTLV("9F06").getValue());//aid
                if (tlvList.getTLV("DF01") != null) {
                    emvAppList.setSelFlag(tlvList.getTLV("DF01").getValue());//选择应用标识
                }
                if (tlvList.getTLV("9F09") != null) {
                    emvAppList.setVersion(tlvList.getTLV("9F09").getValue());//应用版本
                }
                if (tlvList.getTLV("DF11") != null) {
                    emvAppList.setTACDefault(tlvList.getTLV("DF11").getValue());//TAC－缺省
                }
                if (tlvList.getTLV("DF12") != null) {
                    emvAppList.setTACOnline(tlvList.getTLV("DF12").getValue());//TAC－联机
                }
                if (tlvList.getTLV("DF13") != null) {
                    emvAppList.setTACDenial(tlvList.getTLV("DF13").getValue());//TAC－拒绝
                }
                if (tlvList.getTLV("9F1B") != null) {
                    emvAppList.setFloorLimit(Long.parseLong(tlvList.getTLV("9F1B").getValue()));//最低限额
                }
                if (tlvList.getTLV("DF15") != null) {
                    emvAppList.setThreshold(Long.parseLong(tlvList.getTLV("DF15").getValue()));//偏置随机选择的阈值
                }
                if (tlvList.getTLV("DF16") != null) {
                    emvAppList.setMaxTargetPer(Integer.parseInt(tlvList.getTLV("DF16").getValue()));//偏置随机选择的最大目标百分数
                }
                if (tlvList.getTLV("DF17") != null) {
                    emvAppList.setTargetPer(Integer.parseInt(tlvList.getTLV("DF17").getValue()));//随机选择的目标百分数
                }
                if (tlvList.getTLV("DF14") != null) {
                    emvAppList.setDDOL(tlvList.getTLV("DF14").getValue());//缺省DDOL
                }
                if (tlvList.getTLV("DF18") != null) {
                    emvAppList.setBOnlinePin(Integer.parseInt(tlvList.getTLV("DF18").getValue()));
                }
                if (tlvList.getTLV("9F7B") != null) {
                    emvAppList.setEC_TermLimit(Long.parseLong(tlvList.getTLV("9F7B").getValue()));//终端电子现金交易限额
                }
                if (tlvList.getTLV("DF19") != null) {
                    emvAppList.setCL_FloorLimit(Long.parseLong(tlvList.getTLV("DF19").getValue()));//非接触读写器脱机最低限额
                }
                if (tlvList.getTLV("DF20") != null) {
                    emvAppList.setCL_TransLimit(Long.parseLong(tlvList.getTLV("DF20").getValue()));//非接触读写器交易限额
                }
                if (tlvList.getTLV("DF21") != null) {
                    emvAppList.setCL_CVMLimit(Long.parseLong(tlvList.getTLV("DF21").getValue())); //非接触终端CVM限额
                }
                emvCore.addAID(emvAppList.toByteArray());
            } catch (RemoteException ex){
                ex.printStackTrace();
            }
        }
    }

}