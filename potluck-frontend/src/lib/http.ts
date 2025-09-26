import { headers } from "next/headers";

export function buildApiUrl(path: string) {
	if (!path.startsWith("/")) throw new Error("path must start with /");
	if (typeof window === "undefined") {
		const base = process.env.BACKEND_URL ?? defaultOriginFromHeaders();
		return base + path;
	}
	return path;
}

async function defaultOriginFromHeaders() {
	const h = await headers();
	const proto = h.get("x-forwarded-proto") ?? "http";
	const host = h.get("x-forwarded-host") ?? h.get("host") ?? "localhost:3000";
	return `${proto}://${host}`;
}

export async function serverCookieHeader(): Promise<Record<string, string>> {
	if (typeof window !== "undefined") return {};
	const h = await headers();
	const cookie = h.get("cookie");
	return cookie ? { cookie } : {};
}
