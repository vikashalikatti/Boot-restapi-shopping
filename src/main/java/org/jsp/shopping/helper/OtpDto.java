package org.jsp.shopping.helper;

import java.time.LocalDateTime;

public class OtpDto {
	private int otp;
	private LocalDateTime expiryTime;

	public OtpDto() {
	}

	public OtpDto(int otp, LocalDateTime expiryTime) {
		this.otp = otp;
		this.expiryTime = expiryTime;
	}

	public int getOtp() {
		return otp;
	}

	public void setOtp(int otp) {
		this.otp = otp;
	}

	public LocalDateTime getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(LocalDateTime expiryTime) {
		this.expiryTime = expiryTime;
	}

	public boolean isExpired() {
		return LocalDateTime.now().isAfter(this.expiryTime);
	}
}
