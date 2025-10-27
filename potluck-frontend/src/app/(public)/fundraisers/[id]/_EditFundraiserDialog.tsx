"use client";

import * as React from "react";
import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {toast} from "sonner";
import {Button} from "@/components/ui/button";
import {Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger, DialogFooter} from "@/components/ui/dialog";
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {Switch} from "@/components/ui/switch";
import {patchFundraiser} from "@/lib/api/fundraisers.client";
import {combineLocalDateTime} from "@/lib/utils";

const PatchFundraiserSchema = z.object({
	id: z.uuid(),
	title: z.string().max(80).optional(),
	description: z.string().max(500).optional(),
	email: z.email().max(254).optional().or(z.literal("")),
	phoneNumber: z.string().regex(/^\+?[1-9]\d{1,14}$/).optional().or(z.literal("")),
	lat: z.number().min(-90).max(90).optional(),
	lon: z.number().min(-180).max(180).optional(),
	reward: z.boolean().optional(),
	startsDate: z.string().optional(),
	startsTime: z.string().optional(),
	endsDate: z.string().optional(),
	endsTime: z.string().optional(),
});

type PatchForm = z.infer<typeof PatchFundraiserSchema>;

export default function EditFundraiserDialog({initial}: {
	initial: {
		id: string;
		title: string;
		description: string;
		email: string;
		phoneNumber: string;
		lat: number;
		lon: number;
		reward: boolean;
		startsAt: Date;
		endsAt: Date;
	};
}) {
	const [open, setOpen] = React.useState(false);
	const {
		register,
		handleSubmit,
		formState: {isSubmitting},
	} = useForm<PatchForm>({
		resolver: zodResolver(PatchFundraiserSchema),
		defaultValues: {
			id: initial.id,
			title: initial.title ?? "",
			description: initial.description ?? "",
			email: initial.email ?? "",
			phoneNumber: initial.phoneNumber ?? "",
			lat: initial.lat,
			lon: initial.lon,
			reward: initial.reward,
			startsDate: initial.startsAt ? new Date(initial.startsAt).toISOString().slice(0, 10) : "",
			startsTime: initial.startsAt ? new Date(initial.startsAt).toTimeString().slice(0, 8) : "",
			endsDate: initial.endsAt ? new Date(initial.endsAt).toISOString().slice(0, 10) : "",
			endsTime: initial.endsAt ? new Date(initial.endsAt).toTimeString().slice(0, 8) : "",
		},
	});

	const onSubmit = handleSubmit(async (data) => {
		const startsAt =
			data.startsDate
				? combineLocalDateTime(new Date(data.startsDate), data.startsTime || "00:00:00")
				: null;
		const endsAt =
			data.endsDate
				? combineLocalDateTime(new Date(data.endsDate), data.endsTime || "00:00:00")
				: null;

		if (startsAt && endsAt && !(endsAt > startsAt)) {
			toast.error("End must be after start.");
			return;
		}

		try {
			await patchFundraiser({
				id: data.id,
				title: emptyToUndef(data.title),
				description: emptyToUndef(data.description),
				email: emptyToUndef(data.email),
				phoneNumber: emptyToUndef(data.phoneNumber),
				lat: data.lat,
				lon: data.lon,
				reward: data.reward,
				startsAt: startsAt ? startsAt.toISOString() : undefined,
				endsAt: endsAt ? endsAt.toISOString() : undefined,
			});
			toast.success("Saved!");
			setOpen(false);
			window.location.reload();
		} catch (e: unknown) {
			toast.error("Save failed.");
		}
	});

	return (
		<Dialog open={open} onOpenChange={setOpen}>
			<DialogTrigger asChild><Button variant="outline">Edit</Button></DialogTrigger>
			<DialogContent className="sm:max-w-lg">
				<DialogHeader>
					<DialogTitle>Edit Fundraiser</DialogTitle>
				</DialogHeader>

				<form onSubmit={onSubmit} className="space-y-4">
					<input type="hidden" {...register("id")} />

					<div className="grid gap-2">
						<Label htmlFor="title">Title</Label>
						<Input id="title" {...register("title")} />
					</div>

					<div className="grid gap-2">
						<Label htmlFor="description">Description</Label>
						<Input id="description" {...register("description")} />
					</div>

					<div className="grid gap-2">
						<Label htmlFor="email">Email</Label>
						<Input id="email" type="email" {...register("email")} />
					</div>

					<div className="grid gap-2">
						<Label htmlFor="phoneNumber">Phone</Label>
						<Input id="phoneNumber" type="tel" {...register("phoneNumber")} />
					</div>

					<div className="grid grid-cols-2 gap-3">
						<div className="grid gap-2">
							<Label htmlFor="lat">Latitude</Label>
							<Input id="lat" type="number" step="any" {...register("lat", {valueAsNumber: true})} />
						</div>
						<div className="grid gap-2">
							<Label htmlFor="lon">Longitude</Label>
							<Input id="lon" type="number" step="any" {...register("lon", {valueAsNumber: true})} />
						</div>
					</div>

					<div className="flex items-center gap-2">
						<Switch id="reward" {...register("reward")} />
						<Label htmlFor="reward">Includes a reward</Label>
					</div>

					<div className="grid grid-cols-2 gap-3">
						<div className="grid gap-2">
							<Label>Starts (date)</Label>
							<Input type="date" {...register("startsDate")} />
						</div>
						<div className="grid gap-2">
							<Label>Starts (time)</Label>
							<Input type="time" step="1" {...register("startsTime")} />
						</div>
						<div className="grid gap-2">
							<Label>Ends (date)</Label>
							<Input type="date" {...register("endsDate")} />
						</div>
						<div className="grid gap-2">
							<Label>Ends (time)</Label>
							<Input type="time" step="1" {...register("endsTime")} />
						</div>
					</div>

					<DialogFooter>
						<Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancel</Button>
						<Button type="submit" disabled={isSubmitting}>{isSubmitting ? "Saving..." : "Save"}</Button>
					</DialogFooter>
				</form>
			</DialogContent>
		</Dialog>
	);
}

function emptyToUndef<T extends string | undefined>(s: T) {
	return (typeof s === "string" && s.trim() === "") ? undefined : s;
}
