#include <jni.h>
#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include <string.h>
#include "pos_crypto.h"
#include "alipay.h"
#include  <unistd.h>
#include  <sys/types.h>
#include  <sys/stat.h>
#include  <fcntl.h>
#include  <termios.h>


char print_buf[1024] = {0};

static int demo_init();

int hex_string_to_bytes(
        char *hex_string,
        int hex_string_len,
        unsigned char *bytes,
        int bytes_len);

char *bytes_to_hex_string(
        char *print_buf,
        int print_buf_len,
        const unsigned char *bytes,
        int len);

unsigned char hex_of_char(char c);

static void print_info_response(INFO_RESPONSE *response);

typedef struct pubkey {
    int key_id;
    char *pub_key;
} PUBLICKEY;

PUBLICKEY publicKey[50];

JNIEXPORT jint JNICALL
Java_com_spd_alipay_AlipayJni_initdev(JNIEnv *env, jobject obj, jobject key_obj) {
//获取ArrayList 对象
    jclass jcs_alist = (*env)->GetObjectClass(env, key_obj);
    //获取Arraylist的methodid
    jmethodID alist_get = (*env)->GetMethodID(env, jcs_alist, "get",
                                              "(I)Ljava/lang/Object;");
    jmethodID alist_size = (*env)->GetMethodID(env, jcs_alist, "size", "()I");
    jint len = (*env)->CallIntMethod(env, key_obj, alist_size);
    for (int i = 0; i < len; i++) {
        //获取StuInfo对象
        jobject keystu_obj = (*env)->CallObjectMethod(env, key_obj, alist_get, i);
        //获取StuInfo类
        jclass key_cls = (*env)->GetObjectClass(env, keystu_obj);

        jmethodID keyId = (*env)->GetMethodID(env, key_cls, "getKey_id", "()I");
        jmethodID pubKey = (*env)->GetMethodID(env, key_cls, "getPub_key", "()Ljava/lang/String;");
        jint Id = (*env)->CallIntMethod(env, keystu_obj, keyId);
        publicKey[i].key_id = Id;
        jstring classStr = (jstring) (*env)->CallObjectMethod(env, keystu_obj, pubKey);
        char *pubkey = (char*)(*env)->GetStringUTFChars(env, classStr, 0);
        publicKey[i].pub_key = pubkey;
    }

    return demo_init();
}

JNIEXPORT jint JNICALL Java_com_spd_alipay_AlipayJni_release(JNIEnv *env, jobject obj) {
    return uninit();
}

JNIEXPORT jobject JNICALL
Java_com_spd_alipay_AlipayJni_checkAliQrCodeJni(JNIEnv *env, jobject obj, jobject classInfo, jstring Qrcode,
                                     jstring record_id,
                                     jstring pos_id, jstring pos_mf_id, jstring pos_sw_version,
                                     jstring merchant_type, jstring currency, jint amount,
                                     jstring vehicle_id, jstring plate_no, jstring driver_id,
                                     jstring line_info, jstring station_no, jstring lbs_info,
                                     jstring record_type) {
    const char *qrcode;
    qrcode = (*env)->GetStringUTFChars(env, Qrcode, 0);
    int ret;
    jbyte result[1000] = {0};
    char record[1000] = {0}, temp[5] = {0}, retStr[10] = {0};
    int recordLen = 0;
    return check_qrcode_demo((char *) qrcode, record, &recordLen, env, classInfo, record_id,
                             pos_id, pos_mf_id, pos_sw_version,
                             merchant_type, currency, amount,
                             vehicle_id, plate_no, driver_id,
                             line_info, station_no, lbs_info,
                             record_type);
//    retStr[0] = ret;
//    hexToString(retStr, temp, 1);
//    result[0] = temp[0];
//    result[1] = temp[1];
//    memcpy(&result[2], record, recordLen);
//    jbyteArray jarrRV = (*env)->NewByteArray(env, recordLen + 2);
//    (*env)->SetByteArrayRegion(env, jarrRV, 0, recordLen + 2, result);
    //返回一个结构体
//    jclass objectClass = (*env)->FindClass(env, "com/spd/alipay/been/AliCodeinfoData");
//    jfieldID name = (*env)->GetFieldID(env, objectClass, "name", "Ljava/lang/String;");
//    jfieldID inforState = (*env)->GetFieldID(env, objectClass, "inforState", "I");
//    jfieldID average = (*env)->GetFieldID(env, objectClass, "average", "D");
//
//    (*env)->SetObjectField(env, classInfo, name, (*env)->NewStringUTF(env, "hello world"));
//    (*env)->SetIntField(env, classInfo, inforState, ret);
//    (*env)->SetDoubleField(env, classInfo, average, 2.5);
//
//    return classInfo;


}

