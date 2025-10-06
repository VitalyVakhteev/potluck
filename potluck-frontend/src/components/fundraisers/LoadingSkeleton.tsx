import SkeletonRow from "@/components/fundraisers/SkeletonRow";

export default function LoadingSkeleton() {
	return (
		<main className="flex flex-col ml-4 mr-4 px-4 py-6">
			<div className="h-7 w-40 bg-muted rounded mb-4 animate-pulse" />
			<SkeletonRow count={20} />
		</main>
	);
}
