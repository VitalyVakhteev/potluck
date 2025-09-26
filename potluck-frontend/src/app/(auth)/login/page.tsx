"use client";

import { useRouter } from "next/navigation";
import { useAuth } from "@/app/providers";
import { login } from "@/lib/api/auth";
import { toast } from "sonner";

export default function LoginPage() {
	const router = useRouter();
	const { setUser } = useAuth();

	async function onSubmit(formData: FormData) {
		const username = String(formData.get("username"));
		const password = String(formData.get("password"));
		try {
			await login(username, password);
			toast.success("Logged in!");
			const meRes = await fetch("/api/users/me", { credentials: "include" });
			if (meRes.ok) setUser(await meRes.json());
			router.replace("/");
		} catch (e: unknown) {
			toast.error(`Login failed.`);
		}
	}

	return (
		// Todo: Card form, inputs named username/password, submit calls onSubmit, link to Signup
		<p>Test</p>
	);
}
