/**
 * Uiineed Todo List API服务 V2
 * 优化版本：支持请求缓存、错误重试、并发控制
 */

class ApiServiceV2 {
    constructor() {
        this.baseURL = this.getApiBaseUrl();
        this.token = localStorage.getItem('uiineed-jwt-token');
        this.refreshToken = localStorage.getItem('uiineed-refresh-token');

        // 请求缓存
        this.cache = new Map();
        this.cacheTimeout = 5 * 60 * 1000; // 5分钟缓存

        // 请求去重
        this.pendingRequests = new Map();

        // 重试配置
        this.retryConfig = {
            maxRetries: 3,
            retryDelay: 1000,
            retryCondition: (error) => {
                return error.status >= 500 || error.status === 0;
            }
        };

        // 请求拦截器
        this.interceptors = {
            request: [],
            response: []
        };

        // 监听storage变化，同步token
        this.setupStorageListener();
    }

    /**
     * 获取API基础URL
     */
    getApiBaseUrl() {
        // 开发环境检测
        if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
            return process.env.API_BASE_URL || 'http://localhost:8080/api';
        }

        // 生产环境
        return process.env.API_BASE_URL || '/api';
    }

    /**
     * 设置storage监听器
     */
    setupStorageListener() {
        window.addEventListener('storage', (e) => {
            if (e.key === 'uiineed-jwt-token') {
                this.token = e.newValue;
            } else if (e.key === 'uiineed-refresh-token') {
                this.refreshToken = e.newValue;
            }
        });
    }

    /**
     * 添加请求拦截器
     */
    addRequestInterceptor(interceptor) {
        this.interceptors.request.push(interceptor);
    }

    /**
     * 添加响应拦截器
     */
    addResponseInterceptor(interceptor) {
        this.interceptors.response.push(interceptor);
    }

    /**
     * 应用请求拦截器
     */
    async applyRequestInterceptors(config) {
        for (const interceptor of this.interceptors.request) {
            config = await interceptor(config);
        }
        return config;
    }

    /**
     * 应用响应拦截器
     */
    async applyResponseInterceptors(response) {
        for (const interceptor of this.interceptors.response) {
            response = await interceptor(response);
        }
        return response;
    }

    /**
     * 生成缓存键
     */
    getCacheKey(url, options = {}) {
        return `${url}-${JSON.stringify(options)}`;
    }

    /**
     * 检查缓存
     */
    getFromCache(cacheKey) {
        const cached = this.cache.get(cacheKey);
        if (cached && Date.now() - cached.timestamp < this.cacheTimeout) {
            return cached.data;
        }
        this.cache.delete(cacheKey);
        return null;
    }

    /**
     * 设置缓存
     */
    setCache(cacheKey, data) {
        this.cache.set(cacheKey, {
            data,
            timestamp: Date.now()
        });
    }

    /**
     * 发起HTTP请求
     */
    async request(url, options = {}) {
        const cacheKey = this.getCacheKey(url, options);

        // 检查缓存（仅对GET请求）
        if (!options.method || options.method.toUpperCase() === 'GET') {
            const cachedData = this.getFromCache(cacheKey);
            if (cachedData) {
                return cachedData;
            }
        }

        // 检查是否有相同的请求正在进行
        if (this.pendingRequests.has(cacheKey)) {
            return this.pendingRequests.get(cacheKey);
        }

        // 创建请求Promise
        const requestPromise = this._makeRequest(url, options);
        this.pendingRequests.set(cacheKey, requestPromise);

        try {
            const response = await requestPromise;

            // 缓存GET请求的响应
            if (!options.method || options.method.toUpperCase() === 'GET') {
                this.setCache(cacheKey, response);
            }

            return response;
        } finally {
            this.pendingRequests.delete(cacheKey);
        }
    }

    /**
     * 实际发起请求
     */
    async _makeRequest(url, options) {
        // 默认配置
        const config = {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            },
            ...options
        };

        // 添加认证头
        if (this.token) {
            config.headers['Authorization'] = `Bearer ${this.token}`;
        }

        // 应用请求拦截器
        const processedConfig = await this.applyRequestInterceptors(config);

        let lastError;

        // 重试逻辑
        for (let attempt = 0; attempt <= this.retryConfig.maxRetries; attempt++) {
            try {
                const response = await fetch(`${this.baseURL}${url}`, processedConfig);

                // 处理响应
                const processedResponse = await this._handleResponse(response);

                // 应用响应拦截器
                return await this.applyResponseInterceptors(processedResponse);

            } catch (error) {
                lastError = error;

                // 如果是最后一次尝试，直接抛出错误
                if (attempt === this.retryConfig.maxRetries) {
                    throw error;
                }

                // 检查是否应该重试
                if (!this.retryConfig.retryCondition(error)) {
                    throw error;
                }

                // 等待后重试
                await this.delay(this.retryConfig.retryDelay * Math.pow(2, attempt));
            }
        }

        throw lastError;
    }

    /**
     * 处理响应
     */
    async _handleResponse(response) {
        let data;

        try {
            data = await response.json();
        } catch (error) {
            throw new Error('响应解析失败');
        }

        // 检查业务状态码
        if (data.code !== 200) {
            const error = new Error(data.message || '请求失败');
            error.code = data.code;
            error.data = data.data;
            throw error;
        }

        // 检查HTTP状态码
        if (!response.ok) {
            const error = new Error(`HTTP ${response.status}: ${response.statusText}`);
            error.status = response.status;
            error.data = data;
            throw error;
        }

        return data;
    }

    /**
     * 延迟函数
     */
    delay(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    /**
     * 刷新Token
     */
    async refreshAccessToken() {
        if (!this.refreshToken) {
            throw new Error('无有效的刷新令牌');
        }

        try {
            const response = await this.request('/auth/refresh', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: `refreshToken=${encodeURIComponent(this.refreshToken)}`
            });

            const { token, refreshToken } = response.data;

            // 更新token
            this.token = token;
            this.refreshToken = refreshToken;

            // 保存到localStorage
            localStorage.setItem('uiineed-jwt-token', token);
            localStorage.setItem('uiineed-refresh-token', refreshToken);

            return token;
        } catch (error) {
            // 刷新失败，清除token
            this.clearTokens();
            throw error;
        }
    }

    /**
     * 清除tokens
     */
    clearTokens() {
        this.token = null;
        this.refreshToken = null;
        localStorage.removeItem('uiineed-jwt-token');
        localStorage.removeItem('uiineed-refresh-token');

        // 清除所有缓存
        this.cache.clear();
    }

    /**
     * 带自动重试的请求方法
     */
    async requestWithRetry(url, options = {}) {
        try {
            return await this.request(url, options);
        } catch (error) {
            // 如果是认证错误，尝试刷新token
            if (error.status === 401 && this.refreshToken) {
                try {
                    await this.refreshAccessToken();
                    // 重试原请求
                    return await this.request(url, options);
                } catch (refreshError) {
                    // 刷新失败，跳转到登录页
                    this.handleAuthError();
                    throw refreshError;
                }
            }
            throw error;
        }
    }

    /**
     * 处理认证错误
     */
    handleAuthError() {
        this.clearTokens();
        // 可以触发自定义事件或跳转到登录页
        window.dispatchEvent(new CustomEvent('auth:expired'));
    }

    // ==================== API 方法 ====================

    /**
     * 用户认证相关
     */
    auth = {
        // 获取登录二维码
        getQrCode: () => this.request('/auth/qrcode'),

        // 微信登录回调
        wechatCallback: (code, state) =>
            this.request(`/auth/wechat/callback?code=${code}&state=${state}`),

        // 获取用户信息
        getCurrentUser: () => this.request('/auth/me'),

        // 登出
        logout: () => this.request('/auth/logout', { method: 'POST' }),

        // 刷新token
        refreshToken: () => this.refreshAccessToken()
    };

    /**
     * 待办事项相关
     */
    todos = {
        // 获取待办事项列表
        getList: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return this.requestWithRetry(`/todos?${query}`);
        },

        // 获取单个待办事项
        getById: (id) => this.requestWithRetry(`/todos/${id}`),

        // 创建待办事项
        create: (todoData) => this.requestWithRetry('/todos', {
            method: 'POST',
            body: JSON.stringify(todoData)
        }),

        // 更新待办事项
        update: (id, todoData) => this.requestWithRetry(`/todos/${id}`, {
            method: 'PUT',
            body: JSON.stringify(todoData)
        }),

        // 标记完成
        complete: (id) => this.requestWithRetry(`/todos/${id}/complete`, {
            method: 'PUT'
        }),

        // 标记未完成
        uncomplete: (id) => this.requestWithRetry(`/todos/${id}/uncomplete`, {
            method: 'PUT'
        }),

        // 删除待办事项
        delete: (id) => this.requestWithRetry(`/todos/${id}`, {
            method: 'DELETE'
        }),

        // 恢复待办事项
        restore: (id) => this.requestWithRetry(`/todos/${id}/restore`, {
            method: 'PUT'
        }),

        // 批量操作
        batchOperation: (action, ids) => this.requestWithRetry('/todos/batch', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `action=${action}&ids=${ids.join(',')}`
        }),

        // 获取回收站
        getTrash: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return this.requestWithRetry(`/todos/trash?${query}`);
        }
    };

    /**
     * 健康检查
     */
    health = {
        check: () => this.request('/health')
    };

    /**
     * 工具方法
     */
    utils = {
        // 清除缓存
        clearCache: () => {
            this.cache.clear();
        },

        // 获取缓存状态
        getCacheInfo: () => {
            return {
                size: this.cache.size,
                keys: Array.from(this.cache.keys())
            };
        },

        // 设置token
        setTokens: (accessToken, refreshToken) => {
            this.token = accessToken;
            this.refreshToken = refreshToken;
            localStorage.setItem('uiineed-jwt-token', accessToken);
            localStorage.setItem('uiineed-refresh-token', refreshToken);
        },

        // 检查是否已登录
        isAuthenticated: () => {
            return !!this.token;
        }
    };
}

// 创建全局实例
window.apiServiceV2 = new ApiServiceV2();

// 向后兼容的别名
window.apiService = window.apiServiceV2;

// 导出（如果使用模块系统）
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ApiServiceV2;
}