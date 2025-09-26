"use client";

import * as React from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/app/providers";
import { signup, login } from "@/lib/api/auth";
import { toast } from "sonner";

import { Button } from "@/components/ui/button";
import {
	Card,
	CardContent,
	CardDescription,
	CardFooter,
	CardHeader,
	CardTitle,
} from "@/components/ui/card";
import {
	Select,
	SelectContent,
	SelectGroup,
	SelectItem,
	SelectLabel,
	SelectTrigger,
	SelectValue,
} from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import Link from "next/link";

import { SignupSchema } from "@/lib/api/schemas";
import { normalizePhone } from "@/lib/phone";
import type { Role } from "@/lib/api/schemas";

export default function SignupPage() {
	const router = useRouter();
	const { setUser } = useAuth();

	const [role, setRole] = React.useState<Role | "">("");

	async function onSubmit(e: React.FormEvent<HTMLFormElement>) {
		e.preventDefault();
		const fd = new FormData(e.currentTarget);

		const username = String(fd.get("username") ?? "");
		const password = String(fd.get("password") ?? "");
		const email = String(fd.get("email") ?? "");
		const rawPhone = String(fd.get("phone") ?? "");
		const selectedRole = (String(fd.get("role") ?? "") as Role) || role;

		const normalized = normalizePhone(rawPhone);
		if (!normalized) {
			toast.error("Please enter a valid phone number.");
			return;
		}

		const parsed = SignupSchema.safeParse({
			username,
			password,
			email,
			phone: normalized,
			role: selectedRole,
		});
		if (!parsed.success) {
			toast.error(parsed.error.issues[0]?.message ?? "Invalid input.");
			return;
		}

		try {
			await signup(parsed.data);
			toast.success("Account created!");

			await login(username, password);

			const meRes = await fetch("/api/users/me", { credentials: "include" });
			if (meRes.ok) setUser(await meRes.json());

			router.replace("/");
		} catch {
			toast.error("Signup failed.");
		}
	}

	return (
		<Card className="w-full max-w-sm">
			<CardHeader>
				<CardTitle>Sign Up</CardTitle>
				<CardDescription>Fill out the fields below to make an account.</CardDescription>
			</CardHeader>
			<CardContent>
				<form onSubmit={onSubmit} noValidate>
					<div className="flex flex-col gap-6">
						<div className="grid gap-2">
							<div className="flex items-center">
								<Label htmlFor="role">Account Type</Label>
							</div>
							<Select value={role} onValueChange={(v: Role) => setRole(v)}>
								<SelectTrigger aria-label="Account Type">
									<SelectValue placeholder="Select your account type" />
								</SelectTrigger>
								<SelectContent>
									<SelectGroup>
										<SelectLabel>Account Type</SelectLabel>
										<SelectItem value="SEEKER">Fundraiser Seeker</SelectItem>
										<SelectItem value="ORGANIZER">Fundraiser Organizer</SelectItem>
									</SelectGroup>
								</SelectContent>
							</Select>
							<input type="hidden" name="role" value={role} />
						</div>

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
							</div>
							<Input
								id="password"
								name="password"
								type="password"
								autoComplete="new-password"
								required
							/>
						</div>

						<div className="grid gap-2">
							<div className="flex items-center">
								<Label htmlFor="email">Email</Label>
							</div>
							<Input
								id="email"
								name="email"
								type="email"
								placeholder="john@potluck.com"
								autoComplete="email"
								required
							/>
						</div>

						<div className="grid gap-2">
							<div className="flex items-center">
								<Label htmlFor="phone">Phone Number</Label>
							</div>
							<Input
								id="phone"
								name="phone"
								type="tel"
								inputMode="tel"
								placeholder="+1 555 123 4567"
								autoComplete="tel"
								required
							/>
						</div>

						<Button type="submit" className="w-full bg-primary hover:bg-primary-foreground">
							<p className="text-foreground">Sign Up</p>
						</Button>
					</div>
				</form>
			</CardContent>
			<CardFooter className="flex-col gap-2">
				<Button asChild variant="link" className="w-full">
					<Link href="/login" className="text-primary-foreground">
						Back to Login
					</Link>
				</Button>
			</CardFooter>
		</Card>
	);
}