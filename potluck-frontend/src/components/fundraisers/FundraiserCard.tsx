"use client";

import Link from "next/link";
import type { z } from "zod";
import { FundraiserSummary } from "@/lib/api/schemas";

type FundraiserSummaryT = z.infer<typeof FundraiserSummary>;

export default function FundraiserCard({ fundraiser }: { fundraiser: FundraiserSummaryT }) {
	return (
		<div className="rounded-2xl border p-4 hover:shadow-sm transition">
			<div className="flex items-center justify-between mb-2">
				<h3 className="font-medium line-clamp-1">{fundraiser.title}</h3>
				{/* Todo: see if this looks good, otherwise switch to shadcn badges */}
				{fundraiser.active ? (
					<span className="text-xs px-2 py-0.5 rounded-full bg-green-100 text-green-700 dark:bg-green-900/40 dark:text-green-300">
						Active
					</span>
				) : (
					<span className="text-xs px-2 py-0.5 rounded-full bg-zinc-100 text-zinc-700 dark:bg-zinc-800 dark:text-zinc-300">
						Inactive
					</span>
				)}
			</div>

			<div className="text-sm text-muted-foreground space-y-1 mb-3">
				{fundraiser.startsAt && <div>Starts: {new Date(fundraiser.startsAt).toLocaleString()}</div>}
				{fundraiser.endsAt && <div>Ends: {new Date(fundraiser.endsAt).toLocaleString()}</div>}
			</div>

			<Link
				href={`/fundraisers/${fundraiser.id}`}
				className="text-sm font-medium text-primary hover:underline"
			>
				View
			</Link>
		</div>
	);
}
