import {z} from "zod";

export async function apiGetClient<T>(
	nextApiPath: string,
	schema: z.ZodSchema<T>,
	init?: RequestInit,
): Promise<T> {
	const res = await fetch(nextApiPath, {
		method: "GET",
		credentials: "include",
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
