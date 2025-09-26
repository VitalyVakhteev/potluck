"use client"

import * as React from "react"
import Link from "next/link"
import { Moon, Sun } from "lucide-react"
import { useTheme } from "next-themes"

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
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { useAuth } from "@/app/providers";

const components: { title: string; href: string; description: string }[] = [
	{
		title: "Alert Dialog",
		href: "/docs/primitives/alert-dialog",
		description:
			"A modal dialog that interrupts the user with important content and expects a response.",
	},
	{
		title: "Hover Card",
		href: "/docs/primitives/hover-card",
		description:
			"For sighted users to preview content available behind a link.",
	},
	{
		title: "Progress",
		href: "/docs/primitives/progress",
		description:
			"Displays an indicator showing the completion progress of a task, typically displayed as a progress bar.",
	},
	{
		title: "Scroll-area",
		href: "/docs/primitives/scroll-area",
		description: "Visually or semantically separates content.",
	},
	{
		title: "Tabs",
		href: "/docs/primitives/tabs",
		description:
			"A set of layered sections of content—known as tab panels—that are displayed one at a time.",
	},
	{
		title: "Tooltip",
		href: "/docs/primitives/tooltip",
		description:
			"A popup that displays information related to an element when the element receives keyboard focus or the mouse hovers over it.",
	},
]

export default function Navbar() {
	const { resolvedTheme, setTheme } = useTheme()
	const { user } = useAuth();
	const [mounted, setMounted] = React.useState(false)
	React.useEffect(() => setMounted(true), [])

	const flipTheme = () => {
		setTheme(resolvedTheme === "dark" ? "light" : "dark")
	}

	const initial = (user?.username?.[0]?.toUpperCase() ?? "P");
	const isDark = resolvedTheme === "dark";

	const avatarBg = isDark ? "bg-zinc-700" : "bg-primary";
	const avatarText = isDark ? "text-zinc-100" : "text-primary-foreground";

	const avatarHover =
		isDark
			? "hover:bg-zinc-600"
			: "hover:bg-primary-foreground hover:text-primary";

	const canCreate =
		!!user && (user.role === "ORGANIZER" || user.role === "ADMIN");

	return (
		<header className="w-full">
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
										{components.map((component) => (
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
							<NavigationMenuItem>
								<NavigationMenuLink asChild className={navigationMenuTriggerStyle()}>
									<Link href="/leaderboard">Leaderboard</Link>
								</NavigationMenuLink>
							</NavigationMenuItem>
						</NavigationMenuList>
					</NavigationMenu>
				</div>

				<div className="flex flex-row mr-4 mt-4">
					<Input className="mr-4">

					</Input>

					<Tooltip>
						<TooltipTrigger asChild>
							<Button
								variant="outline"
								size="icon"
								onClick={flipTheme}
								aria-pressed={resolvedTheme === "dark"}
								aria-label="Toggle theme"
								className="mr-4"
							>
								{mounted && (
									<>
										<Sun className="h-[1.2rem] w-[1.2rem] scale-100 rotate-0 transition-all dark:scale-0 dark:-rotate-90" />
										<Moon className="absolute h-[1.2rem] w-[1.2rem] scale-0 rotate-90 transition-all dark:scale-100 dark:rotate-0" />
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
											aria-pressed={resolvedTheme === "dark"}
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
								<Avatar className=
									"mr-4 h-9 w-9 ring-0 focus:outline-none focus-visible:ring-2 focus-visible:ring-primary/50"
								>
									<AvatarFallback
										className={[
											"h-full w-full select-none font-semibold",
											"flex items-center justify-center rounded-full transition-colors",
											avatarBg,
											avatarText,
											avatarHover,
										].join(" ")}
										aria-label={user.username}
									>
										{initial}
									</AvatarFallback>
								</Avatar>
							</DropdownMenuTrigger>

							<DropdownMenuContent className="w-56 mt-4 mr-8" align="start">
								<DropdownMenuLabel>My Account</DropdownMenuLabel>
								<DropdownMenuGroup>
									<Link href="/profile">
										<DropdownMenuItem>
											Profile
										</DropdownMenuItem>
									</Link>
									<Link href="/profile/edit">
										<DropdownMenuItem>
											Settings
										</DropdownMenuItem>
									</Link>
								</DropdownMenuGroup>
								<DropdownMenuSeparator />
								<Link href="https://github.com/VitalyVakhteev/potluck">
									<DropdownMenuItem>
										GitHub
									</DropdownMenuItem>
								</Link>
								<DropdownMenuSeparator />
								<DropdownMenuItem>
									Log out
								</DropdownMenuItem>
							</DropdownMenuContent>
						</DropdownMenu>
					) : (
						<Link href="/login">
							<Button
								variant="outline"
								aria-pressed={resolvedTheme === "dark"}
								aria-label="Log In"
								className="bg-primary mr-4 hover:bg-primary-foreground"
							>
								Log In
							</Button>
						</Link>
					)}
				</div>
			</div>
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