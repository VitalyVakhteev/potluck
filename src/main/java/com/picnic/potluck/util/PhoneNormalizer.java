package com.picnic.potluck.util;

public final class PhoneNormalizer {
	private PhoneNormalizer() {
	}

	public static String toE164(String raw, String defaultCountryCode) {
		if (raw == null) throw new IllegalArgumentException("Phone is blank");

		String s = raw.trim();
		boolean hadPlus = s.startsWith("+");
		s = s.replaceAll("[^\\d+]", "");
		if (!hadPlus && s.startsWith("+")) hadPlus = true;

		s = (hadPlus ? "+" : "") + s.replaceAll("\\D", "");

		if (!s.startsWith("+")) {
			if ("+1".equals(defaultCountryCode) && s.length() == 11 && s.startsWith("1")) {
				s = s.substring(1);
			}
			if ("+1".equals(defaultCountryCode) && s.length() == 10) {
				s = defaultCountryCode + s;
			} else {
				s = defaultCountryCode + s;
			}
		}

		if (!s.matches("^\\+[1-9]\\d{7,14}$")) {
			throw new IllegalArgumentException("Invalid phone number");
		}
		return s;
	}

}
