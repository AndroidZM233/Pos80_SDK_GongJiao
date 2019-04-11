package com.spd.yinlianpay.cardparam;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018\9\4 0004.
 */

public class AidData {
    @SerializedName("AID")
    private String AID;                               //9F06
    @SerializedName("AcquierId")
    private String AcquierId;                         //9F01
    @SerializedName("SelFlag")
    private String SelFlag;                           //DF01
    @SerializedName("Version")
    private String Version;                          //9F09
    @SerializedName("Priority")
    private String Priority;
    @SerializedName("FloorLimitCheck")
    private int FloorLimitCheck;
    @SerializedName("FloorLimit")                    //9F1B
    private String FloorLimit;
    @SerializedName("RandTransSel")
    private String RandTransSel;
    @SerializedName("Threshold")                     //DF15
    private String Threshold;
    @SerializedName("TargetPer")
    private String TargetPer;                        //DF17
    @SerializedName("MaxTargetPer")
    private String MaxTargetPer;                     //DF16
    @SerializedName("VelocityCheck")
    private String VelocityCheck;
    @SerializedName("TACDenial")
    private String TACDenial;                        //DF13
    @SerializedName("TACOnline")
    private String TACOnline;                        //DF12
    @SerializedName("TACDefault")
    private String TACDefault;                       //DF11
    @SerializedName("DDOL")
    private String DDOL;                             //DF14
    @SerializedName("TDOL")
    private String TDOL;
    @SerializedName("RiskManData")
    private String RiskManData;                     //9F1D
    @SerializedName("BOnlinePin")
    private String BOnlinePin;                      //DF18
    @SerializedName("EC_TermLimit")
    private String EC_TermLimit;                    //9F7B
    @SerializedName("CL_bStatusCheck")
    private String CL_bStatusCheck;
    @SerializedName("CL_FloorLimit")
    private String CL_FloorLimit;                   //DF19
    @SerializedName("CL_TransLimit")
    private String CL_TransLimit;                   //DF20
    @SerializedName("CL_CVMLimit")
    private String CL_CVMLimit;                      //DF21
    @SerializedName("TermQuali_byte2")
    private String TermQuali_byte2;
    @SerializedName("TermInfoEnableFlag")
    private String TermInfoEnableFlag;
    @SerializedName("MerchId")
    private String MerchId;
    @SerializedName("TermId")
    private String TermId;
    @SerializedName("MerchName")
    private String MerchName;
    @SerializedName("MerchCateCode")
    private String MerchCateCode;                   //9F15
    @SerializedName("TransCurrCode")
    private String TransCurrCode;
    @SerializedName("TransCurrExp")
    private String TransCurrExp;
    @SerializedName("ReferCurrCode")
    private String ReferCurrCode;
    @SerializedName("ReferCurrExp")
    private String ReferCurrExp;

    @SerializedName("UDOL")
    private String UDOL;                            //DF811A
    @SerializedName("MagAvn")
    private String MagAvn;                          //9F6D
    @SerializedName("UcMagStrCVMCapWithCVM")
    private String UcMagStrCVMCapWithCVM;           //DF811E
    @SerializedName("UcMagStrCVMCapNoCVM")
    private String UcMagStrCVMCapNoCVM;             //DF812C
    @SerializedName("UcKernelConfig")
    private String UcKernelConfig;                  //DF811B
    //超过CVM限额使用DF8118 等于或者小于 DF8119
    @SerializedName("UcCVMCap")
    private String UcCVMCap;                       //DF8118
    @SerializedName("UcCVMCapNoCVM")
    private String UcCVMCapNoCVM;                  //DF8119
    @SerializedName("UcCardDataInputCap")
    private String UcCardDataInputCap;             //DF8117
    @SerializedName("SecurityCap")
    private String SecurityCap;                    //DF811F
    @SerializedName("CountryCode")
    private String CountryCode;
    @SerializedName("Interface Device Serial Number")
    private String InterfaceDeviceSerialNumber;