jobject
check_qrcode_demo(char *qrcode_hex, char *record, int *recordLen, JNIEnv *env, jobject classInfo,
                  jstring record_id,
                  jstring pos_id, jstring pos_mf_id, jstring pos_sw_version,
                  jstring merchant_type, jstring currency, jint amounts,
                  jstring vehicle_id, jstring plate_no, jstring driver_id,
                  jstring line_info, jstring station_no, jstring lbs_info,
                  jstring record_type) {
    //返回一个结构体
    jclass objectClass = (*env)->FindClass(env, "com/spd/alipay/been/AliCodeinfoData");
    jfieldID inforState = (*env)->GetFieldID(env, objectClass, "inforState", "I");
    jfieldID keyId = (*env)->GetFieldID(env, objectClass, "keyId", "I");
    jfieldID userId = (*env)->GetFieldID(env, objectClass, "userId", "[B");
    jfieldID cardType = (*env)->GetFieldID(env, objectClass, "cardType", "[B");
    jfieldID cardNo = (*env)->GetFieldID(env, objectClass, "cardNo", "[B");
    jfieldID alipayResult = (*env)->GetFieldID(env, objectClass, "alipayResult", "[B");
//    jfieldID average = (*env)->GetFieldID(env, objectClass, "average", "D");


//    (*env)->SetDoubleField(env, classInfo, average, 2.5);
    int ret = 0;

    int len = 10;
    char array[len];

    unsigned char qrcode[512] = {0};
    int qrcode_len = sizeof(qrcode);
    /*
    * info_response mot
    */
    char cert_sn[7] = {0};
    char mot_code_issuer_no[9] = {0};
    char card_issuer_no[9] = {0};
    char mot_user_id[17] = {0};
    char mot_card_no[21] = {0};
    unsigned char mot_card_data[129] = {0};
    /*
    * info_response alipay
    */
    char alipay_code_issuer_no[9] = {0};
    char card_type[9] = {0};
    char alipay_user_id[17] = {0};
    char alipay_card_no[20] = {0};
    unsigned char alipay_card_data[129] = {0};

    char proto_type[8] = {0};

    INFO_REQUEST info_request;
    INFO_RESPONSE info_response;
    CODE_INFO code_info;
    info_response.code_info = &code_info;

//	char qrcode_hex[] = QRCODE_HEX_DATA;
    int qrcode_hex_len = strlen(qrcode_hex);

    hex_string_to_bytes(qrcode_hex, qrcode_hex_len, qrcode, qrcode_len);

//    printf("mydebug", "===========准备数据================\n");

    /**
     * 获取二维码信息
     * INFO_REQUEST 二维码数据、长度
     * INFO_RESPONSE 二维码内容，如果同时支持2种协议的二维码，初始化时请2种协议对应的返回参数都初始化
     */
    info_request.qrcode = qrcode;
    info_request.qrcode_len = qrcode_hex_len / 2;
    info_response.proto_type = proto_type;
    //mot
    info_response.code_info->mot_code_info.cert_sn = cert_sn;
    info_response.code_info->mot_code_info.cert_sn_len = sizeof(cert_sn);
    info_response.code_info->mot_code_info.card_issuer_no = card_issuer_no;
    info_response.code_info->mot_code_info.card_issuer_no_len = sizeof(card_issuer_no);
    info_response.code_info->mot_code_info.code_issuer_no = mot_code_issuer_no;
    info_response.code_info->mot_code_info.code_issuer_no_len = sizeof(mot_code_issuer_no);
    info_response.code_info->mot_code_info.user_id = mot_user_id;
    info_response.code_info->mot_code_info.user_id_len = sizeof(mot_user_id);
    info_response.code_info->mot_code_info.card_no = mot_card_no;
    info_response.code_info->mot_code_info.card_no_len = sizeof(mot_card_no);
    info_response.code_info->mot_code_info.card_data = mot_card_data;
    info_response.code_info->mot_code_info.card_data_len = sizeof(mot_card_data);
    //alipay
    info_response.code_info->alipay_code_info.card_type = card_type;
    info_response.code_info->alipay_code_info.card_type_len = sizeof(card_type);
    info_response.code_info->alipay_code_info.code_issuer_no = alipay_code_issuer_no;
    info_response.code_info->alipay_code_info.code_issuer_no_len = sizeof(alipay_code_issuer_no);
    info_response.code_info->alipay_code_info.user_id = alipay_user_id;
    info_response.code_info->alipay_code_info.user_id_len = sizeof(alipay_user_id);
    info_response.code_info->alipay_code_info.card_no = alipay_card_no;
    info_response.code_info->alipay_code_info.card_no_len = sizeof(alipay_card_no);
    info_response.code_info->alipay_code_info.card_data = alipay_card_data;
    info_response.code_info->alipay_code_info.card_data_len = sizeof(alipay_card_data);


//    (*env)->SetIntField(env, classInfo, keyId, data5);

    ret = get_qrcode_info(&info_request, &info_response);
//    print_info_response(&info_response);
    if (ret != SUCCESS) {
        (*env)->SetIntField(env, classInfo, inforState, ret);
        return classInfo;
    }
    char *aaa = info_response.code_info->alipay_code_info.user_id;
    //cha* 转jbyteArray
    jbyteArray data1 = (*env)->NewByteArray(env,
                                            (jsize)strlen(info_response.code_info->alipay_code_info.user_id));
    (*env)->SetByteArrayRegion(env, data1, 0,
                               (jsize)strlen(info_response.code_info->alipay_code_info.user_id),
                               (jbyte *) info_response.code_info->alipay_code_info.user_id);
//    char *ttt = "123123132";
//    jbyteArray data1 = (*env)->NewByteArray(env,9);
//    (*env)->SetByteArrayRegion(env, data1, 0, 9, ttt);
    jbyteArray data2 = (*env)->NewByteArray(env,
                                            (jsize)strlen(info_response.code_info->alipay_code_info.card_type));
    (*env)->SetByteArrayRegion(env, data2, 0,
                               (jsize) strlen(info_response.code_info->alipay_code_info.card_type),
                               (jbyte *) info_response.code_info->alipay_code_info.card_type);
    jbyteArray data3 = (*env)->NewByteArray(env,
                                            (jsize)strlen(info_response.code_info->alipay_code_info.card_no));
    (*env)->SetByteArrayRegion(env, data3, 0,
                               (jsize) strlen(info_response.code_info->alipay_code_info.card_no),
                               (jbyte *) info_response.code_info->alipay_code_info.card_no);
//    jbyteArray data5 = (*env)->NewByteArray(env, strlen(info_response.code_info->alipay_code_info.key_id));
//    (*env)->SetByteArrayRegion(env, data5, 0, strlen(info_response.code_info->alipay_code_info.key_id), info_response.code_info->alipay_code_info.key_id);

    (*env)->SetObjectField(env, classInfo, userId, data1);
    (*env)->SetObjectField(env, classInfo, cardType, data2);
    (*env)->SetObjectField(env, classInfo, cardNo, data3);
    (*env)->SetIntField(env, classInfo, inforState, ret);
    //5秒内同一个用户不能连刷


    /**
     * 获取二维码信息后，请根据二维码信息获取指定的密钥，在验证时传入
     */

    /**
     * pos_param中填入商户pos相关信息 至少包括：
     *		- record_id	  (记录id，商户下本次脱机记录唯一id号，record_id必须保证商户唯一，建议通过POS，时间等信息拼装)
     *      - pos_id      (商户下唯一的pos号)
     *      - pos_mf_id   (终端制造商id)
     *      - pos_sw_version (终端软件版本)
     *      - merchant_type （商户mcc码）
     *      - currency (币种 人民币请填入156)
     *      - amount （交易金额， 单位：分）
     *      - vehicle_id （车辆id）
     *      - plate_no  (车牌号)
     *      - driver_id （司机号）
     *      - line_info (线路信息)
     *      - station_no (站点信息)
     *      - lbs_info (地理位置信息)
     *      - record_type (脱机记录类型，公交场景为"BUS", 地铁场景为"SUBWAY")
     */
    POS_PARAM_STRUCT pos_param_struct;
    // = (POS_PARAM_STRUCT *)malloc(sizeof(POS_PARAM_STRUCT));
//    pos_param_struct.record_id = "sh001_20160514140218_000001";
//    pos_param_struct.consumption_type = 0;
//    pos_param_struct.pos_id = "20170000000001";
//    pos_param_struct.pos_mf_id = "9998112123";
//    pos_param_struct.pos_sw_version = "2.6.14.03arm";
//    pos_param_struct.merchant_type = "22";
//    pos_param_struct.currency = "156";
//    pos_param_struct.amount = 1;
//    pos_param_struct.vehicle_id = "vid9702";
//    pos_param_struct.plate_no = "粤A 095852";
//    pos_param_struct.driver_id = "0236245394";
//    pos_param_struct.line_info = "795";
//    pos_param_struct.station_no = "asd";
//    pos_param_struct.lbs_info = "aaaa";
//    pos_param_struct.record_type = "SUBWAY";
    jboolean iscopy;
    const char *recordId = (*env)->GetStringUTFChars(env, record_id, &iscopy);
    pos_param_struct.record_id = recordId;
    (*env)->ReleaseStringUTFChars(env, record_id, recordId);
    pos_param_struct.consumption_type = 0;
    const char *posId = (*env)->GetStringUTFChars(env, pos_id, &iscopy);
    pos_param_struct.pos_id = posId;
    (*env)->ReleaseStringUTFChars(env, pos_id, posId);
    const char *posmfId = (*env)->GetStringUTFChars(env, pos_mf_id, &iscopy);
    pos_param_struct.pos_mf_id = posmfId;
    (*env)->ReleaseStringUTFChars(env, pos_mf_id, posmfId);
    const char *posswVersion = (*env)->GetStringUTFChars(env, pos_sw_version, &iscopy);

    pos_param_struct.pos_sw_version = posswVersion;
    (*env)->ReleaseStringUTFChars(env, pos_sw_version, posswVersion);
    const char *merchantType = (*env)->GetStringUTFChars(env, merchant_type, &iscopy);
    pos_param_struct.merchant_type = merchantType;
    (*env)->ReleaseStringUTFChars(env, merchant_type, merchantType);
    const char *currencyType = (*env)->GetStringUTFChars(env, currency, &iscopy);
    pos_param_struct.currency = currencyType;
    (*env)->ReleaseStringUTFChars(env, currency, currencyType);
    pos_param_struct.amount = amounts;
    const char *vehicleId = (*env)->GetStringUTFChars(env, vehicle_id, &iscopy);
    pos_param_struct.vehicle_id = vehicleId;
    (*env)->ReleaseStringUTFChars(env, vehicle_id, vehicleId);
    const char *plateNo = (*env)->GetStringUTFChars(env, plate_no, &iscopy);
    pos_param_struct.plate_no = plateNo;
    (*env)->ReleaseStringUTFChars(env, plate_no, plateNo);
    const char *driverId = (*env)->GetStringUTFChars(env, driver_id, &iscopy);
    pos_param_struct.driver_id = driverId;
    (*env)->ReleaseStringUTFChars(env, driver_id, driverId);
    const char *lineInfo = (*env)->GetStringUTFChars(env, line_info, &iscopy);
    pos_param_struct.line_info = lineInfo;
    (*env)->ReleaseStringUTFChars(env, line_info, lineInfo);
    const char *stationNo = (*env)->GetStringUTFChars(env, station_no, &iscopy);
    pos_param_struct.station_no = stationNo;
    (*env)->ReleaseStringUTFChars(env, station_no, stationNo);
    const char *lbsInfo = (*env)->GetStringUTFChars(env, lbs_info, &iscopy);
    pos_param_struct.lbs_info = lbsInfo;
    (*env)->ReleaseStringUTFChars(env, lbs_info, lbsInfo);
    const char *recordType = (*env)->GetStringUTFChars(env, record_type, &iscopy);
    pos_param_struct.record_type = recordType;
    (*env)->ReleaseStringUTFChars(env, record_type, recordType);

//
//    printf("mydebug", "===========准备数据结束================\n");
//
//    printf("mydebug", "===========校验二维码开始================\n");
    //拼装验证请求
    VERIFY_REQUEST_V3 verify_request;
    //装入二进制格式的二维码
    verify_request.qrcode = qrcode;
    //装入二进制二维码长度
    verify_request.qrcode_len = strlen(qrcode_hex) / 2;
    //装入pos_param
    verify_request.pos_param_struct = &pos_param_struct;

    //verify_request.public_key = TEST_MOT_DOUBLE_SM2_KEY;
    //verify_request.public_key = TEST_MOT_TRIPLE_SM2_KEY;
//    verify_request.public_key = TEST_ALIPAY_PUBLIC_KEY;
    for (int i = 0; i < 50; i++) {
        if (info_response.code_info->alipay_code_info.key_id == i) {
            char *ah = publicKey->pub_key;
            verify_request.public_key = ah;
            break;
        }
    }

    VERIFY_RESPONSE_V3 verify_response;
    verify_response.record = (char *) malloc(2048);
    verify_response.record_len = 2048;

    /**
     * 调用接口验证二维码的有效性
     */
    ret = verify_qrcode_v3(&verify_request, &verify_response);

    /**
     * 处理返回的结果
     */
    if (ret != SUCCESS) {
        free(verify_response.record);
        (*env)->SetIntField(env, classInfo, inforState, ret);
        return classInfo;
    }
    (*env)->SetIntField(env, classInfo, inforState, ret);
    memcpy(record, verify_response.record, verify_response.record_len);
    *recordLen = verify_response.record_len;

    /**
     * 1.商户可以根据uid判断是否为同一用户重复交易
     */

    /**
     * 2.商户可以根据qrcode判断是否为重复二维码
     *   此判断也可以放在校验二维码前执行，商户可以自行选择
     */

    /**
     * 3.商户需要根据卡类型、卡号、卡数据 综合判断该卡的合法性、以及是否受理该卡
     * 请商户保留 可受理 的脱机记录
     */
    jbyteArray data4 = (*env)->NewByteArray(env,(jsize) strlen(verify_response.record));
    (*env)->SetByteArrayRegion(env, data4, 0, (jsize)strlen(verify_response.record),
                               (jbyte *) verify_response.record);
    (*env)->SetObjectField(env, classInfo, alipayResult, data4);
    free(verify_response.record);
    return classInfo;
}

