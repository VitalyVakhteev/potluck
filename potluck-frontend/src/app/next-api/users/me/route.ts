import {NextRequest, NextResponse} from "next/server";
import {pickContentType} from "@/lib/utils";

const BASE = process.env.BACKEND_URL ?? "http://localhost:8080";

export async function GET(req: NextRequest) {
	const inCookie = req.headers.get("cookie") ?? "";

	const upstream = await fetch(`${BASE}/api/users/me`, {
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

export async function PATCH(req: NextRequest) {
	const inCookie = req.headers.get("cookie") ?? "";
	const contentType = pickContentType(req);
	const body = await req.text();

	const upstream = await fetch(`${BASE}/api/users/me`, {
		method: "PATCH",
		headers: {cookie: inCookie, "content-type": contentType},
		body,
		cache: "no-store",
	});

	const respType = upstream.headers.get("content-type") ?? "application/json";
	const text = await upstream.text();

	return new NextResponse(text, {
		status: upstream.status,
		headers: {"content-type": respType},
	});
}