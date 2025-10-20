import FundraiserListPage from "@/components/fundraisers/FundraiserListPage";
import {FundraisersApi} from "@/lib/api/fundraisers.server";
import {toPageIndex} from "@/lib/utils";

export const dynamic = "force-dynamic";

export default async function FavoritesPage({searchParams,}: {
	searchParams: { page?: string };
}) {
	const params = await searchParams;
	const pageIndex = toPageIndex(params.page);
	const data = await FundraisersApi.myFavorites({page: pageIndex});
	return <FundraiserListPage title="Favorites" page={data} basePath="/fundraisers/favorites"/>;
}
