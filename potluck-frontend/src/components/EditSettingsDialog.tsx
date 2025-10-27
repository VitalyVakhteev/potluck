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
import {toast} from "sonner";
import {isApiError} from "@/lib/api/error";
import {postSettings} from "@/lib/api/users";

type SettingsForm = {
	currentPassword: string;
	newPassword: string;
};

export default function EditSettingsDialog({open, onOpenChange,}: {
	open: boolean;
	onOpenChange: (v: boolean) => void
}) {
	const [form, setForm] = React.useState<SettingsForm>({
		currentPassword: "",
		newPassword: "",
	});
	const [busy, setBusy] = React.useState(false);

	const set = <K extends keyof SettingsForm>(k: K, v: SettingsForm[K]) => setForm((f) => ({...f, [k]: v}));

	const onSave = async () => {
		setBusy(true);
		try {
			if (!form.currentPassword || !form.newPassword) {
				toast.error("Please fill in both passwords.");
				return;
			}
			if (form.newPassword.length < 8) {
				toast.error("New password must be at least 8 characters.");
				return;
			}
			if (form.newPassword === form.currentPassword) {
				toast.error("The new password matches the old password.");
				return;
			}
			const payload: SettingsForm = {
				currentPassword: form.currentPassword,
				newPassword: form.newPassword,
			};

			await postSettings(payload);
			toast.success("Password updated!");
			onOpenChange(false);
			window.location.reload();
		} catch (e) {
			if (isApiError(e)) {
				if (e.code === "CURRENT_PASSWORD_INCORRECT") {
					toast.error("The current password entered is incorrect.");
				} else if (e.code === "PASSWORD_UNCHANGED") {
					toast.error("The new password matches the old password.");
				} else if (e.code === "PASSWORD_TOO_SIMILAR") {
					toast.error("The new password is too similar to your username or email.");
				} else {
					toast.error(e.message || "Failed to save new password.");
				}
			} else {
				toast.error("Failed to save new password.");
			}
		} finally {
			setBusy(false);
		}
	};

	return (
		<Dialog open={open} onOpenChange={onOpenChange}>
			<DialogContent>
				<DialogHeader>
					<DialogTitle>Edit Settings</DialogTitle>
					<DialogDescription>You can update your password here.</DialogDescription>
				</DialogHeader>

				<div className="grid grid-cols-1 gap-4">
					<div className="grid grid-cols-1 gap-2">
						<Label>Current Password</Label>
						<Input
							type="password"
							value={form.currentPassword}
							onChange={e => set("currentPassword", e.target.value)}
						/>
					</div>
					<div className="grid grid-cols-1 gap-2">
						<Label>New Password</Label>
						<Input
							type="password"
							value={form.newPassword}
							onChange={e => set("newPassword", e.target.value)}
						/>
					</div>
				</div>

				<DialogFooter>
					<Button variant="secondary" onClick={() => onOpenChange(false)} disabled={busy}>Cancel</Button>
					<Button variant="outline" onClick={onSave} disabled={busy}
							className="bg-primary hover:bg-primary-foreground">
						{busy ? "Saving..." : "Save"}
					</Button>
				</DialogFooter>
			</DialogContent>
		</Dialog>
	);
}
