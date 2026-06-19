/**
 * Patches NEC_RBAC_With_Responses.postman_collection.json to match the
 * refactored REST endpoints:
 *   POST /users/save       → POST /users        (Create User)
 *   POST /users/save       → PUT  /users/{id}   (Update User)
 *   POST /roles/save       → POST /roles        (Create Role)
 *   POST /roles/save       → PUT  /roles/{id}   (Update Role)
 *   POST /permission-group/save → POST /permission-groups  (Create)
 *   POST /permission-group/save → PUT  /permission-groups  (Update)
 *   POST /role-permission/save  → POST /role-permission    (Create)
 *   POST /role-permission/save  → PUT  /role-permission    (Update)
 */

const fs   = require('fs');
const path = require('path');

const FILE = path.join(__dirname,
  '../src/postman/NEC_RBAC_With_Responses.postman_collection.json');

const col = JSON.parse(fs.readFileSync(FILE, 'utf8'));

// ── helpers ────────────────────────────────────────────────────────────────

function urlObj(rawUrl, pathSegs) {
  return {
    raw:  `{{baseUrl}}/${rawUrl}`,
    host: ['{{baseUrl}}'],
    path: pathSegs
  };
}

/** Walk every item recursively, apply fn(item) */
function walkItems(items, fn) {
  for (const item of items) {
    if (item.item) walkItems(item.item, fn);   // folder
    else fn(item);
  }
}

/** Deep-replace every URL inside saved response examples */
function fixResponseUrls(responses, newUrl, newPath, newMethod) {
  if (!responses) return;
  for (const r of responses) {
    if (!r.originalRequest) continue;
    if (newMethod) r.originalRequest.method = newMethod;
    r.originalRequest.url = urlObj(newUrl, newPath);
  }
}

/** Remove a key from a JSON body string (best-effort) */
function removeBodyKey(raw, key) {
  // Remove  "key": <value>,  or  "key": <value>  (last key)
  return raw
    .replace(new RegExp(`\\s*"${key}"\\s*:\\s*(?:"[^"]*"|null|true|false|\\d+),?\\n?`, 'g'), '')
    .replace(/,(\s*})/, '$1');   // trailing comma before }
}

// ── patch every item ───────────────────────────────────────────────────────

walkItems(col.item, item => {
  const req  = item.request;
  const name = item.name;

  // ── USERS ────────────────────────────────────────────────────────────────

  if (name === 'Save User (Create)') {
    item.name        = 'Create User';
    req.method       = 'POST';
    req.url          = urlObj('api/v1/rbac/users', ['api','v1','rbac','users']);
    // Strip id / null body key
    if (req.body && req.body.raw) req.body.raw = removeBodyKey(req.body.raw, 'id');
    fixResponseUrls(item.response, 'api/v1/rbac/users', ['api','v1','rbac','users'], 'POST');
  }

  if (name === 'Save User (Update)') {
    item.name        = 'Update User';
    req.method       = 'PUT';
    req.url          = urlObj('api/v1/rbac/users/{{userId}}',
                              ['api','v1','rbac','users','{{userId}}']);
    // Remove id from body – it's now the path variable
    if (req.body && req.body.raw) req.body.raw = removeBodyKey(req.body.raw, 'id');
    fixResponseUrls(item.response, 'api/v1/rbac/users/{{userId}}',
                    ['api','v1','rbac','users','{{userId}}'], 'PUT');
  }

  // ── ROLES ────────────────────────────────────────────────────────────────

  if (name === 'Save Role (Create)') {
    item.name        = 'Create Role';
    req.method       = 'POST';
    req.url          = urlObj('api/v1/rbac/roles', ['api','v1','rbac','roles']);
    if (req.body && req.body.raw) req.body.raw = removeBodyKey(req.body.raw, 'roleId');
    // Keep only the 201 response example; rename it
    if (item.response) {
      item.response = item.response.filter(r => r.code === 201 || r.code === 400 || r.code === 409 || r.code === 401 || r.code === 500);
    }
    fixResponseUrls(item.response, 'api/v1/rbac/roles', ['api','v1','rbac','roles'], 'POST');
  }

  if (name === 'Save Role (Create Child)') {
    item.name  = 'Create Role (Child)';
    req.method = 'POST';
    req.url    = urlObj('api/v1/rbac/roles', ['api','v1','rbac','roles']);
    if (req.body && req.body.raw) req.body.raw = removeBodyKey(req.body.raw, 'roleId');
    fixResponseUrls(item.response, 'api/v1/rbac/roles', ['api','v1','rbac','roles'], 'POST');
  }

  // ── PERMISSION GROUPS ────────────────────────────────────────────────────

  if (name === 'Save Permission Group') {
    item.name        = 'Create Permission Group';
    req.method       = 'POST';
    req.url          = urlObj('api/v1/rbac/permission-groups',
                              ['api','v1','rbac','permission-groups']);
    fixResponseUrls(item.response, 'api/v1/rbac/permission-groups',
                    ['api','v1','rbac','permission-groups'], 'POST');
  }

  if (name === 'Save Permission Group (Update)') {
    item.name        = 'Update Permission Group';
    req.method       = 'PUT';
    req.url          = urlObj('api/v1/rbac/permission-groups',
                              ['api','v1','rbac','permission-groups']);
    fixResponseUrls(item.response, 'api/v1/rbac/permission-groups',
                    ['api','v1','rbac','permission-groups'], 'PUT');
  }

  // ── ROLE PERMISSIONS ─────────────────────────────────────────────────────

  if (name === 'Save Role Permission') {
    item.name        = 'Create Role Permission';
    req.method       = 'POST';
    req.url          = urlObj('api/v1/rbac/role-permission',
                              ['api','v1','rbac','role-permission']);
    // rolePermissionId is no longer needed for create
    if (req.body && req.body.raw) req.body.raw = removeBodyKey(req.body.raw, 'rolePermissionId');
    fixResponseUrls(item.response, 'api/v1/rbac/role-permission',
                    ['api','v1','rbac','role-permission'], 'POST');
  }
});