int demo_init() {
    int ret = 0;
    INIT_REQUEST init_request;
    INIT_INFO *init_info_list[2];

    char *card_types[3] = {0};
    INIT_INFO *init_info_mot = (INIT_INFO *) malloc(sizeof(INIT_INFO));

    init_info_mot->proto_type = "MOT";
    init_info_mot->card_type_number = 0;
    init_info_mot->code_issuer_no = "50023301";

    init_info_list[0] = init_info_mot;

    INIT_INFO *init_info_alipay = (INIT_INFO *) malloc(sizeof(INIT_INFO));

    char *card_type_a = "ANT00001";
    char *card_type_b = "T0460100";
    char *card_type_c = "S0JP0000";
    card_types[0] = card_type_a;
    card_types[1] = card_type_b;
    card_types[2] = card_type_c;
    init_info_alipay->proto_type = "ALIPAY";
    init_info_alipay->card_type_number = 3;
    init_info_alipay->code_issuer_no = "00000000";
    init_info_alipay->card_types = (const char **) card_types;

    init_info_list[1] = init_info_alipay;
    init_request.code_issuer_info_number = 2;
    init_request.code_issuer_infos = init_info_list;
    ret = init(&init_request);
    free(init_info_mot);
    return ret;
}

/**
* 字节数组转hex格式字符串
* @param print_buf: 十六进制字符串buffer
* @param print_buf_len: 十六进制字符串buffer长度
* @param bytes: 二进制数据
* @param bytes_len: 二进制数据长度
*/
char *bytes_to_hex_string(
        char *print_buf,
        int print_buf_len,
        const unsigned char *bytes,
        int len) {

    int i = 0;

    /**
    * 入参校验
    */
    if (print_buf == NULL || bytes == NULL || (len * 2 + 1) > print_buf_len) {
        return NULL;
    }

    for (i = 0; i < len; i++) {
        print_buf[i * 2] = g_hex_map_table[(bytes[i] >> 4) & 0x0F];
        print_buf[i * 2 + 1] = g_hex_map_table[(bytes[i]) & 0x0F];
    }
    /**
    * 填充字符串结束符
    */
    print_buf[i * 2] = '\0';
    /**
    * 返回目标地址
    */
    return print_buf;
}

