package co.edu.unbosque.mundialhubbackend.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;

import static org.apache.commons.codec.binary.Base64.encodeBase64;
import static org.apache.commons.codec.binary.Base64.decodeBase64;

/**
 * Clase utilitaria para cifrado y descifrado AES con modo GCM y funciones hash
 * MD5, SHA1, SHA256, SHA384, SHA512.
 * 
 * Usa llave y vector de inicialización (IV) fijos por defecto.
 * 
 * @since 1.0
 */
public class AESUtil {
	private final static String ALGORITMO = "AES";

	private final static String TIPOCIFRADO = "AES/GCM/NoPadding";

	/**
	 * Cifra un texto usando AES-GCM con la llave y vector IV especificados.
	 * 
	 * @param llave Llave secreta
	 * @param iv    Vector de inicialización
	 * @param texto Texto plano a cifrar
	 * @return Texto cifrado codificado en Base64
	 */
	@SuppressWarnings("null")
	public static String encrypt(String llave, String iv, String texto) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(TIPOCIFRADO);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}

		SecretKeySpec secretKeySpec = new SecretKeySpec(llave.getBytes(), ALGORITMO);
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv.getBytes());
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}

		byte[] encrypted = null;
		try {
			encrypted = cipher.doFinal(texto.getBytes());
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}

		return new String(encodeBase64(encrypted));
	}

	/**
	 * Descifra un texto cifrado en Base64 usando AES-GCM con la llave y vector IV.
	 * 
	 * @param llave     Llave secreta
	 * @param iv        Vector de inicialización
	 * @param encrypted Texto cifrado en Base64
	 * @return Texto descifrado plano
	 */
	@SuppressWarnings("null")
	public static String decrypt(String llave, String iv, String encrypted) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(TIPOCIFRADO);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}

		SecretKeySpec secretKeySpec = new SecretKeySpec(llave.getBytes(), ALGORITMO);
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv.getBytes());
		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}

		byte[] enc = decodeBase64(encrypted);
		byte[] decrypted = null;
		try {
			decrypted = cipher.doFinal(enc);
			return new String(decrypted);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Descifra un texto cifrado usando llave y IV por defecto.
	 * 
	 * @param encrypted Texto cifrado en Base64
	 * @return Texto descifrado plano
	 */
	public static String decrypt(String encrypted) {
		String iv = "advancewarsterra";
		String key = "lallavemaestra77";
		return decrypt(key, iv, encrypted);
	}

	/**
	 * Cifra un texto plano usando llave y IV por defecto.
	 * 
	 * @param plainText Texto plano
	 * @return Texto cifrado en Base64
	 */
	public static String encrypt(String plainText) {
		String iv = "advancewarsterra";
		String key = "lallavemaestra77";
		return encrypt(key, iv, plainText);
	}

	/**
	 * Genera hash MD5 del contenido dado.
	 * 
	 * @param content Texto a hashear
	 * @return Hash MD5 en hexadecimal
	 */
	public static String hashingToMD5(String content) {
		return DigestUtils.md5Hex(content);
	}

	/**
	 * Genera hash SHA-1 del contenido dado.
	 * 
	 * @param content Texto a hashear
	 * @return Hash SHA-1 en hexadecimal
	 */
	public static String hashingToSHA1(String content) {
		return DigestUtils.sha1Hex(content);
	}

	/**
	 * Genera hash SHA-256 del contenido dado.
	 * 
	 * @param content Texto a hashear
	 * @return Hash SHA-256 en hexadecimal
	 */
	public static String hashingToSHA256(String content) {
		return DigestUtils.sha256Hex(content);
	}

	/**
	 * Genera hash SHA-384 del contenido dado.
	 * 
	 * @param content Texto a hashear
	 * @return Hash SHA-384 en hexadecimal
	 */
	public static String hashingToSHA384(String content) {
		return DigestUtils.sha384Hex(content);
	}

	/**
	 * Genera hash SHA-512 del contenido dado.
	 * 
	 * @param content Texto a hashear
	 * @return Hash SHA-512 en hexadecimal
	 */
	public static String hashingToSHA512(String content) {
		return DigestUtils.sha512Hex(content);
	}

}
