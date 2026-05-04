// UC19 Topics: JS, DOM manipulation, Event handling, Objects, Classes, Exception handling, ES9 Features, Async, CallBack, Promises, AJAX, Conditional logic. Dynamic UI rendering.

// Class representing the API Client (AJAX/Fetch)
class ApiClient {
    constructor(baseURL) {
        this.baseURL = baseURL;
    }

    // Async/Await (ES9+ feature) and Promises for real AJAX calls
    async post(endpoint, data) {
        const token = localStorage.getItem('qma_token');
        const headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        };

        // Don't send token for auth endpoints (login/signup)
        const isAuthEndpoint = endpoint.includes('/auth/login') || endpoint.includes('/auth/signup');
        if (token && !isAuthEndpoint) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch(`${this.baseURL}${endpoint}`, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `API Error: ${response.status} ${response.statusText}`);
        }

        return await response.json();
    }
}

// App configuration and state object
const config = {
    apiBaseUrl: 'http://localhost:4000', // Default QMA API backend port
    measurements: {
        LENGTH: ['FEET', 'INCH', 'YARDS', 'CENTIMETERS', 'METERS'],
        WEIGHT: ['KILOGRAM', 'GRAM', 'POUND', 'OUNCE'],
        VOLUME: ['LITRE', 'MILLILITRE', 'GALLON'],
        TEMPERATURE: ['CELSIUS', 'FAHRENHEIT', 'KELVIN']
    }
};

// Main UI Controller Class
class QuantityApp {
    constructor() {
        this.api = new ApiClient(config.apiBaseUrl);
        this.currentType = 'LENGTH';

        // Auth state
        this.user = JSON.parse(localStorage.getItem('qma_user'));
        this.isLoginMode = true;

        // DOM Elements
        this.typeList = document.getElementById('measurement-types');
        this.unit1Select = document.getElementById('this-unit');
        this.unit2Select = document.getElementById('that-unit');

        this.val1Input = document.getElementById('this-value');
        this.val2Input = document.getElementById('that-value');

        this.resultContainer = document.getElementById('result-container');
        this.resultText = document.getElementById('result-text');
        this.errorContainer = document.getElementById('error-container');
        this.errorText = document.getElementById('error-text');

        this.init();
    }

    init() {
        this.renderMeasurementTypes();
        this.populateUnits(this.currentType);
        this.bindEvents();
        this.updateAuthUI();
    }

    // Dynamic UI rendering
    renderMeasurementTypes() {
        this.typeList.innerHTML = '';

        // Iterating over Object keys
        Object.keys(config.measurements).forEach(type => {
            const li = document.createElement('li');
            li.textContent = type.charAt(0) + type.slice(1).toLowerCase();
            if (type === this.currentType) li.classList.add('active');

            // Event handling with callback
            li.addEventListener('click', () => this.handleTypeChange(type, li));

            this.typeList.appendChild(li);
        });
    }

    handleTypeChange(newType, element) {
        this.currentType = newType;

        // Update active class
        document.querySelectorAll('#measurement-types li').forEach(el => el.classList.remove('active'));
        element.classList.add('active');

        this.populateUnits(newType);
        this.hideAlerts();
    }

    populateUnits(type) {
        const units = config.measurements[type];

        this.unit1Select.innerHTML = '';
        this.unit2Select.innerHTML = '';

        units.forEach(unit => {
            const option1 = new Option(unit, unit);
            const option2 = new Option(unit, unit);
            this.unit1Select.add(option1);
            this.unit2Select.add(option2);
        });
    }

    bindEvents() {
        // Event listeners for action buttons
        document.getElementById('btn-convert').addEventListener('click', () => this.performOperation('CONVERT'));
        document.getElementById('btn-add').addEventListener('click', () => this.performOperation('ADD'));
        document.getElementById('btn-subtract').addEventListener('click', () => this.performOperation('SUBTRACT'));
        document.getElementById('btn-compare').addEventListener('click', () => this.performOperation('COMPARE'));

        // Landing Page toggle actions
        document.getElementById('link-show-signup').addEventListener('click', (e) => {
            e.preventDefault();
            this.toggleAuthMode(false);
        });
        document.getElementById('link-show-login').addEventListener('click', (e) => {
            e.preventDefault();
            this.toggleAuthMode(true);
        });

        // Dashboard actions
        document.getElementById('btn-logout').addEventListener('click', () => this.handleLogout());

        // Form submissions
        document.getElementById('login-form').addEventListener('submit', (e) => this.handleAuthSubmit(e, 'login'));
        document.getElementById('signup-form').addEventListener('submit', (e) => this.handleAuthSubmit(e, 'signup'));
    }

