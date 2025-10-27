"use client";
import FundraiserCard from "@/components/fundraisers/FundraiserCard";
import type {z} from "zod";
import {FundraiserPage as FundraiserPageSchema} from "@/lib/api/schemas";

type FundraiserPageT = z.infer<typeof FundraiserPageSchema>;

export default function FundraiserGrid({page}: { page: FundraiserPageT }) {
	if (!page.content?.length) {
		return <p className="mt-4 text-sm text-muted-foreground">No fundraisers found.</p>;
	}

	return (
		<div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 mt-4">
			{page.content.map((f) => (
				<FundraiserCard key={f.id} fundraiser={f}/>
			))}
		</div>
	);
}