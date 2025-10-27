import {jsonFetch} from "@/lib/api/error";

export type PatchProfile = {
	bio?: string;
	location?: string;
	bannerColor?: string;
	displayName?: boolean;
	displayEmail?: boolean;
	displayPhone?: boolean;
	firstName?: string | undefined;
	lastName?: string | undefined;
};

export type Settings = {
	currentPassword: string,
	newPassword: string,
}

export async function patchMe(payload: PatchProfile) {
	return jsonFetch<void>("/next-api/users/me", {
		method: "PATCH",
		headers: {"content-type": "application/json"},
		credentials: "include",
		body: JSON.stringify(payload),
	});
}

export async function postSettings(payload: Settings) {
	return jsonFetch<void>("/next-api/users/settings", {
		method: "POST",
		headers: {"content-type": "application/json"},
		credentials: "include",
		body: JSON.stringify(payload),
	})
}