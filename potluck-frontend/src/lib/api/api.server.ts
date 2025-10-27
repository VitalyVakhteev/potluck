import "server-only";
import {z} from "zod";
import {buildServerApiBase, serverCookieHeader} from "@/lib/api/http/server";

export async function apiGetServer<T>(
	path: string,
	schema: z.ZodSchema<T>,
	init?: RequestInit
): Promise<T> {
	const BASE = await buildServerApiBase();
	const headers: HeadersInit = {...(await serverCookieHeader()), ...(init?.headers ?? {})};

	const res = await fetch(`${BASE}${path}`, {
		method: "GET",
		headers,
		cache: "no-store",
		...init,
	});

	if (res.status === 401) {
		const empty = {content: [], empty: true} as unknown;
		return schema.parse(empty);
	}

	if (!res.ok) {
		const text = await res.text().catch(() => "");
		throw new Error(`${res.status} ${res.statusText || ""} :: ${text.slice(0, 500)}`);
	}

	const json = await res.json();
	const parsed = schema.safeParse(json);
	if (!parsed.success) throw new Error("API validation failed: " + JSON.stringify(parsed.error.issues));
	return parsed.data;
}