/**
 * 判断这个char是否是hex格式
 * @param c
 */
static int is_hex_format(char c) {
    int ret = -1;
    if (c >= '0' && c <= '9') {
        ret = 1;
    } else if (c >= 'A' && c <= 'F') {
        ret = 1;
    } else if (c >= 'a' && c <= 'f') {
        ret = 1;
    }
    return ret;
}

/**
* hex格式字符串转字节数组
* @param hex_string: 十六进制字符串
* @param hex_string_len: 十六进制字符串长度
* @param bytes: 二进制数据存储空间
* @param bytes_len: 目标空间长度
*/
int hex_string_to_bytes(
        char *hex_string,
        int hex_string_len,
        unsigned char *bytes,
        int bytes_len) {

    int i = 0;
    /**
    * 校验十六进制字符串长度必须偶数，并且目标存储空间必须足够存放转换后的二进制数据
    */
    if ((hex_string_len % 2 != 0) || (bytes_len * 2 < hex_string_len)) {

//        printf("mydebug", "bytes_len = %d hex_string_len = %d\n", bytes_len, hex_string_len);
        return -1;
    }

    for (i = 0; i < hex_string_len; i += 2) {
        if (is_hex_format(hex_string[i]) != 1) {
            return -1;
        }
        bytes[i / 2] = ((hex_of_char(hex_string[i]) << 4) & 0xF0) |
                       (hex_of_char(hex_string[i + 1]) & 0x0F);
    }
    return 1;
    return 1;
}

