import { FundraiserPage } from "@/lib/api/schemas";

type NearBody = { lat: number; lon: number; radiusKm?: number };
type PageOpts = { page?: number; size?: number };

export async function near(body: NearBody, { page = 0, size = 20 }: PageOpts = {}
) {
	const res = await fetch(`/next-api/fundraisers/near?page=${page}&size=${size}`, {
		method: "POST",
		headers: { "content-type": "application/json" },
		body: JSON.stringify(body),
		credentials: "include",
	});

	if (!res.ok) throw new Error(`near failed: ${res.status}`);

	const json = await res.json();
	const parsed = FundraiserPage.safeParse(json);
	if (!parsed.success) throw new Error("API validation failed");
	return parsed.data;
}
