import { Suspense } from "react";
import { FundraisersApi } from "@/lib/api/fundraisers";
import NearbyClient from "@/app/(public)/NearbyClient";
import FundraiserList from "@/components/FundraiserList";
import SkeletonRow from "@/components/SkeletonRow";
import Link from "next/link";
import { getSession } from "@/lib/session";

async function Section({title, href, children,}: {
	title: string;
	href: string;
	children: React.ReactNode;
}) {
	return (
		<section className="mt-8">
			<div className="flex items-baseline justify-between mb-3">
				<h2 className="text-xl font-semibold">{title}</h2>
				<Link href={href} className="text-sm text-primary-foreground dark:text-primary hover:underline">See all →</Link>
			</div>
			{children}
		</section>
	);
}

export default async function PublicHome() {
	const user = await getSession();
	const [recent, soon, ending] = await Promise.all([
		FundraisersApi.startingSoon(0, 10),
		FundraisersApi.endingSoon(0, 10),
		FundraisersApi.recent(0, 10),
	]);
	const favoritesPromise = user ? await FundraisersApi.favorites(0, 10) : null;
	const feedPromise = user ? await FundraisersApi.feedMe(0, 10) : null;
	const [favorites, feed] = await Promise.all([favoritesPromise, feedPromise]);

	return (
		<main className="flex flex-col ml-4 mr-4 px-4 py-6">
			<h1 className="text-2xl font-bold tracking-tight">Fundraisers</h1>

			{user && (
				<>
					<Section title="Follower Activity" href="/fundraisers/feed/me">
						<FundraiserList items={feed?.content ?? []} initialShow={4} emptyText="No recent activity." />
					</Section>

					<Section title="Favorites" href="/users/favorites">
						<FundraiserList items={favorites?.content ?? []} initialShow={4} emptyText="No favorites yet." />
					</Section>
				</>
			)}

			<Section title="Nearby" href="/fundraisers/near">
				<Suspense fallback={<SkeletonRow count={4} />}>
					<NearbyClient initialShow={4} />
				</Suspense>
			</Section>

			<Section title="Starting Soon" href="/fundraisers/starting-soon">
				<FundraiserList items={soon.content} initialShow={4} />
			</Section>

			<Section title="Ending Soon" href="/fundraisers/ending-soon">
				<FundraiserList items={ending.content} initialShow={4} />
			</Section>

			<Section title="Recently Created" href="/fundraisers?sort=createdAt,desc">
				<FundraiserList items={recent.content} initialShow={4} />
			</Section>
		</main>
	);
}
