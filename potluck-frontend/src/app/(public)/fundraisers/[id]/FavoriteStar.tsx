"use client";

import * as React from "react";
import { Star } from "lucide-react";
import { favoriteFundraiser, unfavoriteFundraiser } from "@/lib/api/favorites.client";
import { Button } from "@/components/ui/button";
import { Tooltip, TooltipTrigger, TooltipContent, TooltipProvider } from "@/components/ui/tooltip";
import { cn } from "@/lib/utils";

type Props = {
	fundraiserId: string;
	initial?: boolean;
	disabled?: boolean;
};

export default function FavoriteStar({ fundraiserId, initial = false, disabled = false }: Props) {
	const [fav, setFav] = React.useState(initial);
	const [pending, setPending] = React.useState(false);

	async function toggle() {
		if (disabled || pending) return;
		setPending(true);
		const prev = fav;
		setFav(!fav);
		try {
			if (!prev) await favoriteFundraiser(fundraiserId);
			else await unfavoriteFundraiser(fundraiserId);
		} catch {
			setFav(prev);
		} finally {
			setPending(false);
		}
	}

	const label = fav ? "Favorited!" : "Favorite";

	return (
		<TooltipProvider>
			<Tooltip>
				<TooltipTrigger asChild>
					<Button
						variant="ghost"
						size="icon"
						aria-label={label}
						className={cn("rounded-full", fav && "text-yellow-500")}
						onClick={toggle}
						disabled={disabled || pending}
					>
						<Star className={cn("h-5 w-5", fav ? "fill-current" : "fill-transparent")} />
					</Button>
				</TooltipTrigger>
				<TooltipContent side="left" className="text-sm">
					{label}
				</TooltipContent>
			</Tooltip>
		</TooltipProvider>
	);
}
