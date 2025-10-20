"use client";

import {useEffect, useMemo, useState} from "react";
import FundraiserList from "@/components/fundraisers/FundraiserList";
import SkeletonRow from "@/components/fundraisers/SkeletonRow";
import EmptyState from "@/components/fundraisers/EmptyState";
import type {z} from "zod";
import {FundraiserSummary} from "@/lib/api/schemas";
import {near} from "@/lib/near";

type FundraiserSummaryT = z.infer<typeof FundraiserSummary>;

export default function NearbyClient({initialShow = 4, defaultRadiusKm = 20}: {
	initialShow?: number;
	defaultRadiusKm?: number;
}) {
	const [items, setItems] = useState<FundraiserSummaryT[] | null>(null);
	const [loading, setLoading] = useState(true);
	const [radiusKm, setRadiusKm] = useState(defaultRadiusKm);

	const ctrlRef = useMemo(() => ({current: new AbortController()}), []);

	useEffect(() => {
		if (!("geolocation" in navigator)) {
			setItems([]);
			setLoading(false);
			return;
		}

		let cancelled = false;

		navigator.geolocation.getCurrentPosition(
			async (pos) => {
				setLoading(true);
				try {
					ctrlRef.current.abort();
					ctrlRef.current = new AbortController();

					const page = await near(
						{
							lat: pos.coords.latitude,
							lon: pos.coords.longitude,
							radiusKm,
						},
						{page: 0, size: initialShow}
					);

					if (!cancelled) setItems(page.content);
				} catch {
					if (!cancelled) setItems([]);
				} finally {
					if (!cancelled) setLoading(false);
				}
			},
			() => {
				setItems([]);
				setLoading(false);
			},
			{enableHighAccuracy: false, timeout: 5000}
		);

		return () => {
			cancelled = true;
			ctrlRef.current.abort();
		};
	}, [radiusKm, initialShow, ctrlRef]);

	if (loading) return <SkeletonRow count={initialShow}/>;

	if (!items || items.length === 0) {
		return <EmptyState text="No nearby fundraisers yet."/>;
	}

	return (
		<>
			{/* Todo: Optional radius control for the home module */}
			{/* Smth like <RadiusChooser value={radiusKm} onChange={setRadiusKm} /> */}
			<FundraiserList items={items} initialShow={initialShow}/>
		</>
	);
}
