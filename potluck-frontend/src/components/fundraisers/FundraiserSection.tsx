import Link from "next/link";

export async function Section({title, href, children,}: {
	title: string;
	href: string;
	children: React.ReactNode;
}) {
	return (
		<section className="mt-8">
			<div className="flex items-baseline justify-between mb-3">
				<h2 className="text-xl font-semibold">{title}</h2>
				<Link href={href} className="text-sm text-primary-foreground dark:text-primary hover:underline">See all
					→</Link>
			</div>
			{children}
		</section>
	);
}
