package com.spd.bosi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bluering.pos.sdk.qr.CertVerInfo;
import com.bluering.pos.sdk.qr.PosQR;
import com.bluering.pos.sdk.qr.QrCodeInfo;
import com.spd.base.utils.Datautils;

public class BositestActivity extends Activity implements View.OnClickListener {

    /**
     * 测试
     */
    private Button mBtnTest;
    private TextView mTvShow;
    private BosiQrManage bosiQrManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bosi_layout);
        initView();
    }

    private void initView() {
        mBtnTest = findViewById(R.id.btn_test);
        mBtnTest.setOnClickListener(this);
        mTvShow = findViewById(R.id.tv_show);
        bosiQrManage = new BosiQrManage();
    }

    String jh;

    ///storage/sdcard0/bosi2/
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_test) {
            //初始化
            CertVerInfo certVerInfo = new CertVerInfo(null, "");
            PosQR.init(this, "/storage/sdcard0/bosicer/");
            certVerInfo = PosQR.queryCertVer();
            mTvShow.append("状态：" + certVerInfo.getResult() + "版本号：" + Datautils.byteArrayToInt(certVerInfo.getCertVerInfo()));
            //更新证书
            String result = PosQR.updateCert(key1);
            mTvShow.append("UpdateCert1:\n" + result);
            result = PosQR.updateCert(key2);
            mTvShow.append("\nUpdateCert2:\n" + result);
            result = PosQR.updateCert(key3);
            mTvShow.append("\nUpdateCert3:\n" + result);
            //验码
            QrCodeInfo qrCodeInfo = PosQR.verifyQrCode(code);
            mTvShow.append(qrCodeInfo.toString());
            mTvShow.append("\n\n二维码具体信息");
            mTvShow.append(qrCodeInfo.toDetailString());

        } else {
        }
    }

    String code = "BSAQEBggD7arUQWdkCx10L6h0oCm3XGAJ6Z1ce3v+S1NrPkbQIQMru0YQ1eQEBEYOfZskGkOZA34BniRx8CaqHqIfzc37U2cG1msbkUXg0EthGRIb5SPIcCegQ1ZcHxdxfxCZPSGQ1mTuuRGzIbFqhGcwQjFHJOxptwRCukusJPtDL7Ll3p0o2Mc9kJRlYy/wnUBzTuGRBwDCOsiNAwmJ3X6OCVjuWAL7HO4AeyW5C50UX2NqFRkHBFjtaAZUOwXrTaQ+hxotu+7Z/a/IrsbcbLuLZGSZpX026xm3F/UTjoBuoOiAiHgHgZ/ijWTkC51m+AldLilhzaD3m31wsZeStEHM=";
    String key1 = "434552540102010101A174774F2DC46BA8EF744A7AE6833C2E9240FF118C541E5DF3E5708F44CEBDA6B0A598B7D1DBDEDD6AC917C04BABEDF83223ED4CE73C32002C3057AFE38AD3F4BE85C6395236CCAB004243A299B398923F7F598B90ACC68F2BA7C082D0A211B56B85A0892C2B48B6A6D42D6F4B8A2F69EBFB1C8DAED53C030B20AC0CE440BC451F8A7A3ECC15386B05889BFEB5901D8B444BEE18A1FF3984C5612FE037C378EC435D0405988D42AB4254B95C270B3B0F887659149B6E093C786EFA117656694E4480796038AD5E9CBE7E5F09F0074FE851265F4F36EED86F349E5DDB6650E64733C92D08EB21C828A385606F58AB05AF81682A89EB8719912F22A4215CE61C4D";
    String key2 = "434552540102010102E3535815BE52FDA6DA5D9D70B7E608AB4F0A9725F84F459CCD7AD090748111FA3DE5ED5F29CCECC1BA7CE457AB2E3055E9CBA15990C6936DC80BCA47563CC64DB180C2B027D882AA716AC18574129ABCBB2598A65C4872A134740CDD0BF285C8F92DF5817F7B9C1B6EFB67DC533EC9416CAFCECBDD6BFC2B65DC9E960E2EEAC1C45581CF134AE14441DC7B8383776D5BFFEA7517642CDF5E2912493CB1F2BE79968A3B966C68A08B3F3FECF46D0C5DCEC01CB268E76A75EF1A7A6683272F2C458C462DC03E55E9C5F290B79D87469B74207ACDAB92A47ECD0563D71E741C20224968854CE370F987ED6CE251A168459221FFC2DA30159A5A53DC87FDF0EB2F52";
    String key3 = "4345525401020101039B7CDA78675EEE5964ED6688AA309FF4CDE526FE83BFA710D1DDAEDA74E961114D275719A8732C57F49CBFA5A3936D467703453E7C4E438794482F1FDD868A1383B8AA14C4516879424E532ED23A0F56E09397E1F7D1F0B358B0CAC12F252E94D4915A34F4F1B0FF8645C9B84295848A7420D7FA8059E1A22EDEF2BF9052EE212A9D79E2C919A9C085C68B0217CC93C1777B342EDA00706CA930673FEEF144EEF01C5BB8F16FC9317C50F7853B94D7B52E3C5C9BF58B8D18AA74F07281E8A6CB347D8016BE87221A1D3CA7326DE7FA22F75272987CD6CDA3488392AA9A6365D0F24E5E0D4B4EFBD9830373583D902C19F699FBE2E13A0163954F9EF236B5FFE2";
}