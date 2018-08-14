package com.wpos.sdkdemo.print;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by HS on 2017/11/17.
 */

public class BTPrinterCommand {


    /**
     * 打印纸一行最大的字节
     */
    private static final int LINE_BYTE_SIZE = 32;

    private static final int LEFT_LENGTH = 20;

    private static final int RIGHT_LENGTH = 12;

    /**
     * 左侧汉字最多显示几个文字
     */
    private static final int LEFT_TEXT_MAX_LENGTH = 8;

    /**
     * 小票打印菜品的名称，上限调到8个字
     */
    public static final int MEAL_NAME_MAX_LENGTH = 8;


    /**
     * 复位打印机
     */
    public static final byte[] RESET = {0x1b, 0x40};

    /**
     * 左对齐
     */
    public static final byte[] ALIGN_LEFT = {0x1b, 0x61, 0x00};

    /**
     * 中间对齐
     */
    public static final byte[] ALIGN_CENTER = {0x1b, 0x61, 0x01};

    /**
     * 右对齐
     */
    public static final byte[] ALIGN_RIGHT = {0x1b, 0x61, 0x02};

    /**
     * 选择加粗模式
     */
    public static final byte[] BOLD = {0x1b, 0x45, 0x01};

    /**
     * 取消加粗模式
     */
    public static final byte[] BOLD_CANCEL = {0x1b, 0x45, 0x00};

    /**
     * 宽高加倍
     */
    public static final byte[] DOUBLE_HEIGHT_WIDTH = {0x1d, 0x21, 0x11};

    /**
     * 宽加倍
     */
    public static final byte[] DOUBLE_WIDTH = {0x1d, 0x21, 0x10};

    /**
     * 高加倍
     */
    public static final byte[] DOUBLE_HEIGHT = {0x1d, 0x21, 0x01};

    /**
     * 字体不放大
     */
    public static final byte[] NORMAL = {0x1d, 0x21, 0x00};

    /**
     * 设置默认行间距
     */
    public static final byte[] LINE_SPACING_DEFAULT = {0x1b, 0x32};
    public static final byte[] printLine ={0x0a};//打印并换行
    /**
     * 设置行间距
     */
//	public static final byte[] LINE_SPACING = {0x1b, 0x32};//{0x1b, 0x33, 0x14};  // 20的行间距（0，255）


//	final byte[][] byteCommands = {
//			{ 0x1b, 0x61, 0x00 }, // 左对齐
//			{ 0x1b, 0x61, 0x01 }, // 中间对齐
//			{ 0x1b, 0x61, 0x02 }, // 右对齐
//			{ 0x1b, 0x40 },// 复位打印机
//			{ 0x1b, 0x4d, 0x00 },// 标准ASCII字体
//			{ 0x1b, 0x4d, 0x01 },// 压缩ASCII字体
//			{ 0x1d, 0x21, 0x00 },// 字体不放大
//			{ 0x1d, 0x21, 0x11 },// 宽高加倍
//			{ 0x1b, 0x45, 0x00 },// 取消加粗模式
//			{ 0x1b, 0x45, 0x01 },// 选择加粗模式
//			{ 0x1b, 0x7b, 0x00 },// 取消倒置打印
//			{ 0x1b, 0x7b, 0x01 },// 选择倒置打印
//			{ 0x1d, 0x42, 0x00 },// 取消黑白反显
//			{ 0x1d, 0x42, 0x01 },// 选择黑白反显
//			{ 0x1b, 0x56, 0x00 },// 取消顺时针旋转90°
//			{ 0x1b, 0x56, 0x01 },// 选择顺时针旋转90°
//	};