    @SerializedName("PPassTACDefault")
    private String PPassTACDefault;
    @SerializedName("PPassTACOnline")
    private String PPassTACOnline;

    public String getTransType() {
        return TransType;
    }

    public void setTransType(String transType) {
        TransType = transType;
    }

    @SerializedName("TransType")
    private String TransType;

    public String getPPassTACDenial() {
        return PPassTACDenial;
    }

    public void setPPassTACDenial(String PPassTACDenial) {
        this.PPassTACDenial = PPassTACDenial;
    }

    public String getPPassTACDefault() {
        return PPassTACDefault;
    }

    public void setPPassTACDefault(String PPassTACDefault) {
        this.PPassTACDefault = PPassTACDefault;
    }

    public String getPPassTACOnline() {
        return PPassTACOnline;
    }

    public void setPPassTACOnline(String PPassTACOnline) {
        this.PPassTACOnline = PPassTACOnline;
    }

    @SerializedName("PPassTACDenial")
    private String PPassTACDenial;

    public String getTerminalCapabilities() {
        return TerminalCapabilities;
    }

    public void setTerminalCapabilities(String terminalCapabilities) {
        TerminalCapabilities = terminalCapabilities;
    }

    @SerializedName("Terminal Capabilities")
    private String TerminalCapabilities;
    @SerializedName("Terminal Type") //9f35
    private String TerminalType;
    @SerializedName("Additional Terminal Capabilities")
    private String AdditionalTerminalCapabilities;
    @SerializedName("Merchant Name and Location")
    private String MerchantNameAndLocation;
    @SerializedName("Mag-stripe Application Version Number (Reader)")
    private String MagStripeAVN;

    public String getMobileSupportId() {
        return MobileSupportId;
    }

    public void setMobileSupportId(String mobileSupportId) {
        MobileSupportId = mobileSupportId;
    }

    @SerializedName("Mobile Support Indicator")
    private String MobileSupportId;
    @SerializedName("Application Life Cycle Data")
    private String AppLifeCycleData;
    @SerializedName("DS Input")
    private String DSInput;
    @SerializedName("DS ODS Info")
    private String DSODSInfo;
    @SerializedName("DS ODS Term")
    private String DSODSTerm;
    @SerializedName("DS AC Type")
    private String DSACType;
    @SerializedName("DS Input Term")
    private String DSInputTerm;
    @SerializedName("DS ODS Info For Reader")
    private String DSODSInfoForReader;
    @SerializedName("Kernel ID")
    private String KernelID;
    @SerializedName("DSVN Term")
    private String DSVNTerm;
    @SerializedName("Max Lifetime Of Torn Transaction Log")
    private String MLTornTransLog;
    @SerializedName("Max Number Of Torn Transaction Log")
    private String MNTornTransLog;
    @SerializedName("RCL_FloorLimit")
    private String RCL_FloorLimit;                   //DF8123
    @SerializedName("RdClssTxnLmtNoONdevice")
    private String RdClssTxnLmtNoONdevice;                   //DF8124
    @SerializedName("RdClssTxnLmtONdevice")
    private String RdClssTxnLmtONdevice;                   //DF8125
    @SerializedName("RCL_CVMLimit")
    private String RCL_CVMLimit;                      //DF8126
    @SerializedName("CL_bAmount0Check")
    private String CL_bAmount0Check;
    @SerializedName("CL_bAmount0Option")
    private String CL_bAmount0Option;
    @SerializedName("EC_bTermLimitCheck")
    private int EC_bTermLimitCheck;
    @SerializedName("CL_bFloorLimitCheck")
    private String CL_bFloorLimitCheck;
    @SerializedName("CL_bTransLimitCheck")
    private String CL_bTransLimitCheck;
    @SerializedName("CL_bCVMLimitCheck")
    private String CL_bCVMLimitCheck;
    @SerializedName("TermTransQuali")
    private String TermTransQuali;      //9F66
    @SerializedName("UcMagSupportFlg")
    private String UcMagSupportFlg;
    @SerializedName("CLNotAllowFlag")
    private String CLNotAllowFlag;
    @SerializedName("TermCapab")
    private String TermCapab;
    @SerializedName("ExTermCapab")
    private String ExTermCapab;


