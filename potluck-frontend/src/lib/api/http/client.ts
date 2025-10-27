export async function jsonFetch<T>(input: RequestInfo | URL, init?: RequestInit): Promise<T> {
	const res = await fetch(input, init);
	const ctype = res.headers.get("content-type") ?? "";
	const isJson = ctype.includes("application/json");
	const body = isJson ? await res.json() : await res.text();

	if (!res.ok) {
		throw new Error((body && body.message) || res.statusText);
	}
	return body as T;
}