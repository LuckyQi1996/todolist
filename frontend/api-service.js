/**
 * API服务层 - 用于与后端通信
 * 替换原有的localStorage存储方案
 *
 * @author Uiineed
 * @version 1.0.0
 */

class ApiService {
    constructor() {
        // API基础URL - 根据部署环境调整
        this.baseURL = 'http://localhost:8080/api';

        // 当前用户信息
        this.currentUser = null;

        // JWT Token
        this.token = localStorage.getItem('uiineed-jwt-token') || null;

        // 初始化
        this.init();
    }

    /**
     * 初始化API服务
     */
    init() {
        if (this.token) {
            this.getUserInfo().then(user => {
                this.currentUser = user;
            }).catch(error => {
                console.error('获取用户信息失败:', error);
                this.logout();
            });
        }
    }

    /**
     * 发送HTTP请求
     * @param {string} url - 请求URL
     * @param {object} options - 请求选项
     * @returns {Promise} - 请求Promise
     */
    async request(url, options = {}) {
        const config = {
            method: options.method || 'GET',
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        };

        // 添加认证头
        if (this.token) {
            config.headers['Authorization'] = `Bearer ${this.token}`;
        }

        try {
            const response = await fetch(`${this.baseURL}${url}`, config);

            // 处理401未授权错误
            if (response.status === 401) {
                this.logout();
                throw new Error('登录已过期，请重新登录');
            }

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || '请求失败');
            }

