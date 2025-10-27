import FundraiserGrid from "./FundraiserGrid";
import Paginator from "./Paginator";
import type {z} from "zod";
import {FundraiserPage as FundraiserPageSchema} from "@/lib/api/schemas";

type FundraiserPageT = z.infer<typeof FundraiserPageSchema>;

export default function FundraiserListPage({title, page, basePath,}: {
	title: string;
	page: FundraiserPageT;
	basePath: string;
}) {
	return (
		<main className="flex flex-col ml-4 mr-4 px-4 py-6">
			<h1 className="text-2xl font-bold tracking-tight">{title}</h1>
			<FundraiserGrid page={page}/>
			<Paginator
				basePath={basePath}
				currentIndex={page.number}
				totalPages={page.totalPages}
			/>
		</main>
	);
}
