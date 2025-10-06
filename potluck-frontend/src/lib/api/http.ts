import {cookies, headers} from "next/headers";

export async function buildApiUrl(path: string): Promise<string> {
	if (!path.startsWith("/")) throw new Error("path must start with /");
	if (typeof window === "undefined") {
		const base = process.env.BACKEND_URL ?? (await defaultOriginFromHeaders());
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
	const jar = await cookies();
	const all = jar.getAll();
	if (!all.length) return {};
	const cookieHeader = all.map(c => `${c.name}=${c.value}`).join("; ");
	return { cookie: cookieHeader };
}