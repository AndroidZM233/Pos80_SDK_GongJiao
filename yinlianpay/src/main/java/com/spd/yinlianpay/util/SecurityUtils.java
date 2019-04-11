package com.spd.yinlianpay.util;


import android.os.Environment;
import android.util.Log;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERInputStream;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.PKCS7SignedData;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.security.auth.x500.X500Principal;

import ui.wangpos.com.utiltool.DESUitl;
import ui.wangpos.com.utiltool.HEXUitl;


/**
 * @Description: $(TODO)
 * @Author: Created by Frank@verifone.com.
 * @Date: $(Date) $(TIME).
 * @Version: 1.0.
 * @Copyright: $(YEAR) VeriFone Inc. All rigths reserved.
 */

public class SecurityUtils {

    private static final String DEFAULT_PLAIN_KEYWORD = "verifone.ccb.keystore";
    private static final String ALIAS_TERMINAL_CER = "termianl_cer";
    private static final String TAG = "keyStore";
    public static KeyStore keyStore;
    private static String KEYSTORE_FILENAME = "ccb_keystore";

    //初始化证书
    public static void init() {
        try {
            keyStore = createOrOpenKeyStore(Environment.getExternalStorageDirectory() + "/" + KEYSTORE_FILENAME, DEFAULT_PLAIN_KEYWORD);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void delCert() {
        try {

            String path = Environment.getExternalStorageDirectory() + "/" + KEYSTORE_FILENAME;
            File keyStoreFile = new File(path);
            if (keyStoreFile.exists()) {
                keyStoreFile.delete();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static KeyStore createOrOpenKeyStore(String path, String password) throws Exception {
        Log.d("KeyStore", path);
        File keyStoreFile = new File(path);
        KeyStore store = KeyStore.getInstance("BKS");
        if (keyStoreFile.exists()) {
            //chCertFileMod(path);
            FileInputStream stream = new FileInputStream(path);
            store.load(stream, password.toCharArray());
            stream.close();
        } else {
            store.load(null, null);
        }
        return store;
    }

    public static boolean storeP7b(byte[] p7bData, KeyPair keypair) {

        Log.v(TAG, "=storeP12Certificate==>Private Key:"
                + HEXUitl.bytesToHex(keypair.getPrivate().getEncoded()));

        String mPath = Environment.getExternalStorageDirectory() + "/" + KEYSTORE_FILENAME;
        System.out.println("111storeP12Certificate***mPath==>" + mPath);
        File p12File = new File(mPath);
        if (p12File.exists()) {
            p12File.delete();
        } else {
            Log.v(TAG, "222storeP12Certificate***p12 file create failed**");
        }
        try {
            FileOutputStream mOut = new FileOutputStream(p12File);
//            chmodFile(p12File);
            for (Certificate c : CertificateFactory.getInstance("X.509").
                    generateCertificates(new ByteArrayInputStream(p7bData))) {
                if (c.getPublicKey().equals(keypair.getPublic())) {
                    keyStore = KeyStore.getInstance("BKS");
                    keyStore.load(null, null);
                    keyStore.setCertificateEntry(KEYSTORE_FILENAME, c);
                    Certificate[] chain = new Certificate[]{c};//new java.security.cert.Certificate[] {cert};
                    keyStore.setKeyEntry(KEYSTORE_FILENAME, keypair.getPrivate(), DEFAULT_PLAIN_KEYWORD.toCharArray(), chain);
                    keyStore.store(mOut, DEFAULT_PLAIN_KEYWORD.toCharArray());
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

       /* try {
            for (Certificate c : CertificateFactory.getInstance("X.509").
                    generateCertificates(new ByteArrayInputStream(p7bData))) {

                *//*keyStore.setCertificateEntry(ALIAS_TERMINAL_CER,c);
                java.security.cert.Certificate[] chain = new java.security.cert.Certificate[] {c};
                keyStore.setKeyEntry(alias, privatekey, DEFAULT_PLAIN_KEYWORD.toCharArray(),chain);
                keyStore.store(mOut, DEFAULT_PLAIN_KEYWORD.toCharArray());*//*
                return storeP12Certificate(c,privatekey,KEYSTORE_FILENAME);

            }
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e2) {
            e2.printStackTrace();
        }
        return false;*/
    }

    private static void chmodFile(File file) {
        Process p;
        int status;
        try {
            p = Runtime.getRuntime().exec("chmod 644 " + file);
            status = p.waitFor();
            if (status == 0) {
                //chmod succeed
                Log.v(TAG, "-=-=-=chmod succeed ");
            } else {
                //chmod failed
                Log.v(TAG, "-=-=-=chmod failed ");
            }
        } catch (Exception e) {
            Log.v(TAG, "=====chmodFile----Exception");
            e.printStackTrace();

        }
    }


    public boolean storeP12Certificate(X509Certificate cert, String alias, PrivateKey privatekey) {
        Log.v(TAG, "=storeP12Certificate==>Private Key:"
                + HEXUitl.bytesToHex(privatekey.getEncoded()));

        String mPath = Environment.getExternalStorageDirectory() + "/" + alias;
        System.out.println("111storeP12Certificate***mPath==>" + mPath);
        File p12File = new File(mPath);
        if (p12File.exists()) {
            p12File.delete();
        } else {
            Log.v(TAG, "222storeP12Certificate***p12 file create failed**");
        }
        try {
            FileOutputStream mOut = new FileOutputStream(p12File);
            chmodFile(p12File);
            keyStore.setCertificateEntry(alias, cert);
            Certificate[] chain = new Certificate[]{cert};
            keyStore.setKeyEntry(alias, privatekey, DEFAULT_PLAIN_KEYWORD.toCharArray(), chain);
            keyStore.store(mOut, DEFAULT_PLAIN_KEYWORD.toCharArray());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean sync() {

        BufferedOutputStream bufferedOutput = null;
        try {
            bufferedOutput = new BufferedOutputStream(new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + KEYSTORE_FILENAME));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        try {
            keyStore.store(bufferedOutput, DEFAULT_PLAIN_KEYWORD.toCharArray());
            chCertFileMod(Environment.getExternalStorageDirectory() + "/" + KEYSTORE_FILENAME);
            return true;
        } catch (KeyStoreException e1) {
            e1.printStackTrace();
            return false;
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
            return false;
        } catch (CertificateException e1) {
            e1.printStackTrace();
            return false;
        }
    }

    private static boolean chCertFileMod(String filepath) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("chmod 600 " + filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int status = 0;
        try {
            status = p.waitFor();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
        Log.i("KeyUtils", "status: " + status + " path:" + filepath);
        if (status == 0) {
            Log.v("KeyUtils", "更改证书权限成功");
            return true;
        }
        Log.v("KeyUtils", "更改证书权限失败");
        return false;
    }

    public static KeyPair generateRSAKeyPair(int keyLength) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(keyLength);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static KeyPair generateRSAKeyPair() {
        return generateRSAKeyPair(1024);
    }

    public static byte[] des(byte[] data, byte[] key) {
        try {
            return DESUitl.autoEncrypt(key, data);
        } catch (Exception ex) {
            return null;
        }

    }

    public static byte[] undes(byte[] data, byte[] key) {
        try {
            return DESUitl.autoDecrypt(key, data);
        } catch (Exception ex) {
            return null;
        }
    }

    public static AuthorityKeyIdentifier createAuthorityKeyIdentifier(PublicKey rootPublicKey) {
        try {
            return new AuthorityKeyIdentifier(new SubjectPublicKeyInfo((ASN1Sequence) new DERInputStream(new ByteArrayInputStream(rootPublicKey.getEncoded())).readObject()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static SubjectKeyIdentifier createSubjectKeyIdentifier(PublicKey clientPublicKey) {
        try {
            return new SubjectKeyIdentifier(new SubjectPublicKeyInfo((ASN1Sequence) new DERInputStream(new ByteArrayInputStream(clientPublicKey.getEncoded())).readObject()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static byte[] generateP10ReqestData(X509Principal principal, KeyPair keyPair) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
        return new PKCS10CertificationRequest("SHA1WITHRSA", principal, keyPair.getPublic(), new BERSet(), keyPair.getPrivate()).getEncoded();
    }

    public static byte[] generateP10ReqestData(String dn, KeyPair keyPair) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
        return new PKCS10CertificationRequest("SHA1WITHRSA", new X500Principal(dn), keyPair.getPublic(), null, keyPair.getPrivate()).getEncoded();
    }

    public static X509Certificate generateSelfSignedCert(KeyPair rootKeyPair, String cn, int years) throws Exception {
        Hashtable attrs = new Hashtable();
        Vector order = new Vector();
        attrs.put(X509Principal.C, "CN");
        attrs.put(X509Principal.O, "CCB");
        attrs.put(X509Principal.OU, "NET_BANK");
        attrs.put(X509Principal.CN, cn);
        order.addElement(X509Principal.CN);
        order.addElement(X509Principal.OU);
        order.addElement(X509Principal.O);
        order.addElement(X509Principal.C);
        X509Name principal = new X509Principal(order, attrs);
        X509V3CertificateGenerator v3CertGenerator = new X509V3CertificateGenerator();
        v3CertGenerator.reset();
        Date beginDate = new Date();
        v3CertGenerator.setSerialNumber(BigInteger.valueOf(1));
        v3CertGenerator.setIssuerDN(principal);
        v3CertGenerator.setNotBefore(beginDate);
        v3CertGenerator.setNotAfter(new Date(beginDate.getTime() + (((long) ((((years * 365) * 24) * 60) * 60)) * 1000)));
        v3CertGenerator.setSubjectDN(new X509Principal(order, attrs));
        v3CertGenerator.setPublicKey(rootKeyPair.getPublic());
        v3CertGenerator.setSignatureAlgorithm("SHA1withRSA");
        v3CertGenerator.addExtension(X509Extensions.SubjectKeyIdentifier, false, createSubjectKeyIdentifier(rootKeyPair.getPublic()));
        v3CertGenerator.addExtension(X509Extensions.AuthorityKeyIdentifier, false, createAuthorityKeyIdentifier(rootKeyPair.getPublic()));
        v3CertGenerator.addExtension(X509Extensions.BasicConstraints, false, new BasicConstraints(false));
        X509Certificate cert = v3CertGenerator.generate(rootKeyPair.getPrivate(), "BC");
        cert.checkValidity(new Date());
        cert.verify(rootKeyPair.getPublic());
        return cert;
    }

    public static PublicKey loadPublicKey(KeyStore store, String alias, String password) throws Exception {
        return (PublicKey) store.getKey(alias, password.toCharArray());
    }

    public static PrivateKey loadPrivateKey(KeyStore store, String alias, String password) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        return (PrivateKey) store.getKey(alias, password.toCharArray());
    }

    public static byte[] calcPKCS7(byte[] data, PrivateKey privateKey) throws KeyStoreException {
        X509Certificate cert = (X509Certificate) keyStore.getCertificate(KEYSTORE_FILENAME);
        if (cert != null) {
            return calcPKCS7(data, cert, privateKey);
        }
        throw new KeyStoreException("证书不存在");
    }

    private static byte[] calcPKCS7(byte[] data, X509Certificate cert, PrivateKey privateKey) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PKCS7SignedData sd = null;
        try {
            sd = new PKCS7SignedData(privateKey, new Certificate[]{cert}, "SHA1", "BC");
            sd.update(data, 0, data.length);
            baos.write(sd.getEncoded());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}