    @SerializedName("PPassTermFLmtFlg")
    private String ppassTermFLmtFlg;

    @SerializedName("PPassRdClssTxnLmtFlg")
    private String PPassRdClssTxnLmtFlg;
    @SerializedName("PPassRdCVMLmtFlg")
    private String PPassRdCVMLmtFlg;

    @SerializedName("PPassRdClssFLmtFlg")
    private String PPassRdClssFLmtFlg;

    @SerializedName("PPassRdClssTxnLmtONdeviceFlg")
    private String PPassRdClssTxnLmtONdeviceFlg;

    @SerializedName("PPRdClssTxnLmtNoONdeviceFlg")
    private String PPRdClssTxnLmtNoONdeviceFlg;

    public String getPpassTermFLmtFlg() {
        return ppassTermFLmtFlg;
    }

    public void setPpassTermFLmtFlg(String ppassTermFLmtFlg) {
        this.ppassTermFLmtFlg = ppassTermFLmtFlg;
    }

    public String getPPassRdClssTxnLmtFlg() {
        return PPassRdClssTxnLmtFlg;
    }

    public void setPPassRdClssTxnLmtFlg(String PPassRdClssTxnLmtFlg) {
        this.PPassRdClssTxnLmtFlg = PPassRdClssTxnLmtFlg;
    }

    public String getPPassRdCVMLmtFlg() {
        return PPassRdCVMLmtFlg;
    }

    public void setPPassRdCVMLmtFlg(String PPassRdCVMLmtFlg) {
        this.PPassRdCVMLmtFlg = PPassRdCVMLmtFlg;
    }

    public String getPPassRdClssFLmtFlg() {
        return PPassRdClssFLmtFlg;
    }

    public void setPPassRdClssFLmtFlg(String PPassRdClssFLmtFlg) {
        this.PPassRdClssFLmtFlg = PPassRdClssFLmtFlg;
    }

    public String getPPassRdClssTxnLmtONdeviceFlg() {
        return PPassRdClssTxnLmtONdeviceFlg;
    }

    public void setPPassRdClssTxnLmtONdeviceFlg(String PPassRdClssTxnLmtONdeviceFlg) {
        this.PPassRdClssTxnLmtONdeviceFlg = PPassRdClssTxnLmtONdeviceFlg;
    }

    public String getPPRdClssTxnLmtNoONdeviceFlg() {
        return PPRdClssTxnLmtNoONdeviceFlg;
    }

    public void setPPRdClssTxnLmtNoONdeviceFlg(String PPRdClssTxnLmtNoONdeviceFlg) {
        this.PPRdClssTxnLmtNoONdeviceFlg = PPRdClssTxnLmtNoONdeviceFlg;
    }

    public String getAID() {
        return AID;
    }

    public void setAID(String AID) {
        this.AID = AID;
    }

    public String getSelFlag() {
        return SelFlag;
    }

