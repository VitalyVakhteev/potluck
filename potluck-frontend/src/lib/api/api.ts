import { z } from "zod";
import {buildApiUrl, serverCookieHeader} from "@/lib/api/http";

export async function apiGet<T>(url: string, schema: z.ZodSchema<T>, { auth = false }: { auth?: boolean } = {}, init?: RequestInit): Promise<T> {
	const headers: HeadersInit = {
		...(auth && typeof window === "undefined" ? await serverCookieHeader() : {}),
		...(init?.headers ?? {}),
	};

	const fullUrl = await buildApiUrl(url);
	const res = await fetch(fullUrl, {
		method: "GET",
		headers,
		cache: "no-store",
		...(typeof window !== "undefined" ? { credentials: "include" } : {}),
		...init,
	});

	if (res.status === 401) {
		const empty = { content: [], empty: true } as unknown;
		return schema.parse(empty);
	}

	if (!res.ok) {
		const text = await res.text().catch(() => "");
		throw new Error(`${res.status} ${res.statusText || ""} :: ${text.slice(0, 500)}`);
	}

	const json = await res.json();
	const parsed = schema.safeParse(json);
	if (!parsed.success) {
		throw new Error("API validation failed: " + JSON.stringify(parsed.error.issues));
	}
	return parsed.data;
}