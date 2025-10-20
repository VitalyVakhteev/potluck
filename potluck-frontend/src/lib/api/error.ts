export class ApiError extends Error {
	code: string;
	status: number;
	fields?: Record<string, unknown>;

	constructor(code: string, message: string, status: number, fields?: Record<string, unknown>) {
		super(message);
		this.code = code;
		this.status = status;
		this.fields = fields;
	}
}

export function isApiError(e: unknown): e is ApiError {
	return !!e && typeof e === "object"
		&& "code" in (e as object)
		&& "status" in (e as object);
}

export type body = {
	code: string;
	error: string;
	status: number;
	fields?: Record<string, unknown>;
}

export async function jsonFetch<T>(input: RequestInfo, init?: RequestInit): Promise<T> {
	const res = await fetch(input, init);
	if (res.ok) {
		const ct = res.headers.get("content-type") || "";
		if (!ct.includes("application/json")) {
			await res.text();
			return (undefined as unknown) as T;
		}

		const text = await res.text();
		if (!text) {
			return (undefined as unknown) as T;
		}
		return JSON.parse(text) as T;
	}

	let body: { code?: string; error?: string; status?: number; fields?: Record<string, unknown> } = {};
	try {
		body = await res.json();
	} catch {
	}

	throw new ApiError(
		body?.code ?? "UNKNOWN",
		body?.error ?? "Request failed",
		body?.status ?? res.status,
		body?.fields
	);
}