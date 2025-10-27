import {apiGetClient} from "@/lib/api/api.client";
import {FundraiserDetail, FundraiserPage} from "@/lib/api/schemas";
import {PAGE_SIZE} from "@/lib/utils";
import {apiGetServer} from "@/lib/api/api.server";

type ListOpts = {
	page?: number;
	size?: number;
	sort?: string;
};

export const FundraisersApi = {
	byId: (id: string) =>
		apiGetServer(`/api/fundraisers/${encodeURIComponent(id)}`, FundraiserDetail),

	feedMe: ({page = 0, size = PAGE_SIZE, sort = "createdAt,DESC"}: ListOpts = {}) =>
		apiGetServer(`/api/fundraisers/feed/me?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, FundraiserPage),

	myFavorites: ({page = 0, size = PAGE_SIZE, sort = "createdAt,DESC"}: ListOpts = {}) =>
		apiGetServer(`/api/users/favorites?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, FundraiserPage),

	favorites: (username: string, {page = 0, size = PAGE_SIZE, sort = "createdAt,DESC"}: ListOpts = {}) =>
		apiGetServer(`/api/users/favorites/${encodeURIComponent(username)}?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, FundraiserPage),

	recent: ({page = 0, size = PAGE_SIZE, sort = "createdAt,DESC"}: ListOpts = {}) =>
		apiGetServer(`/api/fundraisers?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, FundraiserPage),

	startingSoon: ({page = 0, size = PAGE_SIZE, sort = "createdAt,DESC"}: ListOpts = {}) =>
		apiGetServer(`/api/fundraisers/starting-soon?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, FundraiserPage),

	endingSoon: ({page = 0, size = PAGE_SIZE, sort = "createdAt,DESC"}: ListOpts = {}) =>
		apiGetServer(`/api/fundraisers/ending-soon?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, FundraiserPage),

	byUser: (organizerUsername: string, {page = 0, size = PAGE_SIZE, sort = "createdAt,DESC"}: ListOpts = {}) =>
		apiGetServer(`/api/fundraisers/organizer/${encodeURIComponent(organizerUsername)}?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, FundraiserPage),

	favoritesByUser: (username: string, {page = 0, size = PAGE_SIZE, sort = "createdAt,DESC"}: ListOpts = {}) =>
		apiGetServer(`/api/users/favorites/${encodeURIComponent(username)}?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, FundraiserPage),

	search: (q: string, {page = 0, size = PAGE_SIZE, sort = "createdAt,DESC"}: ListOpts = {}) =>
		apiGetServer(`/api/fundraisers/search?q=${encodeURIComponent(q)}&page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, FundraiserPage),

	near: ({page = 0, size = PAGE_SIZE, sort = "createdAt,DESC", lat, lon, radiusKm}: ListOpts & {
		lat: number;
		lon: number;
		radiusKm: number
	}) =>
		apiGetClient(`/next-api/fundraisers/near?page=${page}&size=${size}&sort=${encodeURIComponent(sort)}&lat=${lat}&lon=${lon}&radiusKm=${radiusKm}`, FundraiserPage),
};