static void print_info_response(INFO_RESPONSE *response) {
//    printf("mydebug", "response->proto_type = %s\n", response->proto_type);
//    if (strcmp(response->proto_type, MOT_PROTO_TYPE) == 0) {
//        printf("mydebug", "二维码格式为交通部协议\n");
//
//        printf("mydebug", "code_issuer_no = %s\n",
//               response->code_info->mot_code_info.code_issuer_no);
//        printf("mydebug", "card_issuer_no = %s\n",
//               response->code_info->mot_code_info.card_issuer_no);
//        printf("mydebug", "user_id = %s\n", response->code_info->mot_code_info.user_id);
//        printf("mydebug", "card_no = %s\n", response->code_info->mot_code_info.card_no);
//
//    } else if (strcmp(response->proto_type, ALIPAY_PROTO_TYPE) == 0) {
//        printf("mydebug", "二维码格式为支付宝协议\n");
//
//        printf("mydebug", "key id = %d\n", response->code_info->alipay_code_info.key_id);
//        printf("mydebug", "alg id = %d\n", response->code_info->alipay_code_info.alg_id);
//        printf("mydebug", "card type = %s\n", response->code_info->alipay_code_info.card_type);
//        printf("mydebug", "user_id = %s\n", response->code_info->alipay_code_info.user_id);
//        printf("mydebug", "card_no = %s\n", response->code_info->alipay_code_info.card_no);
//
//    }
}

/**
* hex格式char转二进制
*/
unsigned char hex_of_char(char c) {
    unsigned char tmp = 0;
    if (c >= '0' && c <= '9') {
        tmp = (c - '0');
    } else if (c >= 'A' && c <= 'F') {
        tmp = (c - 'A' + 10);
    } else if (c >= 'a' && c <= 'f') {
        tmp = (c - 'a' + 10);
    }
    return tmp;
}

void hexToString(char *hex, char *str, int len) {
    char *concertbuf = "0123456789abcdef";
    int i;
    for (i = 0; i < len; i++) {
        str[2 * i] = concertbuf[(hex[i] >> 4) & 0x0f];
        str[2 * i + 1] = concertbuf[hex[i] & 0x0f];
    }

}
