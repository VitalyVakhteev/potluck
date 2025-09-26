import FundraiserCard from "./FundraiserCard";
import type { z } from "zod";
import { FundraiserSummary } from "@/lib/api/schemas";

type FundraiserSummaryT = z.infer<typeof FundraiserSummary>;

export default function FundraiserList({items, initialShow = 4, emptyText = "No fundraisers yet."}: {
	items?: FundraiserSummaryT[] | null;
	initialShow?: number;
	emptyText?: string;
}) {
	const list = Array.isArray(items) ? items : [];
	const showCount = Number.isFinite(initialShow) ? Math.max(0, initialShow) : 0;
	const toShow = list.slice(0, showCount);

	if (toShow.length === 0) {
		return <p className="text-sm text-muted-foreground">{emptyText}</p>;
	}

	return (
		<div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
			{toShow.map((f) => (
				<FundraiserCard key={f.id} fundraiser={f} />
			))}
		</div>
	);
}