import { NextRequest, NextResponse } from "next/server";

const BASE = process.env.BACKEND_URL ?? "http://localhost:8080";

export async function proxy(req: NextRequest, upstreamPath: string, init?: RequestInit) {
	const body = await req.text();

	const upstream = await fetch(`${BASE}${upstreamPath}`, {
		method: init?.method ?? req.method,
		headers: {
			"content-type": req.headers.get("content-type") ?? "application/json",
			cookie: req.headers.get("cookie") ?? "",
			...(init?.headers ?? {}),
		},
		body: ["GET", "HEAD"].includes((init?.method ?? req.method).toUpperCase())
			? undefined
			: (init?.body as BodyInit) ?? body,
		redirect: "manual",
		cache: "no-store",
	});

	const text = await upstream.text();
	const res = new NextResponse(text, {
		status: upstream.status,
		headers: {
			"content-type":
				upstream.headers.get("content-type") ?? "application/json",
		},
	});

	const setCookie = upstream.headers.get("set-cookie");
	if (setCookie) res.headers.set("set-cookie", setCookie);
	return res;
}