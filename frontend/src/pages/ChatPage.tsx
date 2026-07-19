import { useState, useRef, useCallback } from 'react';
import { Bubble, Sender } from '@ant-design/x';
import { streamChat } from '../api/chat';

interface Message {
  key: string;
  role: 'user' | 'assistant';
  content: string;
}

let msgId = 0;

export default function ChatPage() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [busy, setBusy] = useState(false);
  const [input, setInput] = useState('');
  const threadRef = useRef<HTMLDivElement>(null);

  const handleSend = useCallback(
    async (text: string) => {
      if (!text.trim() || busy) return;
      setBusy(true);
      const userMsg: Message = {
        key: `u-${msgId++}`,
        role: 'user',
        content: text,
      };
      const replyMsg: Message = {
        key: `a-${msgId++}`,
        role: 'assistant',
        content: '',
      };
      setMessages((prev) => [...prev, userMsg, replyMsg]);
      setInput('');

      try {
        for await (const evt of streamChat('default', text)) {
          if (evt.type === 'token') {
            setMessages((prev) =>
              prev.map((m) =>
                m.key === replyMsg.key
                  ? { ...m, content: m.content + (evt.data ?? '') }
                  : m,
              ),
            );
          } else if (evt.type === 'error') {
            setMessages((prev) =>
              prev.map((m) =>
                m.key === replyMsg.key
                  ? {
                      ...m,
                      content:
                        m.content +
                        `\n\n[Error: ${evt.error ?? 'unknown'}]`,
                    }
                  : m,
              ),
            );
          }
        }
      } catch (e) {
        setMessages((prev) =>
          prev.map((m) =>
            m.key === replyMsg.key
              ? {
                  ...m,
                  content:
                    m.content + '\n\n[Error: Connection failed]',
                }
              : m,
          ),
        );
      } finally {
        setBusy(false);
      }
    },
    [busy],
  );

  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        height: '100%',
      }}
    >
      <div
        ref={threadRef}
        style={{
          flex: 1,
          overflow: 'auto',
          padding: '16px 24px',
        }}
      >
        {messages.length === 0 && (
          <div
            style={{
              textAlign: 'center',
              color: '#999',
              marginTop: 80,
              fontSize: 16,
            }}
          >
            开始与新会话
          </div>
        )}
        {messages.map((m) => (
          <Bubble
            key={m.key}
            placement={m.role === 'user' ? 'end' : 'start'}
            content={m.content}
            avatar={m.role === 'assistant' ? 'Z' : undefined}
          />
        ))}
      </div>
      <div style={{ padding: '0 24px 24px', flexShrink: 0 }}>
        <Sender
          value={input}
          onChange={setInput}
          onSubmit={handleSend}
          loading={busy}
          placeholder="输入消息..."
        />
      </div>
    </div>
  );
}
