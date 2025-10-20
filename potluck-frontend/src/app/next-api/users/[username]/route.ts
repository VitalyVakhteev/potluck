import {NextRequest, NextResponse} from "next/server";

const BASE = process.env.BACKEND_URL ?? "http://localhost:8080";

export async function GET(req: NextRequest, ctx: { params: { username: string } }) {
	const inCookie = req.headers.get("cookie") ?? "";
	const ctx_params = await ctx.params;
	const {username} = ctx_params;

	const upstream = await fetch(`${BASE}/api/users/${encodeURIComponent(username)}`, {
		method: "GET",
		headers: {cookie: inCookie},
		cache: "no-store",
	});

	const contentType = upstream.headers.get("content-type") ?? "application/json";
	const text = await upstream.text();

	return new NextResponse(text, {
		status: upstream.status,
		headers: {"content-type": contentType},
	});
}
