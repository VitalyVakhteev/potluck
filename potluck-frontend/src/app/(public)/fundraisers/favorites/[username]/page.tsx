import FundraiserListPage from "@/components/fundraisers/FundraiserListPage";
import {FundraisersApi} from "@/lib/api/fundraisers.server";
import {toPageIndex, toSort} from "@/lib/utils";

export const dynamic = "force-dynamic";

export default async function FavoritesUserPage({params, searchParams,}: {
	params: { username: string };
	searchParams: { page?: string; size?: string; sort?: string };
}) {
	const param = await searchParams;
	const userParams = await params;
	const page = toPageIndex(param.page);
	const size = Number(param.size ?? 20);
	const sort = toSort(param.sort);
	const data = await FundraisersApi.favoritesByUser(userParams.username, {page, size, sort});

	return <FundraiserListPage title={`Favorites of ${userParams.username}`} page={data}
							   basePath={`/fundraisers/favorites/${userParams.username}`}/>;
}