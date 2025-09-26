import type { NextConfig } from "next";

const nextConfig: NextConfig = {
	async rewrites() {
		const backend = process.env.BACKEND_URL ?? "http://localhost:8080";
		return process.env.NODE_ENV === "development"
			? [{ source: "/api/:path*", destination: `${backend}/api/:path*` }]
			: [];
	},
};
export default nextConfig;