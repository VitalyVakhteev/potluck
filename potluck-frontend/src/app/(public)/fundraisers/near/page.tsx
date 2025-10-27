import FundraiserListPage from "@/components/fundraisers/FundraiserListPage";
import { near } from "@/lib/near";
import EmptyState from "@/components/fundraisers/EmptyState";

const toPageIndex = (v?: string) => Math.max(0, Number(v ?? 0) | 0);
const toSize = (v?: string) => Math.min(100, Math.max(1, Number(v ?? 20) | 0));

export const dynamic = "force-dynamic";

export default async function NearListPage({
											   searchParams,
										   }: {
	searchParams: { lat?: string; lon?: string; radiusKm?: string; page?: string; size?: string };
}) {
	const param = await searchParams;
	const lat = Number(param.lat);
	const lon = Number(param.lon);
	const radiusKm = Number(param.radiusKm ?? 20);
	const page = toPageIndex(param.page);
	const size = toSize(param.size);

	if (!Number.isFinite(lat) || !Number.isFinite(lon)) {
		return (
			<div className="mt-4 ml-8">
				<h1 className="text-2xl font-bold tracking-tight mb-4">Nearby Fundraisers</h1>
				<EmptyState text="No nearby fundraisers yet."/>
			</div>
		);
	}

	const data = await near({ lat, lon, radiusKm }, { page, size });

	const basePath =
		`/fundraisers/near?lat=${encodeURIComponent(lat)}` +
		`&lon=${encodeURIComponent(lon)}` +
		`&radiusKm=${encodeURIComponent(radiusKm)}`;

	return (
		<FundraiserListPage
			title="Nearby fundraisers"
			page={data}
			basePath={basePath}
		/>
	);
}