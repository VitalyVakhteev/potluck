import { proxy } from "../../_proxy";
import { NextRequest } from "next/server";

export async function POST(req: NextRequest) {
	return proxy(req, "/api/auth/logout", { method: "POST" });
}