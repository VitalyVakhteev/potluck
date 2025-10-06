import {NextRequest, NextResponse} from "next/server";
import {pickContentType} from "@/lib/utils";

const BASE = process.env.BACKEND_URL ?? "http://localhost:8080";
const NO_BODY_STATUSES = new Set([204, 205, 304]);

export async function POST(req: NextRequest) {
	const inCookie = req.headers.get("cookie") ?? "";
	const contentType = pickContentType(req);
	const body = await req.text();

	const upstream = await fetch(`${BASE}/api/users/me/password`, {
		method: "POST",
		headers: { cookie: inCookie, "content-type": contentType },
		body,
		cache: "no-store",
		redirect: "manual",
	});

	const status = upstream.status;

	if (NO_BODY_STATUSES.has(status)) {
		return new NextResponse(null, { status });
	}

	const respType = upstream.headers.get("content-type") ?? "application/json";
	const text = await upstream.text();

	return new NextResponse(text, {
		status,
		headers: {"content-type": respType},
	});
}