"use client";

import * as React from "react";
import {useRouter} from "next/navigation";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {toast} from "sonner";

import {Button} from "@/components/ui/button";
import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card";
import {Input} from "@/components/ui/input";
import {Label} from "@/components/ui/label";
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover";
import {Calendar} from "@/components/ui/calendar";
import {ChevronDownIcon} from "lucide-react";

import {CreateFundraiserSchema, type CreateFundraiser} from "@/lib/api/schemas";
import {combineLocalDateTime} from "@/lib/utils";
import {createFundraiser} from "@/lib/api/fundraisers.client";
import {isApiError} from "@/lib/api/error";
import Link from "next/link";

function Required({children}: { children: React.ReactNode }) {
	return <span className="after:ml-0.5 after:text-destructive after:content-['*']">{children}</span>;
}

export default function NewFundraiserPage() {
	const router = useRouter();

	const {
		register,
		handleSubmit,
		control,
		setValue,
		formState: {errors, isSubmitting},
		watch,
	} = useForm<CreateFundraiser>({
		resolver: zodResolver(CreateFundraiserSchema),
		defaultValues: {
			title: "",
			description: "",
			email: "",
			phoneNumber: "",
			lat: undefined as unknown as number,
			lon: undefined as unknown as number,
			reward: false,
			startsAt: null,
			endsAt: null,
		},
		mode: "onBlur",
	});

	const [openStarts, setOpenStarts] = React.useState(false);
	const [openEnds, setOpenEnds] = React.useState(false);
	const [startsDate, setStartsDate] = React.useState<Date | undefined>();
	const [endsDate, setEndsDate] = React.useState<Date | undefined>();
	const [startsTime, setStartsTime] = React.useState<string>(""); // "HH:mm" or "HH:mm:ss"
	const [endsTime, setEndsTime] = React.useState<string>("");

	async function useMyLocation() {
		if (!("geolocation" in navigator)) {
			toast.error("Geolocation not available in this browser.");
			return;
		}
		navigator.geolocation.getCurrentPosition(
			(pos) => {
				const {latitude, longitude} = pos.coords;
				setValue("lat", Number(latitude), {shouldValidate: true, shouldDirty: true});
				setValue("lon", Number(longitude), {shouldValidate: true, shouldDirty: true});
			},
			(err) => toast.error(err.message || "Failed to get location."),
			{enableHighAccuracy: true, maximumAge: 10_000, timeout: 10_000}
		);
	}

	const onSubmit = handleSubmit(async (data) => {
		try {
			// Compose date+time if provided
			const starts = startsDate ? combineLocalDateTime(startsDate, startsTime || "00:00:00") : null;
			const ends = endsDate ? combineLocalDateTime(endsDate, endsTime || "00:00:00") : null;

			if (starts && ends && !(ends > starts)) {
				toast.error("End must be after start.");
				return;
			}

			const payload: CreateFundraiser = {
				...data,
				description: (data.description || "").trim() || undefined,
				email: (data.email || "").trim() || undefined,
				phoneNumber: (data.phoneNumber || "").trim() || undefined,
				startsAt: starts,
				endsAt: ends,
			};

			const res = await createFundraiser(payload);
			toast.success("Fundraiser created!");
			router.replace(`/fundraisers/${res.id}`);
			router.refresh();
		} catch (e: unknown) {
			if (isApiError(e)) {
				if (e.code === "VALIDATION_FAILED" && e.fields) {
					const first = Object.values(e.fields)[0];
					toast.error(Array.isArray(first) ? first[0] : String(first ?? "Invalid input."));
				} else {
					toast.error(e.message || "Failed to create fundraiser.");
				}
			} else {
				toast.error("Failed to create fundraiser.");
			}
		}
	});

	return (
		<div className="min-h-screen grid place-items-center">
			<Card className="w-full max-w-lg">
				<CardHeader>
					<CardTitle>New Fundraiser</CardTitle>
					<CardDescription>Fill out the fields below to make a fundraiser.</CardDescription>
				</CardHeader>

				<CardContent>
					<form onSubmit={onSubmit} noValidate className="flex flex-col gap-6">
						<div className="grid gap-2">
							<Label htmlFor="title"><Required>Title</Required></Label>
							<Input id="title" {...register("title")} placeholder="Potluck for Charity!"/>
							{errors.title && <p className="text-sm text-destructive">{errors.title.message}</p>}
						</div>

						<div className="grid gap-2">
							<Label htmlFor="description">Description</Label>
							<Input id="description" {...register("description")} />
							{errors.description &&
								<p className="text-sm text-destructive">{errors.description.message}</p>}
						</div>

						<div className="grid gap-2">
							<Label htmlFor="email">Email</Label>
							<Input id="email" type="email" placeholder="john@potluck.com" {...register("email")} />
							{errors.email && <p className="text-sm text-destructive">{errors.email.message}</p>}
						</div>

						<div className="grid gap-2">
							<Label htmlFor="phoneNumber">Phone Number</Label>
							<Input id="phoneNumber" type="tel"
								   placeholder="+15551234567" {...register("phoneNumber")} />
							{errors.phoneNumber &&
								<p className="text-sm text-destructive">{errors.phoneNumber.message}</p>}
						</div>

						<div className="grid gap-2 sm:grid-cols-2">
							<div className="grid gap-2">
								<Label htmlFor="lat"><Required>Latitude</Required></Label>
								<Input id="lat" type="number" step="any" {...register("lat", {valueAsNumber: true})} />
								{errors.lat &&
									<p className="text-sm text-destructive">{errors.lat.message as string}</p>}
							</div>
							<div className="grid gap-2">
								<Label htmlFor="lon"><Required>Longitude</Required></Label>
								<Input id="lon" type="number" step="any" {...register("lon", {valueAsNumber: true})} />
								{errors.lon &&
									<p className="text-sm text-destructive">{errors.lon.message as string}</p>}
							</div>
							<div className="sm:col-span-2">
								<Button type="button" variant="outline" onClick={useMyLocation}>Use my location</Button>
							</div>
						</div>

						<div className="flex items-center gap-2">
							<input id="reward" type="checkbox" {...register("reward")} />
							<Label htmlFor="reward">Awards points</Label>
						</div>

						<div className="flex gap-2">
							<div className="flex flex-col gap-2">
								<Label><Required>Starts On</Required></Label>
								<Popover open={openStarts} onOpenChange={setOpenStarts}>
									<PopoverTrigger asChild>
										<Button type="button" variant="outline" className="w-40 justify-between">
											{startsDate ? startsDate.toLocaleDateString() : "Select date"}
											<ChevronDownIcon/>
										</Button>
									</PopoverTrigger>
									<PopoverContent className="w-auto p-0">
										<Calendar
											mode="single"
											selected={startsDate}
											onSelect={(d) => {
												setStartsDate(d ?? undefined);
												setOpenStarts(false);
											}}
										/>
									</PopoverContent>
								</Popover>
							</div>
							<div className="flex flex-col gap-2">
								<Label>Time</Label>
								<Input type="time" step="1" value={startsTime}
									   onChange={(e) => setStartsTime(e.target.value)}/>
							</div>
						</div>

						<div className="flex gap-2">
							<div className="flex flex-col gap-2">
								<Label>Ends On</Label>
								<Popover open={openEnds} onOpenChange={setOpenEnds}>
									<PopoverTrigger asChild>
										<Button type="button" variant="outline" className="w-40 justify-between">
											{endsDate ? endsDate.toLocaleDateString() : "Select date"}
											<ChevronDownIcon/>
										</Button>
									</PopoverTrigger>
									<PopoverContent className="w-auto p-0">
										<Calendar
											mode="single"
											selected={endsDate}
											onSelect={(d) => {
												setEndsDate(d ?? undefined);
												setOpenEnds(false);
											}}
										/>
									</PopoverContent>
								</Popover>
							</div>
							<div className="flex flex-col gap-2">
								<Label>Time</Label>
								<Input type="time" step="1" value={endsTime}
									   onChange={(e) => setEndsTime(e.target.value)}/>
							</div>
						</div>

						<Button type="submit" disabled={isSubmitting} variant="outline"
								className="w-full bg-primary dark:bg-primary-foreground">
							<span className="text-foreground">{isSubmitting ? "Creating..." : "Create"}</span>
						</Button>
					</form>
				</CardContent>

				<CardFooter className="flex-col gap-2">
					<Button asChild variant="link" className="w-full">
						<Link href="/" className="text-primary-foreground dark:text-primary">Back to Home</Link>
					</Button>
				</CardFooter>
			</Card>
		</div>
	);
}