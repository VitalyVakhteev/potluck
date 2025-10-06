"use client";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";

export default function GeolocateAndPush() {
	const router = useRouter();

	const useMyLocation = () => {
		if (!("geolocation" in navigator)) return;
		navigator.geolocation.getCurrentPosition(
			(pos) => {
				const lat = pos.coords.latitude.toFixed(6);
				const lon = pos.coords.longitude.toFixed(6);
				const params = new URLSearchParams({ lat, lon, radiusKm: "20", page: "1" });
				router.replace(`/fundraisers/near?${params.toString()}`);
			},
			() => {},
			{ enableHighAccuracy: false, timeout: 5000 }
		);
	};

	return (
		<Button variant="outline" onClick={useMyLocation} className="mt-3 bg-primary hover:bg-primary-foreground">
			Use my location
		</Button>
	);
}
