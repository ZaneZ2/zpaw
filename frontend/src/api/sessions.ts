export interface InboxEntry {
  sessionKey: string;
  sessionId: string;
  agentId: string;
  label: string | null;
  lastActivityMs: number;
  lastMessage: string | null;
  unread: boolean;
}

export interface TurnEntry {
  id: string;
  role: string;
  content: string | null;
  timestampMs: number;
  toolName: string | null;
  toolInput: string | null;
  toolResult: string | null;
}

export interface CurrentSession {
  sessionKey: string | null;
  exists: boolean;
}

export async function currentSession(
  agentId: string,
): Promise<CurrentSession> {
  const res = await fetch(
    `/api/agents/${encodeURIComponent(agentId)}/chat/session`,
  );
  if (!res.ok)
    throw new Error(`Failed to resolve current session: ${res.status}`);
  return res.json();
}

export async function inbox(
  agentId: string,
): Promise<InboxEntry[]> {
  const res = await fetch(
    `/api/agents/${encodeURIComponent(agentId)}/sessions/inbox`,
  );
  if (!res.ok) throw new Error("Failed to load inbox");
  return res.json();
}

export async function turns(
  agentId: string,
  sessionKey: string,
): Promise<TurnEntry[]> {
  const res = await fetch(
    `/api/agents/${encodeURIComponent(agentId)}/sessions/${encodeURIComponent(sessionKey)}`,
  );
  if (!res.ok) throw new Error("Failed to fetch session turns");
  return res.json();
}
