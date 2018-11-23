package com.spd.base.beenres;

public class QrcodeUploadResult {


    /**
     * Message : Success
     */

    private String Message;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    @Override
    public String toString() {
        return "QrcodeUploadResult{" +
                "Message='" + Message + '\'' +
                '}';
    }
}
