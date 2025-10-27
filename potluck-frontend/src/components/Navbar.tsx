"use client"

import * as React from "react"
import Link from "next/link"
import {DollarSign, Moon, Search, Sun, UserIcon} from "lucide-react"
import {useTheme} from "next-themes"

import {
	NavigationMenu,
	NavigationMenuContent,
	NavigationMenuItem,
	NavigationMenuLink,
	NavigationMenuList,
	NavigationMenuTrigger,
	navigationMenuTriggerStyle,
} from "@/components/ui/navigation-menu"
import {
	Tooltip,
	TooltipContent,
	TooltipTrigger,
} from "@/components/ui/tooltip"
import {
	DropdownMenu,
	DropdownMenuContent,
	DropdownMenuGroup,
	DropdownMenuItem,
	DropdownMenuLabel,
	DropdownMenuSeparator,
	DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Avatar, AvatarFallback} from "@/components/ui/avatar"
import {useAuth} from "@/app/providers";
import {logout} from "@/lib/api/auth";
import {toast} from "sonner";
import {useRouter} from "next/navigation";
import EditSettingsDialog from "@/components/EditSettingsDialog";
import {InputGroup, InputGroupAddon, InputGroupInput} from "@/components/ui/input-group";
import {useEffect, useRef, useState} from "react";

interface Card {
	title: string;
	href: string;
	description: string;
}

const RESTRICTED = new Set(["Favorites", "Follower Fundraisers"]);

const components: Card[] = [
	{
		title: "Follower Fundraisers",
		href: "/fundraisers/feed",
		description:
			"The latest fundraisers from your followers.",
	},
	{
		title: "Favorites",
		href: "/fundraisers/favorites",
		description:
			"Fundraisers on your favorites list.",
	},
	{
		title: "Starting Soon",
		href: "/fundraisers/starting-soon",
		description: "Fundraisers that are starting soon.",
	},
	{
		title: "Ending Soon",
		href: "/fundraisers/ending-soon",
		description:
			"Fundraisers that are ending soon.",
	},
	{
		title: "Nearby Fundraisers",
		href: "/fundraisers/near",
		description:
			"Fundraisers near you.",
	},
	{
		title: "Recently Created",
		href: "/fundraisers",
		description:
			"Recently created fundraisers.",
	},
]

function SearchChooser() {
	const router = useRouter();
	const [q, setQ] = useState("");
	const [open, setOpen] = useState(false);
	const boxRef = useRef<HTMLDivElement | null>(null);

	useEffect(() => {
		const onDocClick = (e: MouseEvent) => {
			if (!boxRef.current) return;
			if (!boxRef.current.contains(e.target as Node)) setOpen(false);
		};
		document.addEventListener("mousedown", onDocClick);
		return () => document.removeEventListener("mousedown", onDocClick);
	}, []);

	const gotoFundraisers = () => {
		if (!q.trim()) return;
		router.push(`/fundraisers/search?q=${encodeURIComponent(q.trim())}&page=0&size=20`);
		setOpen(false);
	};

	const gotoUsers = () => {
		if (!q.trim()) return;
		router.push(`/users/search?q=${encodeURIComponent(q.trim())}&page=0&size=20`);
		setOpen(false);
	};

	const onKeyDown: React.KeyboardEventHandler<HTMLInputElement> = (e) => {
		if (e.key === "Enter") {
			gotoFundraisers();
		} else if (e.key === "ArrowDown") {
			setOpen(true);
		}
	};

	return (
		<div ref={boxRef} className="relative mr-4">
			<InputGroup>
				<InputGroupInput
					value={q}
					onChange={(e) => {
						setQ(e.target.value);
						setOpen(e.target.value.trim().length > 0);
					}}
					onFocus={() => setOpen(q.trim().length > 0)}
					onKeyDown={onKeyDown}
					placeholder="Search…"
					aria-label="Search"
				/>
				<InputGroupAddon>
					<Search/>
				</InputGroupAddon>
			</InputGroup>

			{open && (
				<div
					className="absolute left-0 right-0 mt-1 z-50 rounded-xl border bg-background shadow-sm p-2 space-y-2"
					role="menu"
					aria-label="Search destinations"
				>
					<Button
						variant="ghost"
						className="w-full justify-start"
						onClick={gotoFundraisers}
						role="menuitem"
					>
						<DollarSign className="h-4 w-4 mr-2"/>
						Fundraisers: <span className="ml-1 font-medium truncate">{q}</span>
					</Button>
					<Button
						variant="ghost"
						className="w-full justify-start"
						onClick={gotoUsers}
						role="menuitem"
					>
						<UserIcon className="h-4 w-4 mr-2"/>
						Users: <span className="ml-1 font-medium truncate">{q}</span>
					</Button>
				</div>
			)}
		</div>
	);
}

