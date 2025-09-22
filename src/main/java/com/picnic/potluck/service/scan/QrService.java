package com.picnic.potluck.service.scan;

import com.picnic.potluck.dto.scan.QrResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrService {
	@Value("${app.qr.secret}")
	private String secret;
	@Value("${app.qr.ttl-seconds}")
	private long ttlSeconds;

	public QrResponse generateQr(UUID organizerId, UUID fundraiserId) {
		var exp = Instant.now().plusSeconds(ttlSeconds).getEpochSecond();
		var data = fundraiserId + "." + organizerId + "." + exp;
		var sig = hmacSha256Hex(secret, data);
		// Todo: Ideally, the frontend QR code points to this once we build it.
		// We can also have the frontend point to different URL that sends a request to this URL
		// Either way, revisit this when the time comes.
		var claimPath = "/api/scans/claim?f=" + fundraiserId + "&o=" + organizerId + "&t=" + exp + "&sig=" + sig;
		return new QrResponse(claimPath, exp);
	}

	public void verify(UUID fundraiserId, UUID organizerId, long exp, String sig) {
		if (Instant.now().getEpochSecond() > exp) throw new IllegalArgumentException("QR expired");
		var expected = hmacSha256Hex(secret, fundraiserId + "." + organizerId + "." + exp);
		if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), sig.getBytes(StandardCharsets.UTF_8)))
			throw new IllegalArgumentException("Invalid QR signature");
	}

	private static String hmacSha256Hex(String key, String data) {
		try {
			var mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
			byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
			var sb = new StringBuilder(raw.length * 2);
			for (byte b : raw) sb.append(String.format("%02x", b));
			return sb.toString();
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}
}
