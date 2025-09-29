import { NextRequest, NextResponse } from "next/server";
const BASE = process.env.BACKEND_URL ?? "http://localhost:8080";

export async function POST(_req: NextRequest) {
	const upstream = await fetch(`${BASE}/api/auth/logout`, {
		method: "POST",
		redirect: "manual",
		cache: "no-store",
	});

	const res = new NextResponse(null, { status: upstream.status });
	const setCookie = upstream.headers.get("set-cookie");
	if (setCookie) res.headers.set("set-cookie", setCookie);
	return res;
}