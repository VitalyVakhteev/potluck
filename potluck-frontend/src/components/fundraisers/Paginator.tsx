"use client";

import {
	Pagination,
	PaginationContent,
	PaginationItem,
	PaginationLink,
	PaginationNext,
	PaginationPrevious,
} from "@/components/ui/pagination";
import { toPageParam } from "@/lib/utils";

type Props = {
	basePath: string;
	currentIndex: number;
	totalPages: number;
	query?: Record<string, string | number | undefined>;
};

export default function Paginator({ basePath, currentIndex, totalPages, query }: Props) {
	if (totalPages <= 1) return null;

	const current = toPageParam(currentIndex);
	const prev = current > 1 ? current - 1 : 1;
	const next = current < totalPages ? current + 1 : totalPages;

	const makeHref = (page: number) => {
		const params = new URLSearchParams({ ...(query as object), page: String(page) });
		return `${basePath}?${params.toString()}`;
	};

	return (
		<Pagination className="mt-6">
			<PaginationContent>
				<PaginationItem>
					<PaginationPrevious href={makeHref(prev)} aria-disabled={current === 1} />
				</PaginationItem>

				<PaginationItem>
					<PaginationLink href={makeHref(1)} isActive={current === 1}>
						1
					</PaginationLink>
				</PaginationItem>

				{current > 1 && current < totalPages && (
					<PaginationItem>
						<PaginationLink href={makeHref(current)} isActive>
							{current}
						</PaginationLink>
					</PaginationItem>
				)}

				{totalPages > 1 && (
					<PaginationItem>
						<PaginationLink href={makeHref(totalPages)} isActive={current === totalPages}>
							{totalPages}
						</PaginationLink>
					</PaginationItem>
				)}

				<PaginationItem>
					<PaginationNext href={makeHref(next)} aria-disabled={current === totalPages} />
				</PaginationItem>
			</PaginationContent>
		</Pagination>
	);
}