// ── inject "Update Role" after "Create Role (Child)" ──────────────────────

function injectAfter(items, afterName, newItem) {
  for (let i = 0; i < items.length; i++) {
    if (items[i].item) {
      injectAfter(items[i].item, afterName, newItem);
    } else if (items[i].name === afterName) {
      items.splice(i + 1, 0, newItem);
      return true;
    }
  }
  return false;
}

const updateRoleItem = {
  name: 'Update Role',
  request: {
    method: 'PUT',
    header: [
      { key: 'Content-Type',  value: 'application/json' },
      { key: 'Authorization', value: 'Bearer {{accessToken}}' }
    ],
    body: {
      mode: 'raw',
      raw: JSON.stringify({
        roleName: 'Administrator',
        description: 'Updated description',
        status: 'ACTIVE',
        isParentRole: true,
        parentRoleId: null,
        approvalLimit: null,
        modifiedByUserId: 1
      }, null, 2),
      options: { raw: { language: 'json' } }
    },
    url: urlObj('api/v1/rbac/roles/{{roleId}}',
                ['api','v1','rbac','roles','{{roleId}}'])
  },
  response: [
    {
      name: '200 - Role Updated',
      status: 'OK',
      code: 200,
      _postman_previewlanguage: 'json',
      header: [{ key: 'Content-Type', value: 'application/json' }],
      body: JSON.stringify({
        success: 200,
        message: 'Role updated successfully',
        data: {
          roleId: 1,
          roleName: 'Administrator',
          description: 'Updated description',
          status: 'ACTIVE',
          parentRoleId: null,
          createdAt: '2026-06-18T10:30:00',
          updatedAt: '2026-06-18T10:30:00'
        }
      }, null, 2),
      cookie: [],
      originalRequest: {
        method: 'PUT',
        header: [
          { key: 'Content-Type',  value: 'application/json' },
          { key: 'Authorization', value: 'Bearer {{accessToken}}' }
        ],
        body: {
          mode: 'raw',
          raw: JSON.stringify({
            roleName: 'Administrator',
            description: 'Updated description',
            status: 'ACTIVE'
          }, null, 2),
          options: { raw: { language: 'json' } }
        },
        url: urlObj('api/v1/rbac/roles/{{roleId}}',
                    ['api','v1','rbac','roles','{{roleId}}'])
      }
    }
  ]
};

// inject "Update Role Permission" after "Create Role Permission"
const updateRolePermissionItem = {
  name: 'Update Role Permission',
  request: {
    method: 'PUT',
    header: [
      { key: 'Content-Type',  value: 'application/json' },
      { key: 'Authorization', value: 'Bearer {{accessToken}}' },
      { key: 'X-User-Id',     value: '1' }
    ],
    body: {
      mode: 'raw',
      raw: JSON.stringify({
        roleId: 1,
        roleName: 'Administrator',
        modules: [{
          moduleId: 1,
          moduleName: 'User Management',
          groups: [{
            groupId: 1,
            groupName: 'User Operations',
            permissions: [
              { permissionId: 1, permissionName: 'Create User' },
              { permissionId: 2, permissionName: 'Read User' },
              { permissionId: 5, permissionName: 'Export User' }
            ]
          }]
        }]
      }, null, 2),
      options: { raw: { language: 'json' } }
    },
    url: urlObj('api/v1/rbac/role-permission',
                ['api','v1','rbac','role-permission'])
  },
  response: [
    {
      name: '200 - Permissions Updated',
      status: 'OK',
      code: 200,
      _postman_previewlanguage: 'json',
      header: [{ key: 'Content-Type', value: 'application/json' }],
      body: JSON.stringify({
        success: 200,
        message: 'Role permissions updated successfully',
        data: {
          roleId: 1,
          roleName: 'Administrator',
          modules: [{
            moduleId: 1,
            moduleName: 'User Management',
            groups: [{
              groupId: 1,
              groupName: 'User Operations',
              permissions: [
                { permissionId: 1, permissionName: 'Create User', permissionCode: 'USER_CREATE' },
                { permissionId: 2, permissionName: 'Read User',   permissionCode: 'USER_READ'   },
                { permissionId: 5, permissionName: 'Export User', permissionCode: 'USER_EXPORT' }
              ]
            }]
          }]
        }
      }, null, 2),
      cookie: []
    }
  ]
};

injectAfter(col.item, 'Create Role (Child)', updateRoleItem);
injectAfter(col.item, 'Create Role Permission', updateRolePermissionItem);

// ── update description ─────────────────────────────────────────────────────

col.info.description =
  'NEC Middleware REST API aligned with Spring controllers.\n\n' +
  '**Endpoints follow REST conventions:** POST to create, PUT /{id} to update.\n\n' +
  '**Sample values** match V000 seed data (gender/department/region/district/city id=1). ' +
  'Run **Create Role** before **Create User**, then **Login**.\n\n' +
  '`userId` and tokens are auto-captured from responses.';

// ── write ──────────────────────────────────────────────────────────────────

fs.writeFileSync(FILE, JSON.stringify(col, null, 2), 'utf8');
console.log('✓ Postman collection updated successfully.');
