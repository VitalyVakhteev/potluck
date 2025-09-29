import { NextRequest, NextResponse } from "next/server";

const BASE = process.env.BACKEND_URL ?? "http://localhost:8080";

export async function POST(req: NextRequest) {
	const body = await req.text();

	const upstream = await fetch(`${BASE}/api/auth/login`, {
		method: "POST",
		headers: { "content-type": "application/json" },
		body,
		redirect: "manual",
		cache: "no-store",
	});

	const text = await upstream.text();

	const res = new NextResponse(text, {
		status: upstream.status,
		headers: {
			"content-type": upstream.headers.get("content-type") ?? "application/json",
		},
	});

	const setCookie = upstream.headers.get("set-cookie");
	if (setCookie) res.headers.set("set-cookie", setCookie);

	return res;
}
