package com.spd.alipay.been;

public class PayUploadResult {


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
        return "PayUploadResult{" +
                "Message='" + Message + '\'' +
                '}';
    }
}
