package com.spd.base.been.tianjin;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 张明_ on 2019/2/21.
 * Email 741183142@qq.com
 */
@Entity
public class BaseInfoBackBean {
    /**
     * progversion : 00
     * code : 01
     * white : 20171122
     * binversion : 0
     * bin : 0
     * black : 20161218
     * program :     1.2.79
     */

    private String progversion;
    private String code;
    private String white;
    private String binversion;
    private String bin;
    private String black;
    private String program;
    @Generated(hash = 1426180021)
    public BaseInfoBackBean(String progversion, String code, String white,
            String binversion, String bin, String black, String program) {
        this.progversion = progversion;
        this.code = code;
        this.white = white;
        this.binversion = binversion;
        this.bin = bin;
        this.black = black;
        this.program = program;
    }
    @Generated(hash = 1578477684)
    public BaseInfoBackBean() {
    }
    public String getProgversion() {
        return this.progversion;
    }
    public void setProgversion(String progversion) {
        this.progversion = progversion;
    }
    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getWhite() {
        return this.white;
    }
    public void setWhite(String white) {
        this.white = white;
    }
    public String getBinversion() {
        return this.binversion;
    }
    public void setBinversion(String binversion) {
        this.binversion = binversion;
    }
    public String getBin() {
        return this.bin;
    }
    public void setBin(String bin) {
        this.bin = bin;
    }
    public String getBlack() {
        return this.black;
    }
    public void setBlack(String black) {
        this.black = black;
    }
    public String getProgram() {
        return this.program;
    }
    public void setProgram(String program) {
        this.program = program;
    }

    
}