            return await response.json();
        } catch (error) {
            console.error('API请求错误:', error);
            throw error;
        }
    }

    /**
     * GET请求
     */
    get(url, options = {}) {
        return this.request(url, { ...options, method: 'GET' });
    }

    /**
     * POST请求
     */
    post(url, data, options = {}) {
        return this.request(url, {
            ...options,
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    /**
     * PUT请求
     */
    put(url, data, options = {}) {
        return this.request(url, {
            ...options,
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    /**
     * DELETE请求
     */
    delete(url, options = {}) {
        return this.request(url, { ...options, method: 'DELETE' });
    }

    // ==================== 认证相关API ====================

    /**
     * 获取微信登录二维码
     */
    async getLoginQrCode() {
        return await this.get('/auth/qrcode');
    }

    /**
     * 微信登录回调
     */
    async wechatCallback(code, state) {
        const response = await this.get(`/auth/wechat/callback?code=${code}&state=${state}`);
        if (response.code === 200) {
            this.token = response.data.token;
            this.currentUser = response.data.user;
            localStorage.setItem('uiineed-jwt-token', this.token);
            return response.data;
        } else {
            throw new Error(response.message);
        }
    }

    /**
     * 刷新Token
     */
    async refreshToken(refreshToken) {
        const response = await this.post('/auth/refresh', { refreshToken });
        if (response.code === 200) {
            this.token = response.data.token;
            localStorage.setItem('uiineed-jwt-token', this.token);
            return response.data;
        } else {
            throw new Error(response.message);
        }
    }

    /**
     * 登出
     */
    async logout() {
        try {
            await this.post('/auth/logout');
        } catch (error) {
            console.error('登出请求失败:', error);
        } finally {
            this.token = null;
            this.currentUser = null;
            localStorage.removeItem('uiineed-jwt-token');
            // 触发登出事件
            window.dispatchEvent(new CustomEvent('userLogout'));
        }
    }

    /**
     * 获取当前用户信息
     */
    async getUserInfo() {
        const response = await this.get('/auth/me');
        if (response.code === 200) {
            this.currentUser = response.data;
            return response.data;
        } else {
            throw new Error(response.message);
        }
    }

    // ==================== 待办事项相关API ====================

    /**
     * 获取待办事项列表
     * @param {object} params - 查询参数
     */
    async getTodos(params = {}) {
        const queryString = new URLSearchParams(params).toString();
        const response = await this.get(`/todos${queryString ? '?' + queryString : ''}`);
        if (response.code === 200) {
            return response.data;
        } else {
            throw new Error(response.message);
        }
    }

    /**
     * 获取单个待办事项
     */
    async getTodo(id) {
        const response = await this.get(`/todos/${id}`);
        if (response.code === 200) {
            return response.data;
        } else {
            throw new Error(response.message);
        }
    }

    /**
     * 创建待办事项
     */
    async createTodo(todoData) {
        const response = await this.post('/todos', todoData);
        if (response.code === 200) {
            return response.data;
        } else {
            throw new Error(response.message);
        }
    }

    /**
     * 更新待办事项
     */
    async updateTodo(id, todoData) {
        const response = await this.put(`/todos/${id}`, todoData);
        if (response.code === 200) {
            return response.data;
        } else {
            throw new Error(response.message);
        }
    }

    /**
     * 标记待办事项为完成
     */
    async completeTodo(id) {
        const response = await this.put(`/todos/${id}/complete`);
        if (response.code === 200) {
            return true;
        } else {
            throw new Error(response.message);
        }
    }

    /**
     * 标记待办事项为未完成
     */
    async uncompleteTodo(id) {
        const response = await this.put(`/todos/${id}/uncomplete`);
        if (response.code === 200) {
            return true;
        } else {
            throw new Error(response.message);
        }
    }

    /**
     * 删除待办事项（软删除）
     */
    async deleteTodo(id) {
        const response = await this.delete(`/todos/${id}`);
        if (response.code === 200) {
            return true;
        } else {
            throw new Error(response.message);
        }
    }

    /**
     * 恢复已删除的待办事项
     */
    async restoreTodo(id) {
        const response = await this.put(`/todos/${id}/restore`);
        if (response.code === 200) {
            return response.data;
        } else {
            throw new Error(response.message);
        }
    }

    /**
     * 批量操作待办事项
     */
    async batchOperation(action, ids) {
        const response = await this.post('/todos/batch', { action, ids });
        if (response.code === 200) {
            return true;
        } else {
            throw new Error(response.message);
        }
    }

    /**
     * 获取回收站待办事项
     */
    async getTrashTodos(params = {}) {
        const queryString = new URLSearchParams(params).toString();
        const response = await this.get(`/todos/trash${queryString ? '?' + queryString : ''}`);
        if (response.code === 200) {
            return response.data;
        } else {
            throw new Error(response.message);
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 检查是否已登录
     */
    isLoggedIn() {
        return !!this.token && !!this.currentUser;
    }

    /**
     * 获取当前用户
     */
    getCurrentUser() {
        return this.currentUser;
    }

    /**
     * 获取Token
     */
    getToken() {
        return this.token;
    }

    /**
     * 转换后端待办事项数据格式为前端兼容格式
     */
    convertTodoForFrontend(todo) {
        return {
            id: todo.id,
            title: todo.title,
            description: todo.description,
            priority: todo.priority || Todo.Priority.LOW,
            completed: todo.status === Todo.Status.COMPLETED,
            removed: todo.removed || false,
            categoryId: todo.categoryId,
            dueDate: todo.dueDate,
            reminderTime: todo.reminderTime,
            sortOrder: todo.sortOrder,
            createdAt: todo.createdAt,
            updatedAt: todo.updatedAt,
            completedAt: todo.completedAt,
            deletedAt: todo.deletedAt
        };
    }

    /**
     * 转换前端待办事项数据为后端格式
     */
    convertTodoForBackend(todo) {
        return {
            title: todo.title,
            description: todo.description,
            priority: todo.priority || Todo.Priority.LOW,
            status: todo.completed ? Todo.Status.COMPLETED : Todo.Status.TODO,
            categoryId: todo.categoryId,
            dueDate: todo.dueDate,
            reminderTime: todo.reminderTime,
            sortOrder: todo.sortOrder
        };
    }
}

// 创建全局API服务实例
window.apiService = new ApiService();

// 常量定义
window.Todo = {
    Status: {
        TODO: 0,
        IN_PROGRESS: 1,
        COMPLETED: 2,
        CANCELLED: 3
    },
    Priority: {
        LOW: 1,
        MEDIUM: 2,
        HIGH: 3
    }
};