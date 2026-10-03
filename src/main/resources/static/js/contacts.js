const API = '/api';

const listEl   = document.getElementById('list');
const emptyEl  = document.getElementById('empty');
const msgEl    = document.getElementById('msg');
const addForm  = document.getElementById('addForm');

/* ---------- 统一发请求 ---------- */
// ① 每次都自己判状态码；② 401 一律回登录页（设计文档 L83：跳转由前端做）
async function apiFetch(url, options = {}) {
    const resp = await fetch(url, options);
    if (resp.status === 401) {
        location.href = '/api/login.html';       // 后端 401 的契约：body 是 []，前端只看状态码
        throw new Error('未登录');
    }
    return resp;
}

function showMsg(text) { msgEl.textContent = text || ''; }

/* ---------- 查：加载列表（页面不刷新就靠它） ---------- */
async function load() {
    try {
        const resp = await apiFetch(API + '/contacts');
        const rows = await resp.json();
        render(rows);
    } catch (e) { /* 401 已跳走，这里不用管 */ }
}

function render(rows) {
    listEl.innerHTML = '';                     // 先清空，整表重画（简单可靠）
    emptyEl.hidden = rows.length > 0;

    rows.forEach(c => {
        const tr = document.createElement('tr');

        // ★ 一律用 textContent 填文字，不用 innerHTML —— 防 XSS：
        //   姓名是用户输入，如果拼进 innerHTML，别人存个 <script> 你页面就中招
        [c.id, c.name, c.phone, c.landline || '-'].forEach(v => {
            const td = document.createElement('td');
            td.textContent = v;
            tr.appendChild(td);
        });

        const tdOp = document.createElement('td');
        tdOp.append(makeBtn('改', () => editRow(c)));
        tdOp.append(makeBtn('删', () => delRow(c)));
        tr.appendChild(tdOp);

        listEl.appendChild(tr);
    });
}

function makeBtn(label, handler) {
    const b = document.createElement('button');
    b.type = 'button';
    b.textContent = label;
    b.className = 'mini';
    b.addEventListener('click', handler);
    return b;
}

/* ---------- 增 ---------- */
addForm.addEventListener('submit', async (e) => {
    e.preventDefault();                        // ★ 不加这行表单会真提交、页面会跳走
    const body = {
        name:     document.getElementById('name').value,
        phone:    document.getElementById('phone').value,
        landline: document.getElementById('landline').value
    };
    const resp = await apiFetch(API + '/contacts', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    const data = await resp.json();
    if (data.status === 'success') {
        addForm.reset();
        showMsg('新增成功');
        load();                                  // ★ 重新拉列表 = 页面不刷新，表格自己变
    } else {
        showMsg(data.cause || '新增失败');
    }
});

/* ---------- 改 ---------- */
// 先把当前行填进输入框（省得手打 id 和原值），改完点「保存」
async function editRow(c) {
    let old = c;
    try {
        const resp = await apiFetch(API + '/contacts/' + c.id);   // GET /{id} 拿最新值
        if (resp.status === 404) { showMsg('这条不存在或不属于你'); load(); return; }
        old = await resp.json();
    } catch (e) { return; }

    const oldName   = document.getElementById('name').value;
    const oldPhone  = document.getElementById('phone').value;
    const oldLand   = document.getElementById('landline').value;

    document.getElementById('name').value     = old.name;
    document.getElementById('phone').value    = old.phone;
    document.getElementById('landline').value = old.landline || '';

    const submitBtn = addForm.querySelector('button[type=submit]');
    submitBtn.textContent = '保存修改（id=' + c.id + '）';
    showMsg('改完点「保存修改」');
    document.getElementById('name').focus();

    const onSave = async (ev) => {
        ev.preventDefault();
        const resp = await apiFetch(API + '/contacts/' + c.id, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                name:     document.getElementById('name').value,
                phone:    document.getElementById('phone').value,
                landline: document.getElementById('landline').value
            })
        });
        const data = await resp.json();
        if (data.status === 'success') { showMsg('修改成功'); }
        else { showMsg('修改失败（id 不存在，或不是你的）'); }
        exitEdit();
        load();
    };

    const exitEdit = () => {
        addForm.removeEventListener('submit', onSave);
        submitBtn.textContent = '新增';
        addForm.reset();
        showMsg('');
    };

    addForm.removeEventListener('submit', onSave);
    addForm.addEventListener('submit', onSave, { once: true });
}

/* ---------- 删 ---------- */
async function delRow(c) {
    if (!confirm('确认删除「' + c.name + '」？')) return;
    const resp = await apiFetch(API + '/contacts/' + c.id, { method: 'DELETE' });
    const data = await resp.json();
    showMsg(data.status === 'success' ? '已删除' : '删除失败');
    load();
}

/* ---------- 登出 ---------- */
document.getElementById('logoutBtn').addEventListener('click', async () => {
    await fetch(API + '/logout', { method: 'POST' });   // 故意不用 apiFetch：登出不需要 401 跳转
    location.href = '/api/login.html';
});

document.getElementById('refreshBtn').addEventListener('click', load);

/* ---------- 打开页面就先拉一次 ---------- */
load();