import { headers } from "next/headers";
import {SessionUser, UserDetail} from "@/lib/api/schemas";

const BASE = process.env.BACKEND_URL ?? "http://localhost:8080";

export async function getSession(): Promise<SessionUser | null> {
	const cookie = (await headers()).get("cookie") ?? "";

	const res = await fetch(`${BASE}/api/users/me`, {
		method: "GET",
		headers: { cookie },
		cache: "no-store",
	});

	if (res.ok) {
		const json = await res.json();
		const parsed = UserDetail.safeParse(json);
		return parsed.success ? parsed.data : null;
	}

	return null;
}