    // Async function handling the logic
    async performOperation(operationType) {
        this.hideAlerts();

        // Object payload construction
        const payload = {
            thisQuantityDTO: {
                value: parseFloat(this.val1Input.value),
                unit: this.unit1Select.value,
                measurementType: this.currentType
            },
            thatQuantityDTO: {
                value: parseFloat(this.val2Input.value),
                unit: this.unit2Select.value,
                measurementType: this.currentType
            }
        };

        // Conditional logic for validation
        if (isNaN(payload.thisQuantityDTO.value) || isNaN(payload.thatQuantityDTO.value)) {
            this.showError("Please enter valid numeric values for both quantities.");
            return;
        }

        const endpointMap = {
            'CONVERT': '/api/v1/quantities/convert',
            'ADD': '/api/v1/quantities/add',
            'SUBTRACT': '/api/v1/quantities/subtract',
            'COMPARE': '/api/v1/quantities/compare'
        };

        try {
            // Awaiting the promise
            const result = await this.api.post(endpointMap[operationType], payload);

            // Smart Comparison Fallback: If not equal, check if greater/smaller using divide
            if (operationType === 'COMPARE' && result.resultString === 'false') {
                try {
                    const divResult = await this.api.post('/api/v1/quantities/divide', payload);
                    if (divResult.resultValue > 1) {
                        result.resultString = 'GREATER';
                    } else if (divResult.resultValue < 1) {
                        result.resultString = 'SMALLER';
                    }
                } catch (e) {
                    console.error("Smart comparison fallback failed", e);
                }
            }

            // Dynamic UI update on success
            this.showResult(this.formatResult(operationType, result));

        } catch (error) {
            this.showError(`Operation failed: ${error.message}`);
        }
    }

    formatResult(operation, data) {
        if (data.error) {
            return `Error: ${data.errorMessage}`;
        }

        // Handle Comparison
        if (operation === 'COMPARE') {
            if (data.resultString === 'true') {
                return "✅ The quantities are equal.";
            } else if (data.resultString === 'false') {
                return "❌ The quantities are not equal.";
            }
            return data.resultString || `Result: ${data.resultValue}`;
        }

        // Handle Conversion, Addition, Subtraction
        if (data.resultValue !== undefined && data.resultValue !== null) {
            const unit = data.resultUnit || '';
            return `Result: ${data.resultValue} ${unit}`;
        }

        if (data.resultString) {
            return data.resultString;
        }

        return "Operation completed successfully.";
    }

    showResult(message) {
        this.resultText.textContent = message;
        this.resultContainer.classList.remove('hidden');
    }

    showError(message) {
        this.errorText.textContent = message;
        this.errorContainer.classList.remove('hidden');
    }

    hideAlerts() {
        this.resultContainer.classList.add('hidden');
        this.errorContainer.classList.add('hidden');
    }

    // AUTHENTICATION & VIEW NAVIGATION
    updateAuthUI() {
        const landingPage = document.getElementById('landing-page');
        const mainApp = document.getElementById('main-app');
        const userDisplay = document.getElementById('user-display');

        if (this.user) {
            // Hide landing page completely
            landingPage.classList.add('hidden');
            landingPage.style.display = 'none'; // explicit hide
            // Show main application
            mainApp.classList.remove('hidden');
            mainApp.style.display = 'flex'; // ensure flex layout
            userDisplay.textContent = `Hi, ${this.user.name || this.user.email}`;

            // Render app content if logged in
            this.renderMeasurementTypes();
            this.populateUnits(this.currentType);
        } else {
            // Show landing page again
            landingPage.classList.remove('hidden');
            landingPage.style.display = 'flex';
            // Hide main app
            mainApp.classList.add('hidden');
            mainApp.style.display = 'none';
        }
    }
    

    toggleAuthMode(showLogin) {
        this.isLoginMode = showLogin;
        const loginContainer = document.getElementById('login-form-container');
        const signupContainer = document.getElementById('signup-form-container');

        if (this.isLoginMode) {
            loginContainer.classList.remove('hidden');
            signupContainer.classList.add('hidden');
        } else {
            loginContainer.classList.add('hidden');
            signupContainer.classList.remove('hidden');
        }
    }

    async handleAuthSubmit(event, mode) {
        event.preventDefault();
        this.hideAlerts();

        const data = mode === 'login'
            ? {
                email: document.getElementById('login-email').value,
                password: document.getElementById('login-password').value
            }
            : {
                name: document.getElementById('signup-name').value,
                email: document.getElementById('signup-email').value,
                password: document.getElementById('signup-password').value
            };

        // Manual validation since we removed minlength from HTML
        if (data.password.length < 8) {
            alert("Password must be at least 8 characters long.");
            return;
        }

        try {
            const endpoint = mode === 'login' ? '/api/v1/auth/login' : '/api/v1/auth/signup';
            const response = await this.api.post(endpoint, data);

            if (mode === 'signup') {
                alert("Account created successfully! Please login with your credentials.");
                this.toggleAuthMode(true); // Switch to login mode
                return;
            }

            // For login mode, store token and user data
            const token = response.accessToken || response.token;
            if (token) {
                localStorage.setItem('qma_token', token);
                localStorage.setItem('qma_user', JSON.stringify(response.user || { email: data.email, name: data.name }));
                this.user = JSON.parse(localStorage.getItem('qma_user'));

                this.updateAuthUI();
                alert(`Logged in successfully!`);
            }
        } catch (error) {
            console.error(`Auth Error (${mode}):`, error);
            alert(`Error during ${mode}: ${error.message}`);
        }
    }

    handleLogout() {
        localStorage.removeItem('qma_token');
        localStorage.removeItem('qma_user');
        this.user = null;
        this.updateAuthUI();
    }
}

// Initialize application when DOM is fully loaded
document.addEventListener('DOMContentLoaded', () => {
    new QuantityApp();
});
