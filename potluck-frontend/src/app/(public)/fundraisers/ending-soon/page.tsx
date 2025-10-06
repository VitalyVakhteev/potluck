import FundraiserListPage from "@/components/fundraisers/FundraiserListPage";
import { FundraisersApi } from "@/lib/api/fundraisers";
import { toPageIndex } from "@/lib/utils";

export const dynamic = "force-dynamic";

export default async function EndingPage({searchParams,}: {
	searchParams: { page?: string };
}) {
	const params = await searchParams;
	const pageIndex = toPageIndex(params.page);
	const data = await FundraisersApi.endingSoon({page: pageIndex});
	return <FundraiserListPage title="Ending Soon" page={data} basePath="/fundraisers/ending-soon" />;
}
