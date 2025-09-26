"use client";

import { createContext, useContext, useMemo, useState } from "react";
import { ThemeProvider } from "@/components/ThemeProvider";
import type { SessionUser } from "@/lib/api/schemas";

type AuthCtx = {
	user: SessionUser | null;
	setUser: (u: SessionUser | null) => void;
};
const AuthContext = createContext<AuthCtx | undefined>(undefined);

export default function Providers({ children, initialUser }: { children: React.ReactNode; initialUser: SessionUser | null; }) {
	const [user, setUser] = useState<SessionUser | null>(initialUser);
	const value = useMemo(() => ({ user, setUser }), [user]);

	return (
		<ThemeProvider attribute="class" defaultTheme="system" enableSystem disableTransitionOnChange>
			<AuthContext.Provider value={value}>
				{children}
			</AuthContext.Provider>
		</ThemeProvider>
	);
};


export function useAuth() {
	const ctx = useContext(AuthContext);
	if (!ctx) throw new Error("useAuth must be used within <Providers>");
	return ctx;
}