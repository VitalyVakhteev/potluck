import {NextRequest, NextResponse} from "next/server";

const BASE = process.env.BACKEND_URL ?? "http://localhost:8080";

export async function POST(req: NextRequest, ctx: { params: { id: string } }) {
	const inCookie = req.headers.get("cookie") ?? "";
	const ctx_params = await ctx.params;
	const {id} = ctx_params;

	const upstream = await fetch(`${BASE}/api/users/follows/${encodeURIComponent(id)}`, {
		method: "POST",
		headers: {cookie: inCookie},
		cache: "no-store",
	});

	const respType = upstream.headers.get("content-type") ?? "application/json";
	const text = await upstream.text();

	return new NextResponse(text, {
		status: upstream.status,
		headers: {"content-type": respType},
	});
}

export async function DELETE(req: NextRequest, ctx: { params: { id: string } }) {
	const inCookie = req.headers.get("cookie") ?? "";
	const ctx_params = await ctx.params;
	const {id} = ctx_params;

	const upstream = await fetch(`${BASE}/api/users/follows/${encodeURIComponent(id)}`, {
		method: "DELETE",
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