import {getSession} from "@/lib/api/session";
import {FundraisersApi} from "@/lib/api/fundraisers.server";
import Link from "next/link";
import {Badge} from "@/components/ui/badge";
import {Button} from "@/components/ui/button";
import {Separator} from "@/components/ui/separator";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";
import dynamic from "next/dynamic";

const FundraiserMap = dynamic(() => import("@/components/FundraiserMap"));

export default async function FundraiserPage({params}: { params: { id: string } }) {
	const viewer = await getSession();
	const param = await params;
	const id = param.id;

	if (!z.uuid().safeParse(id).success) return (
		<main className="flex flex-col h-screen items-center justify-center gap-4">
			<h1 className="text-6xl font-semibold">404</h1>
			<h2>Fundraiser not found</h2>
			<p className="text-muted-foreground mt-2">The requested fundraiser does not exist.</p>
			<Link href="/" className="mt-4">
				<Button variant="outline">Back</Button>
			</Link>
		</main>
	);

	const fundraiser = await FundraisersApi.byId(param.id);

	if (!fundraiser) {
		return (
			<main className="flex flex-col h-screen items-center justify-center gap-4">
				<h1 className="text-6xl font-semibold">404</h1>
				<h2>Fundraiser not found</h2>
				<p className="text-muted-foreground mt-2">The requested fundraiser does not exist.</p>
				<Link href="/" className="mt-4">
					<Button variant="outline">Back</Button>
				</Link>
			</main>
		);
	}

	const isOwner = viewer?.id === fundraiser.organizerId;
	const initials = fundraiser.organizerUsername?.[0]?.toUpperCase() ?? "U";

	return (
		<main className="flex flex-col gap-6 px-4 md:px-6 py-6">
			<div className="flex items-start justify-between gap-4">
				<div className="flex flex-col">
					<h1 className="text-2xl md:text-3xl font-bold">{fundraiser.title}</h1>
					<div className="mt-2 flex items-center gap-2">
						<Badge variant={fundraiser.active ? "default" : "destructive"}
							   className="text-foreground dark:text-primary-foreground">
							{fundraiser.active ? "Active" : "Inactive"}
						</Badge>
						{fundraiser.reward && <Badge variant="outline">Points Available</Badge>}
					</div>
				</div>

				{isOwner && (
					<EditFundraiserDialog
						initial={{
							id: fundraiser.id,
							title: fundraiser.title,
							description: fundraiser.description ?? "",
							email: fundraiser.email ?? "",
							phoneNumber: fundraiser.phone ?? "",
							lat: fundraiser.lat,
							lon: fundraiser.lon,
							reward: fundraiser.reward,
							startsAt: fundraiser.startsAt,
							endsAt: fundraiser.endsAt,
						}}
					/>
				)}
			</div>

			<div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
				<section className="lg:col-span-2 flex flex-col gap-6">
					<div className="rounded-xl overflow-hidden border">
						<FundraiserMap lat={fundraiser.lat} lon={fundraiser.lon}/>
					</div>
				</section>

				<aside className="lg:col-span-1">
					<div className="rounded-xl border p-4 flex flex-col gap-3">
						<div className="flex items-center justify-between">
							<h2 className="text-lg font-semibold">Details</h2>
							<FavoriteStar
								fundraiserId={fundraiser.id}
								disabled={!viewer}
							/>
						</div>
						<Separator/>
						<div className="text-sm">
							<div><span
								className="font-semibold">Starts:</span> {new Date(fundraiser.startsAt).toLocaleString()}
							</div>
							<div><span
								className="font-semibold">Ends:</span> {new Date(fundraiser.endsAt).toLocaleString()}
							</div>
							<div className="mt-2"><span
								className="font-semibold">Email:</span> {fundraiser.email ?? "—"}</div>
							<div><span
								className="font-semibold">Phone Number:</span> {fundraiser.phone ?? "Not Provided"}
							</div>
							<div className="mt-2">
								<span
									className="font-semibold">Location:</span> {fundraiser.lat.toFixed(5)}, {fundraiser.lon.toFixed(5)}
							</div>
						</div>
					</div>

					<div className="flex items-start gap-4 mt-8">
						<div className="rounded-xl border p-4 w-full h-full">
							<Avatar className="h-12 w-12">
								<AvatarImage className="bg-primary" src={undefined} alt={fundraiser.organizerUsername}/>
								<AvatarFallback
									className="font-semibold bg-primary dark:bg-zinc-700">{initials}</AvatarFallback>
							</Avatar>

							<div className="flex-1">
								<div className="flex items-center gap-2">
									<Link
										href={`/users/${encodeURIComponent(fundraiser.organizerUsername)}`}
										className="font-semibold hover:underline mt-4"
									>
										{fundraiser.organizerUsername}
									</Link>
								</div>
								<Separator className="my-3"/>
								<p className="text-sm text-muted-foreground whitespace-pre-line">
									{fundraiser.description?.trim() || "No description provided."}
								</p>
							</div>
						</div>
					</div>
				</aside>
			</div>
		</main>
	);
}

import EditFundraiserDialog from "./_EditFundraiserDialog";
import FavoriteStar from "@/app/(public)/fundraisers/[id]/FavoriteStar";
import {notFound} from "next/navigation";
import {z} from "zod";
