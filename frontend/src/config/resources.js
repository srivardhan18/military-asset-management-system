const text = (...values) => values.filter(Boolean).join(' ');

export const resources = {
  assets: {
    endpoint: '/assets',
    title: 'Asset inventory',
    description: 'Every tracked unit, its base, and its readiness state.',
    search: (item) => text(item.assetCode, item.equipmentType?.name, item.base?.code),
    columns: [
      ['Asset code', (item) => item.assetCode],
      ['Equipment', (item) => item.equipmentType?.name],
      ['Base', (item) => item.base?.code],
      ['Quantity', (item) => item.quantity],
      ['Status', (item) => item.status]
    ],
    filters: ['base', 'equipmentType', 'status'],
    create: 'asset'
  },
  purchases: {
    endpoint: '/purchases',
    title: 'Purchases',
    description: 'Procurement history connected to the operational ledger.',
    search: (item) => text(item.referenceNumber, item.equipmentType?.name),
    columns: [
      ['Reference', (item) => item.referenceNumber],
      ['Equipment', (item) => item.equipmentType?.name],
      ['Quantity', (item) => item.quantity],
      ['Unit price', (item) => item.unitPrice],
      ['Date', (item) => item.purchaseDate]
    ],
    filters: ['base', 'equipmentType', 'from', 'to'],
    create: 'purchase'
  },
  transfers: {
    endpoint: '/transfers',
    title: 'Asset transfers',
    description: 'Equipment movement between bases and commands.',
    search: (item) => text(item.referenceNumber, item.fromBase?.code, item.toBase?.code),
    columns: [
      ['Reference', (item) => item.referenceNumber],
      ['From', (item) => item.fromBase?.code],
      ['To', (item) => item.toBase?.code],
      ['Quantity', (item) => item.quantity],
      ['Status', (item) => item.status]
    ],
    filters: ['fromBase', 'toBase', 'equipmentType', 'status', 'from', 'to'],
    create: 'transfer'
  },
  assignments: {
    endpoint: '/assignments',
    title: 'Assignments & expenditures',
    description: 'Personnel allocations and usage events across the network.',
    search: (item) => text(item.personnelName, item.asset?.assetCode),
    columns: [
      ['Personnel', (item) => item.personnelName],
      ['Asset', (item) => item.asset?.assetCode],
      ['Base', (item) => item.base?.code],
      ['Quantity', (item) => item.quantity],
      ['Status', (item) => item.status]
    ]
  },
  bases: {
    endpoint: '/bases',
    title: 'Bases',
    description: 'Locations participating in the command network.',
    search: (item) => text(item.name, item.code, item.location),
    columns: [
      ['Name', (item) => item.name],
      ['Code', (item) => item.code],
      ['Location', (item) => item.location],
      ['Status', (item) => item.active ? 'Active' : 'Inactive']
    ],
    create: 'base'
  },
  users: {
    endpoint: '/users',
    title: 'Personnel',
    description: 'Authorized personnel and assigned command roles.',
    search: (item) => text(item.name, item.email, item.role),
    columns: [
      ['Name', (item) => item.name],
      ['Email', (item) => item.email],
      ['Role', (item) => item.role],
      ['Base', (item) => item.base?.code]
    ],
    create: 'user'
  },
  equipmentTypes: {
    endpoint: '/equipment-types',
    title: 'Equipment types',
    description: 'Controlled vocabulary for inventory classification.',
    search: (item) => text(item.name, item.category),
    columns: [
      ['Name', (item) => item.name],
      ['Category', (item) => item.category],
      ['Description', (item) => item.description]
    ],
    create: 'equipmentType'
  },
  audit: {
    endpoint: '/audit-logs',
    title: 'Audit trail',
    description: 'A chronological record of sensitive system activity.',
    search: (item) => text(item.action, item.entityType, item.entityId, item.user?.name),
    columns: [
      ['Timestamp', (item) => item.timestamp],
      ['User', (item) => item.user?.name],
      ['Action', (item) => item.action],
      ['Entity', (item) => item.entityType],
      ['Entity ID', (item) => item.entityId],
      ['Metadata', (item) => item.metadata]
    ]
  }
};
