import {apiGetServer} from "@/lib/api/api.server";
import {PAGE_SIZE} from "@/lib/utils";
import {UserPage} from "@/lib/api/schemas";

type ListOpts = { page?: number; size?: number; sort?: string };

export const UsersApi = {
	search: (q: string, {page = 0, size = PAGE_SIZE, sort = "createdAt,DESC"}: ListOpts = {}) =>
		apiGetServer(`/api/users/search?q=${encodeURIComponent(q)}&page=${page}&size=${size}&sort=${encodeURIComponent(sort)}`, UserPage),
};
