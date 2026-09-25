export const roleOrder = { ADMIN: 3, BASE_COMMANDER: 2, LOGISTICS_OFFICER: 1 };

export const navItems = [
  { label: 'Overview', short: 'OV', to: '/dashboard' },
  { label: 'Assets', short: 'AI', to: '/assets' },
  { label: 'Purchases', short: 'PR', to: '/purchases' },
  { label: 'Transfers', short: 'TR', to: '/transfers' },
  { label: 'Assignments & Expenditures', short: 'AX', to: '/assignments' },
  { label: 'Bases', short: 'BA', to: '/bases', minRole: 'BASE_COMMANDER' },
  { label: 'Personnel', short: 'US', to: '/users', minRole: 'ADMIN' },
  { label: 'Equipment types', short: 'ET', to: '/equipment-types', minRole: 'ADMIN' },
  { label: 'Audit trail', short: 'AU', to: '/audit', minRole: 'ADMIN' }
];
