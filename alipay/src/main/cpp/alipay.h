
#ifndef DEMO__h
#define DEMO__h

#include <jni.h>

#ifdef __cplusplus

#endif

//#define SUCCESS                  1
//#define MALFORMED_QRCODE        -1
//#define QRCODE_INFO_EXPIRED     -2
//#define QRCODE_KEY_EXPIRED      -3
//#define POS_PARAM_ERROR         -4
//#define QUOTA_EXCEEDED          -5
//#define NO_ENOUGH_MEMORY        -6
//#define SYSTEM_ERROR            -7
//#define CARDTYPE_UNSUPPORTED    -8
//#define NOT_INITIALIZED         -9
//#define ILLEGAL_PARAM           -10
//#define PROTO_UNSUPPORTED       -11
//#define QRCODE_DUPLICATED		-12

typedef struct sucess_qrcode {
    char uid[100];
    long scan_time;
} SUCCESS_QRCODE;

#define TEST_ALIPAY_PUBLIC_KEY  "02AB2FDB0AB23506C48012642E1A572FC46B5D8B8EE3B92A602CC1109921F84B0E"
#define TEST_MOT_TRIPLE_SM2_KEY "026A3047C499D2CFF3C943A459C41E739C7673532B146AB026703D661A08AF11C4"
#define TEST_MOT_DOUBLE_SM2_KEY "024B32DCE5E387EA6B66063E0CD323F7B1DA9FCFCEB67F89610EBDAF2DA43FFF47"

#define QRCODE_HEX_DATA "02010054323038383330323233343033373935345BE77C8B0A0D0A0D00000000000000000000000003BEDDEA3AAA38BABEA523A6727F066248FCCBEE59CA00165A53304A5030303030083030303030303031051234567890473045022100B50CA620DF2FBA5BA2373FF23752D8AD9A0681E785A77D4FE9A490118B2EC2CA02202673E9693C5773D9E003A7F8717EEF80A861D2DBFF53D6FD2AE8F5CED08D7B2E045A5EE0BB373035021900CDF4748F76846E7CF8249FC21B4BDF60F5152B0F9735339302180B10DA8CC1AE9F5184FC5B891F5063D11EF6EC89B6943DFF"

#define AMOUNT_CENT 200

static char g_hex_map_table[] = "0123456789ABCDEF";


jobject
check_qrcode_demo(char *qrcode_hex, char *record, int *recordLen, JNIEnv *env, jobject classInfo,
                  jstring record_id,
                  jstring pos_id, jstring pos_mf_id, jstring pos_sw_version,
                  jstring merchant_type, jstring currency, jint amount,
                  jstring vehicle_id, jstring plate_no, jstring driver_id,
                  jstring line_info, jstring station_no, jstring lbs_info,
                  jstring record_type);

void hexToString(char *hex, char *str, int len);

#ifdef __cplusplus

#endif

#endif
