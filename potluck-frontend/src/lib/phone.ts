export function normalizePhone(input: string): string | null {
	const s: string = input.trim().replace(/[^\d+]/g, "");
	if (/^\+\d{8,15}$/.test(s)) {
		if (/^\+0/.test(s)) return null;
		return s;
	}
	const digits = s.replace(/\D/g, "");
	const nanp = digits.length === 11 && digits.startsWith("1")
		? digits.slice(1)
		: digits;
	if (/^\d{10}$/.test(nanp)) {
		return `+1${nanp}`;
	}
	return null;
}
