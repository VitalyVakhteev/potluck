"use client";

import * as React from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/app/providers";
import { login } from "@/lib/api/auth";
import { toast } from "sonner";

import { Button } from "@/components/ui/button";
import {
	Card,
	CardAction,
	CardContent,
	CardDescription,
	CardFooter,
	CardHeader,
	CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import Link from "next/link";
import { LoginSchema } from "@/lib/api/schemas";

export default function LoginPage() {
	const router = useRouter();
	const { setUser } = useAuth();

	async function onSubmit(e: React.FormEvent<HTMLFormElement>) {
		e.preventDefault();
		const fd = new FormData(e.currentTarget);
		const username = String(fd.get("username") ?? "");
		const password = String(fd.get("password") ?? "");

		const parsed = LoginSchema.safeParse({ username, password });
		if (!parsed.success) {
			toast.error(parsed.error.issues[0]?.message ?? "Invalid input.");
			return;
		}

		try {
			await login(username, password);
			toast.success("Logged in!");
			const meRes = await fetch("/api/users/me", { credentials: "include" });
			if (meRes.ok) {
				setUser(await meRes.json());
			}
			router.replace("/");
		} catch {
			toast.error("Login failed.");
		}
	}

	return (
		<Card className="w-full max-w-sm">
			<CardHeader>
				<CardTitle>Login to your account</CardTitle>
				<CardDescription>Enter your credentials below to login</CardDescription>
				<CardAction>
					<Button asChild variant="link" className="text-primary-foreground">
						<Link href="/signup">Sign Up</Link>
					</Button>
				</CardAction>
			</CardHeader>
			<CardContent>
				<form onSubmit={onSubmit} noValidate>
					<div className="flex flex-col gap-6">
						<div className="grid gap-2">
							<Label htmlFor="username">Username</Label>
							<Input
								id="username"
								name="username"
								type="text"
								placeholder="johndoe"
								autoComplete="username"
								required
							/>
						</div>
						<div className="grid gap-2">
							<div className="flex items-center">
								<Label htmlFor="password">Password</Label>
								<a
									href="#"
									className="ml-auto inline-block text-sm underline-offset-4 hover:underline"
								>
									Forgot your password?
								</a>
							</div>
							<Input
								id="password"
								name="password"
								type="password"
								autoComplete="current-password"
								required
							/>
						</div>
						<Button type="submit" className="w-full bg-primary hover:bg-primary-foreground">
							<p className="text-foreground">Login</p>
						</Button>
					</div>
				</form>
			</CardContent>
			<CardFooter className="flex-col gap-2">
				<Button variant="outline" className="w-full">
					<p className="text-foreground">Login with Google</p>
				</Button>
			</CardFooter>
		</Card>
	);
}
