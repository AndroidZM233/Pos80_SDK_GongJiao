package com.spd.base;

import com.spd.base.utils.Datautils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        byte[] id = new byte[2];
        if (id == null) {
            System.out.println("为空");
        } else {
            System.out.println("不为空" + Datautils.byteArrayToString(id));
        }
    }
}