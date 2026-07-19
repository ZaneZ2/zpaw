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
