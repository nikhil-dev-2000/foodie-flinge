package com.example.foodiefling.ui

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec
import java.util.Base64

object HandelPassword {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val SECRET_KEY = "1234567890123456" // 16-byte key for AES-128
    private const val IV = "1234567890123456" // 16-byte IV for AES

    fun encrypt(password: String): String {
        val secretKey: SecretKey = SecretKeySpec(SECRET_KEY.toByteArray(), ALGORITHM)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val ivParams = IvParameterSpec(IV.toByteArray())
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams)

        val encryptedBytes = cipher.doFinal(password.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decrypt(encryptedPassword: String): String {
        val secretKey: SecretKey = SecretKeySpec(SECRET_KEY.toByteArray(), ALGORITHM)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val ivParams = IvParameterSpec(IV.toByteArray())
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams)

        val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword))
        return String(decryptedBytes)
    }
}