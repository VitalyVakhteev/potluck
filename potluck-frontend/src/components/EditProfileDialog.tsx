"use client";

import * as React from "react";
import {
	Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
import { Textarea } from "@/components/ui/textarea";
import { toast } from "sonner";

type ProfileForm = {
	firstName: string;
	lastName: string;
	bio: string;
	location: string;
	bannerColor: string;
	displayName: boolean;
	displayEmail: boolean;
	displayPhone: boolean;
};

export default function EditProfileDialog({ initial }: { initial: ProfileForm }) {
	const [open, setOpen] = React.useState(false);
	const [form, setForm] = React.useState<ProfileForm>(initial);
	const [busy, setBusy] = React.useState(false);

	const set = <K extends keyof ProfileForm>(k: K, v: ProfileForm[K]) => setForm((f) => ({ ...f, [k]: v }));

	const onSave = async () => {
		setBusy(true);
		try {
			const res = await fetch("/next-api/users/me", {
				method: "PATCH",
				credentials: "include",
				headers: { "content-type": "application/json" },
				body: JSON.stringify(form),
			});
			if (!res.ok) throw new Error("Failed to save profile");
			toast.success("Profile updated");
			setOpen(false);
			window.location.reload();
		} catch (e: unknown) {
			toast.error("Failed to save profile");
		} finally {
			setBusy(false);
		}
	};

	return (
		<Dialog open={open} onOpenChange={setOpen}>
			<DialogTrigger asChild>
				<Button variant="outline" className="bg-primary hover:bg-primary-foreground">Edit Profile</Button>
			</DialogTrigger>
			<DialogContent>
				<DialogHeader>
					<DialogTitle>Edit Profile</DialogTitle>
					<DialogDescription>Update your profile details.</DialogDescription>
				</DialogHeader>

				<div className="grid grid-cols-1 md:grid-cols-2 gap-3">
					<div>
						<Label>First Name</Label>
						<Input value={form.firstName} onChange={e => set("firstName", e.target.value)} />
					</div>
					<div>
						<Label>Last Name</Label>
						<Input value={form.lastName} onChange={e => set("lastName", e.target.value)} />
					</div>
					<div className="md:col-span-2">
						<Label>Bio</Label>
						<Textarea rows={3} value={form.bio} onChange={e => set("bio", e.target.value)} />
					</div>
					<div>
						<Label>Location</Label>
						<Input value={form.location} onChange={e => set("location", e.target.value)} />
					</div>
					<div>
						<Label>Banner Color (hex)</Label>
						<Input placeholder="#0ea5e9" value={form.bannerColor} onChange={e => set("bannerColor", e.target.value)} />
					</div>
					<div className="flex items-center gap-2">
						<Switch checked={form.displayName} onCheckedChange={(v) => set("displayName", v)} />
						<Label>Display Name</Label>
					</div>
					<div className="flex items-center gap-2">
						<Switch checked={form.displayEmail} onCheckedChange={(v) => set("displayEmail", v)} />
						<Label>Display Email</Label>
					</div>
					<div className="flex items-center gap-2">
						<Switch checked={form.displayPhone} onCheckedChange={(v) => set("displayPhone", v)} />
						<Label>Display Phone</Label>
					</div>
				</div>

				<DialogFooter>
					<Button variant="secondary" onClick={() => setOpen(false)} disabled={busy}>Cancel</Button>
					<Button onClick={onSave} disabled={busy} className="bg-primary hover:bg-primary-foreground">
						{busy ? "Saving..." : "Save"}
					</Button>
				</DialogFooter>
			</DialogContent>
		</Dialog>
	);
}
