"use client";

import * as React from "react";
import {
	Dialog,
	DialogContent,
	DialogDescription,
	DialogFooter,
	DialogHeader,
	DialogTitle,
	DialogTrigger,
} from "@/components/ui/dialog";
import {Button} from "@/components/ui/button";
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {Switch} from "@/components/ui/switch";
import {Textarea} from "@/components/ui/textarea";
import {toast} from "sonner";
import {isApiError} from "@/lib/api/error";
import {patchMe} from "@/lib/api/users";
import {isOnlyDots} from "@/lib/utils";

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


type PatchProfile = Omit<ProfileForm, "firstName" | "lastName"> & Partial<Pick<ProfileForm, "firstName" | "lastName">>;

const FIELD_LABELS: Record<string, string> = {
	firstName: "First name",
	lastName: "Last name",
	bio: "Bio",
	location: "Location",
	bannerColor: "Banner color",
	displayName: "Display name",
	displayEmail: "Display email",
	displayPhone: "Display phone",
};

function formatFieldErrors(fields: Record<string, string | string[]>) {
	return Object.entries(fields).map(([k, v]) => {
		const label = FIELD_LABELS[k] ?? k;
		const msg = Array.isArray(v) ? v[0] : v;
		return `${label}: ${msg}`;
	});
}

export default function EditProfileDialog({ initial }: { initial: ProfileForm }) {
	const [open, setOpen] = React.useState(false);
	const [form, setForm] = React.useState<ProfileForm>(initial);
	const [busy, setBusy] = React.useState(false);

	const set = <K extends keyof ProfileForm>(k: K, v: ProfileForm[K]) => setForm((f) => ({ ...f, [k]: v }));

	const onSave = async () => {
		setBusy(true);
		try {
			const payload: PatchProfile = {
				bio: form.bio,
				location: form.location,
				bannerColor: form.bannerColor,
				displayName: form.displayName,
				displayEmail: form.displayEmail,
				displayPhone: form.displayPhone,
			};

			if (form.displayName) {
				if (form.firstName !== undefined && !isOnlyDots(form.firstName)) {
					payload.firstName = form.firstName;
				}
				if (form.lastName !== undefined && !isOnlyDots(form.lastName)) {
					payload.lastName = form.lastName;
				}
			}

			await patchMe(payload);
			toast.success("Profile updated");
			setOpen(false);
			window.location.reload();
		} catch (e) {
			if (isApiError(e)) {
				if (e.code === "VALIDATION_FAILED" && e.fields) {
					const messages = formatFieldErrors(e.fields as Record<string, string | string[]>);
					toast.error(
						<div>
							<div className="font-medium">Please fix the following:</div>
							<ul className="list-disc pl-5 mt-1">
								{messages.map((m, i) => <li key={i}>{m}</li>)}
							</ul>
						</div>
					);
				} else {
					toast.error(e.message || "Failed to save profile.");
				}
			} else {
				toast.error("Failed to save profile.");
			}
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

				<div className="grid grid-cols-1 md:grid-cols-2 gap-4">
					<div className="grid grid-cols-1 gap-2">
						<Label>First Name</Label>
						<Input value={form.firstName} onChange={e => set("firstName", e.target.value)} placeholder={form.displayName ? "First name" : "..."} disabled={!form.displayName} />
					</div>
					<div className="grid grid-cols-1 gap-2">
						<Label>Last Name</Label>
						<Input value={form.lastName} onChange={e => set("lastName", e.target.value)} placeholder={form.displayName ? "Last name" : "..."} disabled={!form.displayName} />
					</div>
					<div className="grid grid-cols-1 gap-2 md:col-span-2">
						<Label>Bio</Label>
						<Textarea rows={3} value={form.bio} onChange={e => set("bio", e.target.value)} />
					</div>
					<div className="grid grid-cols-1 gap-2">
						<Label>Location</Label>
						<Input value={form.location} onChange={e => set("location", e.target.value)} />
					</div>
					<div className="grid grid-cols-1 gap-2">
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
					<Button variant="outline" onClick={onSave} disabled={busy} className="bg-primary hover:bg-primary-foreground">
						{busy ? "Saving..." : "Save"}
					</Button>
				</DialogFooter>
			</DialogContent>
		</Dialog>
	);
}
