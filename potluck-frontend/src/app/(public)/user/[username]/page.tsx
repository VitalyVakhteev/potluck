import { getSession } from "@/lib/session";
import { UserDetail, FundraiserPage, FundraiserSummary } from "@/lib/api/schemas";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

import FundraiserList from "@/components/FundraiserList";
import SkeletonRow from "@/components/SkeletonRow";

import FollowButton from "@/components/FollowButton";
import EditProfileDialog from "@/components/EditProfileDialog";
import {Button} from "@/components/ui/button";
import Link from "next/link";

export const dynamic = "force-dynamic";

async function fetchUser(username: string) {
	const res = await fetch(`${process.env.NEXT_PUBLIC_APP_URL ?? ""}/next-api/users/${encodeURIComponent(username)}`, {
		cache: "no-store",
	});
	if (!res.ok) return null;
	const json = await res.json();
	const parsed = UserDetail.safeParse(json);
	return parsed.success ? parsed.data : null;
}

async function fetchFavorites(username: string) {
	try {
		const res = await fetch(`${process.env.NEXT_PUBLIC_APP_URL ?? ""}/next-api/favorites/u/${encodeURIComponent(username)}`, { cache: "no-store" });
		if (!res.ok) return { content: [], empty: true };
		const json = await res.json();
		const parsed = FundraiserPage.safeParse(json);
		return parsed.success ? parsed.data : { content: [], empty: true };
	} catch { return { content: [], empty: true }; }
}

async function fetchFundraisers(username: string) {
	try {
		const res = await fetch(`${process.env.NEXT_PUBLIC_APP_URL ?? ""}/next-api/fundraisers/u/${encodeURIComponent(username)}`, { cache: "no-store" });
		if (!res.ok) return { content: [], empty: true };
		const json = await res.json();
		const parsed = FundraiserPage.safeParse(json);
		return parsed.success ? parsed.data : { content: [], empty: true };
	} catch { return { content: [], empty: true }; }
}

export default async function ProfilePage({ params }: { params: { username: string } }) {
	const viewer = await getSession();
	const param = await params;
	const username = param.username;
	const user = await fetchUser(username);
	if (!user) {
		return (
			<main className="flex flex-col h-screen items-center justify-center gap-4">
				<h1 className="text-6xl font-semibold">404</h1>
				<h2>User not found</h2>
				<p className="text-muted-foreground mt-2">The user &#34;{username}&#34; does not exist.</p>
				<Link href="/" className="mt-4">
					<Button variant="outline">
						Back
					</Button>
				</Link>
			</main>
		);
	}

	const isSelf = viewer?.id === user.id;
	const isOrganizer = user.role === "ORGANIZER";
	const banner = user.bannerColor || "hsl(var(--primary))";

	const [favorites, fundraisers] = await Promise.all([
		fetchFavorites(user.username),
		isOrganizer ? fetchFundraisers(user.username) : Promise.resolve({ content: [], empty: true }),
	]);

	const initials = user.username?.[0]?.toUpperCase() ?? "U";
	const nameShown = user.displayName && (user.firstName || user.lastName);
	const emailShown = user.displayEmail && !!user.email;
	const phoneShown = user.displayPhone && !!user.phoneNumber;

	return (
		<main className="flex flex-col md:mx-6 mx-2 md:px-6 px-2 py-6">
			<div
				className="w-full h-40 md:h-56 rounded-xl bg-muted"
				// style={{ background: banner }}
			/>

			<div className="-mt-12 md:-mt-24 flex flex-row gap-4 items-end">
				<div className="relative">
					<div className="h-24 w-24 md:h-32 md:w-32 rounded-full ring-4 ring-background overflow-hidden bg-background">
						<Avatar className="h-full w-full">
							<AvatarImage src={undefined} alt={user.username} />
							<AvatarFallback className="text-xl md:text-2xl font-semibold bg-primary dark:bg-zinc-700">
								{initials}
							</AvatarFallback>
						</Avatar>
					</div>
				</div>

				<div className="flex-1 flex flex-col md:flex-row md:items-center md:justify-between gap-3">
					<div>
						<div className="flex items-center gap-3">
							<h1 className="text-2xl md:text-3xl font-bold">{user.username}</h1>
							<Badge variant="secondary" className="uppercase bg-primary-foreground">{user.role}</Badge>
						</div>

						<div className="text-sm text-muted-foreground mt-8 flex gap-4">
							<span><strong>{user.followersCount ?? 0}</strong> Followers</span>
							<span><strong>{user.followingCount ?? 0}</strong> Following</span>
							<span><strong>{user.favoritesCount ?? 0}</strong> Favorites</span>
						</div>
					</div>

					<div className="flex flex-col md:items-end gap-1 mr-4 -mb-4">
						<div className="mb-4">
							<div className="text-sm">
								<span className="font-semibold">Total Points:</span> {user.totalPoints ?? 0}
							</div>
							{isOrganizer && (
								<div className="text-sm">
									<span className="font-semibold">Fundraisers:</span> {user.totalFundraisers ?? 0}
								</div>
							)}
							{nameShown && (
								<div className="text-sm">
									<span className="font-semibold">Name:</span> {user.firstName} {user.lastName}
								</div>
							)}
							{emailShown && (
								<div className="text-sm">
									<span className="font-semibold">Email:</span> {user.email}
								</div>
							)}
							{phoneShown && (
								<div className="text-sm">
									<span className="font-semibold">Phone:</span> {user.phoneNumber}
								</div>
							)}
							{!!user.location && (
								<div className="text-sm">
									<span className="font-semibold">Location:</span> {user.location}
								</div>
							)}
						</div>

						<div className="mt-4">
							{isSelf ? (
								<EditProfileDialog
									initial={{
										firstName: user.firstName ?? "",
										lastName: user.lastName ?? "",
										bio: user.bio ?? "",
										location: user.location ?? "",
										bannerColor: user.bannerColor ?? "",
										displayName: user.displayName ?? false,
										displayEmail: user.displayEmail ?? false,
										displayPhone: user.displayPhone ?? false,
									}}
								/>
							) : (
								<FollowButton
									viewerId={viewer?.id ?? null}
									viewedUserId={user.id}
									initialState={"unknown"}
								/>
							)}
						</div>
					</div>
				</div>
			</div>

			<div className="mt-6 grid grid-cols-1 lg:grid-cols-3 gap-6">
				<section className="lg:col-span-1">
					<h2 className="text-lg font-semibold mb-2">About</h2>
					<p className="text-sm text-muted-foreground">
						{user.bio?.trim() ? user.bio : "This user hasn't set a bio."}
					</p>
				</section>
			</div>

			<div className="mt-6 flex flex-col">
				<section className="lg:col-span-2">
					<h2 className="text-lg font-semibold mb-2">Favorites</h2>
					{favorites.content?.length ? (
						<FundraiserList items={favorites.content} initialShow={6} emptyText="No favorites yet." />
					) : (
						<p className="text-sm text-muted-foreground">No favorites yet.</p>
					)}

					<div className="mt-6">
						<h2 className="text-lg font-semibold mb-2">Fundraisers</h2>
						{isOrganizer ? (
							fundraisers.content?.length ? (
								<FundraiserList items={fundraisers.content} initialShow={6} emptyText="No fundraisers yet." />
							) : (
								<p className="text-sm text-muted-foreground">No fundraisers yet.</p>
							)
						) : (
							<p className="text-sm text-muted-foreground">No fundraisers yet.</p>
						)}
					</div>
				</section>
			</div>
		</main>
	);
}
