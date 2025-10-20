import {Suspense} from "react";
import {FundraisersApi} from "@/lib/api/fundraisers.server";
import NearbyClient from "@/components/NearbyClient";
import FundraiserList from "@/components/fundraisers/FundraiserList";
import SkeletonRow from "@/components/fundraisers/SkeletonRow";
import {getSession} from "@/lib/api/session";
import {Section} from "@/components/fundraisers/FundraiserSection";

export const dynamic = "force-dynamic";

export default async function PublicHome() {
	const user = await getSession();

	const [startingSoon, endingSoon, recent] = await Promise.all([
		FundraisersApi.startingSoon({
			page: 0,
			size: 4
		}),
		FundraisersApi.endingSoon({
			page: 0,
			size: 4
		}),
		FundraisersApi.recent({
			page: 0,
			size: 4
		}),
	]);
	const [favorites, feed] = user ? await Promise.all([
		FundraisersApi.myFavorites({
			page: 0,
			size: 4
		}),
		FundraisersApi.feedMe({
			page: 0,
			size: 4
		})]) : [null, null];

	return (
		<main className="flex flex-col ml-4 mr-4 px-4 py-6">
			<h1 className="text-2xl font-bold tracking-tight">Fundraisers</h1>

			{!!user && (
				<>
					<Section title="Follower Activity" href="/fundraisers/feed">
						<FundraiserList items={feed?.content ?? []} initialShow={4} emptyText="No recent activity."/>
					</Section>

					<Section title="Favorites" href="/fundraisers/favorites">
						<FundraiserList items={favorites?.content ?? []} initialShow={4} emptyText="No favorites yet."/>
					</Section>
				</>
			)}

			<Section title="Nearby" href="/fundraisers/near">
				<Suspense fallback={<SkeletonRow count={4}/>}>
					<NearbyClient initialShow={4}/>
				</Suspense>
			</Section>

			<Section title="Starting Soon" href="/fundraisers/starting-soon">
				<FundraiserList items={startingSoon.content} initialShow={4}/>
			</Section>

			<Section title="Ending Soon" href="/fundraisers/ending-soon">
				<FundraiserList items={endingSoon.content} initialShow={4}/>
			</Section>

			<Section title="Recently Created" href="/fundraisers">
				<FundraiserList items={recent.content} initialShow={4}/>
			</Section>
		</main>
	);
}
