const API = '/api';

const form  = document.getElementById('registerForm');
const msgEl = document.getElementById('msg');

form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const username  = document.getElementById('username').value;
    const password  = document.getElementById('password').value;
    const password2 = document.getElementById('password2').value;

    // ★ 这条检查只有前端能做 —— 后端根本没收到 password2 这个字段，
    //   它不在接口契约里（M9-设计.md L88：/register 只收 username + password）
    if (password !== password2) {
        msgEl.textContent = '两次输入的密码不一致';
        return;
    }

    try {
        const resp = await fetch(API + '/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })     // ← 只发两个字段
        });

        if (resp.status === 401) { msgEl.textContent = '未登录'; return; }

        const data = await resp.json();
        if (data.status === 'success') {
            // 设计文档决策（M9-设计.md L80）：注册即自动登录，服务端已建 session、下发 JSESSIONID
            location.href = '/api/contacts.html';
        } else {
            msgEl.textContent = data.cause || '注册失败';
        }
    } catch (err) {
        msgEl.textContent = '网络异常：' + err.message;
    }
});