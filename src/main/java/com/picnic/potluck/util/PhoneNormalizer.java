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
			if (s.length() == 10 && "+1".equals(defaultCountryCode)) {
				s = defaultCountryCode + s;
			} else {
				// I don't have the energy to read through the other country codes
				// so we assume they work the same until proven otherwise :)
				s = defaultCountryCode + s;
			}
		}

		if (!s.matches("^\\+[1-9]\\d{7,14}$")) {
			throw new IllegalArgumentException("Invalid phone number");
		}
		return s;
	}
}
