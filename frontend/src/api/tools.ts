export interface ToolDefinition {
	name: string;
	description: string;
	enabled: boolean;
}

export async function listTools(): Promise<ToolDefinition[]> {
	const res = await fetch("/api/tools");
	if (!res.ok) throw new Error(`Failed to list tools: ${res.status}`);
	return res.json();
}
