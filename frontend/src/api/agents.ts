export interface AgentDefinition {
	id: string;
	name: string;
	builtin: boolean;
}

export async function listAgents(): Promise<AgentDefinition[]> {
	const res = await fetch("/api/agents");
	if (!res.ok) throw new Error(`Failed to list agents: ${res.status}`);
	return res.json();
}
