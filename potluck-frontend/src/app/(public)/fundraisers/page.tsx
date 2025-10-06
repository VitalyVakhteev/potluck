import FundraiserListPage from "@/components/fundraisers/FundraiserListPage";
import { FundraisersApi } from "@/lib/api/fundraisers";
import { toPageIndex } from "@/lib/utils";

export const dynamic = "force-dynamic";

export default async function RecentPage({searchParams,}: {
	searchParams: { page?: string };
}) {
	const params = await searchParams;
	const pageIndex = toPageIndex(params.page);
	const data = await FundraisersApi.recent({page: pageIndex});
	return <FundraiserListPage title="Recently Created" page={data} basePath="/fundraisers/" />;
}
