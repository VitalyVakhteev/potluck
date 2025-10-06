import {jsonFetch} from "@/lib/api/error";

export async function signup(payload: { username: string; email: string; phone: string; password: string; role: "SEEKER" | "ORGANIZER"; }) {
	return jsonFetch<{ userId: string; username: string; role: string; }>("/next-api/auth/signup", {
		method: "POST",
		headers: { "content-type": "application/json" },
		credentials: "include",
		body: JSON.stringify(payload),
	});
}

export async function login(username: string, password: string) {
	return jsonFetch<{ userId: string; username: string; role: string; }>("/next-api/auth/login", {
		method: "POST",
		headers: { "content-type": "application/json" },
		credentials: "include",
		body: JSON.stringify({ username, password }),
	});
}

export async function logout() {
	return jsonFetch<void>("/next-api/auth/logout", {
		method: "POST",
		credentials: "include",
	});
}