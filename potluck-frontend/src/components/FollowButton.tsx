"use client";

import * as React from "react";
import { Button } from "@/components/ui/button";
import {
	Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger,
} from "@/components/ui/dialog";
import Link from "next/link";

type FollowState = "following" | "not-following" | "unknown";

export default function FollowButton({viewerId, viewedUserId, initialState = "unknown",}: {
	viewerId: string | null;
	viewedUserId: string;
	initialState?: FollowState;
}) {
	const [state, setState] = React.useState<FollowState>(initialState);
	const [open, setOpen] = React.useState(false);

	const noViewer = !viewerId;

	const follow = async () => {
		// TODO: wire the proxy endpoints, including the viewed and viewer too to POST /next-api/follows/{id}
		// const res = await fetch(`/next-api/follows/${viewedUserId}`, { method: "POST", credentials: "include" });
		// if (res.ok) setState("following");
		setState("following");
	};

	const unfollow = async () => {
		// TODO: add in DELETE endpoint /next-api/follows/{id}
		// const res = await fetch(`/next-api/follows/${viewedUserId}`, { method: "DELETE", credentials: "include" });
		// if (res.ok) setState("not-following");
		setState("not-following");
	};

	if (noViewer) {
		return (
			<Dialog open={open} onOpenChange={setOpen}>
				<DialogTrigger asChild>
					<Button variant="outline" className="bg-primary hover:bg-primary-foreground">Follow</Button>
				</DialogTrigger>
				<DialogContent>
					<DialogHeader>
						<DialogTitle>Login required</DialogTitle>
						<DialogDescription>
							You need to be logged in to follow users.
						</DialogDescription>
					</DialogHeader>
					<DialogFooter className="gap-2 sm:justify-end">
						<Button variant="secondary" onClick={() => setOpen(false)}>Close</Button>
						<Button asChild className="bg-primary">
							<Link href="/login">Go to Login</Link>
						</Button>
					</DialogFooter>
				</DialogContent>
			</Dialog>
		);
	}

	return state === "following" ? (
		<Button variant="outline" className="bg-primary hover:bg-primary-foreground" onClick={unfollow}>Following</Button>
	) : (
		<Button variant="outline" className="bg-primary hover:bg-primary-foreground" onClick={follow}>Follow</Button>
	);
}