    /**
     * 打印两列
     *
     * @param leftText  左侧文字
     * @param rightText 右侧文字
     * @return
     */
    public static String printTwoData(String leftText, String rightText) {
        StringBuilder sb = new StringBuilder();
        int leftTextLength = getBytesLength(leftText);
        int rightTextLength = getBytesLength(rightText);
        sb.append(leftText);

        // 计算两侧文字中间的空格
        int marginBetweenMiddleAndRight = LINE_BYTE_SIZE - leftTextLength - rightTextLength;

        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }
        sb.append(rightText);
        return sb.toString();
    }

    /**
     * 打印三列
     *
     * @param leftText   左侧文字
     * @param middleText 中间文字
     * @param rightText  右侧文字
     * @return
     */
    public static String printThreeData(String leftText, String middleText, String rightText) {
        StringBuilder sb = new StringBuilder();
        // 左边最多显示 LEFT_TEXT_MAX_LENGTH 个汉字 + 两个点
        if (leftText.length() > LEFT_TEXT_MAX_LENGTH) {
            leftText = leftText.substring(0, LEFT_TEXT_MAX_LENGTH) + "..";
        }
        int leftTextLength = getBytesLength(leftText);
        int middleTextLength = getBytesLength(middleText);
        int rightTextLength = getBytesLength(rightText);

        sb.append(leftText);
        // 计算左侧文字和中间文字的空格长度
        int marginBetweenLeftAndMiddle = LEFT_LENGTH - leftTextLength - middleTextLength / 2;

        for (int i = 0; i < marginBetweenLeftAndMiddle; i++) {
            sb.append(" ");
        }
        sb.append(middleText);

        // 计算右侧文字和中间文字的空格长度
        int marginBetweenMiddleAndRight = RIGHT_LENGTH - middleTextLength / 2 - rightTextLength;

        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }

        // 打印的时候发现，最右边的文字总是偏右一个字符，所以需要删除一个空格
        sb.delete(sb.length() - 1, sb.length()).append(rightText);
        return sb.toString();
    }

    /**
     * 获取数据长度
     *
     * @param msg
     * @return
     */
    private static int getBytesLength(String msg) {
        return msg.getBytes(Charset.forName("GB2312")).length;
    }

    /**
     * 格式化菜品名称，最多显示MEAL_NAME_MAX_LENGTH个数
     *
     * @param name
     * @return
     */
    public static String formatMealName(String name) {
        if (TextUtils.isEmpty(name)) {
            return name;
        }
        if (name.length() > MEAL_NAME_MAX_LENGTH) {
            return name.substring(0, 8) + "..";
        }
        return name;
    }

    public static byte[] getdata(String s){
        if(TextUtils.isEmpty(s))
            return null;

        try {
            return s.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static byte[] setCommand(ArrayList<byte[]> data){
        byte[] allData = new byte[0];
        int position = 0;
        for(byte[] buffer:data){
            position = allData.length;
            allData = Arrays.copyOf(allData,position+buffer.length);
            System.arraycopy(buffer, 0, allData, position, buffer.length);

        }
        return allData;
    }
//    ArrayList<byte[]> data = new ArrayList<byte[]>();
//                    data.add(BTPrinterCommand.RESET);
//                    data.add(BTPrinterCommand.LINE_SPACING_DEFAULT);
//                    data.add(BTPrinterCommand.ALIGN_CENTER);
//                    data.add(getdata("微智智能终端 签购单"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("POS SALES SLIP"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("--------------------------------"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("MERCHANT NAME"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata(BTPrinterCommand.printTwoData("商户名称","CUPTEST")));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("MERCHANT NO"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata(BTPrinterCommand.printTwoData("商户编号","000000000000001")));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("TERMINAL NO"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata(BTPrinterCommand.printTwoData("终端编号","98765432")));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata(BTPrinterCommand.printTwoData("操作员号(OPERATOR)","01")));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata(BTPrinterCommand.printTwoData("卡号(CARD NUMBER)","4761 73** **** 0010")));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("--------------------------------"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata(BTPrinterCommand.printTwoData("发卡行号(ISS NO)","14334")));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata(BTPrinterCommand.printTwoData("收单行号(ACQ NO)","54546")));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata(BTPrinterCommand.printTwoData("交易类别(TXN TYPE)","GOODS/Purchase")));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata(BTPrinterCommand.printTwoData("有效期(EXP.DATE)","2020/12")));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata(BTPrinterCommand.printTwoData("批次号(BATCH NO)","000002")));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata(BTPrinterCommand.printTwoData("凭证号(VOUCHER NO)","000093")));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata(BTPrinterCommand.printTwoData("授权码(AUTH NO)","98765432")));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata(BTPrinterCommand.printTwoData("日期/时间(DATE/TIME)","2017/10/25 10:22:08")));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata(BTPrinterCommand.printTwoData("金额(AMOUT)","RMB 5.00")));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata(BTPrinterCommand.printTwoData("小费(TIPS)","RMB 0.00")));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata(BTPrinterCommand.printTwoData("总计(TOTAL)","RMB 5.00")));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("--------------------------------"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(BTPrinterCommand.ALIGN_LEFT);
//                    data.add(getdata("备注:"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("REFERENCE:"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("ARQC: 882D8427A268E214"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("AID: A0000000031010"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("TVR: 8080000800"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("TSI: 6800"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("ATC: 0001"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("应用标签: VISACREDIT"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("首选名称: VISA DebitCredit"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(BTPrinterCommand.ALIGN_CENTER);
//                    data.add(getdata("--------------------------------"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("本人确认以上交易，同意将其记入本卡帐户"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("I ACKNOWLEDGE SATISFACTORY"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("I RECEIPT OF RELATIVE GOODS/SERVICES"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("--------------------------------"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(getdata("商户存根  MERCHANT COPY"));
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(BTPrinterCommand.printLine);
//                    data.add(BTPrinterCommand.printLine);
//
//    result = mPrinter.escposBlueToothPrint(setCommand(data));
}
