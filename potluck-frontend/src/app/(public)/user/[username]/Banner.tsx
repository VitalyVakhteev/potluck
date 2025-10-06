"use client";

import * as React from "react";
import { pickTextOnHex, pickTextOnRgb } from "@/lib/utils";

function parseComputedRgb(input: string): [number, number, number] | null {
	// the regex accepts "rgb(12, 34, 56)" or "rgba(12,34,56,1)"
	const m = input.match(/rgba?\(\s*([\d.]+)\s*,\s*([\d.]+)\s*,\s*([\d.]+)(?:\s*,.*)?\)/i);
	if (!m) return null;
	return [Number(m[1]), Number(m[2]), Number(m[3])];
}

function resolveToRgb(color: string): [number, number, number] | null {
	if (color.startsWith("#")) {
		const hexRgb = pickTextOnHex(color) === "#ffffff" ? [255,255,255] : [0,0,0];
	}
	const el = document.createElement("div");
	el.style.display = "none";
	document.body.appendChild(el);

	let c = color.trim();
	if (c.startsWith("var(")) {
		const varName = c.slice(4, -1).trim();
		const root = getComputedStyle(document.documentElement);
		const resolved = root.getPropertyValue(varName).trim();
		if (resolved) c = resolved;
	}

	el.style.backgroundColor = c;
	const computed = getComputedStyle(el).backgroundColor;
	document.body.removeChild(el);

	return parseComputedRgb(computed);
}

export function Banner({color, className, children,}: {
	color: string;
	className?: string;
	children: React.ReactNode;
}) {
	const [textColor, setTextColor] = React.useState<string>("#000000");

	React.useEffect(() => {
		if (color.startsWith("#")) {
			setTextColor(pickTextOnHex(color));
			return;
		}
		const rgb = resolveToRgb(color);
		if (rgb) setTextColor(pickTextOnRgb(rgb));
		else setTextColor("#000000");
	}, [color]);

	return (
		<section className={["w-full rounded-xl overflow-hidden", className].filter(Boolean).join(" ")}
				 style={{ backgroundColor: color, color: textColor }}>
			{children}
		</section>
	);
}
