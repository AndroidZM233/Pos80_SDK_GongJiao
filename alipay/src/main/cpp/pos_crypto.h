#ifndef __POS_CRYPTO_H__
#define __POS_CRYPTO_H__

#ifdef __cplusplus
extern "C" {
#endif

#ifndef _OFFLINE_MODEL_H_

#define SUCCESS							(  1)
#define MALFORMED_QRCODE				( -1)
#define QRCODE_INFO_EXPIRED 			( -2)
#define QRCODE_KEY_EXPIRED  			( -3)
#define POS_PARAM_ERROR					( -4)
#define QUOTA_EXCEEDED					( -5)
#define NO_ENOUGH_MEMORY   				( -6)
#define SYSTEM_ERROR 					( -7)
#define CARDTYPE_UNSUPPORTED 			( -8)
#define NOT_INITIALIZED					( -9)
#define ILLEGAL_PARAM					(-10)
#define PROTO_UNSUPPORTED 				(-11)
#define QRCODE_DUPLICATED				(-12)
#define INSTITUTION_NOT_SUPPORT 		(-13)
#define INIT_DUPLICATED					(-14)
	
#define ANT_BUS_PAY ("ANT00001")

#define ALIPAY_PROTO_TYPE ("ALIPAY")
#define MOT_PROTO_TYPE ("MOT")

#endif
/**
*  验证二维码的请求
*  参数：qrcode 格式的二维码信息
*  参数：qrcode_len 二维码长度
*  参数：master_pub_key hex格式的支付宝公钥
		例："028C452939E766BB17DE9EAE0390F7896545D40C565859C9FBFB1B424974915D86"
*  参数：pos_param json形式的pos信息 长度不能大于2048
		ex：{
				"pos_id":"sh001",
				"type":"SINGLE",
				"subject":"杭州公交190路",
				"record_id":"123456"
			}
*  参数：amount_cent 交易金额 如果无交易金额（如两次过闸机才算交易的情况）就传入0
*/
typedef struct _verify_request{
    const unsigned char* qrcode;
    int qrcode_len;
    const char* master_pub_key;
    const char* pos_param;
	int amount_cent;
}VERIFY_REQUEST;

/**
 * 验证二维码的响应信息
 * @param: uid 存放输出uid的buffer
 * @param: uid_len 输出的uid的buffer长度 不能小于17
 * @param: record 存放输出record的buffer 返还的record是标准字符串信息
 * @param: record_len 存record的buffer的长度 不能小于2048
 */
typedef struct _verify_response{
	char* uid;
	int uid_len;
	char* record;
	int record_len;
}VERIFY_RESPONSE;

/**
*  验证二维码的请求 版本二
*  参数：qrcode 格式的二维码信息
*  参数：qrcode_len 二维码长度
*  参数：pos_param json形式的pos信息 长度不能大于1024
		ex：{
				"pos_id":"sh001",
				"type":"SINGLE",
				"subject":"杭州公交190路",
				"record_id":"123456"
			}
*  参数：amount_cent 交易金额 如果无交易金额（如两次过闸机才算交易的情况）就传入0
*/
typedef struct _verify_request_v2{
    const unsigned char* qrcode;
    int qrcode_len;
    const char* pos_param;
	int amount_cent;
}VERIFY_REQUEST_V2;

/**
 * 验证二维码的响应信息 版本二
 * @param: uid 存放输出uid的buffer
 * @param: uid_len 输出的uid的buffer长度 不能小于17
 * @param: record 存放输出record的buffer 返还的record是标准字符串信息
 * @param: record_len 存record的buffer的长度 不能小于2048
 * @param: card_no 存放输出卡号的buffer
 * @param: card_no_len 存card_no的buffer长度 不能小于17
 * @param: card_data 存放输出卡数据的buffer
 * @param: card_data_len 存card_data的buffer长度 不能小于65
 * @param: card_type 存放输出卡类型的buffer
 * @param: card_type_len 存card_type的buffer长度 不能小于9
 */
typedef struct _verify_response_v2{
	char* uid;
	int uid_len;
	char* record;
	int record_len;
	char* card_no;
	int card_no_len;
	unsigned char* card_data;
	int card_data_len;
	char* card_type;
	int card_type_len;
}VERIFY_RESPONSE_V2;

/**
 * 二维码验证V3中使用的机具参数模型
 --------------必传-----------------
 * @param:record_id 脱机记录流水号
 * @param:consumption_type 消费类型 0 - 单次消费 1 - 复合消费
 * @param:pos_id 终端编号
 * @param:pos_mf_id 终端厂商编号
 * @param:pos_sw_version 终端软件版本号
 * @param:merchant_type 商户MCC类型号
 ---------------可选----------------
 * @param:amount 金额（消费类型为0时必传）
 * @param:currency 币种
 * @param:vehicle_id 车辆编号
 * @param:plate_no 车牌号
 * @param:driver_id 司机编号
 * @param:line_info 线路信息
 * @param:station_no 站点编号
 * @param: lbs_info 定位信息
 */
typedef struct _pos_param_struct{
	const char* record_id;
	int consumption_type;
	const char* pos_id;
	const char* pos_mf_id;
	const char* pos_sw_version;
	const char* merchant_type;
	const char* currency;
	int amount;
	const char* vehicle_id;
	const char* plate_no;
	const char* driver_id;
	const char* line_info;
	const char* station_no;
	const char* lbs_info;
	const char* record_type;
}POS_PARAM_STRUCT;

/**
*  验证二维码的请求 版本三
*  参数：qrcode 格式的二维码信息
*  参数：qrcode_len 二维码长度
*  参数：pos_param pos信息结构体
*/
typedef struct _verify_request_v3{
    const unsigned char* qrcode;
    int qrcode_len;
    POS_PARAM_STRUCT* pos_param_struct;
	const char* public_key;

}VERIFY_REQUEST_V3;

/**
 * 验证二维码的响应信息 版本三
 * @param: record 存放输出record的buffer 返还的record是标准字符串信息
 * @param: record_len 存record的buffer的长度
 */
typedef struct _verify_response_v3{
	char* record;
	int record_len;
}VERIFY_RESPONSE_V3;

/**
 * 验证二维码V3 支持交通部协议
 * @param: request_v3 验证请求
 * @param: respone_v3 验证响应
 * @return: 见pos_crypto.h定义的错误码
 *
 * */	
int verify_qrcode_v3(VERIFY_REQUEST_V3* request_v3, 
					VERIFY_RESPONSE_V3* response_v3);


/**
 * 初始化请求
 * @param: proto_type
 *		"ALIPAY" - 支付宝协议  "MOT" - 交通部协议
 * @param: card_types
 * @param: card_type_number
 * @param: code_issuer_no
 * 		"50023301" - 交通部协议中支付宝发码机构
 *      "00000000" - 支付宝协议中支付宝发码机构
 */
typedef struct _init_info{
	const char* proto_type;
	const char** card_types;
	int card_type_number;
	const char* code_issuer_no;
}INIT_INFO;

/**
 * 初始化机构请求结构体
 * @param: code_issuer_infos 机构初始化信息列表
 * @param: code_issuer_info_number 初始化机构数 
 */
typedef struct _init_request{
	INIT_INFO** code_issuer_infos;
	int code_issuer_info_number;
}INIT_REQUEST;	

/**
 * 初始化机构
 * @param: request 初始化机构请求结构体
 */
int init(INIT_REQUEST* request);


/**
 * 反初始化
 * 调用以回收资源
 */
int uninit();

/**
 * 交通部码信息
 * @param:sign_mode 签名模式 
		两级SM2签名 0 
		三级SM2签名 1
 * @param:cert_sn 证书号
 * @param:cert_sn_len 证书号长度
 * @param:ca_key_idx 根证书索引号
 * @param:card_issuer_no 发卡机构号
 * @param:card_issuer_no_len 发卡机构号长度
 * @param:code_issuer_no 发码机构号
 * @param:code_issuer_no_len 发码机构号长度
 * @param:user_id 用户ID
 * @param:user_id_len 用户ID长度
 * @param:card_biz_type 卡业务类型
 * @param:card_no 卡号
 * @param:card_no_len 卡号长度
 * @param:card_data 卡数据
 * @param:card_data_len 卡数据长度
 */
typedef struct _mot_code_info{
	int sign_mode;
	char* cert_sn;
	int cert_sn_len;
	int ca_key_idx;
	char* card_issuer_no;
	int card_issuer_no_len;
	char* code_issuer_no;
	int code_issuer_no_len;
	char* user_id;
	int user_id_len;
	int card_biz_type;
	char* card_no;
	int card_no_len;
	unsigned char* card_data;
	int card_data_len;
}MOT_CODE_INFO;

/**
 * @param:alg_id 算法ID
 * @param:key_id 密钥ID
 * @param:code_issuer_no 发码机构号
 * @param:code_issuer_no_len 发码机构号长度
 * @param:user_id 用户ID
 * @param:user_id_len 用户ID长度
 * @param:card_type 卡类型
 * @param:card_type_len 卡类型长度
 * @param:card_no 卡号
 * @param:card_no_len 卡号长度
 * @param:card_data 卡数据
 * @param:card_data_len 卡数据长度
 */
typedef struct _alipay_code_info{
	int alg_id;
	int key_id;
	char* code_issuer_no;
	int code_issuer_no_len;
	char* user_id;
	int user_id_len;
	char* card_type;
	int card_type_len;
	char* card_no;
	int card_no_len;
	unsigned char* card_data;
	int card_data_len;
}ALIPAY_CODE_INFO;
	
/**
*  获取二维码信息的请求
*  @param: qrcode 格式的二维码信息
*  @param: qrcode_len 二维码长度
*/
typedef struct _info_request{
    const unsigned char* qrcode;
    int qrcode_len;
}INFO_REQUEST;

/**
 * 码信息 - 包含交通部协议与支付宝协议返回的类型
 *
 */
typedef struct _code_info{
	MOT_CODE_INFO mot_code_info;
	ALIPAY_CODE_INFO alipay_code_info;
}CODE_INFO;
/**
 * 获取二维码信息 响应
 * @param: proto_type 协议类型
 * @param: code_info 码信息 支付宝码内容或交通部卡内容
 */
typedef struct _info_response{
	char* proto_type;
	CODE_INFO* code_info;
}INFO_RESPONSE;

/**
 * 获取二维码信息
 * @param: request response
 * @return: 成功返回 1 失败见错误码
 */
int get_qrcode_info(INFO_REQUEST* request, 
					INFO_RESPONSE* response);

/**
 * 获取当前so库版本号
 * @param: version 用来存放版本号的buffer
 * @param: version_len 用来存放版本号的buffer长度，不能小于16
 * @return: 是否成功
 */
int get_libposoffline_version(char* version, int version_len);


/**
 * 初始化验证二维码
 * 录入支付宝公钥以及支持的卡类型
 * @param: key_list 从支付宝开放网关拉取的秘钥信息，以json形式组织成字符串传入
 *   ex:
 		[
			{"key_id":0,"public_key":"02170D3C441AF17AE1010A4095B974BF1FE1EA48FCD65BE060A5AD577ABB885088"},
		    {"key_id":1,"public_key":"03410D92CDAB5BC9349731136619A93FC3225DE6B235E839F6BEB41A77B79A0424"},
		    ...
	    ]
 * @param: card_type_list 可支持的卡类型列表
 * @param:
 * 	ex:
		[
			"HZ000001",
			"HZ000002",
			"WH000001"
		]
 */
int init_pos_verify(const char* key_list, const char* card_type_list);


/**
 * 根据机构号执行初始化
 * 以机构号为维度，录入支付宝公钥以及支持的卡类型
 * @param: key_list_json 从支付宝开放网关拉取的秘钥信息，以json形式组织成字符串传入
 *   ex:
 		[
			{"key_id":0,"public_key":"02170D3C441AF17AE1010A4095B974BF1FE1EA48FCD65BE060A5AD577ABB885088"},
		    {"key_id":1,"public_key":"03410D92CDAB5BC9349731136619A93FC3225DE6B235E839F6BEB41A77B79A0424"},
		    ...
	    ]
 * @param: card_type_list 可支持的卡类型列表
 * @param:
 * 	ex:
		[
			"HZ000001",
			"HZ000002",
			"WH000001"
		]
 * @param: institution_no 机构号
 *  ex: 支付宝为{0x00,0x00,0x00,0x00}
 */							  
int init_pos_verify_with_institution_no(const char* key_list, const char* card_type_list, const char* institution_no);

/**
 * 清理释放占用内存
 * 
 * 程序退出前，请调用该方法，否则可能会导致内存泄漏
 */
void free_pos_verify();

/**
 * 验证二维码信息 版本二
 * @param：request 验证请求
 * @return: 成功返回 1 失败见错误码
 *
 * */
int verify_qrcode_v2(VERIFY_REQUEST_V2* request_v2, 
					VERIFY_RESPONSE_V2* response_v2);

/**
 * 【已废弃】
 * 获取二维码中指定的密钥id
 * @param: qrcode 二进制的二维码信息
 * @return: 成功返回大于等于0的密钥ID 失败见错误码
 *
 * */
int get_key_id(	const unsigned char* qrcode);

/**
 * 【已废弃】
 * 验证二维码信息
 * @param：request 验证请求
 * @return: 成功返回 1 失败见错误码
 *
 * */
int verify_qrcode(VERIFY_REQUEST* request,
				VERIFY_RESPONSE* response);

#ifdef __cplusplus
}
#endif
#endif
