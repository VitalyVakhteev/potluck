import FundraiserListPage from "@/components/fundraisers/FundraiserListPage";
import {FundraisersApi} from "@/lib/api/fundraisers.server";
import {toPageIndex, toSort} from "@/lib/utils";

export const dynamic = "force-dynamic";

export default async function ByUserPage({params, searchParams,}: {
	params: { username: string };
	searchParams: { page?: string; size?: string; sort?: string };
}) {
	const param = await searchParams;
	const userParam = await params;
	const page = toPageIndex(param.page);
	const size = Number(param.size ?? 20);
	const sort = toSort(param.sort);
	const data = await FundraisersApi.byUser(userParam.username, {page, size, sort});

	return <FundraiserListPage title={`Fundraisers by ${userParam.username}`} page={data} basePath={`/fundraisers/user/${userParam.username}`}/>;
}