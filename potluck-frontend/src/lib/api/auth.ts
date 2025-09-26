export async function login(username: string, password: string) {
	const res = await fetch("/api/auth/login", {
		method: "POST",
		headers: { "content-type": "application/json" },
		credentials: "include",
		body: JSON.stringify({ username, password }),
	});
	if (!res.ok) throw new Error("Login failed");
}

export async function signup(payload: {
	username: string; email: string; phone: string; password: string; role: "SEEKER" | "ORGANIZER";
}) {
	const res = await fetch("/api/auth/signup", {
		method: "POST",
		headers: { "content-type": "application/json" },
		credentials: "include",
		body: JSON.stringify(payload),
	});
	if (!res.ok) throw new Error("Signup failed");
}

export async function logout() {
	const res = await fetch("/api/auth/logout", {
		method: "POST",
		credentials: "include",
	});
	if (!res.ok) throw new Error("Logout failed");
}
