import FundraiserListPage from "@/components/fundraisers/FundraiserListPage";
import {FundraisersApi} from "@/lib/api/fundraisers.server";
import {toPageIndex} from "@/lib/utils";

export const dynamic = "force-dynamic";

export default async function StartingPage({searchParams,}: {
	searchParams: { page?: string };
}) {
	const params = await searchParams;
	const pageIndex = toPageIndex(params.page);
	const data = await FundraisersApi.startingSoon({page: pageIndex});
	return <FundraiserListPage title="Starting Soon" page={data} basePath="/fundraisers/starting-soon"/>;
}
