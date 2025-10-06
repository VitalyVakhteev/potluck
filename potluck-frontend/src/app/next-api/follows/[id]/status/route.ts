import { NextRequest, NextResponse } from "next/server";

const BASE = process.env.BACKEND_URL ?? "http://localhost:8080";

export async function GET(req: NextRequest, ctx: { params: { id: string } }) {
	const inCookie = req.headers.get("cookie") ?? "";
	const { id } = ctx.params;

	const upstream = await fetch(`${BASE}/api/users/follows/${encodeURIComponent(id)}/status`, {
		method: "GET",
		headers: { cookie: inCookie },
		cache: "no-store",
	});

	const contentType = upstream.headers.get("content-type") ?? "application/json";
	const text = await upstream.text();

	return new NextResponse(text, {
		status: upstream.status,
		headers: { "content-type": contentType },
	});
}