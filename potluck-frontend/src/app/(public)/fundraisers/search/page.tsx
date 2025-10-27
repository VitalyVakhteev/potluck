import FundraiserListPage from "@/components/fundraisers/FundraiserListPage";
import {FundraisersApi} from "@/lib/api/fundraisers.server";
import {toPageIndex, toSort} from "@/lib/utils";
import EmptyState from "@/components/fundraisers/EmptyState";

export const dynamic = "force-dynamic";

export default async function FundraiserSearchPage({
													   searchParams,
												   }: {
	searchParams: { q?: string; page?: string; size?: string; sort?: string };
}) {
	const q = (searchParams.q ?? "").trim();
	const page = toPageIndex(searchParams.page);
	const size = Number(searchParams.size ?? 20);
	const sort = toSort(searchParams.sort);

	if (!q) {
		const empty = {
			content: [],
			pageable: {
				pageNumber: 0,
				pageSize: size,
				sort: {empty: true, sorted: false, unsorted: true},
				offset: 0,
				paged: true,
				unpaged: false
			},
			last: true, totalElements: 0, totalPages: 0, size, number: 0, first: true, numberOfElements: 0,
			sort: {empty: true, sorted: false, unsorted: true}, empty: true,
		};
		return (
			<div className="mt-4 ml-8">
				<h1 className="text-2xl font-bold tracking-tight mb-4">{`Results for "${q}"`}</h1>
				<EmptyState text="No fundraisers found."/>
			</div>
		);
	}

	const data = await FundraisersApi.search(q, {page, size, sort});
	return (
		<FundraiserListPage
			title={`Results for "${q}"`}
			page={data}
			basePath={`/fundraisers/search?q=${encodeURIComponent(q)}`}
		/>
	);
}
