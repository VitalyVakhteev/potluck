import {z} from "zod";
import {toPageIndex, toSort} from "@/lib/utils";

export const CreateFundraiserSchema = z.object({
	title: z.string().min(1, "Title is required").max(80),
	description: z.string().max(500).optional().or(z.literal("")),
	email: z.email("Enter a valid email").max(254).optional().or(z.literal("")),
	phoneNumber: z
		.string()
		.regex(/^\+?[1-9]\d{1,14}$/, "Enter a valid E.164 phone (e.g. +15551234567)")
		.optional()
		.or(z.literal("")),
	lat: z.number("Latitude is required").min(-90).max(90),
	lon: z.number("Longitude is required").min(-180).max(180),
	reward: z.boolean(),
	startsAt: z.date().optional().nullable(),
	endsAt: z.date().optional().nullable(),
});
export type CreateFundraiser = z.infer<typeof CreateFundraiserSchema>;

export type CreateFundraiserResponse = {
	id: string;
	title: string;
	startsAt: string;
	endsAt: string
};

export const FundraiserSummary = z.looseObject({
	id: z.uuid(),
	title: z.string(),
	active: z.boolean(),
	lat: z.number(),
	lon: z.number(),
	startsAt: z.coerce.date(),
	endsAt: z.coerce.date(),
	organizerUsername: z.string(),
});
export type FundraiserSummary = z.infer<typeof FundraiserSummary>;

export const FundraiserDetail = z.looseObject({
	id: z.uuid(),
	title: z.string(),
	description: z.string().nullable().optional(),
	email: z.string().nullable().optional(),
	phone: z.string().nullable().optional(),
	active: z.boolean(),
	reward: z.boolean(),
	lat: z.number(),
	lon: z.number(),
	startsAt: z.coerce.date(),
	endsAt: z.coerce.date(),
	organizerId: z.string(),
	organizerUsername: z.string(),
});
export type FundraiserDetail = z.infer<typeof FundraiserDetail>;

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

export const Page = <T extends z.ZodTypeAny>(item: T) => z.looseObject({
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

export const nearbyParamsSchema = z.object({
	page: z.string().optional(),
	size: z.string().optional(),
	sort: z.string().optional(),
	lat: z.string(),
	lon: z.string(),
	radiusKm: z.string().optional(),
});

export function parseNearby(searchParams: Record<string, string | string[] | undefined>) {
	const sp = Object.fromEntries(Object.entries(searchParams).map(([k, v]) => [k, Array.isArray(v) ? v[0] : v]));
	const parsed = nearbyParamsSchema.safeParse(sp);
	if (!parsed.success) return null;

	const {page, size, sort, lat, lon, radiusKm} = parsed.data;
	return {
		page: toPageIndex(page),
		size: Number(size ?? 20),
		sort: toSort(sort),
		lat: Number(lat),
		lon: Number(lon),
		radiusKm: Number(radiusKm ?? 20),
	};
}

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

export const UserSummary = z.looseObject({
	id: z.uuid(),
	username: z.string(),
	totalPoints: z.number(),
	totalFundraisers: z.number(),
});
export type UserSummary = z.infer<typeof UserSummary>;
export const UserPage = Page(UserSummary);

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
	firstName: z.string().optional().nullable(),
	lastName: z.string().optional().nullable(),
	email: z.string(),
	phoneNumber: z.string(),
	displayName: z.boolean().default(false),
	displayEmail: z.boolean().default(false),
	displayPhone: z.boolean().default(false),
});
export type SessionUser = z.infer<typeof UserDetail>;