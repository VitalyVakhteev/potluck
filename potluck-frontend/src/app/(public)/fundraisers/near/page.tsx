import FundraiserListPage from "@/components/fundraisers/FundraiserListPage";
import { FundraisersApi } from "@/lib/api/fundraisers";
import {parseNearby} from "@/lib/api/schemas";
import GeolocateAndPush from "@/components/GeolocateAndPush";

export const dynamic = "force-dynamic";

export default async function NearbyPage({ searchParams }: { searchParams: Record<string, string | string[] | undefined> }) {
	const opts = parseNearby(searchParams);

	// Todo: throw in input fields to set lat/lon, radius in km to replace a button prompt
	if (!opts) {
		return (
			<main className="flex flex-col ml-4 mr-4 px-4 py-6">
				<h1 className="text-2xl font-bold tracking-tight">Nearby</h1>
				<p className="text-sm text-muted-foreground mt-2">Share your location to see nearby fundraisers.</p>
				<GeolocateAndPush />
			</main>
		);
	}

	const data = await FundraisersApi.near(opts);
	return <FundraiserListPage title="Nearby" page={data} basePath="/fundraisers/near" />;
}