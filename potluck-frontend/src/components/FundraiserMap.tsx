"use client";

export default function FundraiserMap({ lat, lon }: { lat: number; lon: number }) {
	const url = new URL("https://www.google.com/maps");
	url.pathname = "/embed/v1/view"; // Embed API
	const q = `${lat},${lon}`;
	const simple = `https://maps.google.com/maps?q=${encodeURIComponent(q)}&z=14&output=embed`;

	return (
		<iframe
			title="Fundraiser Location"
			src={simple}
			loading="lazy"
			referrerPolicy="no-referrer-when-downgrade"
			className="w-full aspect-[16/9]"
		/>
);
}
