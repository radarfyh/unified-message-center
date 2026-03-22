// 1. 全局函数 - 带消息提示
window.sendRedisCommand = function(action) {
    const instanceId = window.location.pathname.split('/')[2];
    fetch(`/actuator/redis/${action}`, {
        method: 'POST'
    })
    .then(response => {
        if (!response.ok) throw new Error('操作失败');
        return response.text();
    })
    .then(text => {
        showToast(`${action === 'start' ? '启动' : '停止'}Redis成功`, 'success');
        setTimeout(() => window.location.reload(), 1500);
    })
    .catch(err => {
        showToast(`操作失败: ${err.message}`, 'error');
    });
};

// 2. 显示Toast通知
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `notification is-${type}`;
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
        min-width: 250px;
        animation: fadeIn 0.3s;
    `;
    
    toast.innerHTML = `
        <button class="delete" onclick="this.parentElement.remove()"></button>
        ${message}
    `;
    
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

// 3. 检查是否已存在Redis菜单
function hasRedisMenu() {
    const links = document.querySelectorAll('a.navbar-link');
    for (const link of links) {
        if (link.textContent.trim() === 'Redis') {
            return true;
        }
    }
    return false;
}

// 4. 创建Redis二级菜单
function createRedisMenu() {
    // 主菜单项
    const menuItem = document.createElement('div');
    menuItem.className = 'navbar-item has-dropdown is-hoverable';
    menuItem.innerHTML = `
        <a class="navbar-link">Redis</a>
        <div class="navbar-dropdown">
            <a role="button" class="navbar-item" onclick="sendRedisCommand('start')">
                <span class="icon-text">
                    <span class="icon has-text-success">
                        <i class="fas fa-play"></i>
                    </span>
                    <span>启动Redis</span>
                </span>
            </a>
            <a role="button" class="navbar-item" onclick="sendRedisCommand('stop')">
                <span class="icon-text">
                    <span class="icon has-text-danger">
                        <i class="fas fa-stop"></i>
                    </span>
                    <span>停止Redis</span>
                </span>
            </a>
        </div>
    `;

    // 插入到语言菜单前
    const navbarEnd = document.querySelector('.navbar-end');
    const languageMenu = document.querySelector('.navbar-item.has-dropdown');
    
    if (languageMenu) {
        navbarEnd.insertBefore(menuItem, languageMenu);
    } else {
        navbarEnd.appendChild(menuItem);
    }
}

// 5. 初始化逻辑
function init() {
    // 等待必要元素加载
    if (!document.querySelector('.navbar-end')) {
        setTimeout(init, 100);
        return;
    }
    
    // 防止重复创建
    if (!hasRedisMenu()) {
        createRedisMenu();
        
        // 添加CSS动画
        const style = document.createElement('style');
        style.textContent = `
            @keyframes fadeIn {
                from { opacity: 0; transform: translateY(-20px); }
                to { opacity: 1; transform: translateY(0); }
            }
        `;
        document.head.appendChild(style);
    }
}

// 启动初始化
document.addEventListener('DOMContentLoaded', init);
setTimeout(init, 2000); // 双重保险