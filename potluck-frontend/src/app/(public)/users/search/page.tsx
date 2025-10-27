import {UsersApi} from "@/lib/api/users.server";
import {toPageIndex, toSort} from "@/lib/utils";
import Link from "next/link";
import {UserSummary} from "@/lib/api/schemas";
import {UserIcon} from "lucide-react";
import * as React from "react";

export const dynamic = "force-dynamic";

export default async function UserSearchPage({
												 searchParams,
											 }: {
	searchParams: { q?: string; page?: string; size?: string; sort?: string };
}) {
	const q = (searchParams.q ?? "").trim();
	const page = toPageIndex(searchParams.page);
	const size = Number(searchParams.size ?? 20);
	const sort = toSort(searchParams.sort);

	const data = q ? await UsersApi.search(q, {page, size, sort}) : null;

	return (
		<main className="flex flex-col ml-4 mr-4 px-4 py-6">
			<h1 className="text-2xl font-bold tracking-tight">
				{q ? <>Users matching “{q}”</> : "Search users"}
			</h1>

			{!q ? (
				<p className="mt-4 text-sm text-muted-foreground">Type a query to search for users.</p>
			) : !data || data.content.length === 0 ? (
				<p className="mt-4 text-sm text-muted-foreground">No users found.</p>
			) : (
				<ul className="mt-4 space-y-3">
					{data.content.map((u: UserSummary) => (
						<li key={u.id} className="rounded-xl border p-3">
							<div className="flex items-center justify-between">
								<div>
									<div className="flex items-center justify-center">
										<UserIcon className="h-4 w-4 mr-2"/>
										<div className="font-medium">{u.username}</div>
									</div>
								</div>
								<div className="flex items-center justify-center">
									<div className="mr-4">Total Points: {u.totalPoints}</div>
									<div className="mr-4">Total Fundraisers: {u.totalFundraisers}</div>
									<Link
										href={`/users/${encodeURIComponent(u.username)}`}
										className="text-sm font-medium text-primary-foreground dark:text-primary hover:underline"
									>
										View
									</Link>
								</div>
							</div>
						</li>
					))}
				</ul>
			)}

		</main>
	);
}