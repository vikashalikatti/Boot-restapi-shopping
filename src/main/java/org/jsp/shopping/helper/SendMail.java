package org.jsp.shopping.helper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.jsp.shopping.dto.Customer;
import org.jsp.shopping.dto.Merchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class SendMail {
	@Autowired
	JavaMailSender mailSender;
	@Autowired
	Configuration configuration;

	public boolean sendOtp(Merchant merchant) throws TemplateNotFoundException, MalformedTemplateNameException,
			ParseException, IOException, TemplateException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

		try {
			helper.setFrom("E-kart <gangsteryt111@gmail.com>");
			helper.setTo(merchant.getEmail());
			helper.setSubject("Verify Email for Account Creation");

			Template template = configuration.getTemplate("merchant-template.ftl");
			Map<String, Object> model = new HashMap<>();
			model.put("merchant", merchant);
			model.put("otp", merchant.getOtp());
			String genderPrefix = "Mr.";
			if ("female".equalsIgnoreCase(merchant.getGender())) {
				genderPrefix = "Ms.";
			} else {
				genderPrefix = "Mr.";
			}
			model.put("genderPrefix", genderPrefix);

			LocalDateTime otpExpirationTime = LocalDateTime.now().plusMinutes(5);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedOTPExpirationTime = otpExpirationTime.format(formatter);
			model.put("formattedOTPExpirationTime", formattedOTPExpirationTime);
			System.out.println("Formatted OTP Expiration Time: " + formattedOTPExpirationTime);

			String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

			helper.setText(content, true);
			mailSender.send(mimeMessage);
			return true;

		} catch (MessagingException e) {
			return false;
		}

	}

	public boolean sendLink(Customer customer) throws Exception {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

		try {
			helper.setFrom("E-kart <gangsteryt111@gmail.com>");
			helper.setTo(customer.getEmail());
			helper.setSubject("Verify Email for Account Creation");

			Template template = configuration.getTemplate("customer-sendLink.ftl");
			Map<String, Object> model = new HashMap<>();
			model.put("merchant", customer);
//			model.put("otp", customer.getOtp());
			String genderPrefix = "Mr.";
			if ("female".equalsIgnoreCase(customer.getGender())) {
				genderPrefix = "Ms.";
			} else {
				genderPrefix = "Mr.";
			}
			model.put("genderPrefix", genderPrefix);
			String email = customer.getEmail();
			model.put("email", email);
			model.put("customer", customer);
			LocalDateTime otpExpirationTime = LocalDateTime.now().plusMinutes(5);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedOTPExpirationTime = otpExpirationTime.format(formatter);
			model.put("formattedOTPExpirationTime", formattedOTPExpirationTime);
			String token = customer.getToken();
			model.put("token", token);
			String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

			helper.setText(content, true);
			mailSender.send(mimeMessage);
			return true;

		} catch (MessagingException e) {
			return false;

		}
	}

	public boolean sendResetLink(Customer customer) throws Exception {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

		try {
			helper.setFrom("E-kart <gangsteryt111@gmail.com>");
			helper.setTo(customer.getEmail());
			helper.setSubject("Verify Email for Account Creation");

			Template template = configuration.getTemplate("customer-sendLink-rest.ftl");
			Map<String, Object> model = new HashMap<>();
			model.put("customer", customer);
//			model.put("otp", customer.getOtp());
			String genderPrefix = "Mr.";
			if ("female".equalsIgnoreCase(customer.getGender())) {
				genderPrefix = "Ms.";
			} else {
				genderPrefix = "Mr.";
			}
			model.put("genderPrefix", genderPrefix);
			String email = customer.getEmail();
			model.put("email", email);
			LocalDateTime otpExpirationTime = LocalDateTime.now().plusMinutes(5);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedOTPExpirationTime = otpExpirationTime.format(formatter);
			model.put("formattedOTPExpirationTime", formattedOTPExpirationTime);
			String token = customer.getToken();
			model.put("token", token);
			String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

			helper.setText(content, true);
			mailSender.send(mimeMessage);
			return true;

		} catch (MessagingException e) {
			return false;

		}
	}
}
