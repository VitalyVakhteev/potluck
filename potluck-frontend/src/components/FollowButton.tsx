"use client";

import * as React from "react";
import {Button} from "@/components/ui/button";
import {
	Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger,
} from "@/components/ui/dialog";
import Link from "next/link";
import {useRouter} from "next/navigation";
import {useEffect} from "react";

type FollowState = "following" | "not-following" | "unknown";

export default function FollowButton({viewerId, viewedUserId, initialState = "unknown",}: {
	viewerId: string | null;
	viewedUserId: string;
	initialState?: FollowState;
}) {
	const router = useRouter();
	const [state, setState] = React.useState<FollowState>(initialState);
	const [busy, setBusy] = React.useState(false);
	const noViewer = !viewerId;

	useEffect(() => {
		if (state !== "unknown" || noViewer) return;
		(async () => {
			try {
				const res = await fetch(`/next-api/follows/${viewedUserId}/status`, {credentials: "include"});
				if (!res.ok) throw new Error();
				const json = await res.json() as { following: boolean };
				setState(json.following ? "following" : "not-following");
			} catch {
				setState("not-following");
			}
		})();
	}, [state, noViewer, viewedUserId]);

	const follow = async () => {
		if (busy) return;
		setBusy(true);
		const prev = state;
		setState("following");
		try {
			const res = await fetch(`/next-api/follows/${viewedUserId}`, {
				method: "POST",
				credentials: "include",
			});
			if (!res.ok) throw new Error();
			router.refresh();
		} catch {
			setState(prev);
		} finally {
			setBusy(false);
		}
	};

	const unfollow = async () => {
		if (busy) return;
		setBusy(true);
		const prev = state;
		setState("not-following");
		try {
			const res = await fetch(`/next-api/follows/${viewedUserId}`, {
				method: "DELETE",
				credentials: "include",
			});
			if (!res.ok) throw new Error();
			router.refresh();
		} catch {
			setState(prev);
		} finally {
			setBusy(false);
		}
	};

	if (noViewer) {
		return (
			<Dialog open={busy} onOpenChange={setBusy}>
				<DialogTrigger asChild>
					<Button variant="outline" className="bg-primary hover:bg-primary-foreground">
						Follow
					</Button>
				</DialogTrigger>
				<DialogContent>
					<DialogHeader>
						<DialogTitle>Login required</DialogTitle>
						<DialogDescription>
							You need to be logged in to follow users.
						</DialogDescription>
					</DialogHeader>
					<DialogFooter className="gap-2 sm:justify-end">
						<Button variant="secondary" onClick={() => setBusy(false)}>Close</Button>
						<Link href="/login">
							<Button variant="outline" className="bg-primary hover:bg-primary-foreground">
								Go to Login
							</Button>
						</Link>
					</DialogFooter>
				</DialogContent>
			</Dialog>
		);
	}

	return state === "following" ? (
		<Button variant="outline" className="bg-primary hover:bg-primary-foreground" onClick={unfollow}>
			{busy ? "..." : "Following"}
		</Button>
	) : (
		<Button variant="outline" className="bg-primary hover:bg-primary-foreground" onClick={follow}>
			{busy ? "..." : "Follow"}
		</Button>
	);
}
