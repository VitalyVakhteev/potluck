import { NextRequest, NextResponse } from "next/server";
import { buildServerApiBase, serverCookieHeader } from "@/lib/api/http/server";

export async function POST(req: NextRequest) {
	const BASE = await buildServerApiBase();
	const cookie = (await serverCookieHeader()).cookie ?? "";
	const body = await req.text();

	const upstream = await fetch(`${BASE}/api/users/favorites`, {
		method: "POST",
		headers: { "content-type": "application/json", cookie },
		body,
		cache: "no-store",
	});

	const ctype = upstream.headers.get("content-type") ?? "application/json";
	const text = await upstream.text();
	return new NextResponse(text, { status: upstream.status, headers: { "content-type": ctype } });
}

export async function DELETE(req: NextRequest) {
	const BASE = await buildServerApiBase();
	const cookie = (await serverCookieHeader()).cookie ?? "";
	const body = await req.text();

	const upstream = await fetch(`${BASE}/api/users/favorites`, {
		method: "DELETE",
		headers: { "content-type": "application/json", cookie },
		body,
		cache: "no-store",
	});

	const ctype = upstream.headers.get("content-type") ?? "application/json";
	const text = await upstream.text();
	return new NextResponse(text, { status: upstream.status, headers: { "content-type": ctype } });
}
