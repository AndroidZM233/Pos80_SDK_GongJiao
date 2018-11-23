#ifndef _QRCODENODULE_H
#define _QRCODENODULE_H

#ifdef __cplusplus
extern "C" {
#endif

#ifndef IN
#define IN
#define OUT
#endif

#define	RESULT_ERROR				0xFFFFFFFF //失败
#define	RESULT_OK					0x00000000 //成功
#define	RESULT_OK_CERT_NEED_UPD		0x00000001 //成功，证书待更新
#define RESULT_ERR_QRCODE_DATA_ERR	0x80000001 //二维码数据错误
#define RESULT_ERR_QRCODE_AUTH_ERR	0x80000002 //二维码签名错误
#define RESULT_ERR_CERT_NEED_UPD	0x80000003 //证书需立即更新
#define RESULT_ERR_MAC_ALG_ERR		0x80000004 //MAC算法未知

typedef unsigned char   BYTE;

//////////////////////////////////////////////////////////////////////////
/**
@fun		VerifyCode
@brief      验证解密二维码数据

@param      (i)BYTE	*	uQRCode			：二维码QR Code解码值
@param      (o)BYTE	*	uIssuerData		：发码方数据（格式参见接口文档）
@param      (o)BYTE	*	uTransactionData：乘车用户数据（格式参见接口文档）

@retval     int
			RESULT_OK						 成功
			RESULT_OK_CERT_NEED_UPD			 成功，证书待更新
			RESULT_ERR_QRCODE_DATA_ERR		 二维码数据错误
			RESULT_ERR_QRCODE_AUTH_ERR		 二维码签名错误

@exception  无
*/
//////////////////////////////////////////////////////////////////////////
int VerifyCode(IN BYTE * uQRCode, OUT BYTE * uIssuerData, OUT BYTE * uTransactionData);

//////////////////////////////////////////////////////////////////////////
/**
@fun		UpdateCert
@brief      证书更新

@param      (i)BYTE	*	uCerts			：最新证书内容

@retval     int
			RESULT_OK					更新成功
			其他值						更新失败
@exception  无
*/
//////////////////////////////////////////////////////////////////////////
int UpdateCert(IN BYTE* uCerts);

//////////////////////////////////////////////////////////////////////////
/**
@fun		QueryCertVer
@brief      查询证书版本信息

@param      (i)BYTE	*	uCertsInfo		：证书版本信息

@retval     int
			RESULT_OK					查询成功
			其他值						查询失败
@exception  无
*/
//////////////////////////////////////////////////////////////////////////
int QueryCertVer(OUT BYTE* uCertsVerInfo);

//////////////////////////////////////////////////////////////////////////
/**
@fun		VerifyMac
@brief      验证二维码中MAC值

@param      (i)char	*	cQRCode			：二维码QR Code解码值
@param      (o)BYTE	*	uCardID			：用户虚拟卡ID
@param      (o)int	*	cardIdLen		：用户虚拟卡ID长度

@retval     int
			RESULT_OK						 成功
			RESULT_ERR_QRCODE_DATA_ERR		 二维码数据错误
			RESULT_ERR_QRCODE_AUTH_ERR		 二维码签名错误

@exception  无
*/
//////////////////////////////////////////////////////////////////////////
int VerifyMac(IN char * cQRCode, OUT BYTE * uCardID, OUT int *iCardIdLen);

//////////////////////////////////////////////////////////////////////////
/**
@fun		SetCertFilePath
@brief      设置证书存储路径
			设置证书路径格式要以"/"结尾，例如 "/home/test/"

@param      (i)char	*	cFilePath		：要存储证书路径地址

@retval     无

@exception
			RESULT_OK					设置成功
			其他值						设置失败
*/
//////////////////////////////////////////////////////////////////////////
int SetCertFilePath(char * cFilePath);

//////////////////////////////////////////////////////////////////////////
/**
@fun		GetSoVer
@brief      获取So库版本号

@param      (i)int	*	iVerNo		：So库版本号

@retval     无

@exception  无
*/
//////////////////////////////////////////////////////////////////////////
void GetSoVer(int* iVerNo);

#ifdef __cplusplus
}
#endif

#endif
