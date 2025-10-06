import {apiGet} from "@/lib/api/api";
import {CreateFundraiser, FundraiserPage} from "@/lib/api/schemas";
import {PAGE_SIZE} from "@/lib/utils";
import {jsonFetch} from "@/lib/api/error";

type ListOpts = {
	page?: number;
	size?: number;
	sort?: string;
};

export const FundraisersApi = {
	feedMe: ({ page = 0, size = PAGE_SIZE, sort = "createdAt,DESC" }: ListOpts = {}) =>
		apiGet(`/api/fundraisers/feed/me?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, FundraiserPage, {auth: true}),

	favorites: ({ page = 0, size = PAGE_SIZE, sort = "createdAt,DESC" }: ListOpts = {}) =>
		apiGet(`/api/users/favorites?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, FundraiserPage, { auth: true }),

	recent:    ({ page = 0, size = PAGE_SIZE, sort = "createdAt,DESC" }: ListOpts = {}) =>
		apiGet(`/api/fundraisers?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, FundraiserPage),

	startingSoon: ({ page = 0, size = PAGE_SIZE, sort = "createdAt,DESC" }: ListOpts = {}) =>
		apiGet(`/api/fundraisers/starting-soon?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, FundraiserPage),

	endingSoon: ({ page = 0, size = PAGE_SIZE, sort = "createdAt,DESC" }: ListOpts = {}) =>
		apiGet(`/api/fundraisers/ending-soon?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, FundraiserPage),

	byUser: (organizerUsername: string, { page = 0, size = PAGE_SIZE, sort = "createdAt,DESC" }: ListOpts = {}) =>
		apiGet(`/api/fundraisers/organizer/${encodeURIComponent(organizerUsername)}?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, FundraiserPage),

	favoritesByUser: (username: string, { page = 0, size = PAGE_SIZE, sort = "createdAt,DESC" }: ListOpts = {}) =>
		apiGet(`/api/users/favorites/${encodeURIComponent(username)}?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, FundraiserPage),

	near: ({ page = 0, size = PAGE_SIZE, sort = "createdAt,DESC", lat, lon, radiusKm }: ListOpts & { lat: number; lon: number; radiusKm: number }) =>
		apiGet(`/api/fundraisers/near?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}&lat=${lat}&lon=${lon}&radiusKm=${radiusKm}`, FundraiserPage),
};