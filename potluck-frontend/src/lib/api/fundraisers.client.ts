import { jsonFetch } from "@/lib/api/http/client";
import type { CreateFundraiser } from "@/lib/api/schemas";

export type CreateFundraiserResponse = {
	id: string; title: string; startsAt: string | null; endsAt: string | null;
};

export async function createFundraiser(payload: CreateFundraiser) {
	const body = {
		...payload,
		startsAt: payload.startsAt ? new Date(payload.startsAt).toISOString() : null,
		endsAt: payload.endsAt ? new Date(payload.endsAt).toISOString() : null,
	};

	return jsonFetch<CreateFundraiserResponse>("/next-api/fundraisers", {
		method: "POST",
		headers: { "content-type": "application/json" },
		credentials: "include",
		body: JSON.stringify(body),
	});
}

export async function patchFundraiser(payload: {
	id: string;
	title?: string;
	description?: string;
	email?: string;
	phoneNumber?: string;
	lat?: number;
	lon?: number;
	reward?: boolean;
	startsAt?: string;
	endsAt?: string;
}) {
	return jsonFetch(`/next-api/fundraisers`, {
		method: "PATCH",
		headers: { "content-type": "application/json" },
		credentials: "include",
		body: JSON.stringify(payload),
	});
}
