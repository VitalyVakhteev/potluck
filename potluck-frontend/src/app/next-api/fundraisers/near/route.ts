import { NextRequest, NextResponse } from "next/server";

const BASE = process.env.BACKEND_URL ?? "http://localhost:8080";

export async function POST(req: NextRequest) {
	const cookie = req.headers.get("cookie") ?? "";

	const body = await req.json().catch(() => null);
	const upstream = await fetch(`${BASE}/api/fundraisers/near`, {
		method: "POST",
		headers: {
			"content-type": "application/json",
			cookie,
		},
		body: JSON.stringify(body),
	});

	const contentType = upstream.headers.get("content-type") ?? "application/json";
	const text = await upstream.text();

	return new NextResponse(text, {
		status: upstream.status,
		headers: { "content-type": contentType },
	});
}