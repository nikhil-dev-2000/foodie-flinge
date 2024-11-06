package com.example.foodiefling.ui
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSender(private val email: String, private val password: String) {

    fun sendEmail(to: String, subject: String, message: String) {
        val properties = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.starttls.enable", "true")
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(email, password)
            }
        })

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val mimeMessage = MimeMessage(session).apply {
                    setFrom(InternetAddress(email))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                    setSubject(subject)
                    setText(message)
                }
                Transport.send(mimeMessage)
            } catch (e: Exception) {
                e.printStackTrace() // Handle the exception as needed
            }
        }
    }
}

