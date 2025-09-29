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

export type Role = "SEEKER" | "ORGANIZER";

export const LoginSchema = z.object({
	username: z.string().min(3, "Username must be at least 3 characters."),
	password: z.string().min(8, "Password must be at least 8 characters."),
});

export const SignupSchema = z.object({
	username: z.string().min(3, "Username must be at least 3 characters."),
	password: z.string().min(8, "Password must be at least 8 characters."),
	email: z.email("Enter a valid email."),
	phone: z.string().min(8, "Enter a valid phone number."),
	role: z.enum(["SEEKER", "ORGANIZER"]),
});

export const UserDetail = z.looseObject({
	id: z.uuid(),
	role: z.string(),
	username: z.string(),
	bio: z.string().optional().nullable(),
	location: z.string().optional().nullable(),
	bannerColor: z.string().optional().nullable(),
	totalPoints: z.coerce.number().optional().nullable().default(0),
	totalFundraisers: z.coerce.number().optional().nullable().default(0),
	followersCount: z.number().optional().nullable().default(0),
	followingCount: z.number().optional().nullable().default(0),
	favoritesCount: z.number().optional().nullable().default(0),
	firstName: z.string(),
	lastName: z.string(),
	email: z.string(),
	phoneNumber: z.string(),
	displayName: z.boolean().default(false),
	displayEmail: z.boolean().default(false),
	displayPhone: z.boolean().default(false),
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