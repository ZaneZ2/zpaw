# Task 4: 搭建前端脚手架

## Files to Create

- `frontend/package.json`
- `frontend/vite.config.ts`
- `frontend/tsconfig.json`
- `frontend/index.html`
- `frontend/src/vite-env.d.ts`
- `frontend/src/main.tsx`
- `frontend/src/App.tsx`

## Context

This is the Phase 1 frontend scaffolding for zpaw. The build output must go to `src/main/resources/static/` so Spring Boot serves the frontend.

## Exact File Contents

### package.json

```json
{
  "name": "zpaw-frontend",
  "private": true,
  "version": "0.1.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc -b && vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^19.0.0",
    "react-dom": "^19.0.0",
    "react-router-dom": "^7.0.0",
    "@ant-design/x": "^2.8.0",
    "@ant-design/pro-layout": "^7.0.0",
    "@ant-design/pro-components": "^2.0.0",
    "antd": "^5.22.0",
    "@ant-design/icons": "^5.5.0"
  },
  "devDependencies": {
    "@types/react": "^19.0.0",
    "@types/react-dom": "^19.0.0",
    "@vitejs/plugin-react": "^4.3.0",
    "typescript": "^5.7.0",
    "vite": "^6.0.0"
  }
}
```

### vite.config.ts

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  build: {
    outDir: '../src/main/resources/static',
    emptyOutDir: true,
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:9426',
        changeOrigin: true,
      },
    },
  },
});
```

### tsconfig.json

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "isolatedModules": true,
    "moduleDetection": "force",
    "noEmit": true,
    "jsx": "react-jsx",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "noUncheckedSideEffectImports": true
  },
  "include": ["src"]
}
```

### index.html

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>zPaw - Personal Agent</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.tsx"></script>
  </body>
</html>
```

### src/vite-env.d.ts

```typescript
/// <reference types="vite/client" />
```

### src/main.tsx

```tsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import App from './App';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </ConfigProvider>
  </React.StrictMode>,
);
```

### src/App.tsx

```tsx
import { Routes, Route, Navigate } from 'react-router-dom';
import ProLayout from '@ant-design/pro-layout';
import {
  ToolOutlined,
  WechatOutlined,
  SettingOutlined,
} from '@ant-design/icons';

function Placeholder({ title }: { title: string }) {
  return (
    <div
      style={{
        padding: 48,
        textAlign: 'center',
        color: '#999',
        fontSize: 18,
      }}
    >
      {title} — 开发中
    </div>
  );
}

function Dashboard() {
  return <Placeholder title="zPaw 仪表盘" />;
}

export default function App() {
  return (
    <ProLayout
      title="zPaw"
      logo="https://img.alicdn.com/imgextra/i1/O1CN01nTg6w21NqT5qFKH1u_!!6000000001621-55-tps-550-550.svg"
      route={{
        path: '/',
        routes: [
          { path: '/agents', name: 'Agents', icon: <WechatOutlined /> },
          { path: '/tools', name: '工具', icon: <ToolOutlined /> },
          { path: '/settings', name: '设置', icon: <SettingOutlined /> },
        ],
      }}
      menuItemRender={(item, dom) => <a href={item.path || '/'}>{dom}</a>}
    >
      <Routes>
        <Route path="/" element={<Navigate to="/agents" replace />} />
        <Route path="/agents" element={<Dashboard />} />
        <Route path="/tools" element={<Placeholder title="工具管理" />} />
        <Route path="/settings" element={<Placeholder title="设置" />} />
      </Routes>
    </ProLayout>
  );
}
```

## Commands to Run

```bash
cd frontend
npm install
npm run build
```

Expected: Build succeeds, output goes to `src/main/resources/static/`
