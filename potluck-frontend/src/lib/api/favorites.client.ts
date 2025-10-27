import {jsonFetch} from "@/lib/api/http/client";

export async function favoriteFundraiser(fundraiserId: string) {
	return jsonFetch("/next-api/users/favorites", {
		method: "POST",
		headers: {"content-type": "application/json"},
		credentials: "include",
		body: JSON.stringify({fundraiserId}),
	});
}

export async function unfavoriteFundraiser(fundraiserId: string) {
	return jsonFetch("/next-api/users/favorites", {
		method: "DELETE",
		headers: {"content-type": "application/json"},
		credentials: "include",
		body: JSON.stringify({fundraiserId}),
	});
}