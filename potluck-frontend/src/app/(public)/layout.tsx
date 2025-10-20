import Navbar from "@/components/Navbar";

export default function PublicLayout({children}: { children: React.ReactNode }) {
	return (
		<>
			{/* I hate this structure... but it's the only way I can think of to fix a navbar bug with it layering under */}
			<div className="sticky z-50">
				<Navbar />
			</div>
			{/* Todo: footer? */}
			<>
				<main>{children}</main>
			</>
		</>
	);
}