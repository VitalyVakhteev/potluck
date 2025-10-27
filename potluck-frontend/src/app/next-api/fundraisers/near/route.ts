import {NextRequest, NextResponse} from "next/server";

const BASE = process.env.BACKEND_URL ?? "http://localhost:8080";

export async function POST(req: NextRequest) {
	const cookie = req.headers.get("cookie") ?? "";
	const {searchParams} = new URL(req.url);

	const lat = Number(searchParams.get("lat"));
	const lon = Number(searchParams.get("lon"));
	const radiusKm = Number(searchParams.get("radiusKm") ?? 20);

	const page = Number(searchParams.get("page") ?? 0);
	const size = Number(searchParams.get("size") ?? 20);
	const sort = searchParams.get("sort") ?? "createdAt,DESC";

	if (!Number.isFinite(lat) || !Number.isFinite(lon)) {
		return NextResponse.json(
			{code: "BAD_REQUEST", error: "Latitude and Longitude are required", status: 400},
			{status: 400}
		);
	}

	const upstream = await fetch(`${BASE}/api/fundraisers/near?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, {
		method: "POST",
		headers: {"content-type": "application/json", cookie},
		body: JSON.stringify({lat, lon, radiusKm}),
		cache: "no-store",
	});

	const contentType = upstream.headers.get("content-type") ?? "application/json";
	const text = await upstream.text();

	return new NextResponse(text, {status: upstream.status, headers: {"content-type": contentType}});
}
