export default function SkeletonRow({ count = 4 }: { count?: number }) {
	return (
		<div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 mt-4">
			{Array.from({ length: count }).map((_, i) => (
				<div
					key={i}
					className="rounded-2xl border p-4 animate-pulse"
					aria-hidden="true"
				>
					<div className="h-4 w-1/2 bg-muted rounded mb-3" />
					<div className="space-y-2">
						<div className="h-3 w-1/2 bg-muted rounded" />
						<div className="h-3 w-1/3 bg-muted rounded" />
					</div>
					<div className="h-4 w-20 bg-muted rounded mt-4" />
				</div>
			))}
		</div>
	);
}