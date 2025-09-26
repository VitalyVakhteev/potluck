import { FundraiserPage } from "@/lib/api/schemas";
import { apiGet } from "@/lib/api";

export const FundraisersApi = {
	favorites: (page = 0, size = 10) =>
		apiGet(`/api/users/favorites?page=${page}&size=${size}`, FundraiserPage, { auth: true }),

	feedMe: (page = 0, size = 10) =>
		apiGet(`/api/fundraisers/feed/me?page=${page}&size=${size}`, FundraiserPage, { auth: true }),

	startingSoon: (page = 0, size = 10) =>
		apiGet(`/api/fundraisers/starting-soon?page=${page}&size=${size}`, FundraiserPage),

	endingSoon: (page = 0, size = 10) =>
		apiGet(`/api/fundraisers/ending-soon?page=${page}&size=${size}`, FundraiserPage),

	recent: (page = 0, size = 10) =>
		apiGet(`/api/fundraisers?page=${page}&size=${size}`, FundraiserPage),
};