export default function Navbar() {
	const {refresh} = useRouter();
	const {resolvedTheme, setTheme} = useTheme()
	const {user, setUser} = useAuth();
	const [mounted, setMounted] = React.useState(false)
	const [settingsOpen, setSettingsOpen] = React.useState(false);
	React.useEffect(() => setMounted(true), [])

	const flipTheme = () => {
		setTheme(resolvedTheme === "dark" ? "light" : "dark")
	}

	const initial = (user?.username?.[0]?.toUpperCase() ?? "P");
	const isDark = resolvedTheme === "dark";

	const avatarBg = isDark ? "bg-zinc-700" : "bg-primary";
	const avatarText = isDark ? "text-zinc-100" : "text-foreground";
	const avatarHover = isDark ? "hover:bg-zinc-600" : "hover:bg-primary-foreground hover:text-primary";

	const canCreate = !!user && (user.role === "ORGANIZER" || user.role === "ADMIN");
	const selfUrl = `/users/${user?.username}`;

	const filterCards = (cards: Card[]) => !!user ? cards : cards.filter(c => !RESTRICTED.has(c.title));

	const triggerLogout = () => {
		logout().then(() => {
			toast.success("Logged out!");
			setUser(null)
			refresh();
		})
			.catch(() => toast.error("Logout failed."));
	}

	return (
		<header className="sticky top-0 w-full bg-white dark:bg-background">
			<div className="flex items-center justify-between w-full">
				<div className="flex items-center gap-4 mt-4 ml-8">
					<Link
						href="/"
						aria-label="Potluck home"
						className="select-none whitespace-nowrap shrink-0
						text-xl sm:text-2xl font-semibold tracking-tight
						text-primary-foreground dark:text-secondary-foreground
						mb-1"
					>
						Potluck
					</Link>


					<NavigationMenu viewport={false}>
						<NavigationMenuList>
							<NavigationMenuItem>
								<NavigationMenuLink asChild className={navigationMenuTriggerStyle()}>
									<Link href="/">Home</Link>
								</NavigationMenuLink>
							</NavigationMenuItem>
							<NavigationMenuItem>
								<NavigationMenuTrigger>Fundraisers</NavigationMenuTrigger>
								<NavigationMenuContent>
									<ul className="grid w-[400px] gap-2 md:w-[500px] md:grid-cols-2 lg:w-[600px]">
										{filterCards(components).map((component) => (
											<ListItem
												key={component.title}
												title={component.title}
												href={component.href}
											>
												{component.description}
											</ListItem>
										))}
									</ul>
								</NavigationMenuContent>
							</NavigationMenuItem>
							{/*<NavigationMenuItem>*/}
							{/*	<NavigationMenuLink asChild className={navigationMenuTriggerStyle()}>*/}
							{/*		<Link href="/leaderboard">Leaderboard</Link>*/}
							{/*	</NavigationMenuLink>*/}
							{/*</NavigationMenuItem>*/}
						</NavigationMenuList>
					</NavigationMenu>
				</div>

				<div className="flex flex-row mr-4 mt-4">
					<SearchChooser/>

					<Tooltip>
						<TooltipTrigger asChild>
							<Button
								variant="outline"
								size="icon"
								onClick={flipTheme}
								aria-pressed={mounted ? isDark : undefined}
								aria-label="Toggle theme"
								className="mr-4"
							>
								{mounted && (
									<>
										<Sun
											className="h-[1.2rem] w-[1.2rem] scale-100 rotate-0 transition-all dark:scale-0 dark:-rotate-90"/>
										<Moon
											className="absolute h-[1.2rem] w-[1.2rem] scale-0 rotate-90 transition-all dark:scale-100 dark:rotate-0"/>
									</>
								)}
							</Button>
						</TooltipTrigger>
						<TooltipContent>
							Change Theme
						</TooltipContent>
					</Tooltip>

					{canCreate && (
						<Tooltip>
							<TooltipTrigger asChild>
								<div className="mr-4">
									<Link href="/fundraisers/new">
										<Button
											variant="outline"
											aria-label="Create"
											className="bg-primary hover:bg-primary-foreground"
										>
											Create
										</Button>
									</Link>
								</div>
							</TooltipTrigger>
							<TooltipContent>
								Create a Fundraiser
							</TooltipContent>
						</Tooltip>
					)}

					{user ? (
						<DropdownMenu>
							<DropdownMenuTrigger asChild>
								<Avatar
									className="mr-4 h-9 w-9 ring-0 focus:outline-none focus-visible:ring-2 focus-visible:ring-primary/50">
									<AvatarFallback
										className={mounted ? [
											"h-full w-full select-none font-semibold",
											"flex items-center justify-center rounded-full transition-colors",
											avatarBg,
											avatarText,
											avatarHover,
										].join(" ") : undefined}
										aria-label={user.username}
									>
										{initial}
									</AvatarFallback>
								</Avatar>
							</DropdownMenuTrigger>

							<DropdownMenuContent className="w-56 mt-4 mr-8" align="start">
								<DropdownMenuLabel className="text-lg">{user.username}</DropdownMenuLabel>
								<DropdownMenuGroup>
									<Link href={selfUrl}>
										<DropdownMenuItem>
											Profile
										</DropdownMenuItem>
									</Link>
									<DropdownMenuItem onSelect={(e) => {
										e.preventDefault();
										setSettingsOpen(true);
									}}>
										Settings
									</DropdownMenuItem>
								</DropdownMenuGroup>
								<DropdownMenuSeparator/>
								<Link href="https://github.com/VitalyVakhteev/potluck">
									<DropdownMenuItem>
										GitHub
									</DropdownMenuItem>
								</Link>
								<DropdownMenuSeparator/>
								<div onClick={triggerLogout}>
									<DropdownMenuItem>
										Log out
									</DropdownMenuItem>
								</div>
							</DropdownMenuContent>
						</DropdownMenu>
					) : (
						<Link href="/login">
							<Button
								variant="outline"
								aria-label="Log In"
								className="bg-primary mr-4 hover:bg-primary-foreground"
							>
								Log In
							</Button>
						</Link>
					)}
				</div>
			</div>

			<EditSettingsDialog open={settingsOpen} onOpenChange={setSettingsOpen}/>
		</header>
	);
}


function ListItem({
					  title,
					  children,
					  href,
					  ...props
				  }: React.ComponentPropsWithoutRef<"li"> & { href: string }) {
	return (
		<li {...props}>
			<NavigationMenuLink asChild>
				<Link href={href}>
					<div className="text-sm leading-none font-medium">{title}</div>
					<p className="text-muted-foreground line-clamp-2 text-sm leading-snug">
						{children}
					</p>
				</Link>
			</NavigationMenuLink>
		</li>
	)
}