package com.spd.base;

import com.spd.base.dbbeen.RunParaFile;
import com.spd.base.utils.AppUtils;
import com.spd.base.utils.Datautils;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void test(){
        String deviceSN = AppUtils.getDeviceSN2();
        System.out.println(deviceSN);
    }
    /**
     * 语言       1个字节    2个字节      4个字节       8个字节
     * java       byte    char、short    int           long
     * c          char     short         int、long
     * <p>
     * C中可以定义有符号的变量和无符号的变量类型 signed char范围是-128~127，unsigned char的范围是0~255
     * Java中只有带符号的变量
     */


    @Test
    public void test1() {
        //byte、short的位操作都是先转换成int类型，再进行位操作。

        //0001 0010
        byte a = (byte) 0x12;
        //1000 0001
        byte b = (byte) 0x81;

        //计算机中都是用补码来存储数据
        //正数补码为原码，负数补码为去符号位后，原码取反加1

        //1000 0001
        // 000 0001
        // 000 0000
        // -111 1111

        int c = b;
        System.out.println(c);


//        for (valid = 0, i = 0; i < 3; i++) {
//            valid <<= 8;
//            valid += ucCpuYuePower[i] & 0xFF;
//        }

        //byte类型的数字要&0xff再赋值给int类型，其本质原因就是想保持二进制补码的一致性。
        //当byte要转化为int的时候，高的24位必然会补1，这样，其二进制补码其实已经不一致了，
        // &0xff可以将高的24位置为0，低8位保持原样。这样做的目的就是为了保证二进制数据的一致性。


        //1000 0001
        //1111 1111 1111 1111 1111 1111 1000 0001
        //0000 0000 0000 0000 0000 0000 1111 1111

        //0000 0000 0000 0000 0000 0000 1000 0001
//        byte b = (byte) 0x81;
//        int 0xffffff81 发生了符号补位
    }

    //  >> 和 >>>的区别就是有无符号的区别
    //右移运算符>>(有符号)
    //无符号右移运算符>>>

    @Test
    public void test2() {
        //对于正数，两种位操作都是一样。

        //0001 0010
        //0000 0100
        byte a = (byte) 0x12;
        //0000 0100  ---10
        int i = a >> 2;
        System.out.println(i);
        //0000 0100  ---10
        int i1 = a >>> 2;
        System.out.println(i1);

        //但对于负数来说就不一样了，带符号右移>>后最高位补1，无符号右移>>>最高位补0。

        //1000 0001
        byte b = (byte) 0x81;


        //111111 1111 1111 1111 1111 1111 1000 00

        // -111 1111 1111 1111 1111 1111 1110 0000 -1

        // 111 1111 1111 1111 1111 1111 1110 0000

        // 111 1111 1111 1111 1111 1111 1101 1111
        //   000 0000 0000 0000 0000 0000 0010 0000

        //                              0010 0000
        //-32
        int i2 = b >> 2;
        System.out.println(i2);
        //0011 1111 1111 1111 1111 1111 1110 0000
        //0
        //01111 1111 1111 1111 1111 1111 1000 00
        //
        int i3 = b >>> 2;
        System.out.println(i3);

    }


//    位异或运算（^）
//    运算规则是：两个数转为二进制，然后从高位开始比较，如果相同则为0，不相同则为1。
//    位与运算符（&）
//    运算规则：两个数都转为二进制，然后从高位开始比较，如果两个数都为1则为1，否则为0。
//    位或运算符（|）
//    运算规则：两个数都转为二进制，然后从高位开始比较，两个数只要有一个为1则为1，否则就为0。
//    位非运算符（~）
//    运算规则：如果位为0，结果是1，如果位为1，结果是0.




}