    public void setSelFlag(String selFlag) {
        SelFlag = selFlag;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriority(String priority) {
        Priority = priority;
    }

    public int getFloorLimitCheck() {
        return FloorLimitCheck;
    }

    public void setFloorLimitCheck(int floorLimitCheck) {
        FloorLimitCheck = floorLimitCheck;
    }

    public String getFloorLimit() {
        return FloorLimit;
    }

    public void setFloorLimit(String floorLimit) {
        FloorLimit = floorLimit;
    }

    public String getRandTransSel() {
        return RandTransSel;
    }

    public void setRandTransSel(String randTransSel) {
        RandTransSel = randTransSel;
    }

    public String getThreshold() {
        return Threshold;
    }

    public void setThreshold(String threshold) {
        Threshold = threshold;
    }

    public String getTargetPer() {
        return TargetPer;
    }

    public void setTargetPer(String targetPer) {
        TargetPer = targetPer;
    }

    public String getMaxTargetPer() {
        return MaxTargetPer;
    }

    public void setMaxTargetPer(String maxTargetPer) {
        MaxTargetPer = maxTargetPer;
    }

    public String getVelocityCheck() {
        return VelocityCheck;
    }

    public void setVelocityCheck(String velocityCheck) {
        VelocityCheck = velocityCheck;
    }

    public String getTACDenial() {
        return TACDenial;
    }

    public void setTACDenial(String TACDenial) {
        this.TACDenial = TACDenial;
    }

    public String getTACOnline() {
        return TACOnline;
    }

    public void setTACOnline(String TACOnline) {
        this.TACOnline = TACOnline;
    }

    public String getTACDefault() {
        return TACDefault;
    }

    public void setTACDefault(String TACDefault) {
        this.TACDefault = TACDefault;
    }

    public String getDDOL() {
        return DDOL;
    }

    public void setDDOL(String DDOL) {
        this.DDOL = DDOL;
    }

    public String getTDOL() {
        return TDOL;
    }

    public void setTDOL(String TDOL) {
        this.TDOL = TDOL;
    }

    public String getRiskManData() {
        return RiskManData;
    }

    public void setRiskManData(String riskManData) {
        RiskManData = riskManData;
    }

    public String getAcquierId() {
        return AcquierId;
    }

    public void setAcquierId(String acquierId) {
        AcquierId = acquierId;
    }

    public String getBOnlinePin() {
        return BOnlinePin;
    }

    public void setBOnlinePin(String BOnlinePin) {
        this.BOnlinePin = BOnlinePin;
    }

    public String getEC_TermLimit() {
        return EC_TermLimit;
    }

    public void setEC_TermLimit(String EC_TermLimit) {
        this.EC_TermLimit = EC_TermLimit;
    }

    public String getCL_bStatusCheck() {
        return CL_bStatusCheck;
    }

    public void setCL_bStatusCheck(String CL_bStatusCheck) {
        this.CL_bStatusCheck = CL_bStatusCheck;
    }

    public String getCL_FloorLimit() {
        return CL_FloorLimit;
    }

    public void setCL_FloorLimit(String CL_FloorLimit) {
        this.CL_FloorLimit = CL_FloorLimit;
    }

    public String getCL_TransLimit() {
        return CL_TransLimit;
    }

    public void setCL_TransLimit(String CL_TransLimit) {
        this.CL_TransLimit = CL_TransLimit;
    }

    public String getCL_CVMLimit() {
        return CL_CVMLimit;
    }

    public void setCL_CVMLimit(String CL_CVMLimit) {
        this.CL_CVMLimit = CL_CVMLimit;
    }

    public String getTermQuali_byte2() {
        return TermQuali_byte2;
    }

    public void setTermQuali_byte2(String termQuali_byte2) {
        TermQuali_byte2 = termQuali_byte2;
    }

    public String getTermInfoEnableFlag() {
        return TermInfoEnableFlag;
    }

    public void setTermInfoEnableFlag(String termInfoEnableFlag) {
        TermInfoEnableFlag = termInfoEnableFlag;
    }

    public String getMerchId() {
        return MerchId;
    }

    public void setMerchId(String merchId) {
        MerchId = merchId;
    }

    public String getTermId() {
        return TermId;
    }

    public void setTermId(String termId) {
        TermId = termId;
    }

    public String getMerchName() {
        return MerchName;
    }

    public void setMerchName(String merchName) {
        MerchName = merchName;
    }

    public String getMerchCateCode() {
        return MerchCateCode;
    }

    public void setMerchCateCode(String merchCateCode) {
        MerchCateCode = merchCateCode;
    }

    public String getTransCurrCode() {
        return TransCurrCode;
    }

    public void setTransCurrCode(String transCurrCode) {
        TransCurrCode = transCurrCode;
    }

    public String getTransCurrExp() {
        return TransCurrExp;
    }

    public void setTransCurrExp(String transCurrExp) {
        TransCurrExp = transCurrExp;
    }

    public String getReferCurrCode() {
        return ReferCurrCode;
    }

    public void setReferCurrCode(String referCurrCode) {
        ReferCurrCode = referCurrCode;
    }

    public String getReferCurrExp() {
        return ReferCurrExp;
    }

    public void setReferCurrExp(String referCurrExp) {
        ReferCurrExp = referCurrExp;
    }

    public String getUDOL() {
        return UDOL;
    }

    public void setUDOL(String UDOL) {
        this.UDOL = UDOL;
    }

    public String getMagAvn() {
        return MagAvn;
    }

    public void setMagAvn(String magAvn) {
        MagAvn = magAvn;
    }

    public String getUcMagStrCVMCapWithCVM() {
        return UcMagStrCVMCapWithCVM;
    }

    public void setUcMagStrCVMCapWithCVM(String ucMagStrCVMCapWithCVM) {
        UcMagStrCVMCapWithCVM = ucMagStrCVMCapWithCVM;
    }

    public String getUcMagStrCVMCapNoCVM() {
        return UcMagStrCVMCapNoCVM;
    }

    public void setUcMagStrCVMCapNoCVM(String ucMagStrCVMCapNoCVM) {
        UcMagStrCVMCapNoCVM = ucMagStrCVMCapNoCVM;
    }

    public String getUcKernelConfig() {
        return UcKernelConfig;
    }

    public void setUcKernelConfig(String ucKernelConfig) {
        UcKernelConfig = ucKernelConfig;
    }

    public String getUcCVMCap() {
        return UcCVMCap;
    }

    public void setUcCVMCap(String ucCVMCap) {
        UcCVMCap = ucCVMCap;
    }

    public String getUcCVMCapNoCVM() {
        return UcCVMCapNoCVM;
    }

    public void setUcCVMCapNoCVM(String ucCVMCapNoCVM) {
        UcCVMCapNoCVM = ucCVMCapNoCVM;
    }

    public String getUcCardDataInputCap() {
        return UcCardDataInputCap;
    }

    public void setUcCardDataInputCap(String ucCardDataInputCap) {
        UcCardDataInputCap = ucCardDataInputCap;
    }

    public String getSecurityCap() {
        return SecurityCap;
    }

    public void setSecurityCap(String securityCap) {
        SecurityCap = securityCap;
    }

    public String getRdClssTxnLmtNoONdevice() {
        return RdClssTxnLmtNoONdevice;
    }

    public void setRdClssTxnLmtNoONdevice(String rdClssTxnLmtNoONdevice) {
        RdClssTxnLmtNoONdevice = rdClssTxnLmtNoONdevice;
    }

    public String getRdClssTxnLmtONdevice() {
        return RdClssTxnLmtONdevice;
    }

    public void setRdClssTxnLmtONdevice(String rdClssTxnLmtONdevice) {
        RdClssTxnLmtONdevice = rdClssTxnLmtONdevice;
    }

    public String getCL_bAmount0Check() {
        return CL_bAmount0Check;
    }

    public void setCL_bAmount0Check(String CL_bAmount0Check) {
        this.CL_bAmount0Check = CL_bAmount0Check;
    }

    public String getCL_bAmount0Option() {
        return CL_bAmount0Option;
    }

    public void setCL_bAmount0Option(String CL_bAmount0Option) {
        this.CL_bAmount0Option = CL_bAmount0Option;
    }

    public int getEC_bTermLimitCheck() {
        return EC_bTermLimitCheck;
    }

    public void setEC_bTermLimitCheck(int EC_bTermLimitCheck) {
        this.EC_bTermLimitCheck = EC_bTermLimitCheck;
    }

    public String getCL_bFloorLimitCheck() {
        return CL_bFloorLimitCheck;
    }

    public void setCL_bFloorLimitCheck(String CL_bFloorLimitCheck) {
        this.CL_bFloorLimitCheck = CL_bFloorLimitCheck;
    }

    public String getCL_bTransLimitCheck() {
        return CL_bTransLimitCheck;
    }

    public void setCL_bTransLimitCheck(String CL_bTransLimitCheck) {
        this.CL_bTransLimitCheck = CL_bTransLimitCheck;
    }

    public String getCL_bCVMLimitCheck() {
        return CL_bCVMLimitCheck;
    }

    public void setCL_bCVMLimitCheck(String CL_bCVMLimitCheck) {
        this.CL_bCVMLimitCheck = CL_bCVMLimitCheck;
    }

    public String getTermTransQuali() {
        return TermTransQuali;
    }

    public void setTermTransQuali(String termTransQuali) {
        TermTransQuali = termTransQuali;
    }

    public String getUcMagSupportFlg() {
        return UcMagSupportFlg;
    }

    public void setUcMagSupportFlg(String ucMagSupportFlg) {
        UcMagSupportFlg = ucMagSupportFlg;
    }

    public String getCLNotAllowFlag() {
        return CLNotAllowFlag;
    }

    public void setCLNotAllowFlag(String CLNotAllowFlag) {
        this.CLNotAllowFlag = CLNotAllowFlag;
    }

    public String getTerminalType() {
        return TerminalType;
    }

    public void setTerminalType(String terminalType) {
        TerminalType = terminalType;
    }

    public String getTermCapab() {
        return TermCapab;
    }

    public void setTermCapab(String termCapab) {
        TermCapab = termCapab;
    }

    public String getExTermCapab() {
        return ExTermCapab;
    }

    public void setExTermCapab(String exTermCapab) {
        ExTermCapab = exTermCapab;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    @Override
    public String toString() {
        return "aids{" +
                "AID='" + AID + '\'' +
                ", SelFlag='" + SelFlag + '\'' +
                ", Version='" + Version + '\'' +
                ", Priority='" + Priority + '\'' +
                ", FloorLimitCheck='" + FloorLimitCheck + '\'' +
                ", FloorLimit='" + FloorLimit + '\'' +
                ", RandTransSel='" + RandTransSel + '\'' +
                ", Threshold='" + Threshold + '\'' +
                ", TargetPer='" + TargetPer + '\'' +
                ", MaxTargetPer='" + MaxTargetPer + '\'' +
                ", VelocityCheck='" + VelocityCheck + '\'' +
                ", TACDenial='" + TACDenial + '\'' +
                ", TACOnline='" + TACOnline + '\'' +
                ", TACDefault='" + TACDefault + '\'' +
                ", DDOL='" + DDOL + '\'' +
                ", TDOL='" + TDOL + '\'' +
                ", RiskManData='" + RiskManData + '\'' +
                ", AcquierId='" + AcquierId + '\'' +
                ", BOnlinePin='" + BOnlinePin + '\'' +
                ", EC_TermLimit='" + EC_TermLimit + '\'' +
                ", CL_bStatusCheck='" + CL_bStatusCheck + '\'' +
                ", CL_FloorLimit='" + CL_FloorLimit + '\'' +
                ", CL_TransLimit='" + CL_TransLimit + '\'' +
                ", CL_CVMLimit='" + CL_CVMLimit + '\'' +
                ", TermQuali_byte2='" + TermQuali_byte2 + '\'' +
                ", TermInfoEnableFlag='" + TermInfoEnableFlag + '\'' +
                ", MerchId='" + MerchId + '\'' +
                ", TermId='" + TermId + '\'' +
                ", MerchName='" + MerchName + '\'' +
                ", MerchCateCode='" + MerchCateCode + '\'' +
                ", TransCurrCode='" + TransCurrCode + '\'' +
                ", TransCurrExp='" + TransCurrExp + '\'' +
                ", ReferCurrCode='" + ReferCurrCode + '\'' +
                ", ReferCurrExp='" + ReferCurrExp + '\'' +
                '}';
    }

}
