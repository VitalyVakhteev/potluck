import type {Metadata} from "next";
import {Geist, Geist_Mono} from "next/font/google";
import {getSession} from "@/lib/api/session";
import "./globals.css";
import Providers from "@/app/providers";
import {Toaster} from "sonner";

const geistSans = Geist({
	variable: "--font-geist-sans",
	subsets: ["latin"],
});

const geistMono = Geist_Mono({
	variable: "--font-geist-mono",
	subsets: ["latin"],
});

export const metadata: Metadata = {
	title: "Potluck",
	description: "Potluck: The Fundraiser Site",
};

export default async function RootLayout({children}: Readonly<{ children: React.ReactNode; }>) {
	const user = await getSession();

	return (
		<html lang="en" suppressHydrationWarning>
		<body className={`${geistSans.variable} ${geistMono.variable} antialiased`}>
		<Providers initialUser={user}>
			{children}
			<Toaster/>
		</Providers>
		</body>
		</html>
	);
}
