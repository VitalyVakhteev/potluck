import { z } from "zod";

export const FundraiserSummary = z.looseObject({
	id: z.uuid(),
	title: z.string(),
	active: z.boolean(),
	lat: z.number().nullable().optional(),
	lon: z.number().nullable().optional(),
	startsAt: z.date().nullable().optional(),
	endsAt: z.date().nullable().optional(),
});
export type FundraiserSummary = z.infer<typeof FundraiserSummary>;

export const UserDetail = z.looseObject({
	id: z.uuid(),
	username: z.string(),
	totalPoints: z.number(),
	role: z.string(),
});
export type SessionUser = z.infer<typeof UserDetail>;

const SortMeta = z.looseObject({
	empty: z.boolean(),
	sorted: z.boolean(),
	unsorted: z.boolean(),
});

const PageableMeta = z.looseObject({
	pageNumber: z.number(),
	pageSize: z.number(),
	sort: SortMeta,
	offset: z.number(),
	paged: z.boolean(),
	unpaged: z.boolean(),
});

export const Page = <T extends z.ZodTypeAny>(item: T) =>
	z.looseObject({
		content: z.array(item),
		pageable: PageableMeta,
		last: z.boolean(),
		totalElements: z.number(),
		totalPages: z.number(),
		size: z.number(),
		number: z.number(),
		first: z.boolean(),
		numberOfElements: z.number(),
		sort: SortMeta,
		empty: z.boolean(),
	});

export const FundraiserPage = Page(FundraiserSummary);
