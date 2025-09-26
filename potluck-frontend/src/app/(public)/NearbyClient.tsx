"use client";

import { useEffect, useState } from "react";
import FundraiserList from "@/components/FundraiserList";
import SkeletonRow from "@/components/SkeletonRow";
import EmptyState from "@/components/EmptyState";
import { z } from "zod";
import { FundraiserPage, FundraiserSummary } from "@/lib/api/schemas";

type FundraiserSummaryT = z.infer<typeof FundraiserSummary>;

export default function NearbyClient({ initialShow = 4 }: { initialShow?: number }) {
	const [items, setItems] = useState<FundraiserSummaryT[] | null>(null);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		if (!("geolocation" in navigator)) {
			setItems([]);
			setLoading(false);
			return;
		}

		navigator.geolocation.getCurrentPosition(
			async (pos) => {
				try {
					const res = await fetch("/next-api/fundraisers/near", {
						method: "POST",
						headers: { "content-type": "application/json" },
						body: JSON.stringify({
							lat: pos.coords.latitude,
							lon: pos.coords.longitude,
							radiusKm: 20,
						}),
						credentials: "include",
					});

					if (!res.ok) { setItems([]); return; }

					const ctype = res.headers.get("content-type") ?? "";
					if (!ctype.includes("application/json")) { setItems([]); return; }

					const json = (await res.json()) as unknown;
					const parsed = FundraiserPage.safeParse(json);
					setItems(parsed.success ? parsed.data.content : []);
				} catch {
					setItems([]);
				} finally {
					setLoading(false);
				}
			},
			() => { setItems([]); setLoading(false); },
			{ enableHighAccuracy: false, timeout: 5000 }
		);
	}, []);

	if (loading) return <SkeletonRow count={initialShow} />;

	if (!items || items.length === 0) {
		return <EmptyState text="No nearby fundraisers yet." />;
	}

	return <FundraiserList items={items} initialShow={initialShow} />;
}