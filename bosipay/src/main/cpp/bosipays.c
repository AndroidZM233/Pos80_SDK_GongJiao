#include <jni.h>
#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include <string.h>
#include "QRCodeModule.h"
#include  <unistd.h>
#include  <sys/types.h>
#include  <sys/stat.h>
#include  <fcntl.h>
#include  <termios.h>


JNIEXPORT jint JNICALL
Java_com_spd_bosipay_BosiPayJni_initdev(JNIEnv *env, jobject obj) {
    return 2;
}

JNIEXPORT jint JNICALL Java_com_spd_bosipay_BosiPayJni_relese(JNIEnv *env, jobject obj) {
    return 78;
}

JNIEXPORT jobject JNICALL
Java_com_spd_bosipay_BosiPayJni_checkAliQrCodeJni(JNIEnv *env, jobject obj, jobject classInfo,
                                                  jstring Qrcode,
                                                  jstring record_id,
                                                  jstring pos_id, jstring pos_mf_id,
                                                  jstring pos_sw_version,
                                                  jstring merchant_type, jstring currency,
                                                  jint amount,
                                                  jstring vehicle_id, jstring plate_no,
                                                  jstring driver_id,
                                                  jstring line_info, jstring station_no,
                                                  jstring lbs_info,
                                                  jstring record_type) {

}



