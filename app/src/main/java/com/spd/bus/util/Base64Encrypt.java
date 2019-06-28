package com.spd.bus.util;

/**
 * Class: Base64Encrypt
 * package：com.tjyitong.reservationsystem.util
 * Created by hzjst on 2018/3/15.
 * E_mail：hzjstning@163.com
 * Description：
 */
public class Base64Encrypt {
    private static final String CODES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    // base64解密
    public static byte[] base64Decode(String input) {
        if (input.length() % 4 != 0) {
            throw new IllegalArgumentException( "Invalid base64 input" );
        }
        byte decoded[] = new byte[((input.length() * 3) / 4)
                - (input.indexOf( '=' ) > 0 ? (input.length() - input.indexOf( '=' )) : 0)];
        char[] inChars = input.toCharArray();
        int j = 0;
        int b[] = new int[4];
        for (int i = 0; i < inChars.length; i += 4) {
            // This could be made faster (but more complicated) by precomputing
            // these index locations.
            b[0] = CODES.indexOf( inChars[i] );
            b[1] = CODES.indexOf( inChars[i + 1] );
            b[2] = CODES.indexOf( inChars[i + 2] );
            b[3] = CODES.indexOf( inChars[i + 3] );
            decoded[j++] = (byte) ((b[0] << 2) | (b[1] >> 4));
            if (b[2] < 64) {
                decoded[j++] = (byte) ((b[1] << 4) | (b[2] >> 2));
                if (b[3] < 64) {
                    decoded[j++] = (byte) ((b[2] << 6) | b[3]);
                }
            }
        }
        return decoded;
    }

    // base64加密
    public static String base64Encode(byte[] in) {
        StringBuilder out = new StringBuilder( (in.length * 4) / 3 );
        int b;
        for (int i = 0; i < in.length; i += 3) {
            b = (in[i] & 0xFC) >> 2;
            out.append( CODES.charAt( b ) );
            b = (in[i] & 0x03) << 4;
            if (i + 1 < in.length) {
                b |= (in[i + 1] & 0xF0) >> 4;
                out.append( CODES.charAt( b ) );
                b = (in[i + 1] & 0x0F) << 2;
                if (i + 2 < in.length) {
                    b |= (in[i + 2] & 0xC0) >> 6;
                    out.append( CODES.charAt( b ) );
                    b = in[i + 2] & 0x3F;
                    out.append( CODES.charAt( b ) );
                } else {
                    out.append( CODES.charAt( b ) );
                    out.append( '=' );
                }
            } else {
                out.append( CODES.charAt( b ) );
                out.append( "==" );
            }
        }
        return out.toString();
    }
}