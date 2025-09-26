export function normalizePhone(input: string): string | null {
	const digits = input.replace(/[^\d+]/g, "");
	if (/^\+\d{8,15}$/.test(digits)) return digits;
	const ten = digits.replace(/\D/g, "");
	if (/^\d{10}$/.test(ten)) return `+1${ten}`;
	return null;
}
