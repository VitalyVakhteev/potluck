import 'server-only';
import {cache} from 'react';
import {headers} from 'next/headers';
import {UserDetail, type SessionUser} from '@/lib/api/schemas';
import {serverCookieHeader} from "@/lib/api/http/server";

const getOrigin = async () => {
	const h = await headers();
	const proto = h.get('x-forwarded-proto') ?? 'http';
	const host = h.get('x-forwarded-host') ?? h.get('host')!;
	if (!host) throw new Error("Host header missing");
	return `${proto}://${host}`;
};

export const getSession = cache(async (): Promise<SessionUser | null> => {
	const origin = await getOrigin();
	const hdrs = await serverCookieHeader();

	const res = await fetch(`${origin}/next-api/users/me`, {
		method: "GET",
		cache: "no-store",
		headers: hdrs,
	});

	if (res.status === 401) return null;
	if (!res.ok) return null;

	const json = await res.json();
	const parsed = UserDetail.safeParse(json);
	return parsed.success ? parsed.data : null;
});