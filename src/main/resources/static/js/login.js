// 统一前缀：/api = 后端 context-path。写绝对路径，将来换域名不用改。
const API = '/api';

const form = document.getElementById('loginForm');
const msgEl = document.getElementById('msg');

form.addEventListener('submit', async (e) => {
    e.preventDefault();                       // ★ 关键：不加这行，表单会真提交、页面会跳走 —— 就违背了「页面不刷新」

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const resp = await fetch(API + '/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })   // ★ JS 对象 → JSON 文本
        });

        // ★ fetch 遇 4xx 不报错（MDN 原文），所以必须自己判状态码
        if (resp.status === 401) {
            msgEl.textContent = '未登录';
            return;
        }

        const data = await resp.json();         // ★ JSON 文本 → JS 对象

        if (data.status === 'success') {
            // 此刻浏览器已收到 Set-Cookie: JSESSIONID，后续请求会自动带上（同源，无需写任何代码）
            location.href = '/api/contacts.html';
        } else {
            msgEl.textContent = data.cause || '登录失败';
        }
    } catch (err) {
        // 只有「网络断了 / URL 写错」才会进这里，不是 4xx
        msgEl.textContent = '网络异常：' + err.message;
    }
});