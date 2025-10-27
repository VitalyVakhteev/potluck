export default function EmptyState({text}: { text: string }) {
	return <p className="text-sm text-muted-foreground">{text}</p>;
}