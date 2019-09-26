package utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class AsyncEmailer implements Runnable {
	private String fromEmail;
	private String toEmail;
	private String subject;
	private String password;

	public AsyncEmailer() {
	}

	public AsyncEmailer(String fromEmail, String toEmail, String subject, String password) {
		this.fromEmail = fromEmail;
		this.toEmail = toEmail;
		this.subject = subject;
		this.password = password;
	}

	@Override
	public void run() {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP Host
		props.put("mail.smtp.socketFactory.port", "465"); // SSL Port
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL Factory Class
		props.put("mail.smtp.auth", "true"); // Enabling SMTP Authentication
		props.put("mail.smtp.port", "465"); // SMTP Port

		Authenticator auth = new Authenticator() {
			// override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		};
		Session session = Session.getDefaultInstance(props, auth);
		System.out.println("Session created");
		EmailUtil.sendEmail(session, toEmail, subject, "SSLEmail Testing Body");

	}

}
