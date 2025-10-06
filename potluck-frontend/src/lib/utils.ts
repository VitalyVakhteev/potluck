import {type ClassValue, clsx} from "clsx"
import {twMerge} from "tailwind-merge"
import {NextRequest} from "next/server";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export const PAGE_SIZE = 20;

export function toPageIndex(raw?: string | string[]) {
	const n = Number(Array.isArray(raw) ? raw[0] : raw);
	return Number.isFinite(n) && n > 0 ? n - 1 : 0;
}

export function toPageParam(index: number): number {
	return index + 1;
}

export function toSort(raw?: string | string[], fallback = "createdAt,DESC") {
	return (Array.isArray(raw) ? raw[0] : raw) || fallback;
}

export function pickContentType(req: NextRequest) {
	return req.headers.get("content-type") ?? "application/json";
}

export const isOnlyDots = (s?: string) => !!s && /^\.+$/.test(s.trim());

export function combineLocalDateTime(d: Date | undefined, timeHHMMSS: string | undefined) {
	if (!d || !timeHHMMSS) return null;
	const [hh, mm, ss = "0"] = timeHHMMSS.split(":");
	const dt = new Date(d);
	dt.setHours(Number(hh), Number(mm), Number(ss), 0);
	return dt;
}

// What follows is a series of functions that helpz calculate what color the text should be
// all to match user banner color preference on the profile page...
export function hexToRgb(hex: string): [number, number, number] | null {
	const m = hex.trim().replace(/^#/, "");
	if (m.length === 3) {
		const r = parseInt(m[0] + m[0], 16);
		const g = parseInt(m[1] + m[1], 16);
		const b = parseInt(m[2] + m[2], 16);
		return [r, g, b];
	}
	if (m.length === 6) {
		const r = parseInt(m.slice(0, 2), 16);
		const g = parseInt(m.slice(2, 4), 16);
		const b = parseInt(m.slice(4, 6), 16);
		return [r, g, b];
	}
	return null;
}

function srgbToLinear(c: number) {
	const cs = c / 255;
	// these magic numbers are as follows:
	// 0.04045 is the point on the srgb transfer curve at which the linear and power functions diverge significantly;
	// if it's less than that point, we use the linear slope 12.92 to convert numbers
	// else, we use the inverse power function with the appropriate factors to convert numbers
	// 2.4 is the srgb gamma exponent
	return cs <= 0.04045 ? cs / 12.92 : Math.pow((cs + 0.055) / 1.055, 2.4);
}

export function luminance([r, g, b]: [number, number, number]) {
	const R = srgbToLinear(r);
	const G = srgbToLinear(g);
	const B = srgbToLinear(b);
	// These numbers are the Rec. 709 luma coefficients for red, green, and blue
	return 0.2126 * R + 0.7152 * G + 0.0722 * B;
}

export function contrastRatio(l1: number, l2: number) {
	// First, if l1 is bigger than l2, we set l1 to be first, else we switch them
	const [a, b] = l1 > l2 ? [l1, l2] : [l2, l1];
	// Then, because the WCAG defines contrast as such, we add 0.05 for flare
	return (a + 0.05) / (b + 0.05);
}

export function pickTextOnRgb(rgb: [number, number, number]): "#000000" | "#ffffff" {
	const L = luminance(rgb);
	const black = contrastRatio(L, 0);
	const white = contrastRatio(L, 1);
	return white > black ? "#ffffff" : "#000000";
}

export function pickTextOnHex(hex: string): "#000000" | "#ffffff" {
	const rgb = hexToRgb(hex);
	if (!rgb) return "#000000";
	return pickTextOnRgb(rgb);
}